package hantonik.messy.network.packet.data;

import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.client.ClientsideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public record S2CDataClientReadMessagePacket(UUID clientId, UUID conversationId,
                                             int readToIndex) implements Packet<ClientsideDataPacketListener> {
    public S2CDataClientReadMessagePacket(PacketBuffer buffer) {
        this(buffer.readUUID(), buffer.readUUID(), buffer.readVarInt());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(this.clientId);
        buffer.writeUUID(this.conversationId);
        buffer.writeVarInt(this.readToIndex);
    }

    @Override
    public void handle(ClientsideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleClientReadMessage(this);
    }
}
