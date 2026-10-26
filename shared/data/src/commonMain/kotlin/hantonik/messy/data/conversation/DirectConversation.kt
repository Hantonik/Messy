package hantonik.messy.data.conversation

import kotlin.time.Instant
import kotlin.uuid.Uuid

data class DirectConversation(override val id: Uuid,
                              val participantA: ConversationParticipant, val participantB: ConversationParticipant,
                              override val createdAt: Instant,
                              override val lastMessageIndex: Int) : Conversation {
    override val type = ConversationType.DIRECT
    override val conversationParticipants = listOf(this.participantA, this.participantB)
}