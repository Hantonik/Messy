package hantonik.messy.data.message

import hantonik.messy.data.file.FileMetadata
import hantonik.messy.data.message.content.EncryptedContent
import kotlin.time.Instant
import kotlin.uuid.Uuid

data class EncryptedMessage(val messageId: Uuid?, val index: Int,
                            val conversationId: Uuid, val senderId: Uuid,
                            val type: MessageType, val isSilent: Boolean,
                            val encryptedContent: EncryptedContent?, val namedEncryptedKeys: Map<Uuid, ByteArray>, val attachments: List<FileMetadata>,
                            val sentAt: Instant?, val deliveredAt: Instant?, val namedReadAt: Map<Uuid, Instant>, val updatedAt: Instant?, val deletedAt: Instant?) {
    constructor(index: Int, conversationId: Uuid, senderId: Uuid, type: MessageType, isSilent: Boolean, encryptedContent: EncryptedContent?, namedEncryptedKeys: Map<Uuid, ByteArray>, attachments: List<FileMetadata>) :
            this(null, index, conversationId, senderId, type, isSilent, encryptedContent, namedEncryptedKeys, attachments, null, null, mapOf(), null, null)
}