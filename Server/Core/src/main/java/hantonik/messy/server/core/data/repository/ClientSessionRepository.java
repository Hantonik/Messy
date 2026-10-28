package hantonik.messy.server.core.data.repository;

import hantonik.messy.server.core.data.entity.client.ClientSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface ClientSessionRepository extends JpaRepository<ClientSessionEntity, UUID> {
    Optional<ClientSessionEntity> findByTokenHash(byte[] tokenHash);

    Stream<ClientSessionEntity> streamAllByClientId(UUID clientId);

    Stream<ClientSessionEntity> streamAllByDeviceId(UUID deviceId);

    @Query("SELECT c FROM ClientSessionEntity c ORDER BY c.expiresAt ASC")
    Stream<ClientSessionEntity> streamAllFromMostExpired();
}
