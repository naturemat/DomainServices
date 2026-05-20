package com.sportsclub.performance.domain.model.entity;

import com.sportsclub.shared.domain.model.FatigueLevel;
import java.time.LocalDateTime;
import java.util.UUID;

public class FatigueMetrics {
    private final UUID id;
    private final UUID athleteId;
    private final FatigueLevel fatigueLevel;
    private final LocalDateTime calculatedAt;

    public FatigueMetrics(UUID id, UUID athleteId, FatigueLevel fatigueLevel, LocalDateTime calculatedAt) {
        this.id = id; this.athleteId = athleteId; this.fatigueLevel = fatigueLevel; this.calculatedAt = calculatedAt;
    }

    public UUID getId() { return id; }
    public UUID getAthleteId() { return athleteId; }
    public FatigueLevel getFatigueLevel() { return fatigueLevel; }
    public LocalDateTime getCalculatedAt() { return calculatedAt; }

    public static FatigueMetrics create(UUID athleteId, FatigueLevel fatigueLevel, LocalDateTime calculatedAt) {
        return new FatigueMetrics(UUID.randomUUID(), athleteId, fatigueLevel, calculatedAt);
    }
}