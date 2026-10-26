package hantonik.messy.server.core.data.key;

import io.netty.util.internal.MacAddressUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class DeviceCompositeKey {
    private UUID id;
    private byte[] macAddress;

    @Override
    public String toString() {
        return "{messageId=" + this.id + ", macAddress=" + MacAddressUtil.formatAddress(this.macAddress) + "}";
    }
}
