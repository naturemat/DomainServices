package application.dto;

import domain.model.entities.Routine;
import domain.model.enums.RecoverySuggestion;
import domain.model.valueobjects.Intensity;
import java.util.UUID;

public class RoutineResponse {
    private UUID id;
    private UUID athleteId;
    private String athleteName;
    private String name;
    private String description;
    private int recommendedDurationMinutes;
    private Intensity recommendedIntensity;
    private RecoverySuggestion recoverySuggestion;

    public RoutineResponse() {}

    public static RoutineResponse fromDomain(Routine routine, String athleteName) {
        RoutineResponse response = new RoutineResponse();
        response.id = routine.getId();
        response.athleteId = routine.getAthleteId();
        response.athleteName = athleteName;
        response.name = routine.getName();
        response.description = routine.getDescription();
        response.recommendedDurationMinutes = routine.getRecommendedDurationMinutes();
        response.recommendedIntensity = routine.getRecommendedIntensity();
        response.recoverySuggestion = routine.getRecoverySuggestion();
        return response;
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

    public Intensity getRecommendedIntensity() {
        return recommendedIntensity;
    }

    public RecoverySuggestion getRecoverySuggestion() {
        return recoverySuggestion;
    }
}