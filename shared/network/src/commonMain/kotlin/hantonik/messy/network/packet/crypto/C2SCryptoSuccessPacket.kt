package hantonik.messy.network.packet.crypto

import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.server.ServersideCryptoPacketListener
import hantonik.messy.network.packet.PacketBuffer

class C2SCryptoSuccessPacket(@Suppress("unused") buffer: PacketBuffer) : Packet<ServersideCryptoPacketListener> {
    override fun handle(handler: ServersideCryptoPacketListener) = handler.handleSuccess(this)
}