package com.sportsclub.training.domain.model.entity;

import com.sportsclub.training.domain.model.valueobject.Intensity;
import com.sportsclub.training.domain.model.valueobject.SessionId;
import java.time.LocalDateTime;
import java.util.UUID;

public class TrainingSession {
    private final SessionId sessionId;
    private final UUID athleteId;
    private final LocalDateTime sessionDate;
    private final int durationMinutes;
    private final Intensity intensity;
    private final int caloriesBurned;
    private final LocalDateTime createdAt;

    public TrainingSession(SessionId sessionId, UUID athleteId, LocalDateTime sessionDate,
                           int durationMinutes, Intensity intensity, int caloriesBurned, LocalDateTime createdAt) {
        this.sessionId = sessionId;
        this.athleteId = athleteId;
        this.sessionDate = sessionDate;
        this.durationMinutes = durationMinutes;
        this.intensity = intensity;
        this.caloriesBurned = caloriesBurned;
        this.createdAt = createdAt;
    }

    public SessionId getSessionId() { return sessionId; }
    public UUID getAthleteId() { return athleteId; }
    public LocalDateTime getSessionDate() { return sessionDate; }
    public int getDurationMinutes() { return durationMinutes; }
    public Intensity getIntensity() { return intensity; }
    public int getCaloriesBurned() { return caloriesBurned; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static TrainingSession create(UUID athleteId, LocalDateTime sessionDate,
                                        int durationMinutes, Intensity intensity, int caloriesBurned) {
        return new TrainingSession(SessionId.generate(), athleteId, sessionDate, durationMinutes,
            intensity, caloriesBurned, LocalDateTime.now());
    }
}