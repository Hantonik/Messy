package hantonik.messy.data.conversation

import kotlin.time.Instant
import kotlin.uuid.Uuid

data class ConversationParticipant(val conversationId: Uuid, val clientId: Uuid,
                                   val nickname: String?,
                                   val mutedUntil: Instant?,
                                   val readOnly: Boolean, val admin: Boolean,
                                   val joinedAt: Instant, val leftAt: Instant?)