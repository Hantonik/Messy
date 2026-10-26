package hantonik.messy.server.core.data.entity.conversation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "direct_conversations")
@PrimaryKeyJoinColumn(name = "conversation_id")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DirectConversationEntity extends ConversationEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "conversation_id", referencedColumnName = "conversation_id", insertable = false, updatable = false),
            @JoinColumn(name = "client_a_id", referencedColumnName = "client_id", insertable = false, updatable = false)
    })
    private ConversationParticipantEntity participantA;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "conversation_id", referencedColumnName = "conversation_id", insertable = false, updatable = false),
            @JoinColumn(name = "client_b_id", referencedColumnName = "client_id", insertable = false, updatable = false)
    })
    private ConversationParticipantEntity participantB;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        var directConversation = (DirectConversationEntity) obj;

        return this.getId() != null && this.getId().equals(directConversation.getId());
    }

    @Override
    public String toString() {
        return "DirectConversationEntity[conversationId=" + this.getId() + "]";
    }
}
