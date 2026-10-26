package hantonik.messy.server.core.data.entity.client;

import hantonik.messy.server.core.data.entity.device.DeviceEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clientSessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientSessionEntity {
    @Id
    @Generated
    private UUID id;

    private UUID clientId;
    @Nullable
    private UUID deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientEntity client;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    @Nullable
    private DeviceEntity device;

    private byte[] tokenHash;

    @Generated(writable = true)
    private Instant createdAt;
    @Setter
    private Instant expiresAt;

    @Setter
    @Nullable
    private Instant lastUsedAt;
    @Setter
    @Nullable
    private Instant revokedAt;

    public ClientSessionEntity(UUID clientId, @Nullable UUID deviceId, byte[] tokenHash, Instant expiresAt) {
        this.clientId = clientId;
        this.deviceId = deviceId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        var clientSession = (ClientSessionEntity) obj;

        return this.id != null && this.id.equals(clientSession.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ClientSessionEntity[messageId=" + this.id +
                ", createdAt=" + this.createdAt + ", expiresAt=" + this.expiresAt +
                ", lastUsedAt=" + this.lastUsedAt + ", revokedAt=" + this.revokedAt + "]";
    }
}
