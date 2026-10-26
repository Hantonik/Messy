package hantonik.messy.server.core.data.entity.file;

import com.google.common.collect.Lists;
import hantonik.messy.server.core.data.entity.client.ClientEntity;
import hantonik.messy.server.core.data.entity.message.MessageEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileEntity {
    @Id
    @Generated
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @Nullable
    private ClientEntity owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "message_id")
    )
    private MessageEntity message;

    private String originalName;
    private String mimeType;

    private Long sizeBytes;
    private byte[] sha256;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    private List<FileChunkEntity> fileChunks = Lists.newArrayList();

    @Generated(writable = true)
    private Instant createdAt;

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
        return "FileEntity[messageId=" + this.id +
                ", originalName=" + this.originalName + ", mimeType=" + this.mimeType +
                ", sizeBytes=" + this.sizeBytes + ", createdAt=" + this.createdAt + "]";
    }
}
