package hantonik.messy.server.core.data.entity.conversation;

import hantonik.messy.server.core.data.entity.client.ClientEntity;
import hantonik.messy.server.core.data.key.ConversationClientCompositeKey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_conversation_secrets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupConversationSecretEntity {
    @EmbeddedId
    private ConversationClientCompositeKey key = new ConversationClientCompositeKey();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("conversationInternalId")
    @JoinColumn(name = "conversation_internal_id")
    private ConversationEntity conversation;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clientInternalId")
    @JoinColumn(name = "client_internal_id")
    private ClientEntity client;

    private byte[] encryptedKey;
    private byte[] keyNonce;

    public GroupConversationSecretEntity(ConversationEntity conversation, ClientEntity client, byte[] encryptedKey, byte[] keyNonce) {
        this.conversation = conversation;
        this.client = client;
        this.encryptedKey = encryptedKey;
        this.keyNonce = keyNonce;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        var groupConversationSecret = (GroupConversationSecretEntity) obj;

        return this.key != null && this.key.equals(groupConversationSecret.key);
    }

    @Override
    public int hashCode() {
        return this.key != null ? this.key.hashCode() : this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "GroupConversationSecretEntity[key=" + this.key + "]";
    }
}
