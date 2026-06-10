package com.sportsclub.training.domain.service;

import com.sportsclub.training.domain.model.entity.TrainingSession;
import com.sportsclub.training.domain.model.valueobject.FatigueLevel;
import com.sportsclub.training.domain.model.valueobject.RecoverySuggestion;
import com.sportsclub.training.domain.model.valueobject.SportType;
import com.sportsclub.training.domain.policy.FatigueConfiguration;
import java.time.LocalDateTime;
import java.util.List;

public class RecoverySuggester {
    private final FatigueConfiguration fatigueRules;

    public RecoverySuggester(FatigueConfiguration fatigueRules) {
        this.fatigueRules = fatigueRules;
    }

    public RecoverySuggestion suggest(FatigueLevel currentFatigue, SportType sportType,
            List<TrainingSession> recentSessions, LocalDateTime currentTime) {
        if (currentFatigue == null || sportType == null)
            throw new IllegalArgumentException("Fatigue level and sport type cannot be null");

        int consecutiveLowDays = countConsecutiveLowDays(recentSessions);
        if (currentFatigue == FatigueLevel.LOW && consecutiveLowDays >= 3)
            return RecoverySuggestion.INCREASE_INTENSITY;

        int sessionsThisWeek = (int) recentSessions.stream()
                .filter(s -> java.time.Duration.between(s.getSessionDate(), currentTime).toHours() <= 168)
                .count();

        int totalFatiguePoints = recentSessions.stream()
                .filter(s -> java.time.Duration.between(s.getSessionDate(), currentTime)
                        .toHours() <= fatigueRules.getRecoveryWindowHours())
                .mapToInt(TrainingSession::getFatigueContribution)
                .sum();

        if (fatigueRules.needsAbsoluteRest(totalFatiguePoints, sessionsThisWeek))
            return RecoverySuggestion.ABSOLUTE_REST;

        return currentFatigue.getRecoverySuggestion(sportType);
    }

    private int countConsecutiveLowDays(List<TrainingSession> recentSessions) {
        if (recentSessions == null || recentSessions.isEmpty())
            return 0;
        List<TrainingSession> ordered = recentSessions.stream()
                .sorted((a, b) -> b.getSessionDate().compareTo(a.getSessionDate()))
                .toList();
        int count = 0;
        for (TrainingSession session : ordered) {
            int contribution = session.getFatigueContribution();
            FatigueLevel level = fatigueRules.classifyFatigue(contribution);
            if (level == FatigueLevel.LOW)
                count++;
            else
                break;
        }
        return count;
    }
}
