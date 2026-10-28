package hantonik.messy.network.packet.data;

import hantonik.messy.data.message.EncryptedMessage;
import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.client.ClientsideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

public record S2CDataClientSentMessagePacket(EncryptedMessage message) implements Packet<ClientsideDataPacketListener> {
    public S2CDataClientSentMessagePacket(PacketBuffer buffer) {
        this(buffer.readEncryptedMessage());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeEncryptedMessage(this.message);
    }

    @Override
    public void handle(ClientsideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleClientSentMessage(this);
    }
}
