package hantonik.messy.network.packet.data;

import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.client.ClientsideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

import java.time.Instant;
import java.util.UUID;

public record S2CDataSentMessageConfirmPacket(UUID messageId,
                                              Instant sentAt) implements Packet<ClientsideDataPacketListener> {
    public S2CDataSentMessageConfirmPacket(PacketBuffer buffer) {
        this(buffer.readUUID(), buffer.readInstant());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(this.messageId);
        buffer.writeInstant(this.sentAt);
    }

    @Override
    public void handle(ClientsideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleSentMessageConfirm(this);
    }
}
