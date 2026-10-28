package hantonik.messy.network.packet.data;

import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.client.ClientsideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

import java.time.Instant;
import java.util.UUID;

public record S2CDataReadMessageConfirmPacket(UUID clientId,
                                              Instant readAt) implements Packet<ClientsideDataPacketListener> {
    public S2CDataReadMessageConfirmPacket(PacketBuffer buffer) {
        this(buffer.readUUID(), buffer.readInstant());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(this.clientId);
        buffer.writeInstant(this.readAt);
    }

    @Override
    public void handle(ClientsideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleReadMessageConfirm(this);
    }
}
