package hantonik.messy.data.conversation

import hantonik.messy.data.file.FileMetadata
import kotlin.time.Instant
import kotlin.uuid.Uuid

class NamedGroupConversation(override val id: Uuid, val name: String, val icon: FileMetadata?, val description: String?,
                             val creatorId: Uuid?, val ownerId: Uuid?, override val conversationParticipants: List<ConversationParticipant>,
                             val encryptedKey: ByteArray, val keyNonce: ByteArray,
                             override val createdAt: Instant,
                             override val lastMessageIndex: Int) : Conversation {
    override val type = ConversationType.GROUP
}