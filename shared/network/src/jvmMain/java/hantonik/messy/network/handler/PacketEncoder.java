package hantonik.messy.network.handler;

import hantonik.messy.network.Connection;
import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.exception.SkipPacketException;
import hantonik.messy.network.packet.PacketBuffer;
import hantonik.messy.network.packet.PacketBufferExtensionsKt;
import hantonik.messy.network.packet.PacketFlow;
import hantonik.messy.util.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

import java.io.IOException;

@RequiredArgsConstructor
public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final PacketFlow flow;

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet<?> packet, ByteBuf out) throws Exception {
        var protocol = ctx.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get();

        if (protocol == null)
            throw new RuntimeException("ConnectionProtocol unknown: " + packet);
        else {
            var index = protocol.getPacketId(this.flow, packet);

            LOGGER.debug(Connection.PACKET_SENT_MARKER, "OUT: [{}:{}] {}", protocol, index, packet.getClass().getName());

            if (index == null)
                throw new IOException("Can't serialize unregistered packet: " + packet.getClass().getName());
            else {
                var packetBuffer = new PacketBuffer();
                packetBuffer.writeInt(index);

                try {
                    packet.encode(packetBuffer);

                    var size = packetBuffer.getSize();

                    if (size > 0x80000)
                        throw new IllegalArgumentException("Packet too big (is " + size + ", should be less than " + 0x80000 + " bytes): " + packet);

                    PacketBufferExtensionsKt.writeToByteBuf(packetBuffer, out);
                } catch (Throwable e) {
                    LOGGER.error("Error encoding packet {}", index, e);

                    if (packet.isSkippable())
                        throw new SkipPacketException(e);

                    throw e;
                }
            }
        }
    }
}
