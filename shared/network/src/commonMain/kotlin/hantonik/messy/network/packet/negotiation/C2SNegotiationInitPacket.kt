package hantonik.messy.network.packet.negotiation

import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.server.ServersideNegotiationPacketListener
import hantonik.messy.network.packet.PacketBuffer

class C2SNegotiationInitPacket(val protocolVersion: Int) : Packet<ServersideNegotiationPacketListener> {
    constructor(buffer: PacketBuffer) : this(buffer.readInt())

    override fun encode(buffer: PacketBuffer) = buffer.writeInt(this.protocolVersion)

    override fun handle(handler: ServersideNegotiationPacketListener) = handler.handleInit(this)
}