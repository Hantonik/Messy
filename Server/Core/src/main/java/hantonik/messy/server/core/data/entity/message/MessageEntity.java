package hantonik.messy.server.core.data.entity.message;

import com.google.common.collect.Lists;
import hantonik.messy.data.message.MessageType;
import hantonik.messy.server.core.data.entity.client.ClientEntity;
import hantonik.messy.server.core.data.entity.conversation.ConversationEntity;
import hantonik.messy.server.core.data.entity.conversation.ConversationParticipantEntity;
import hantonik.messy.server.core.data.entity.file.FileEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("NullableProblems")
@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageEntity {
    @Generated
    @Column(updatable = false)
    private UUID id;
    @Id
    @Generated
    private UUID internalId;

    @Generated
    @Column(updatable = false)
    private Integer messageIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_internal_id", updatable = false)
    private ConversationEntity conversation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_internal_id", updatable = false)
    private ClientEntity sender;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "conversation_internal_id", referencedColumnName = "conversation_internal_id", insertable = false, updatable = false),
            @JoinColumn(name = "sender_internal_id", referencedColumnName = "client_internal_id", insertable = false, updatable = false)
    })
    private ConversationParticipantEntity participant;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;
    @Setter
    @Generated(writable = true)
    private Boolean isSilent;

    @Nullable
    private byte[] encryptedContent;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<MessageSecretEntity> messageSecrets = Lists.newArrayList();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_internal_id"),
            inverseJoinColumns = @JoinColumn(name = "file_internal_id")
    )
    private List<FileEntity> fileAttachments = Lists.newArrayList();

    @Generated(writable = true)
    @Column(updatable = false)
    private Instant sentAt;

    @Setter
    @Generated(writable = true)
    private Instant updatedAt;
    @Setter
    @Nullable
    private Instant deletedAt;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<MessageReceiptEntity> messageReceipts = Lists.newArrayList();

    @Version
    @Getter(AccessLevel.PRIVATE)
    private Long version;

    public MessageEntity(ConversationEntity conversation, ClientEntity sender, MessageType messageType, boolean isSilent, @Nullable byte[] encryptedContent) {
        this.conversation = conversation;
        this.sender = sender;
        this.messageType = messageType;
        this.isSilent = isSilent;
        this.encryptedContent = encryptedContent;
    }

    public boolean hasFileAttached() {
        if (this.id == null)
            throw new IllegalStateException("Cannot gather file attachments before database read");

        return !this.fileAttachments.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        var message = (MessageEntity) obj;

        return this.id != null && this.id.equals(message.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "MessageEntity[internalId=" + this.id + ", internalId=" + this.internalId +
                ", messageIndex=" + this.messageIndex +
                ", messageType=" + this.messageType + ", isSilent=" + this.isSilent +
                ", sentAt=" + this.sentAt + ", updatedAt=" + this.updatedAt + ", deletedAt=" + this.deletedAt + "]";
    }
}
