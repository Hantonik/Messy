package hantonik.messy.network.packet.data;

import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.client.ClientsideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public record S2CDataClientReceivedMessagePacket(UUID clientId,
                                                 UUID messageId) implements Packet<ClientsideDataPacketListener> {
    public S2CDataClientReceivedMessagePacket(PacketBuffer buffer) {
        this(buffer.readUUID(), buffer.readUUID());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(this.clientId);
        buffer.writeUUID(this.messageId);
    }

    @Override
    public void handle(ClientsideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleClientReceivedMessage(this);
    }
}
