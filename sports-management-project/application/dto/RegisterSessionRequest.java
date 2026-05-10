package application.dto;

import domain.model.valueobjects.Intensity;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class RegisterSessionRequest {
    @NotNull(message = "Athlete ID is required")
    private UUID athleteId;

    @NotNull(message = "Session date is required")
    private LocalDateTime sessionDate;

    @NotNull(message = "Duration is required")
    private Integer durationMinutes;

    @NotNull(message = "Intensity is required")
    private Intensity intensity;

    private Integer caloriesBurned;

    public RegisterSessionRequest() {}

    public RegisterSessionRequest(UUID athleteId, LocalDateTime sessionDate,
                                  Integer durationMinutes, Intensity intensity, Integer caloriesBurned) {
        this.athleteId = athleteId;
        this.sessionDate = sessionDate;
        this.durationMinutes = durationMinutes;
        this.intensity = intensity;
        this.caloriesBurned = caloriesBurned;
    }

    public UUID getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(UUID athleteId) {
        this.athleteId = athleteId;
    }

    public LocalDateTime getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDateTime sessionDate) {
        this.sessionDate = sessionDate;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Intensity getIntensity() {
        return intensity;
    }

    public void setIntensity(Intensity intensity) {
        this.intensity = intensity;
    }

    public Integer getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Integer caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }
}