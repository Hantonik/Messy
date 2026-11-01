package hantonik.messy.server.core.data.entity.message;

import hantonik.messy.server.core.data.entity.client.ClientEntity;
import hantonik.messy.server.core.data.key.MessageClientCompositeKey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message_secrets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageSecretEntity {
    @EmbeddedId
    private MessageClientCompositeKey key = new MessageClientCompositeKey();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("messageInternalId")
    @JoinColumn(name = "message_internal_id")
    private MessageEntity message;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clientInternalId")
    @JoinColumn(name = "client_internal_id")
    private ClientEntity client;

    private byte[] encryptedKey;
    private byte[] contentIv;

    @Deprecated
    public MessageSecretEntity(MessageEntity message, ClientEntity client, byte[] encryptedKey, byte[] contentIv) {
        this.message = message;
        this.client = client;
        this.encryptedKey = encryptedKey;
        this.contentIv = contentIv;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        var messageSecret = (MessageSecretEntity) obj;

        return this.key != null && this.key.equals(messageSecret.key);
    }

    @Override
    public int hashCode() {
        return this.key != null ? this.key.hashCode() : this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "MessageSecretEntity[key=" + this.key + "]";
    }
}
