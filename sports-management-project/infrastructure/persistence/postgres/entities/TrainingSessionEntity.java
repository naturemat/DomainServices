package infrastructure.persistence.postgres.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "training_sessions")
public class TrainingSessionEntity {

    @Id
    @Column(name = "session_id")
    private UUID sessionId;

    @Column(name = "athlete_id", nullable = false)
    private UUID athleteId;

    @Column(name = "session_date", nullable = false)
    private LocalDateTime sessionDate;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "intensity", nullable = false)
    private IntensityEnum intensity;

    @Column(name = "calories_burned")
    private Integer caloriesBurned;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public TrainingSessionEntity() {}

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
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

    public IntensityEnum getIntensity() {
        return intensity;
    }

    public void setIntensity(IntensityEnum intensity) {
        this.intensity = intensity;
    }

    public Integer getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Integer caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public enum IntensityEnum {
        LIGHT, MODERATE, HIGH, EXTREME
    }
}