package hantonik.messy.network.packet.data;

import hantonik.messy.data.client.ClientActivity;
import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.client.ClientsideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public record S2CDataUpdateStatusPacket(UUID clientId,
                                        ClientActivity activity) implements Packet<ClientsideDataPacketListener> {
    public S2CDataUpdateStatusPacket(PacketBuffer buffer) {
        this(buffer.readUUID(), buffer.readClientActivity());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(this.clientId);
        buffer.writeClientActivity(this.activity);
    }

    @Override
    public void handle(ClientsideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleUpdateStatus(this);
    }
}
