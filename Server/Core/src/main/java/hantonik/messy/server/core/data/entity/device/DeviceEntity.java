package hantonik.messy.server.core.data.entity.device;

import hantonik.messy.data.device.DeviceInfo;
import hantonik.messy.data.device.DeviceType;
import hantonik.messy.server.core.data.key.DeviceCompositeKey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Generated;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@Entity
@Table(name = "devices")
@IdClass(DeviceCompositeKey.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceEntity {
    @Generated
    @Column(updatable = false)
    private UUID id;

    @Id
    @Generated
    private UUID internalId;
    @Id
    @Column(name = "mac_address")
    @ColumnTransformer(read = "mac_address::bytea", write = "?::macaddr8")
    private byte[] macAddress;

    @Nullable
    private String name;
    @Nullable
    private String os;

    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    public DeviceEntity(DeviceInfo deviceInfo) {
        this(deviceInfo.getMacAddress(), deviceInfo.getName(), deviceInfo.getOs(), deviceInfo.getDeviceType());
    }

    public DeviceEntity(byte[] macAddress, @Nullable String name, @Nullable String os, DeviceType deviceType) {
        this.macAddress = macAddress;
        this.name = name;
        this.os = os;
        this.deviceType = deviceType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        var device = (DeviceEntity) obj;

        return this.id != null && this.id.equals(device.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return "DeviceEntity[internalId=" + this.id + ", internalId=" + this.internalId +
                ", name=" + this.name + ", os=" + this.os +
                ", deviceType=" + this.deviceType + "]";
    }
}
