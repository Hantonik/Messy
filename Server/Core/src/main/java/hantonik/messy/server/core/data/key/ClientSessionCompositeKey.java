package hantonik.messy.server.core.data.key;

import lombok.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ClientSessionCompositeKey implements Serializable {
    private UUID clientInternalId;
    private byte[] tokenHash;

    @Override
    public String toString() {
        return "{clientInternalId=" + this.clientInternalId + ", tokenHash=" + Arrays.toString(this.tokenHash) + "}";
    }
}
