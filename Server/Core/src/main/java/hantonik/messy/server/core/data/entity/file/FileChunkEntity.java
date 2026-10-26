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
    @MapsId("fileId")
    @JoinColumn(name = "file_id")
    private FileEntity file;

    private byte[] encryptedChunk;
    private byte[] chunkNonce;

    public Integer getChunkIndex() {
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
        return this.key != null ? this.key.hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "FileChunkEntity[key=" + this.key + "]";
    }
}
