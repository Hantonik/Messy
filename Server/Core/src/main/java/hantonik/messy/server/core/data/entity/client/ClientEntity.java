package hantonik.messy.server.core.data.entity.client;

import com.google.common.collect.Lists;
import dev.whyoleg.cryptography.algorithms.XDH;
import hantonik.messy.encryption.Crypt;
import hantonik.messy.encryption.exception.CryptException;
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

@Getter
@Entity
@Table(name = "clients")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientEntity {
    @Generated
    @Column(updatable = false)
    private UUID id;
    @Id
    @Generated
    private UUID internalId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icon_internal_id")
    @Nullable
    private FileEntity icon;
    private byte[] encryptedFirstName;
    private byte[] encryptedLastName;

    @Setter
    private String email;
    @Setter
    @Nullable
    private String phone;

    private byte[] loginHash;
    private byte[] passwordSalt;

    private byte[] publicKey;

    private byte[] encryptedPrivateKey;
    private byte[] privateKeyIv;

    private byte[] encryptedDataKey;
    private byte[] dataKeyIv;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<ClientSessionEntity> sessions = Lists.newArrayList();

    @Generated(writable = true)
    @Column(updatable = false)
    private Instant createdAt;
    @Setter
    @Nullable
    private Instant deletedAt;
    @Setter
    @Nullable
    private Instant lastSeenAt;

    @Version
    @Getter(AccessLevel.PRIVATE)
    private Long version;

    public ClientEntity(byte[] encryptedFirstName, byte[] encryptedLastName, String email, @Nullable String phone, byte[] loginHash, byte[] passwordSalt, XDH.PublicKey publicKey, byte[] encryptedPrivateKey, byte[] privateKeyIv, byte[] encryptedDataKey, byte[] dataKeyIv) {
        this.encryptedFirstName = encryptedFirstName;
        this.encryptedLastName = encryptedLastName;

        this.email = email;
        this.phone = phone;

        this.loginHash = loginHash;
        this.passwordSalt = passwordSalt;

        this.publicKey = Crypt.encodeXDHPublicKey(publicKey);

        this.encryptedPrivateKey = encryptedPrivateKey;
        this.privateKeyIv = privateKeyIv;

        this.encryptedDataKey = encryptedDataKey;
        this.dataKeyIv = dataKeyIv;
    }

    public void changePassword(byte[] loginHash, byte[] passwordSalt) {
        this.loginHash = loginHash;
        this.passwordSalt = passwordSalt;
    }

    public void changeKeyPair(byte[] publicKey, byte[] encryptedPrivateKey, byte[] privateKeyIv) {
        this.publicKey = publicKey;
        this.encryptedPrivateKey = encryptedPrivateKey;
        this.privateKeyIv = privateKeyIv;
    }

    public void updatePrivateKey(byte[] encryptedPrivateKey, byte[] privateKeyIv) {
        this.encryptedPrivateKey = encryptedPrivateKey;
        this.privateKeyIv = privateKeyIv;
    }

    public void updateDataKey(byte[] encryptedDataKey, byte[] dataKeyIv) {
        this.encryptedDataKey = encryptedDataKey;
        this.dataKeyIv = dataKeyIv;
    }

    public XDH.PublicKey getPublicKey() {
        try {
            return Crypt.decodeXDHPublicKeyX448(this.publicKey);
        } catch (CryptException e) {
            throw new IllegalStateException("Malformed public key bytes", e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        var client = (ClientEntity) obj;

        return this.id != null && this.id.equals(client.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ClientEntity[internalId=" + this.id + ", internalId=" + this.internalId +
                ", email=" + this.email +
                ", createdAt=" + this.createdAt + ", lastSeenAt=" + this.lastSeenAt + "]";
    }
}