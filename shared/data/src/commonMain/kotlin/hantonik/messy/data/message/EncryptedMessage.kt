package hantonik.messy.data.message

import hantonik.messy.data.file.FileMetadata
import kotlin.time.Instant
import kotlin.uuid.Uuid

class EncryptedMessage(val messageId: Uuid?, val index: Int,
                       val conversationId: Uuid, val senderId: Uuid,
                       val type: MessageType, val isSilent: Boolean,
                       val encryptedContent: ByteArray?, val namedSecrets: Map<Uuid, EncryptedMessageSecret>, val attachments: List<FileMetadata>,
                       val sentAt: Instant?, val namedDeliveredAt: Map<Uuid, Instant>?, val namedReadAt: Map<Uuid, Instant>, val updatedAt: Instant?, val deletedAt: Instant?) {
    constructor(index: Int, conversationId: Uuid, senderId: Uuid, type: MessageType, isSilent: Boolean, encryptedContent: ByteArray?, namedSecrets: Map<Uuid, EncryptedMessageSecret>, attachments: List<FileMetadata>) :
            this(null, index, conversationId, senderId, type, isSilent, encryptedContent, namedSecrets, attachments, null, mapOf(), mapOf(), null, null)
}