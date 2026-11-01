package hantonik.messy.server.core.data.entity.file;

import com.google.common.collect.Lists;
import hantonik.messy.server.core.data.entity.client.ClientEntity;
import hantonik.messy.server.core.data.entity.message.MessageEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileEntity {
    @Generated
    @Column(updatable = false)
    private UUID id;
    @Id
    @Generated
    private UUID internalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_internal", updatable = false)
    @Nullable
    private ClientEntity uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "file_internal_id"),
            inverseJoinColumns = @JoinColumn(name = "message_internal_id")
    )
    @Nullable
    private MessageEntity message;

    @Column(updatable = false)
    private String originalName;
    @Column(updatable = false)
    private String mimeType;

    @Column(updatable = false)
    private Long sizeBytes;
    @Column(updatable = false)
    private byte[] sha256;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    private List<FileChunkEntity> fileChunks = Lists.newArrayList();

    @Generated(writable = true)
    @Column(updatable = false)
    private Instant createdAt;

    public FileEntity(@NonNull ClientEntity uploadedBy, String originalName, String mimeType, long sizeBytes, byte[] sha256) {
        this.uploadedBy = uploadedBy;
        this.originalName = originalName;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.sha256 = sha256;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        var file = (FileEntity) obj;

        return this.id != null && this.id.equals(file.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "FileEntity[internalId=" + this.id + ", internalId=" + this.internalId +
                ", originalName=" + this.originalName + ", mimeType=" + this.mimeType +
                ", sizeBytes=" + this.sizeBytes + ", createdAt=" + this.createdAt + "]";
    }
}
