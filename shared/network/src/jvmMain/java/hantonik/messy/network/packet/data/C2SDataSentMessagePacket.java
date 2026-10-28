package hantonik.messy.network.packet.data;

import hantonik.messy.data.message.EncryptedMessage;
import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.server.ServersideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

public record C2SDataSentMessagePacket(
        EncryptedMessage encryptedMessage) implements Packet<ServersideDataPacketListener> {
    public C2SDataSentMessagePacket(PacketBuffer buffer) {
        this(buffer.readEncryptedMessage());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeEncryptedMessage(this.encryptedMessage);
    }

    @Override
    public void handle(ServersideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleSentMessage(this);
    }
}
