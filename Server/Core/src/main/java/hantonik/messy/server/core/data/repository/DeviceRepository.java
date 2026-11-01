package hantonik.messy.server.core.data.repository;

import hantonik.messy.server.core.data.entity.device.DeviceEntity;
import hantonik.messy.server.core.data.key.DeviceCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, DeviceCompositeKey> {
    Optional<DeviceEntity> findById(UUID id);

    Optional<DeviceEntity> findByMacAddress(byte[] macAddress);
}
