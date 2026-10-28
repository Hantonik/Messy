package hantonik.messy.network.packet.auth

import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.client.ClientsideAuthPacketListener
import hantonik.messy.network.packet.PacketBuffer

class S2CAuthRegisterDataRequestPacket(@Suppress("unused") buffer: PacketBuffer) : Packet<ClientsideAuthPacketListener> {
    override fun handle(handler: ClientsideAuthPacketListener) = handler.handleRegisterDataRequest(this)
}