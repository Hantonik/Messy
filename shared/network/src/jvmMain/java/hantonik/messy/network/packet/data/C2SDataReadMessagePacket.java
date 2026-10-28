package hantonik.messy.network.packet.data;

import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.server.ServersideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public record C2SDataReadMessagePacket(UUID conversationId,
                                       int readToIndex) implements Packet<ServersideDataPacketListener> {
    public C2SDataReadMessagePacket(PacketBuffer buffer) {
        this(buffer.readUUID(), buffer.readVarInt());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(this.conversationId);
        buffer.writeVarInt(this.readToIndex);
    }

    @Override
    public void handle(ServersideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleReadMessage(this);
    }
}
