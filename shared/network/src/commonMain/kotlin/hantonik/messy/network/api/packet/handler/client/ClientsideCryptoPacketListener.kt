package hantonik.messy.network.api.packet.handler.client

import hantonik.messy.network.ConnectionProtocol
import hantonik.messy.network.api.packet.handler.PacketListener
import hantonik.messy.network.packet.crypto.S2CCryptoResponsePacket

interface ClientsideCryptoPacketListener : PacketListener<ClientsideCryptoPacketListener> {
    fun handleResponse(packet: S2CCryptoResponsePacket)

    override val protocol: ConnectionProtocol
        get() = ConnectionProtocol.CRYPTO
}