package hantonik.messy.server.core.data.repository;

import hantonik.messy.server.core.data.entity.client.ClientEntity;
import hantonik.messy.server.core.data.entity.file.FileEntity;
import hantonik.messy.server.core.data.entity.message.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, UUID> {
    Optional<FileEntity> findBySizeBytesAndSha256(long sizeBytes, byte[] sha256);

    @Query("SELECT f FROM FileEntity f WHERE f.uploadedBy = :uploadedBy")
    Stream<FileEntity> streamAllByCreator(@Param("createdBy") ClientEntity uploadedBy);

    List<FileEntity> findAllByMessage(MessageEntity message);
}
