package hantonik.messy.server.core.data.entity.mappers

import hantonik.messy.data.file.FileMetadata
import hantonik.messy.server.core.data.entity.file.FileEntity
import kotlin.time.toKotlinInstant
import kotlin.uuid.toKotlinUuid

fun FileEntity.toFileMetadata(): FileMetadata {
    if (this.id == null)
        throw IllegalStateException("Cannot access data before database read")

    return FileMetadata(this.id.toKotlinUuid(), this.uploadedBy?.id!!.toKotlinUuid(), this.originalName, this.mimeType!!, this.sizeBytes, this.sha256, this.createdAt?.toKotlinInstant())
}