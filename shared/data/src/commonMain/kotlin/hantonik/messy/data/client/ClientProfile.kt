package hantonik.messy.data.client

import dev.whyoleg.cryptography.algorithms.XDH
import hantonik.messy.data.file.FileMetadata
import kotlin.time.Instant
import kotlin.uuid.Uuid

class ClientProfile(override val id: Uuid,
                    override val icon: FileMetadata?, override val encryptedFirstName: ByteArray, override val encryptedLastName: ByteArray,
                    val email: String, val phone: String?,
                    override val publicKey: XDH.PublicKey,
                    val encryptedPrivateKey: ByteArray, val privateKeyIv: ByteArray,
                    val encryptedDataKey: ByteArray, val dataKeyIv: ByteArray,
                    override val createdAt: Instant, override val deletedAt: Instant?) : ClientData(id, icon, encryptedFirstName, encryptedLastName, publicKey, createdAt, deletedAt) {
    constructor(clientData: ClientData, email: String, phone: String?, encryptedPrivateKey: ByteArray, privateKeyIv: ByteArray, encryptedDataKey: ByteArray, dataKeyIv: ByteArray) : this(clientData.id, clientData.icon, clientData.encryptedFirstName, clientData.encryptedLastName, email, phone, clientData.publicKey, encryptedPrivateKey, privateKeyIv, encryptedDataKey, dataKeyIv, clientData.createdAt, clientData.deletedAt)
}
