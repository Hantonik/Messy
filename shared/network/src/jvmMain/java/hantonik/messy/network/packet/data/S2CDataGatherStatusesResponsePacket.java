package hantonik.messy.network.packet.data;

import hantonik.messy.data.client.ClientActivity;
import hantonik.messy.network.api.packet.Packet;
import hantonik.messy.network.api.packet.handler.client.ClientsideDataPacketListener;
import hantonik.messy.network.packet.PacketBuffer;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

public record S2CDataGatherStatusesResponsePacket(
        List<ClientActivity> activities) implements Packet<ClientsideDataPacketListener> {
    public S2CDataGatherStatusesResponsePacket(PacketBuffer buffer) {
        var activities = new ArrayList<ClientActivity>();

        var size = buffer.readVarInt();

        for (var i = 0; i < size; i++)
            activities.add(buffer.readClientActivity());

        this(activities);
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeVarInt(this.activities.size());

        for (var activity : this.activities)
            buffer.writeClientActivity(activity);
    }

    @Override
    public void handle(ClientsideDataPacketListener handler, ChannelHandlerContext context) {
        handler.handleGatherStatusesResponse(this);
    }
}
