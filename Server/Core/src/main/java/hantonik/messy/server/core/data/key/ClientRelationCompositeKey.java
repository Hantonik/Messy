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
public class ClientRelationCompositeKey implements Serializable {
    private UUID clientId;
    private UUID targetId;

    @Override
    public String toString() {
        return "{clientId=" + this.clientId + ", targetId=" + this.targetId + "}";
    }
}
