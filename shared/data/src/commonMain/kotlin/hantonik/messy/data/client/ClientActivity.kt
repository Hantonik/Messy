package hantonik.messy.data.client

import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.uuid.Uuid

data class ClientActivity(val clientId: Uuid, var lastSeenAt: Instant?, var activeSince: Instant?) {
    constructor(clientId: Uuid, lastSeenAt: Instant?) : this(clientId, lastSeenAt, null)

    // TODO: Cycle
    // if (!this.lastSeenAt.isBefore(Instant.now().minus(FIVE_MINUTES)))
    //     this.activeSince = this.lastSeenAt;

    fun getStatus(): ClientStatus {
        this.activeSince?.let {
            if (this.lastSeenAt == null)
                return ClientStatus.INACTIVE

            if (this.lastSeenAt!! > it.plus(15.minutes))
                return ClientStatus.IDLE

            if (this.lastSeenAt!! < Clock.System.now().minus(5.minutes))
                this.activeSince = null
            else
                return ClientStatus.ACTIVE
        }

        return ClientStatus.INACTIVE
    }
}
