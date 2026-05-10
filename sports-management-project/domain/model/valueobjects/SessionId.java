package domain.model.valueobjects;

import java.util.UUID;

public final class SessionId {
    private final UUID value;

    private SessionId(UUID value) {
        this.value = value;
    }

    public static SessionId generate() {
        return new SessionId(UUID.randomUUID());
    }

    public static SessionId from(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("SessionId cannot be null");
        }
        return new SessionId(uuid);
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionId sessionId = (SessionId) o;
        return value.equals(sessionId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}