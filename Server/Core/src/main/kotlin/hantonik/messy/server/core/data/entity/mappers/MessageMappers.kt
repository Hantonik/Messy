package hantonik.messy.server.core.data.entity.mappers

import com.google.common.collect.Maps
import hantonik.messy.data.message.EncryptedMessage
import hantonik.messy.data.message.content.EncryptedContent
import hantonik.messy.server.core.data.entity.file.FileEntity
import hantonik.messy.server.core.data.entity.message.MessageEntity
import kotlin.time.Instant
import kotlin.time.toKotlinInstant
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid


fun MessageEntity.toEncryptedContent(): EncryptedContent? {
    if (this.id == null)
        throw IllegalStateException("Cannot access data before database read")

    return if (this.encryptedContent == null)
        null
    else
        EncryptedContent(this.encryptedContent, this.contentNonce)
}

fun MessageEntity.toEncryptedMessage(): EncryptedMessage {
    if (this.id == null)
        throw IllegalStateException("Cannot access data before database read")

    val namedEncryptedKeys: MutableMap<Uuid, ByteArray> = Maps.newHashMap()

    for (secret in this.messageSecrets)
        namedEncryptedKeys[secret.client.id.toKotlinUuid()] = secret.encryptedKey

    val namedReadAt: MutableMap<Uuid, Instant> = Maps.newHashMap()

    for (receipts in this.messageReceipts)
        receipts.readAt?.let { namedReadAt[receipts.client.id.toKotlinUuid()] = it.toKotlinInstant() }

    return EncryptedMessage(this.id.toKotlinUuid(), this.messageIndex,
        this.conversation.id.toKotlinUuid(), this.sender.id.toKotlinUuid(),
        this.messageType, this.isSilent,
        this.toEncryptedContent(), namedEncryptedKeys, this.fileAttachments.stream().map(FileEntity::toFileMetadata).toList(),
        this.sentAt?.toKotlinInstant(), this.deliveredAt?.toKotlinInstant(), namedReadAt, this.updatedAt?.toKotlinInstant(), this.deletedAt?.toKotlinInstant())
}