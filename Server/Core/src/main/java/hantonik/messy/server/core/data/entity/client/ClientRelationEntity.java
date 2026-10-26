package hantonik.messy.server.core.data.entity.client;

import hantonik.messy.server.core.data.key.ClientRelationCompositeKey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

@Entity
@Table(name = "client_relations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientRelationEntity {
    @EmbeddedId
    private ClientRelationCompositeKey key = new ClientRelationCompositeKey();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clientId")
    @JoinColumn(name = "client_id")
    private ClientEntity client;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("targetId")
    @JoinColumn(name = "target_id")
    private ClientEntity target;

    @Setter
    @Nullable
    private Instant mutedUntil;
    @Setter
    @Generated(writable = true)
    private Boolean isBlocked;

    @Getter(AccessLevel.PRIVATE)
    @Version
    private Long version;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        var clientRelation = (ClientRelationEntity) obj;

        return this.key != null && this.key.equals(clientRelation.key);
    }

    @Override
    public int hashCode() {
        return this.key != null ? this.key.hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ClientRelationEntity[key=" + this.key +
                ", mutedUntil=" + this.mutedUntil + ", isBlocked=" + this.isBlocked + "]";
    }
}
