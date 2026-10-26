package hantonik.messy.server.core.data.entity.conversation;

import hantonik.messy.server.core.data.entity.client.ClientEntity;
import hantonik.messy.server.core.data.entity.file.FileEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Entity
@Table(name = "group_conversations")
@PrimaryKeyJoinColumn(name = "conversation_id")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupConversationEntity extends ConversationEntity {
    private String name;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icon_id")
    @Nullable
    private FileEntity icon;
    @Nullable
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @Nullable
    private ClientEntity creator;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @Nullable
    private ClientEntity owner;

    @Getter(AccessLevel.PRIVATE)
    @Version
    private Long version;

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
        return "GroupConversationEntity[conversationId=" + this.getId() +
                ", name=" + this.name + ", description=" + this.description + "]";
    }
}
