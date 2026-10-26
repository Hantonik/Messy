package hantonik.messy.data.file

import kotlin.time.Instant
import kotlin.uuid.Uuid

class FileMetadata(val fileId: Uuid?, val ownerId: Uuid,
                   val originalName: String, val mimeType: String,
                   val sizeBytes: Long, val sha256: ByteArray,
                   val createdAt: Instant?) {
    constructor(ownerId: Uuid, originalName: String, mimeType: String, sizeBytes: Long, sha256: ByteArray) :
            this(null, ownerId, originalName, mimeType, sizeBytes, sha256, null)
}
