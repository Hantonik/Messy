package hantonik.messy.server.core.data.entity.client;

import hantonik.messy.server.core.data.entity.device.DeviceEntity;
import hantonik.messy.server.core.data.key.ClientSessionCompositeKey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Arrays;

@Entity
@Table(name = "client_sessions")
@IdClass(ClientSessionCompositeKey.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientSessionEntity {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_internal_id")
    private ClientEntity client;
    @Id
    @Column(updatable = false)
    private byte[] tokenHash;

    @Column(updatable = false)
    private byte[] encryptedPrivateKeyFragment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_internal_id", updatable = false)
    @Nullable
    private DeviceEntity device;

    @Generated(writable = true)
    @Column(updatable = false)
    private Instant createdAt;
    @Setter
    private Instant expiresAt;

    @Setter
    @Nullable
    private Instant lastUsedAt;
    @Setter
    @Nullable
    private Instant revokedAt;

    @Version
    @Getter(AccessLevel.PRIVATE)
    private Long version;

    public ClientSessionEntity(ClientEntity client, byte[] tokenHash, byte[] encryptedPrivateKeyFragment, @Nullable DeviceEntity device, Instant expiresAt) {
        this.client = client;
        this.tokenHash = tokenHash;
        this.encryptedPrivateKeyFragment = encryptedPrivateKeyFragment;
        this.device = device;
        this.expiresAt = expiresAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        var clientSession = (ClientSessionEntity) obj;

        return this.tokenHash != null && Arrays.equals(this.tokenHash, clientSession.tokenHash);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ClientSessionEntity[tokenHash=" + Arrays.toString(this.tokenHash) +
                ", createdAt=" + this.createdAt + ", expiresAt=" + this.expiresAt +
                ", lastUsedAt=" + this.lastUsedAt + ", revokedAt=" + this.revokedAt + "]";
    }
}
