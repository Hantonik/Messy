package hantonik.messy.network.packet.crypto

import dev.whyoleg.cryptography.algorithms.XDH
import hantonik.messy.encryption.Crypt
import hantonik.messy.encryption.exception.CryptException
import hantonik.messy.network.api.packet.Packet
import hantonik.messy.network.api.packet.handler.client.ClientsideCryptoPacketListener
import hantonik.messy.network.packet.PacketBuffer

class S2CCryptoResponsePacket(val publicKey: XDH.PublicKey, val encryptedChallenge: ByteArray) : Packet<ClientsideCryptoPacketListener> {
    constructor(buffer: PacketBuffer) : this(
        try {
            Crypt.decodeXDHPublicKeyX25519(buffer.readByteArray())
        } catch (e: CryptException) {
            throw IllegalStateException("Malformed public key bytes", e)
        },
        buffer.readByteArray()
    )

    override fun encode(buffer: PacketBuffer) {
        buffer.writeByteArray(Crypt.encodeXDHPublicKey(this.publicKey))
        buffer.writeByteArray(this.encryptedChallenge)
    }

    override fun handle(handler: ClientsideCryptoPacketListener) = handler.handleResponse(this)
}