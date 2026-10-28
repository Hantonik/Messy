package hantonik.messy.network.packet.data;

import hantonik.messy.data.message.EncryptedMessage;
import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.client.ClientsideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

public record S2CDataGatherMessagesResponsePacket(List<EncryptedMessage> encryptedMessages) implements Packet<ClientsideDataPacketListener> {
    public S2CDataGatherMessagesResponsePacket(PacketBuffer buffer) {
        var encryptedMessages = new ArrayList<EncryptedMessage>();
        var size = buffer.readVarInt();

        for (var i = 0; i < size; i++)
            encryptedMessages.add(buffer.readEncryptedMessage());

        this(encryptedMessages);
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeVarInt(this.encryptedMessages.size());

        for (var encryptedMessage : this.encryptedMessages)
            buffer.writeEncryptedMessage(encryptedMessage);
    }

    @Override
    public void handle(ClientsideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleGatherMessagesResponse(this);
    }
}
