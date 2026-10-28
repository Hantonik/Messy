package hantonik.messy.server.core.data.repository;

import hantonik.messy.server.core.data.entity.client.ClientRelationEntity;
import hantonik.messy.server.core.data.key.ClientRelationCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRelationRepository extends JpaRepository<ClientRelationEntity, ClientRelationCompositeKey> {
    List<ClientRelationEntity> findAllByClientId(UUID clientId);
}
