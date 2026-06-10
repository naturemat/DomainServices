package com.sportsclub.training.domain.service;

import com.sportsclub.training.domain.model.entity.Athlete;
import com.sportsclub.training.domain.model.entity.Routine;
import com.sportsclub.training.domain.model.entity.TrainingSession;
import com.sportsclub.training.domain.model.valueobject.FatigueLevel;
import com.sportsclub.training.domain.model.valueobject.RecoverySuggestion;
import com.sportsclub.training.domain.model.valueobject.SportType;
import com.sportsclub.training.domain.policy.FatigueConfiguration;
import com.sportsclub.training.domain.port.out.AthleteRepository;
import com.sportsclub.training.domain.port.out.TrainingSessionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RoutineRecommender {
    private final AthleteRepository athleteRepository;
    private final FatigueCalculator fatigueCalculator;
    private final RecoverySuggester recoverySuggester;
    private final TrainingSessionRepository sessionRepository;
    private final FatigueConfiguration fatigueRules;

    public RoutineRecommender(AthleteRepository athleteRepository, FatigueCalculator fatigueCalculator,
            RecoverySuggester recoverySuggester, TrainingSessionRepository sessionRepository,
            FatigueConfiguration fatigueRules) {
        this.athleteRepository = athleteRepository;
        this.fatigueCalculator = fatigueCalculator;
        this.recoverySuggester = recoverySuggester;
        this.sessionRepository = sessionRepository;
        this.fatigueRules = fatigueRules;
    }

    public Routine recommendRoutine(UUID athleteId, SportType sportType) {
        Athlete athlete = athleteRepository.findById(athleteId).orElse(null);
        LocalDateTime now = LocalDateTime.now();
        List<TrainingSession> recentSessions = sessionRepository.findRecentByAthleteId(athleteId,
                now.minusHours(fatigueRules.getRecoveryWindowHours()));
        FatigueLevel fatigue = fatigueCalculator.calculateFatigue(recentSessions, now);
        RecoverySuggestion suggestion = recoverySuggester.suggest(fatigue, sportType, recentSessions, now);

        int age = athlete != null ? athlete.calculateAge() : 25;
        int weeklyMinutes = sumDurationLastWeek(recentSessions, now);

        int duration = fatigueRules.adjustDurationForAge(
                fatigueRules.adjustDurationForVolume(suggestion.getBaseDurationMinutes(), weeklyMinutes), age);
        int displayDuration = duration > 0 ? duration : 20;

        return Routine.create(athleteId,
                suggestion.buildRoutineName(sportType),
                suggestion.buildDescription(fatigue),
                displayDuration,
                suggestion.getRecommendedIntensity(),
                suggestion);
    }

    private int sumDurationLastWeek(List<TrainingSession> sessions, LocalDateTime now) {
        return sessions.stream()
                .filter(s -> java.time.Duration.between(s.getSessionDate(), now).toHours() <= 168)
                .mapToInt(TrainingSession::getDurationMinutes)
                .sum();
    }
}
