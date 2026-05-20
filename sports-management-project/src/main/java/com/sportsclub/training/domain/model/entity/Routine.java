package com.sportsclub.training.domain.model.entity;

import com.sportsclub.training.domain.model.valueobject.Intensity;
import com.sportsclub.training.domain.model.enums.RecoverySuggestion;
import java.time.LocalDateTime;
import java.util.UUID;

public class Routine {
    private final UUID id;
    private final UUID athleteId;
    private final String name;
    private final String description;
    private final int recommendedDurationMinutes;
    private final Intensity recommendedIntensity;
    private final RecoverySuggestion recoverySuggestion;
    private final LocalDateTime createdAt;

    public Routine(UUID id, UUID athleteId, String name, String description,
                   int recommendedDurationMinutes, Intensity recommendedIntensity,
                   RecoverySuggestion recoverySuggestion, LocalDateTime createdAt) {
        this.id = id;
        this.athleteId = athleteId;
        this.name = name;
        this.description = description;
        this.recommendedDurationMinutes = recommendedDurationMinutes;
        this.recommendedIntensity = recommendedIntensity;
        this.recoverySuggestion = recoverySuggestion;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getAthleteId() { return athleteId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getRecommendedDurationMinutes() { return recommendedDurationMinutes; }
    public Intensity getRecommendedIntensity() { return recommendedIntensity; }
    public RecoverySuggestion getRecoverySuggestion() { return recoverySuggestion; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Routine create(UUID athleteId, String name, String description,
                                 int recommendedDurationMinutes, Intensity recommendedIntensity,
                                 RecoverySuggestion recoverySuggestion) {
        return new Routine(UUID.randomUUID(), athleteId, name, description, recommendedDurationMinutes,
            recommendedIntensity, recoverySuggestion, LocalDateTime.now());
    }
}