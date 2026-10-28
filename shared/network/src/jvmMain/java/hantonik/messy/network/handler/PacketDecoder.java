package hantonik.messy.network.handler;

import hantonik.messy.network.Connection;
import hantonik.messy.network.packet.PacketBufferExtensionsKt;
import hantonik.messy.network.packet.PacketFlow;
import hantonik.messy.util.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class PacketDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final PacketFlow flow;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        if (buffer.readableBytes() != 0) {
            var packetBuffer = PacketBufferExtensionsKt.toPacketBuffer(buffer);
            var protocol = ctx.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get();

            var index = packetBuffer.readInt();

            var packet = protocol.createPacket(this.flow, index, packetBuffer);

            if (packet == null)
                throw new IOException("Bad packet index " + index);

            var size = packetBuffer.getSize();

            if (size > 0)
                throw new IOException("Packet" + protocol.getId() + "/" + index + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + size + " bytes extra whilst reading packet " + index);

            out.add(packet);

            LOGGER.debug(Connection.PACKET_RECEIVED_MARKER, "IN: [{}:{}] {}", protocol, index, packet.getClass().getName());
        }
    }
}
