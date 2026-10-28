package hantonik.messy.network.packet.data;

import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.client.ClientsideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

public record S2CDataPongPacket(long time) implements Packet<ClientsideDataPacketListener> {
    public S2CDataPongPacket(PacketBuffer buffer) {
        this(buffer.readVarLong());
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeVarLong(this.time);
    }

    @Override
    public void handle(ClientsideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handlePong(this);
    }
}
