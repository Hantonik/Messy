package hantonik.messy.server.core.data.entity.conversation;

import hantonik.messy.data.conversation.ConversationType;
import hantonik.messy.server.core.data.entity.client.ClientEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "direct_conversations")
@PrimaryKeyJoinColumn(name = "conversation_internal_id")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DirectConversationEntity extends ConversationEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_a_internal_id", updatable = false)
    private ClientEntity clientA;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_b_internal_id", updatable = false)
    private ClientEntity clientB;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "conversation_internal_id", referencedColumnName = "conversation_internal_id", insertable = false, updatable = false),
            @JoinColumn(name = "client_a_internal_id", referencedColumnName = "client_internal_id", insertable = false, updatable = false)
    })
    private ConversationParticipantEntity participantA;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "conversation_internal_id", referencedColumnName = "conversation_internal_id", insertable = false, updatable = false),
            @JoinColumn(name = "client_b_internal_id", referencedColumnName = "client_internal_id", insertable = false, updatable = false)
    })
    private ConversationParticipantEntity participantB;

    public DirectConversationEntity(ClientEntity clientA, ClientEntity clientB) {
        super(ConversationType.DIRECT);

        this.clientA = clientA;
        this.clientB = clientB;
    }

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
        return "DirectConversationEntity[conversationId=" + this.getId() + ", conversationInternalId=" + this.getInternalId() + "]";
    }
}
