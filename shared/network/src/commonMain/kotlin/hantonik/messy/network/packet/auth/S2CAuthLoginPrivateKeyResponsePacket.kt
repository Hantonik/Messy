package hantonik.messy.network.packet.auth

import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.client.ClientsideAuthPacketListener
import hantonik.messy.network.packet.PacketBuffer

class S2CAuthLoginPrivateKeyResponsePacket(val encryptedPrivateKey: ByteArray, val encryptedChallenge: ByteArray) : Packet<ClientsideAuthPacketListener> {
    constructor(buffer: PacketBuffer) : this(
        buffer.readByteArray(),
        buffer.readByteArray()
    )

    override fun encode(buffer: PacketBuffer) {
        buffer.writeByteArray(this.encryptedPrivateKey)
        buffer.writeByteArray(this.encryptedChallenge)
    }

    override fun handle(handler: ClientsideAuthPacketListener) = handler.handleLoginPrivateKeyResponse(this)
}