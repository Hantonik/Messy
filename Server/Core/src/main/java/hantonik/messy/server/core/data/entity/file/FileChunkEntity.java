package hantonik.messy.server.core.data.entity.file;

import hantonik.messy.server.core.data.key.FileChunkCompositeKey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "file_chunks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileChunkEntity {
    @EmbeddedId
    private FileChunkCompositeKey key = new FileChunkCompositeKey();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("fileInternalId")
    @JoinColumn(name = "file_internal_id")
    private FileEntity file;

    @Column(updatable = false)
    private byte[] encryptedChunk;
    @Column(updatable = false)
    private byte[] chunkNonce;

    public FileChunkEntity(FileEntity file, int chunkIndex, byte[] encryptedChunk, byte[] chunkNonce) {
        this.key = new FileChunkCompositeKey(null, chunkIndex);
        this.file = file;
        this.encryptedChunk = encryptedChunk;
        this.chunkNonce = chunkNonce;
    }

    public int getChunkIndex() {
        if (this.key == null)
            throw new IllegalStateException("Cannot gather chunkIndex value before database read");

        return this.key.getChunkIndex();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        var fileChunk = (FileChunkEntity) obj;

        return this.key != null && this.key.equals(fileChunk.key);
    }

    @Override
    public int hashCode() {
        return this.key != null ? this.key.hashCode() : this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "FileChunkEntity[key=" + this.key + "]";
    }
}
