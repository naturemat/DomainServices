package domain.services;

import domain.model.entities.TrainingSession;
import domain.model.valueobjects.FatigueLevel;
import domain.model.valueobjects.Intensity;
import domain.policies.FatigueRules;
import java.time.LocalDateTime;
import java.util.List;

public class FatigueCalculationService {

    private final FatigueRules fatigueRules;

    public FatigueCalculationService(FatigueRules fatigueRules) {
        this.fatigueRules = fatigueRules;
    }

    public FatigueLevel calculateFatigue(List<TrainingSession> recentSessions, LocalDateTime currentTime) {
        if (recentSessions == null || recentSessions.isEmpty()) {
            return FatigueLevel.LOW;
        }

        int totalFatiguePoints = 0;
        int sessionsInRecoveryPeriod = 0;

        for (TrainingSession session : recentSessions) {
            if (isWithinRecoveryWindow(session.getSessionDate(), currentTime)) {
                sessionsInRecoveryPeriod++;
                totalFatiguePoints += calculateSessionFatiguePoints(session);
            }
        }

        if (sessionsInRecoveryPeriod == 0) {
            return FatigueLevel.LOW;
        }

        return determineFatigueLevel(totalFatiguePoints);
    }

    private int calculateSessionFatiguePoints(TrainingSession session) {
        int basePoints = session.getDurationMinutes() / 10;
        int intensityMultiplier = session.getIntensity().getFatigueMultiplier();
        return basePoints * intensityMultiplier;
    }

    private boolean isWithinRecoveryWindow(LocalDateTime sessionDate, LocalDateTime currentTime) {
        long hoursSinceSession = java.time.Duration.between(sessionDate, currentTime).toHours();
        return hoursSinceSession <= fatigueRules.getRecoveryWindowHours();
    }

    private FatigueLevel determineFatigueLevel(int totalPoints) {
        if (totalPoints >= fatigueRules.getHighFatigueThreshold()) {
            return FatigueLevel.HIGH;
        } else if (totalPoints >= fatigueRules.getMediumFatigueThreshold()) {
            return FatigueLevel.MEDIUM;
        } else {
            return FatigueLevel.LOW;
        }
    }

    public FatigueLevel applyRestReduction(FatigueLevel currentFatigue, int restDays) {
        int reduction = restDays * fatigueRules.getRestDayReductionRate();
        int newValue = currentFatigue.getValue() - reduction;

        if (newValue <= FatigueLevel.LOW.getValue()) {
            return FatigueLevel.LOW;
        } else if (newValue <= FatigueLevel.MEDIUM.getValue()) {
            return FatigueLevel.MEDIUM;
        } else {
            return FatigueLevel.HIGH;
        }
    }
}