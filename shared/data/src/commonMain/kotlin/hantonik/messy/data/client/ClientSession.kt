package hantonik.messy.data.client

import hantonik.messy.data.device.DeviceInfo
import kotlin.time.Instant
import kotlin.uuid.Uuid

class ClientSession(val id: Uuid, val clientId: Uuid,
                    val device: DeviceInfo?, val tokenHash: ByteArray,
                    val createdAt: Instant, val expiresAt: Instant,
                    val lastUsedAt: Instant?)