package hantonik.messy.network.packet.auth

import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.server.ServersideAuthPacketListener
import hantonik.messy.network.packet.PacketBuffer

class C2SAuthRegisterRequestPacket(val email: String, val loginHash: ByteArray, val passwordSalt: ByteArray) : Packet<ServersideAuthPacketListener> {
    constructor(buffer: PacketBuffer) : this(
        buffer.readUtf(255),
        buffer.readByteArray(),
        buffer.readByteArray()
    )

    override fun encode(buffer: PacketBuffer) {
        buffer.writeUtf(this.email, 255)

        buffer.writeByteArray(this.loginHash)
        buffer.writeByteArray(this.passwordSalt)
    }

    override fun handle(handler: ServersideAuthPacketListener) = handler.handleRegisterRequest(this)
}