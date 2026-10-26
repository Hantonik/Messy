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
public class MessageClientCompositeKey implements Serializable {
    private UUID messageId;
    private UUID clientId;

    @Override
    public String toString() {
        return "{messageId=" + this.messageId + ", clientId=" + this.clientId + "}";
    }
}
