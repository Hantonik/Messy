package hantonik.messy.network.packet.auth

import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.server.ServersideAuthPacketListener
import hantonik.messy.network.packet.PacketBuffer

class C2SAuthLoginRequestPacket(val challenge: ByteArray) : Packet<ServersideAuthPacketListener> {
    constructor(buffer: PacketBuffer) : this(buffer.readByteArray())

    override fun encode(buffer: PacketBuffer) = buffer.writeByteArray(this.challenge)

    override fun handle(handler: ServersideAuthPacketListener) = handler.handleLoginRequest(this)
}