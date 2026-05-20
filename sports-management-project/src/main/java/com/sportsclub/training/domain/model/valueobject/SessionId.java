package com.sportsclub.training.domain.model.valueobject;

import java.util.UUID;

public record SessionId(UUID value) {
    public static SessionId generate() { return new SessionId(UUID.randomUUID()); }
    public static SessionId from(UUID uuid) { return new SessionId(uuid); }
    public UUID getValue() { return value; }
    @Override public String toString() { return value.toString(); }
}