package hantonik.messy.data.conversation

import kotlin.time.Instant
import kotlin.uuid.Uuid

interface Conversation {
    val id: Uuid
    val type: ConversationType

    val conversationParticipants: List<ConversationParticipant>

    val createdAt: Instant

    val lastMessageIndex: Int
}