package hantonik.messy.server.core.service;

import dev.whyoleg.cryptography.algorithms.RSA;
import dev.whyoleg.cryptography.random.CryptographyRandom;
import hantonik.messy.data.device.DeviceInfo;
import hantonik.messy.encryption.Crypt;
import hantonik.messy.encryption.exception.CryptException;
import hantonik.messy.server.core.data.entity.client.ClientEntity;
import hantonik.messy.server.core.data.entity.client.ClientSessionEntity;
import hantonik.messy.server.core.data.entity.device.DeviceEntity;
import hantonik.messy.server.core.data.repository.ClientRepository;
import hantonik.messy.server.core.data.repository.ClientSessionRepository;
import hantonik.messy.server.core.data.repository.DeviceRepository;
import hantonik.messy.server.core.service.exception.InvalidDataException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientAuthenticationService {
    private final ClientStatusService clientStatusService;
    private final FileTransferService fileTransferService;

    private final ClientRepository clientRepository;
    private final ClientSessionRepository clientSessionRepository;
    private final DeviceRepository deviceRepository;

    @Transactional
    public ClientEntity loginClient(String login, byte[] rawPassword) {
        return this.clientRepository.findByEmailIgnoreCase(login)
                .or(() -> this.clientRepository.findByPhone(login))
                .filter(client -> {
                    try {
                        return MessageDigest.isEqual(Crypt.digestDataPBKDF2WithSHA512(rawPassword, client.getPasswordSalt()), client.getPasswordHash());
                    } catch (CryptException e) {
                        throw new IllegalStateException(e);
                    }
                })
                .orElseThrow(() -> new InvalidDataException(InvalidDataException.Cause.INVALID_CREDENTIALS));
    }

    @Transactional
    public ClientEntity registerClient(String firstName, String lastName, String email, @Nullable String phone, byte[] rawPassword, RSA.OAEP.PublicKey publicKey, byte[] encryptedPrivateKey, byte[] privateKeyNonce) {
        if (this.clientRepository.existsByEmail(email))
            throw new InvalidDataException(InvalidDataException.Cause.DUPLICATE_EMAIL);

        if (phone != null && this.clientRepository.existsByPhone(phone))
            throw new InvalidDataException(InvalidDataException.Cause.DUPLICATE_PHONE);

        var passwordSalt = Crypt.generateSalt();

        try {
            var passwordHash = Crypt.digestDataPBKDF2WithSHA512(rawPassword, passwordSalt);

            return this.clientRepository.save(new ClientEntity(firstName, lastName, email, phone, passwordHash, passwordSalt, publicKey, encryptedPrivateKey, privateKeyNonce));
        } catch (CryptException e) {
            throw new IllegalStateException(e);
        }
    }

    @Transactional
    public DeviceEntity registerDevice(DeviceInfo deviceInfo) {
        return this.deviceRepository.findByMacAddress(deviceInfo.getMacAddress())
                .orElseGet(() -> this.deviceRepository.save(new DeviceEntity(deviceInfo)));
    }

    // TODO new byte[32]
    @Transactional
    public ClientSessionEntity createSession(UUID clientId, @Nullable DeviceEntity deviceEntity, byte[] rawToken, int durationInDays) {
        try {
            var clientSession = this.getValidSessionToken(clientId, deviceEntity.getMacAddress());
            clientSession.setRevokedAt(Instant.now());

            this.clientSessionRepository.deleteById(clientSession.getId());
        } catch (IllegalArgumentException ignore) {}

        CryptographyRandom.Default.nextBytes(rawToken);

        try {
            return this.clientSessionRepository.save(new ClientSessionEntity(clientId, deviceEntity.getId(), Crypt.digestDataSHA256(rawToken), Instant.now().plus(Duration.ofDays(durationInDays))));
        } catch (CryptException e) {
            throw new IllegalStateException(e);
        }
    }

    @Transactional
    public ClientSessionEntity getValidSessionToken(UUID clientId, byte[] macAddress) {
        var now = Instant.now();

        return this.clientSessionRepository.streamAllByClientId(clientId)
                .filter(session -> session.getExpiresAt().isAfter(now))
                .filter(session -> session.getRevokedAt() == null)
                .filter(session -> Arrays.equals(session.getDevice().getMacAddress(), macAddress))
                .findFirst()
                .orElseThrow(() -> new InvalidDataException(InvalidDataException.Cause.NO_SESSION));
    }

    @Transactional
    public ClientSessionEntity renewSession(UUID clientSessionId, int durationInDays) {
        var clientSession = this.clientSessionRepository.findById(clientSessionId)
                .orElseThrow(() -> new InvalidDataException(InvalidDataException.Cause.NO_SESSION));

        clientSession.setExpiresAt(Instant.now().plus(Duration.ofDays(durationInDays)));

        return clientSession;
    }
}
