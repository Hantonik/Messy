package hantonik.messy.data.client

import dev.whyoleg.cryptography.algorithms.XDH
import hantonik.messy.data.file.FileMetadata
import kotlin.time.Instant
import kotlin.uuid.Uuid

class RelatedClientData(override val id: Uuid,
                             override val icon: FileMetadata?, override val encryptedFirstName: ByteArray, override val encryptedLastName: ByteArray,
                             override val publicKey: XDH.PublicKey,
                             override val createdAt: Instant, override val deletedAt: Instant?,
                             val mutedUntil: Instant?, val isBlocked: Boolean) : ClientData(id, icon, encryptedFirstName, encryptedLastName, publicKey, createdAt, deletedAt) {
    constructor(clientData: ClientData, mutedUntil: Instant?, isBlocked: Boolean) : this(clientData.id, clientData.icon, clientData.encryptedFirstName, clientData.encryptedLastName, clientData.publicKey, clientData.createdAt, clientData.deletedAt, mutedUntil, isBlocked)
}
