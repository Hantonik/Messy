package hantonik.messy.server.core.data.repository;

import hantonik.messy.server.core.data.entity.file.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, UUID> {
    Optional<FileEntity> findBySha256(byte[] sha256);

    List<FileEntity> findAllByOwnerId(UUID ownerId);

    List<FileEntity> findAllByMessageId(UUID messageId);
}
