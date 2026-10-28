package hantonik.messy.network.packet.auth

import dev.whyoleg.cryptography.algorithms.XDH
import hantonik.messy.encryption.Crypt
import hantonik.messy.encryption.exception.CryptException
import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.server.ServersideAuthPacketListener
import hantonik.messy.network.packet.PacketBuffer

class C2SAuthRegisterDataResponsePacket(
    val encryptedFirstName: ByteArray, val encryptedLastName: ByteArray,
    val phone: String?,
    val publicKey: XDH.PublicKey,
    val encryptedPrivateKey: ByteArray, val privateKeyNonce: ByteArray
) : Packet<ServersideAuthPacketListener> {
    constructor(buffer: PacketBuffer) : this(
        buffer.readByteArray(), buffer.readByteArray(),
        if (buffer.readBoolean()) buffer.readUtf(20) else null,
        try {
            Crypt.decodeXDHPublicKeyX448(buffer.readByteArray())
        } catch (e: CryptException) {
            throw IllegalStateException("Malformed public key bytes", e)
        },
        buffer.readByteArray(), buffer.readByteArray()
    )

    override fun encode(buffer: PacketBuffer) {
        buffer.writeByteArray(this.encryptedFirstName)
        buffer.writeByteArray(this.encryptedLastName)

        buffer.writeBoolean(this.phone != null)

        if (this.phone != null)
            buffer.writeUtf(this.phone, 20)

        buffer.writeByteArray(Crypt.encodeXDHPublicKey(this.publicKey))

        buffer.writeByteArray(this.encryptedPrivateKey)
        buffer.writeByteArray(this.privateKeyNonce)
    }

    override fun handle(handler: ServersideAuthPacketListener) = handler.handleRegisterDataResponse(this)
}