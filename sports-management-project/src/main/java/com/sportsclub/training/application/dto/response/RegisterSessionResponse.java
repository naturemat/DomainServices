package com.sportsclub.training.application.dto.response;

import com.sportsclub.training.domain.model.entity.Routine;
import com.sportsclub.training.domain.model.enums.RecoverySuggestion;
import com.sportsclub.shared.domain.model.FatigueLevel;
import com.sportsclub.training.domain.model.valueobject.Intensity;
import com.sportsclub.training.domain.model.valueobject.SessionId;
import java.time.LocalDateTime;
import java.util.UUID;

public class RegisterSessionResponse {
    private SessionId sessionId;
    private UUID athleteId;
    private LocalDateTime sessionDate;
    private int durationMinutes;
    private Intensity intensity;
    private FatigueLevel fatigueLevel;
    private Routine recommendedRoutine;
    private RecoverySuggestion recoverySuggestion;
    private LocalDateTime createdAt;

    public RegisterSessionResponse() {}
    public RegisterSessionResponse(SessionId sessionId, UUID athleteId, LocalDateTime sessionDate, int durationMinutes, Intensity intensity, FatigueLevel fatigueLevel, Routine recommendedRoutine, RecoverySuggestion recoverySuggestion, LocalDateTime createdAt) {
        this.sessionId = sessionId; this.athleteId = athleteId; this.sessionDate = sessionDate; this.durationMinutes = durationMinutes; this.intensity = intensity; this.fatigueLevel = fatigueLevel; this.recommendedRoutine = recommendedRoutine; this.recoverySuggestion = recoverySuggestion; this.createdAt = createdAt;
    }

    public SessionId getSessionId() { return sessionId; }
    public UUID getAthleteId() { return athleteId; }
    public LocalDateTime getSessionDate() { return sessionDate; }
    public int getDurationMinutes() { return durationMinutes; }
    public Intensity getIntensity() { return intensity; }
    public FatigueLevel getFatigueLevel() { return fatigueLevel; }
    public Routine getRecommendedRoutine() { return recommendedRoutine; }
    public RecoverySuggestion getRecoverySuggestion() { return recoverySuggestion; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}