package hantonik.messy.server.core.data.entity.message;

import hantonik.messy.server.core.data.entity.client.ClientEntity;
import hantonik.messy.server.core.data.key.MessageClientCompositeKey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

@Entity
@Table(name = "message_receipts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageReceiptEntity {
    @EmbeddedId
    private MessageClientCompositeKey key = new MessageClientCompositeKey();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("messageId")
    @JoinColumn(name = "message_id")
    private MessageEntity message;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clientId")
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @Setter
    @Nullable
    private Instant readAt;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        var messageReceipt = (MessageReceiptEntity) obj;

        return this.key != null && this.key.equals(messageReceipt.key);
    }

    @Override
    public int hashCode() {
        return this.key != null ? this.key.hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "MessageReceiptEntity[key=" + this.key +
                ", namedReadAt=" + this.readAt + "]";
    }
}
