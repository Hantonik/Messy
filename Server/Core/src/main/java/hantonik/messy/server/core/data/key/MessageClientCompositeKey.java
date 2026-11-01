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
public class MessageClientCompositeKey implements Serializable {
    private UUID messageInternalId;
    private UUID clientInternalId;

    @Override
    public String toString() {
        return "{messageInternalId=" + this.messageInternalId + ", clientInternalId=" + this.clientInternalId + "}";
    }
}
