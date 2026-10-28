package hantonik.messy.network.packet.data;

import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.client.ClientsideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

public record S2CDataClientChangedGroupIconPacket() implements Packet<ClientsideDataPacketListener> {
    public S2CDataClientChangedGroupIconPacket(PacketBuffer ignore) {
        this();
    }

    @Override
    public void handle(ClientsideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleClientChangedGroupIcon(this);
    }
}
