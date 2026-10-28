package hantonik.messy.network.packet.data;

import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.server.ServersideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

public record C2SDataPingPacket(long time) implements Packet<ServersideDataPacketListener> {
    public C2SDataPingPacket(PacketBuffer buffer) {
        this(buffer.readVarLong());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeVarLong(this.time);
    }

    @Override
    public void handle(ServersideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handlePing(this);
    }
}
