package com.sportsclub.training.application.dto.request;

import com.sportsclub.training.domain.model.valueobject.Intensity;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class RegisterSessionRequest {
    @NotNull private UUID athleteId;
    @NotNull private LocalDateTime sessionDate;
    @NotNull private Integer durationMinutes;
    @NotNull private String intensity;
    private Integer caloriesBurned;

    public RegisterSessionRequest() {}
    public RegisterSessionRequest(UUID athleteId, LocalDateTime sessionDate, Integer durationMinutes, String intensity, Integer caloriesBurned) {
        this.athleteId = athleteId; this.sessionDate = sessionDate; this.durationMinutes = durationMinutes; this.intensity = intensity; this.caloriesBurned = caloriesBurned;
    }

    public UUID getAthleteId() { return athleteId; }
    public void setAthleteId(UUID athleteId) { this.athleteId = athleteId; }
    public LocalDateTime getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDateTime sessionDate) { this.sessionDate = sessionDate; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getIntensity() { return intensity; }
    public void setIntensity(String intensity) { this.intensity = intensity; }
    public Integer getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Integer caloriesBurned) { this.caloriesBurned = caloriesBurned; }
    public Intensity getIntensityEnum() { return intensity != null ? Intensity.valueOf(intensity) : null; }
}