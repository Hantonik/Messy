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

import java.util.UUID;

@Getter
@Entity
@Table(name = "devices")
@IdClass(DeviceCompositeKey.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceEntity {
    @Id
    @Generated
    @Column(name = "id")
    private UUID id;
    @Id
    @Column(name = "mac_address")
    @ColumnTransformer(read = "mac_address::bytea", write = "?::macaddr8")
    private byte[] macAddress;

    private String name;
    private String os;
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    public DeviceEntity(DeviceInfo deviceInfo) {
        this.macAddress = deviceInfo.getMacAddress();
        this.name = deviceInfo.getName();
        this.os = deviceInfo.getOs();
        this.deviceType = deviceInfo.getDeviceType();
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
        return "DeviceEntity[messageId=" + this.id +
                ", name=" + this.name + ", os=" + this.os + ", deviceType=" + this.deviceType + "]";
    }
}
