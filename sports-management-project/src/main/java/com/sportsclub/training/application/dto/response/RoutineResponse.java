package com.sportsclub.training.application.dto.response;

import com.sportsclub.training.domain.model.entity.Routine;
import java.util.UUID;

public class RoutineResponse {
    private final UUID id;
    private final UUID athleteId;
    private final String athleteName;
    private final String name;
    private final String description;
    private final int recommendedDurationMinutes;
    private final String recommendedIntensity;
    private final String recoverySuggestion;

    private RoutineResponse(UUID id, UUID athleteId, String athleteName, String name, String description,
            int recommendedDurationMinutes, String recommendedIntensity, String recoverySuggestion) {
        this.id = id;
        this.athleteId = athleteId;
        this.athleteName = athleteName;
        this.name = name;
        this.description = description;
        this.recommendedDurationMinutes = recommendedDurationMinutes;
        this.recommendedIntensity = recommendedIntensity;
        this.recoverySuggestion = recoverySuggestion;
    }

    public static RoutineResponse fromDomain(Routine routine, String athleteName) {
        return new RoutineResponse(
                routine.getId(),
                routine.getAthleteId(),
                athleteName,
                routine.getName(),
                routine.getDescription(),
                routine.getRecommendedDurationMinutes(),
                routine.getRecommendedIntensity().getDescription(),
                routine.getRecoverySuggestion().getDescription());
    }

    public UUID getId() {
        return id;
    }

    public UUID getAthleteId() {
        return athleteId;
    }

    public String getAthleteName() {
        return athleteName;
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

    public String getRecommendedIntensity() {
        return recommendedIntensity;
    }

    public String getRecoverySuggestion() {
        return recoverySuggestion;
    }
}