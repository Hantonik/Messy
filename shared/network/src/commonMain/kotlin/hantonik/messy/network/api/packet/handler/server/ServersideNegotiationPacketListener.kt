package hantonik.messy.network.api.packet.handler.server

import hantonik.messy.network.ConnectionProtocol
import hantonik.messy.network.api.packet.handler.PacketListener
import hantonik.messy.network.packet.negotiation.C2SNegotiationInitPacket

interface ServersideNegotiationPacketListener : PacketListener<ServersideNegotiationPacketListener> {
    fun handleInit(packet: C2SNegotiationInitPacket)

    override val protocol: ConnectionProtocol
        get() = ConnectionProtocol.NEGOTIATION
}