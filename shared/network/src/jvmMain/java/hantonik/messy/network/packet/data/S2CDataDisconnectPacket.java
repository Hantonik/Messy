package hantonik.messy.network.packet.data;

import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.client.ClientsideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

public record S2CDataDisconnectPacket(String reason) implements Packet<ClientsideDataPacketListener> {
    public S2CDataDisconnectPacket(PacketBuffer buffer) {
        this(buffer.readUtf(0x40000));
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUtf(this.reason, 0x40000);
    }

    @Override
    public void handle(ClientsideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleDisconnect(this);
    }
}
