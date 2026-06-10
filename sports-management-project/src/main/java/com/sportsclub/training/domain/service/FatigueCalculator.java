package com.sportsclub.training.domain.service;

import com.sportsclub.training.domain.model.entity.TrainingSession;
import com.sportsclub.training.domain.policy.FatigueConfiguration;
import com.sportsclub.training.domain.model.valueobject.FatigueLevel;
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
        boolean hasSessionsInWindow = false;
        for (TrainingSession session : recentSessions) {
            if (isWithinRecoveryWindow(session.getSessionDate(), currentTime)) {
                hasSessionsInWindow = true;
                totalFatiguePoints += session.getFatigueContribution();
            }
        }
        if (!hasSessionsInWindow)
            return FatigueLevel.LOW;
        return fatigueRules.classifyFatigue(totalFatiguePoints);
    }

    private boolean isWithinRecoveryWindow(LocalDateTime sessionDate, LocalDateTime currentTime) {
        return java.time.Duration.between(sessionDate, currentTime).toHours() <= fatigueRules.getRecoveryWindowHours();
    }

    public FatigueLevel applyRestReduction(FatigueLevel currentFatigue, int restDays) {
        return fatigueRules.applyRestReduction(currentFatigue, restDays);
    }
}