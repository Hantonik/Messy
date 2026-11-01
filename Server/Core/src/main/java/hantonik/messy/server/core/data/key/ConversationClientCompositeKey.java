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
public class ConversationClientCompositeKey implements Serializable {
    private UUID conversationInternalId;
    private UUID clientInternalId;

    @Override
    public String toString() {
        return "{conversationInternalId=" + this.conversationInternalId + ", clientInternalId=" + this.clientInternalId + "}";
    }
}
