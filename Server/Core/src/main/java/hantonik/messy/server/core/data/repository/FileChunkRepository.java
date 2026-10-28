package hantonik.messy.server.core.data.repository;

import hantonik.messy.server.core.data.entity.file.FileChunkEntity;
import hantonik.messy.server.core.data.key.FileChunkCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface FileChunkRepository extends JpaRepository<FileChunkEntity, FileChunkCompositeKey> {
    Stream<FileChunkEntity> streamAllByFileId(UUID fileId);
}
