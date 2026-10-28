package hantonik.messy.network.packet.data;

import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.server.ServersideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

public record C2SDataParticipantReadOnlyChangeRequestPacket() implements Packet<ServersideDataPacketListener> {
    public C2SDataParticipantReadOnlyChangeRequestPacket(PacketBuffer ignore) {
        this();
    }

    @Override
    public void handle(ServersideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleParticipantReadOnlyChangeRequest(this);
    }
}
