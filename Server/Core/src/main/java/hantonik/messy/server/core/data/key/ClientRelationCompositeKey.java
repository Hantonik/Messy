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
public class ClientRelationCompositeKey implements Serializable {
    private UUID clientInternalId;
    private UUID targetInternalId;

    @Override
    public String toString() {
        return "{clientInternalId=" + this.clientInternalId + ", targetInternalId=" + this.targetInternalId + "}";
    }
}
