package hantonik.messy.network.packet.auth

import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.client.ClientsideAuthPacketListener
import hantonik.messy.network.packet.PacketBuffer

class S2CAuthLoginSaltResponsePacket(val passwordSalt: ByteArray) : Packet<ClientsideAuthPacketListener> {
    constructor(buffer: PacketBuffer) : this(buffer.readByteArray())

    override fun encode(buffer: PacketBuffer) = buffer.writeByteArray(this.passwordSalt)

    override fun handle(handler: ClientsideAuthPacketListener) = handler.handleLoginSaltResponse(this)
}