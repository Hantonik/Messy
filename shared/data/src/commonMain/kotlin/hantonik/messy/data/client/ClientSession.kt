package hantonik.messy.data.client

import hantonik.messy.data.device.DeviceInfo
import kotlin.time.Instant
import kotlin.uuid.Uuid

class ClientSession(val clientId: Uuid, val tokenHash: ByteArray,
                    val encryptedPrivateKeyFragment: ByteArray,
                    val device: DeviceInfo?,
                    val createdAt: Instant, val expiresAt: Instant,
                    val lastUsedAt: Instant?)