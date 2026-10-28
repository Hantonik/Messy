package hantonik.messy.server.core.service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public final class InvalidDataException extends RuntimeException {
    private final Cause cause;

    public InvalidDataException(Cause cause) {
        super(cause.message);

        this.cause = cause;
    }

    @RequiredArgsConstructor
    public enum Cause {
        INVALID_CREDENTIALS("Invalid login or password"),
        DUPLICATE_EMAIL("Client with this email already exists"),
        DUPLICATE_PHONE("Client with this phone number already exists"),
        NO_SESSION("No valid session");

        private final String message;
    }
}
