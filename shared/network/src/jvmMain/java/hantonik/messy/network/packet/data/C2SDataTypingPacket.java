package hantonik.messy.network.packet.data;

import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.server.ServersideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public record C2SDataTypingPacket(UUID conversationId) implements Packet<ServersideDataPacketListener> {
    public C2SDataTypingPacket(PacketBuffer buffer) {
        this(buffer.readUUID());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUUID(this.conversationId);
    }

    @Override
    public void handle(ServersideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleTyping(this);
    }
}
