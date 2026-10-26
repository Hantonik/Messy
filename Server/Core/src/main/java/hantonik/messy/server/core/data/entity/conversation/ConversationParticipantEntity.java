package hantonik.messy.server.core.data.entity.conversation;

import hantonik.messy.server.core.data.entity.client.ClientEntity;
import hantonik.messy.server.core.data.key.ConversationClientCompositeKey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

@Entity
@Table(name = "conversation_participants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConversationParticipantEntity {
    @EmbeddedId
    private ConversationClientCompositeKey key = new ConversationClientCompositeKey();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("conversationId")
    @JoinColumn(name = "conversation_id")
    private ConversationEntity conversation;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clientId")
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @Setter
    @Nullable
    private String nickname;
    @Setter
    @Nullable
    private Instant mutedUntil;
    @Setter
    @Generated(writable = true)
    private Boolean readOnly;
    @Setter
    @Generated(writable = true)
    private Boolean isAdmin;

    @Generated(writable = true)
    private Instant joinedAt;
    @Setter
    @Nullable
    private Instant leftAt;

    @Getter(AccessLevel.PRIVATE)
    @Version
    private Long version;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        var conversationParticipant = (ConversationParticipantEntity) obj;

        return this.key != null && this.key.equals(conversationParticipant.key);
    }

    @Override
    public int hashCode() {
        return this.key != null ? this.key.hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ConversationParticipantEntity[key=" + this.key +
                ", joinedAt=" + this.joinedAt + ", leftAt=" + this.leftAt + "]";
    }
}
