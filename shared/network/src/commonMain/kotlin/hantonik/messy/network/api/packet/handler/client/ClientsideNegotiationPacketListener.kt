package hantonik.messy.network.api.packet.handler.client

import hantonik.messy.network.ConnectionProtocol
import hantonik.messy.network.api.packet.handler.PacketListener
import hantonik.messy.network.packet.negotiation.S2CNegotiationAckPacket

interface ClientsideNegotiationPacketListener : PacketListener<ClientsideNegotiationPacketListener> {
    fun handleAck(packet: S2CNegotiationAckPacket)

    override val protocol: ConnectionProtocol
        get() = ConnectionProtocol.NEGOTIATION
}