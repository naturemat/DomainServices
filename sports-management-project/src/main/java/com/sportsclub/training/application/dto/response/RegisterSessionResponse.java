package com.sportsclub.training.application.dto.response;

import com.sportsclub.training.domain.model.entity.Routine;
import com.sportsclub.training.domain.model.enums.RecoverySuggestion;
import com.sportsclub.shared.domain.model.FatigueLevel;
import com.sportsclub.training.domain.model.valueobject.Intensity;
import com.sportsclub.training.domain.model.valueobject.SessionId;
import java.time.LocalDateTime;
import java.util.UUID;

public class RegisterSessionResponse {
    private final SessionId sessionId;
    private final UUID athleteId;
    private final LocalDateTime sessionDate;
    private final int durationMinutes;
    private final String intensity;
    private final String fatigueLevel;
    private final Routine recommendedRoutine;
    private final String recoverySuggestion;
    private final LocalDateTime createdAt;

    public RegisterSessionResponse(SessionId sessionId, UUID athleteId, LocalDateTime sessionDate, int durationMinutes,
            Intensity intensity, FatigueLevel fatigueLevel, Routine recommendedRoutine,
            RecoverySuggestion recoverySuggestion, LocalDateTime createdAt) {
        this.sessionId = sessionId;
        this.athleteId = athleteId;
        this.sessionDate = sessionDate;
        this.durationMinutes = durationMinutes;
        this.intensity = intensity.getDescription();
        this.fatigueLevel = fatigueLevel.getDescription();
        this.recommendedRoutine = recommendedRoutine;
        this.recoverySuggestion = recoverySuggestion.getDescription();
        this.createdAt = createdAt;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public UUID getAthleteId() {
        return athleteId;
    }

    public LocalDateTime getSessionDate() {
        return sessionDate;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getIntensity() {
        return intensity;
    }

    public String getFatigueLevel() {
        return fatigueLevel;
    }

    public Routine getRecommendedRoutine() {
        return recommendedRoutine;
    }

    public String getRecoverySuggestion() {
        return recoverySuggestion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}