package hantonik.messy.server.core.data.entity.conversation;

import hantonik.messy.data.conversation.ConversationType;
import hantonik.messy.server.core.data.entity.client.ClientEntity;
import hantonik.messy.server.core.data.entity.file.FileEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Entity
@Table(name = "group_conversations")
@PrimaryKeyJoinColumn(name = "conversation_internal_id")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupConversationEntity extends ConversationEntity {
    private String name;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icon_internal_id")
    @Nullable
    private FileEntity icon;
    @Nullable
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_internal", updatable = false)
    @Nullable
    private ClientEntity createdBy;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_internal_id")
    @Nullable
    private ClientEntity owner;

    @Version
    @Getter(AccessLevel.PRIVATE)
    private Long version;

    public GroupConversationEntity(String name, @Nullable FileEntity icon, @Nullable String description, @NonNull ClientEntity createdBy) {
        super(ConversationType.GROUP);

        this.name = name;
        this.icon = icon;
        this.description = description;
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        var groupConversation = (GroupConversationEntity) obj;

        return this.getId() != null && this.getId().equals(groupConversation.getId());
    }

    @Override
    public String toString() {
        return "GroupConversationEntity[conversationId=" + this.getId() + ", conversationInternalId=" + this.getInternalId() +
                ", name=" + this.name + ", description=" + this.description + "]";
    }
}
