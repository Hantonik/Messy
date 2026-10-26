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
public class ConversationClientCompositeKey implements Serializable {
    private UUID conversationId;
    private UUID clientId;

    @Override
    public String toString() {
        return "{conversationId=" + this.conversationId + ", clientId=" + this.clientId + "}";
    }
}
