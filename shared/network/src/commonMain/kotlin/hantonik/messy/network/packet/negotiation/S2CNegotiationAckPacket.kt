package hantonik.messy.network.packet.negotiation

import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.client.ClientsideNegotiationPacketListener
import hantonik.messy.network.packet.PacketBuffer

class S2CNegotiationAckPacket(val protocolVersion: Int) : Packet<ClientsideNegotiationPacketListener> {
    constructor(buffer: PacketBuffer) : this(buffer.readInt())

    override fun encode(buffer: PacketBuffer) = buffer.writeInt(this.protocolVersion)

    override fun handle(handler: ClientsideNegotiationPacketListener) = handler.handleAck(this)
}