package hantonik.messy.server.core.data.key;

import io.netty.util.internal.MacAddressUtil;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCompositeKey {
    private UUID internalId;
    private byte[] macAddress;

    @Override
    public String toString() {
        return "{internalId=" + this.internalId + ", macAddress=" + MacAddressUtil.formatAddress(this.macAddress) + "}";
    }
}
