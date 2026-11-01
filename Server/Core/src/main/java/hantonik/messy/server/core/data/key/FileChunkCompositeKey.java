package hantonik.messy.server.core.data.key;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class FileChunkCompositeKey implements Serializable {
    private UUID fileInternalId;
    private Integer chunkIndex;

    @Override
    public String toString() {
        return "{fileInternalId=" + this.fileInternalId + ", chunkIndex=" + this.chunkIndex + "}";
    }
}
