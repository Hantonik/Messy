package hantonik.messy.server.core.data.entity.mappers

import hantonik.messy.data.client.*
import hantonik.messy.server.core.data.entity.client.ClientEntity
import hantonik.messy.server.core.data.entity.client.ClientRelationEntity
import hantonik.messy.server.core.data.entity.client.ClientSessionEntity
import kotlin.time.toKotlinInstant
import kotlin.uuid.toKotlinUuid

fun ClientEntity.toClientData(): ClientData {
    if (this.id == null)
        throw IllegalStateException("Cannot access data before database read")

    return ClientData(this.id.toKotlinUuid(), this.icon?.toFileMetadata(), this.encryptedFirstName, this.encryptedLastName, this.publicKey, this.createdAt.toKotlinInstant(), this.deletedAt?.toKotlinInstant())
}

fun ClientEntity.toClientProfile(): ClientProfile {
    if (this.id == null)
        throw IllegalStateException("Cannot access data before database read")

    return ClientProfile(this.id.toKotlinUuid(), this.icon?.toFileMetadata(), this.encryptedFirstName, this.encryptedLastName, this.email, this.phone, this.publicKey, this.encryptedPrivateKey, this.privateKeyIv, this.encryptedDataKey, this.dataKeyIv, this.createdAt.toKotlinInstant(), this.deletedAt?.toKotlinInstant())
}

fun ClientEntity.getClientActivity(): ClientActivity {
    if (this.id == null)
        throw IllegalStateException("Cannot access data before database read")

    return ClientActivity(this.id.toKotlinUuid(), this.lastSeenAt?.toKotlinInstant())
}

fun ClientRelationEntity.toRelatedClientData(): RelatedClientData {
    if (this.key == null)
        throw IllegalStateException("Cannot access data before database read")

    return RelatedClientData(this.client.id.toKotlinUuid(), this.client.icon?.toFileMetadata(), this.client.encryptedFirstName, this.client.encryptedLastName, this.client.publicKey, this.client.createdAt.toKotlinInstant(), this.client.deletedAt?.toKotlinInstant(), this.mutedUntil?.toKotlinInstant(), this.isBlocked)
}

fun ClientSessionEntity.toClientSession(): ClientSession {
    if (this.tokenHash == null)
        throw IllegalStateException("Cannot access data before database read")

    return ClientSession(this.client.id.toKotlinUuid(), this.tokenHash, this.encryptedPrivateKeyFragment, this.device?.toDeviceInfo(), this.createdAt.toKotlinInstant(), this.expiresAt.toKotlinInstant(), this.lastUsedAt?.toKotlinInstant())
}