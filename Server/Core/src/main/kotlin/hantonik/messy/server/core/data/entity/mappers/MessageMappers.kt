package hantonik.messy.server.core.data.entity.mappers

import com.google.common.collect.Maps
import hantonik.messy.data.message.EncryptedMessage
import hantonik.messy.data.message.EncryptedMessageSecret
import hantonik.messy.server.core.data.entity.file.FileEntity
import hantonik.messy.server.core.data.entity.message.MessageEntity
import kotlin.time.Instant
import kotlin.time.toKotlinInstant
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

fun MessageEntity.toEncryptedMessage(): EncryptedMessage {
    if (this.id == null)
        throw IllegalStateException("Cannot access data before database read")

    val namedEncryptedKeys: MutableMap<Uuid, EncryptedMessageSecret> = Maps.newHashMap()

    for (secret in this.messageSecrets)
        namedEncryptedKeys[secret.client.id.toKotlinUuid()] = EncryptedMessageSecret(secret.encryptedKey, secret.contentIv)

    val namedDeliveredAt: MutableMap<Uuid, Instant> = Maps.newHashMap()
    val namedReadAt: MutableMap<Uuid, Instant> = Maps.newHashMap()

    for (receipts in this.messageReceipts) {
        receipts.deliveredAt?.let { namedDeliveredAt[receipts.client.id.toKotlinUuid()] = it.toKotlinInstant() }
        receipts.readAt?.let { namedReadAt[receipts.client.id.toKotlinUuid()] = it.toKotlinInstant() }
    }

    return EncryptedMessage(this.id.toKotlinUuid(), this.messageIndex,
        this.conversation.id.toKotlinUuid(), this.sender.id.toKotlinUuid(),
        this.messageType, this.isSilent,
        this.encryptedContent, namedEncryptedKeys, this.fileAttachments.stream().map(FileEntity::toFileMetadata).toList(),
        this.sentAt?.toKotlinInstant(), namedDeliveredAt, namedReadAt, this.updatedAt?.toKotlinInstant(), this.deletedAt?.toKotlinInstant())
}