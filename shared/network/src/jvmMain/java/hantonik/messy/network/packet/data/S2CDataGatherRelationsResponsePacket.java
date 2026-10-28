package hantonik.messy.network.packet.data;

import hantonik.messy.data.client.RelatedClientData;
import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.client.ClientsideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

public record S2CDataGatherRelationsResponsePacket(
        List<RelatedClientData> clients) implements Packet<ClientsideDataPacketListener> {
    public S2CDataGatherRelationsResponsePacket(PacketBuffer buffer) {
        var clients = new ArrayList<RelatedClientData>();

        var size = buffer.readVarInt();

        for (var i = 0; i < size; i++)
            clients.add(buffer.readRelatedClientData());

        this(clients);
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeVarInt(this.clients.size());

        for (var client : this.clients)
            buffer.writeRelatedClientData(client);
    }

    @Override
    public void handle(ClientsideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleGatherRelationsResponse(this);
    }
}
