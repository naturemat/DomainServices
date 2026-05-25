package com.sportsclub.training.domain.service;

import com.sportsclub.training.domain.model.entity.TrainingSession;
import com.sportsclub.training.domain.policy.FatigueConfiguration;
import com.sportsclub.shared.domain.model.FatigueLevel;
import java.time.LocalDateTime;
import java.util.List;

public class FatigueCalculator {
    private final FatigueConfiguration fatigueRules;

    public FatigueCalculator(FatigueConfiguration fatigueRules) {
        this.fatigueRules = fatigueRules;
    }

    public FatigueLevel calculateFatigue(List<TrainingSession> recentSessions, LocalDateTime currentTime) {
        if (recentSessions == null || recentSessions.isEmpty())
            return FatigueLevel.LOW;
        int totalFatiguePoints = 0;
        int sessionsInRecoveryPeriod = 0;
        for (TrainingSession session : recentSessions) {
            if (isWithinRecoveryWindow(session.getSessionDate(), currentTime)) {
                sessionsInRecoveryPeriod++;
                totalFatiguePoints += (session.getDurationMinutes() / 10)
                        * session.getIntensity().getFatigueMultiplier();
            }
        }
        if (sessionsInRecoveryPeriod == 0)
            return FatigueLevel.LOW;
        if (totalFatiguePoints >= fatigueRules.getHighFatigueThreshold())
            return FatigueLevel.HIGH;
        if (totalFatiguePoints >= fatigueRules.getMediumFatigueThreshold())
            return FatigueLevel.MEDIUM;
        return FatigueLevel.LOW;
    }

    private boolean isWithinRecoveryWindow(LocalDateTime sessionDate, LocalDateTime currentTime) {
        return java.time.Duration.between(sessionDate, currentTime).toHours() <= fatigueRules.getRecoveryWindowHours();
    }

    public FatigueLevel applyRestReduction(FatigueLevel currentFatigue, int restDays) {
        int newValue = currentFatigue.getValue() - (restDays * fatigueRules.getRestDayReductionRate());
        if (newValue <= FatigueLevel.LOW.getValue())
            return FatigueLevel.LOW;
        if (newValue <= FatigueLevel.MEDIUM.getValue())
            return FatigueLevel.MEDIUM;
        return FatigueLevel.HIGH;
    }
}