package hantonik.messy.network.packet.data;

import hantonik.messy.data.client.ClientActivity;
import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.server.ServersideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

public record C2SDataStatusUpdateResponsePacket(
        ClientActivity activity) implements Packet<ServersideDataPacketListener> {
    public C2SDataStatusUpdateResponsePacket(PacketBuffer buffer) {
        this(buffer.readClientActivity());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeClientActivity(this.activity);
    }

    @Override
    public void handle(ServersideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleStatusUpdateResponse(this);
    }
}
