package hantonik.messy.network;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.whyoleg.cryptography.algorithms.AES;
import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.PacketListener;
import hantonik.messy.network.exception.SkipPacketException;
import hantonik.messy.network.handler.CipherDecoder;
import hantonik.messy.network.handler.CipherEncoder;
import hantonik.messy.network.handler.PacketDecoder;
import hantonik.messy.network.handler.PacketEncoder;
import hantonik.messy.network.packet.PacketFlow;
import hantonik.messy.network.packet.PacketSendListener;
import hantonik.messy.util.LogUtils;
import hantonik.messy.util.Util;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueIoHandler;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class Connection extends SimpleChannelInboundHandler<Packet<?>> implements ConnectionHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Marker NETWORK_MARKER = MarkerFactory.getMarker("NETWORK");
    public static final Marker PACKET_MARKER = Util.make(MarkerFactory.getMarker("NETWORK_PACKETS"), marker -> marker.add(NETWORK_MARKER));
    public static final Marker PACKET_RECEIVED_MARKER = Util.make(MarkerFactory.getMarker("PACKET_RECEIVED"), marker -> marker.add(PACKET_MARKER));
    public static final Marker PACKET_SENT_MARKER = Util.make(MarkerFactory.getMarker("PACKET_SENT"), marker -> marker.add(PACKET_MARKER));

    public static final AttributeKey<ConnectionProtocol> ATTRIBUTE_PROTOCOL = AttributeKey.valueOf("protocol");

    public static final Supplier<MultiThreadIoEventLoopGroup> NETWORK_WORKER_GROUP = Suppliers.memoize(() -> new MultiThreadIoEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Client IO #%d").setDaemon(true).build(), NioIoHandler.newFactory()));
    public static final Supplier<MultiThreadIoEventLoopGroup> NETWORK_EPOLL_WORKER_GROUP = Suppliers.memoize(() -> new MultiThreadIoEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Epoll Client IO #%d").setDaemon(true).build(), EpollIoHandler.newFactory()));
    public static final Supplier<MultiThreadIoEventLoopGroup> NETWORK_KQUEUE_WORKER_GROUP = Suppliers.memoize(() -> new MultiThreadIoEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Kqueue Client IO #%d").setDaemon(true).build(), KQueueIoHandler.newFactory()));

    private final Queue<PacketHolder> queue = Queues.newConcurrentLinkedQueue();

    private final List<Runnable> disconnectionListeners = Collections.synchronizedList(Lists.newArrayList());

    @Getter
    private final PacketFlow flow;

    private Channel channel;
    @Getter
    private SocketAddress remoteAddress;
    @Getter
    private PacketListener<?> packetListener;

    @Getter
    private String disconnectionReason;

    @Getter
    private boolean encrypted;
    private boolean disconnectionHandled;

    private boolean handlingFault;

    public static Connection connectToServer(InetSocketAddress address) {
        var connection = new Connection(PacketFlow.S2C);
        connect(address, connection).syncUninterruptibly();

        return connection;
    }

    public static ChannelFuture connect(InetSocketAddress address, Connection connection) {
        Supplier<? extends EventLoopGroup> eventLoopGroup;
        Class<? extends SocketChannel> socketChannelClass;

        if (KQueue.isAvailable()) {
            eventLoopGroup = NETWORK_KQUEUE_WORKER_GROUP;
            socketChannelClass = KQueueSocketChannel.class;
        } else if (Epoll.isAvailable()) {
            eventLoopGroup = NETWORK_EPOLL_WORKER_GROUP;
            socketChannelClass = EpollSocketChannel.class;
        } else {
            eventLoopGroup = NETWORK_WORKER_GROUP;
            socketChannelClass = NioSocketChannel.class;
        }

        return new Bootstrap().group(eventLoopGroup.get()).handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException ignore) {}

                var pipeline = channel.pipeline();

                pipeline.addLast("timeout", new ReadTimeoutHandler(30));
                configureSerialization(pipeline, PacketFlow.S2C);
                pipeline.addLast("packet_handler", connection);
            }
        }).channel(socketChannelClass).connect(address.getAddress(), address.getPort());
    }

    public static void configureSerialization(ChannelPipeline pipeline, PacketFlow receiving) {
        pipeline.addLast("splitter", new LengthFieldBasedFrameDecoder(0x80000, 0, 4, 0, 4))
                .addLast("prepender", new LengthFieldPrepender(4))
//                .addLast("splitter", new VarInt21FrameDecoder())
//                .addLast("prepender", new VarInt21LengthFieldPrepender())
                .addLast("decoder", new PacketDecoder(receiving))
                .addLast("encoder", new PacketEncoder(receiving.getOpposite()));
    }

    @Override
    public synchronized void configureEncryption(AES.GCM.@NonNull Key key) {
        this.channel.pipeline().addBefore("splitter", "decryptor", new CipherDecoder(key));
        this.channel.pipeline().addBefore("splitter", "encryptor", new CipherEncoder(key));

        this.encrypted = true;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        this.channel = ctx.channel();
        this.remoteAddress = this.channel.remoteAddress();

        try {
            this.setProtocol(ConnectionProtocol.NEGOTIATION);
        } catch (Throwable e) {
            LOGGER.error(LogUtils.FATAL, "Failed to change protocol to handshake", e);
        }
    }

    @Override
    public void setProtocol(@NonNull ConnectionProtocol protocol) {
        this.channel.attr(ATTRIBUTE_PROTOCOL).set(protocol);
        this.channel.config().setAutoRead(true);

        LOGGER.debug("Enabled auto read");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.disconnect("End of stream");
    }

    public void disconnect(@NonNull String reason) {
        if (this.isConnected()) {
            this.channel.close().awaitUninterruptibly();

            this.disconnectionReason = reason;
            LOGGER.warn("Disconnected due to a reason: {}", reason);
        }
    }

    public void handleDisconnection() {
        if (this.channel != null && !this.channel.isOpen()) {
            if (this.disconnectionHandled)
                LOGGER.warn("handleDisconnection() called twice");
            else {
                this.disconnectionHandled = true;

                if (this.disconnectionReason != null)
                    this.packetListener.onDisconnect(this.disconnectionReason);
                else if (this.packetListener != null)
                    this.packetListener.onDisconnect("Disconnected");

                for (var listener : this.disconnectionListeners)
                    listener.run();
            }
        }
    }

    public void setReadOnly() {
        LOGGER.debug("Disabled auto namedReadAt");

        if (this.channel != null)
            this.channel.config().setAutoRead(false);
    }

    public boolean isConnecting() {
        return this.channel == null;
    }

    @Override
    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet<?> packet) {
        if (this.channel.isOpen()) {
            if (this.packetListener == null)
                throw new IllegalStateException("Received a packet before the packet listener was initialized");
            else {
                if (this.packetListener.shouldHandlePacket(packet)) {
                    try {
                        genericsFtw(packet, this.packetListener);
                    } catch (RejectedExecutionException e) {
                        this.disconnect("Server closed");
                    } catch (ClassCastException e) {
                        LOGGER.error("Received {} that couldn't be processed", packet.getClass(), e);

                        this.disconnect("Server sentAt an invalid packet");
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends PacketListener<T>> void genericsFtw(Packet<T> packet, PacketListener<?> listener) {
        packet.handle((T) listener);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof SkipPacketException)
            LOGGER.debug("Skipping packet due to errors", cause.getCause());
        else {
            var flag = !this.handlingFault;

            this.handlingFault = true;

            if (this.channel.isOpen()) {
                if (cause instanceof TimeoutException) {
                    LOGGER.debug("Timeout", cause);

                    this.disconnect("Timed out");
                } else {
                    var reason = "Internal Exception: " + cause;

                    if (flag) {
                        LOGGER.debug("Failed to sentAt packet", cause);

                        if (this.flow == PacketFlow.S2C)
                            this.sendPacket(this.getProtocol().createDisconnectionPacket(reason), PacketSendListener.thanRun(() -> this.disconnect(reason)));
                        else
                            this.disconnect(reason);

                        this.setReadOnly();
                    } else {
                        LOGGER.debug("Double fault", cause);

                        this.disconnect(reason);
                    }
                }
            }
        }
    }

    private void flushQueue() {
        if (this.channel != null && this.channel.isOpen()) {
            synchronized (this.queue) {
                PacketHolder holder;

                while ((holder = this.queue.poll()) != null)
                    this.sendPacketInternal(holder.packet, holder.listener);
            }
        }
    }

    public void sendPacket(Packet<?> packet) {
        this.sendPacket(packet, null);
    }

    public void sendPacket(@NonNull Packet<?> packet, @Nullable PacketSendListener listener) {
        if (this.isConnected()) {
            this.flushQueue();

            this.sendPacketInternal(packet, listener);
        } else
            this.queue.add(new PacketHolder(packet, listener));
    }

//    public void doTasks() {
//        this.flushQueue();
//
//        if (this.packetListener instanceof ActivePacketListener activeListener)
//            activeListener.doTasks();
//
//        if (!this.isConnected() && !this.disconnectionHandled)
//            this.handleDisconnection();
//
//        if (this.channel != null)
//            this.channel.flush();
//    }

    private void sendPacketInternal(Packet<?> packet, @Nullable PacketSendListener listener) {
        var packetProtocol = ConnectionProtocol.Companion.getProtocolForPacket(packet);
        var currentProtocol = this.getProtocol();

        if (packetProtocol != currentProtocol) {
            if (packetProtocol == null)
                throw new IllegalStateException("Encountered packet without set protocol: " + packet);

            this.setReadOnly();
        }

        if (this.channel.eventLoop().inEventLoop())
            this.sendPacketInternal(packet, listener, packetProtocol, currentProtocol);
        else
            this.channel.eventLoop().execute(() -> this.sendPacketInternal(packet, listener, packetProtocol, currentProtocol));
    }

    private void sendPacketInternal(Packet<?> packet, @Nullable PacketSendListener listener, ConnectionProtocol newProtocol, ConnectionProtocol currentProtocol) {
        if (newProtocol != currentProtocol)
            this.setProtocol(newProtocol);

        var channelFuture = this.channel.writeAndFlush(packet);

        if (listener != null) {
            channelFuture.addListener(future -> {
                if (future.isSuccess())
                    listener.onSuccess();
                else {
                    var failurePacket = listener.onFailure();

                    if (failurePacket != null)
                        this.channel.writeAndFlush(failurePacket).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            });
        }

        channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    @NonNull
    @Override
    public ConnectionProtocol getProtocol() {
        return this.channel.attr(ATTRIBUTE_PROTOCOL).get();
    }

    @Override
    public void setListener(@NonNull PacketListener<?> listener) {
        this.packetListener = listener;
    }

    public void addDisconnectionListener(Runnable listener) {
        this.disconnectionListeners.add(listener);
    }

    private record PacketHolder(Packet<?> packet, @Nullable PacketSendListener listener) {

    }
}
