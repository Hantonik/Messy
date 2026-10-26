package hantonik.messy.data.client

import dev.whyoleg.cryptography.algorithms.XDH
import hantonik.messy.data.file.FileMetadata
import kotlin.time.Instant
import kotlin.uuid.Uuid

open class ClientData(open val id: Uuid,
                      open val icon: FileMetadata?, open val encryptedFirstName: ByteArray, open val encryptedLastName: ByteArray,
                      open val publicKey: XDH.PublicKey,
                      open val createdAt: Instant, open val deletedAt: Instant?)
