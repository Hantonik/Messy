package hantonik.messy.network.packet.data;

import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.client.ClientsideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public record S2CDataClientStoppedTypingPacket(UUID clientId,
                                               UUID conversationId) implements Packet<ClientsideDataPacketListener> {
    public S2CDataClientStoppedTypingPacket(PacketBuffer buffer) {
        this(buffer.readUUID(), buffer.readUUID());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(this.clientId);
        buffer.writeUUID(this.conversationId);
    }

    @Override
    public void handle(ClientsideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleClientStoppedTyping(this);
    }
}
