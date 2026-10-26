package hantonik.messy.data.client

import dev.whyoleg.cryptography.algorithms.XDH
import hantonik.messy.data.file.FileMetadata
import kotlin.time.Instant
import kotlin.uuid.Uuid

class ClientProfile(override val id: Uuid,
                    override val icon: FileMetadata?, override val encryptedFirstName: ByteArray, override val encryptedLastName: ByteArray,
                    val email: String, val phone: String?,
                    override val publicKey: XDH.PublicKey, val encryptedPrivateKey: ByteArray,
                    override val createdAt: Instant, override val deletedAt: Instant?) : ClientData(id, icon, encryptedFirstName, encryptedLastName, publicKey, createdAt, deletedAt) {
    constructor(clientData: ClientData, email: String, phone: String?, encryptedPrivateKey: ByteArray) : this(clientData.id, clientData.icon, clientData.encryptedFirstName, clientData.encryptedLastName, email, phone, clientData.publicKey, encryptedPrivateKey, clientData.createdAt, clientData.deletedAt)
}
