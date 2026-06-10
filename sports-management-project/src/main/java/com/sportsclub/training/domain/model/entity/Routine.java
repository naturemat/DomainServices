package com.sportsclub.training.domain.model.entity;

import com.sportsclub.training.domain.model.valueobject.FatigueLevel;
import com.sportsclub.training.domain.model.valueobject.Intensity;
import com.sportsclub.training.domain.model.valueobject.RecoverySuggestion;
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

    public UUID getId() {
        return id;
    }

    public UUID getAthleteId() {
        return athleteId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getRecommendedDurationMinutes() {
        return recommendedDurationMinutes;
    }

    public Intensity getRecommendedIntensity() {
        return recommendedIntensity;
    }

    public RecoverySuggestion getRecoverySuggestion() {
        return recoverySuggestion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isSuitableFor(FatigueLevel fatigue) {
        return switch (fatigue) {
            case HIGH -> recommendedIntensity == Intensity.LIGHT;
            case MEDIUM -> recommendedIntensity != Intensity.HIGH;
            case LOW -> true;
        };
    }

    public static Routine create(UUID athleteId, String name, String description,
            int recommendedDurationMinutes, Intensity recommendedIntensity,
            RecoverySuggestion recoverySuggestion) {
        if (athleteId == null) throw new IllegalArgumentException("Athlete ID must not be null");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Routine name must not be blank");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Description must not be blank");
        if (recommendedDurationMinutes < 0) throw new IllegalArgumentException("Duration must not be negative");
        if (recommendedIntensity == null) throw new IllegalArgumentException("Intensity must not be null");
        if (recoverySuggestion == null) throw new IllegalArgumentException("Recovery suggestion must not be null");
        validateSuggestionConsistency(recoverySuggestion, recommendedIntensity);
        return new Routine(UUID.randomUUID(), athleteId, name, description, recommendedDurationMinutes,
                recommendedIntensity, recoverySuggestion, LocalDateTime.now());
    }

    private static void validateSuggestionConsistency(RecoverySuggestion suggestion, Intensity intensity) {
        Intensity expected = suggestion.getRecommendedIntensity();
        if (expected != intensity) {
            throw new IllegalArgumentException(
                    "Intensity " + intensity + " is inconsistent with suggestion " + suggestion
                            + ". Expected: " + expected);
        }
    }
}