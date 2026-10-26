package hantonik.messy.server.core.data.key;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class FileChunkCompositeKey implements Serializable {
    private UUID fileId;
    private Integer chunkIndex;

    @Override
    public String toString() {
        return "{fileId=" + this.fileId + ", chunkIndex=" + this.chunkIndex + "}";
    }
}
