package hantonik.messy.server.core.data.entity.conversation;

import com.google.common.collect.Lists;
import hantonik.messy.data.conversation.ConversationType;
import hantonik.messy.server.core.data.entity.message.MessageEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Generated;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Always refresh after inserting a new message.
 */
@Entity
@Table(name = "conversations")
@DynamicUpdate
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConversationEntity {
    @Generated
    @Column(updatable = false)
    private UUID id;
    @Id
    @Generated
    private UUID internalId;

    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private ConversationType type;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private List<ConversationParticipantEntity> conversationParticipants = Lists.newArrayList();
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private List<MessageEntity> messages = Lists.newArrayList();

    @Column(updatable = false)
    @Generated(writable = true)
    private Instant createdAt;

    @Generated
    @Column(updatable = false)
    private Integer lastMessageIndex;

    @Version
    @Getter(AccessLevel.PRIVATE)
    private Long version;

    protected ConversationEntity(ConversationType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        var conversation = (ConversationEntity) obj;

        return this.id != null && this.id.equals(conversation.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ConversationEntity[internalId=" + this.id + ", internalId=" + this.internalId +
                ", type=" + this.type +
                ", createdAt=" + this.createdAt + "]";
    }
}
