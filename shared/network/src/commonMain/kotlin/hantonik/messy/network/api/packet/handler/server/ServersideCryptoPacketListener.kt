package hantonik.messy.network.api.packet.handler.server

import hantonik.messy.network.ConnectionProtocol
import hantonik.messy.network.api.packet.handler.PacketListener
import hantonik.messy.network.packet.crypto.C2SCryptoSuccessPacket
import hantonik.messy.network.packet.crypto.C2SCryptoInitPacket

interface ServersideCryptoPacketListener : PacketListener<ServersideCryptoPacketListener> {
    fun handleInit(packet: C2SCryptoInitPacket)

    fun handleSuccess(packet: C2SCryptoSuccessPacket)

    override val protocol: ConnectionProtocol
        get() = ConnectionProtocol.CRYPTO
}