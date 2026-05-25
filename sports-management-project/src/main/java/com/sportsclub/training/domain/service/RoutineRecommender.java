package com.sportsclub.training.domain.service;

import com.sportsclub.training.domain.model.entity.Routine;
import com.sportsclub.training.domain.model.enums.RecoverySuggestion;
import com.sportsclub.training.domain.model.valueobject.Intensity;
import com.sportsclub.training.domain.model.valueobject.SportType;
import com.sportsclub.training.domain.port.out.AthleteProfileRepository;
import com.sportsclub.training.domain.port.out.TrainingSessionRepository;
import com.sportsclub.shared.domain.model.FatigueLevel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.sportsclub.training.domain.model.entity.AthleteProfile;
import com.sportsclub.training.domain.model.entity.TrainingSession;

public class RoutineRecommender {
    private final AthleteProfileRepository profileRepository;
    private final FatigueCalculator fatigueCalculator;
    private final TrainingSessionRepository sessionRepository;

    public RoutineRecommender(AthleteProfileRepository profileRepository, FatigueCalculator fatigueCalculator,
            TrainingSessionRepository sessionRepository) {
        this.profileRepository = profileRepository;
        this.fatigueCalculator = fatigueCalculator;
        this.sessionRepository = sessionRepository;
    }

    public Routine recommendRoutine(UUID athleteId, SportType sportType) {
        AthleteProfile profile = profileRepository.findByAthleteId(athleteId);
        List<TrainingSession> recentSessions = sessionRepository.findRecentByAthleteId(athleteId,
                LocalDateTime.now().minusHours(72));
        FatigueLevel fatigue = fatigueCalculator.calculateFatigue(recentSessions, LocalDateTime.now());
        int age = profile.getAthlete().calculateAge();
        boolean isCompetitionSoon = profile.hasCompetitionInNextDays(3);
        int weeklyLoad = profile.getTotalMinutesThisWeek();

        if (isCompetitionSoon && fatigue == FatigueLevel.LOW)
            return createTaperingRoutine(athleteId, sportType);
        if (weeklyLoad > 300 && fatigue == FatigueLevel.MEDIUM)
            return createDeloadRoutine(athleteId, sportType);
        if (age < 18 && fatigue == FatigueLevel.HIGH)
            return createYouthRecoveryRoutine(athleteId, sportType);
        return getStandardRecommendation(fatigue, athleteId, sportType);
    }

    private Routine createTaperingRoutine(UUID athleteId, SportType sportType) {
        return Routine.create(athleteId, "Descarga - " + sportType.getDisplayName(),
                "Reducción de carga previa a competencia", 30, Intensity.LIGHT, RecoverySuggestion.ACTIVE_RECOVERY);
    }

    private Routine createDeloadRoutine(UUID athleteId, SportType sportType) {
        return Routine.create(athleteId, "Semana de Descarga - " + sportType.getDisplayName(),
                "Reducción de volumen semanal", 40, Intensity.MODERATE, RecoverySuggestion.MODERATE_WORKOUT);
    }

    private Routine createYouthRecoveryRoutine(UUID athleteId, SportType sportType) {
        return Routine.create(athleteId, "Recuperación Jóvenes - " + sportType.getDisplayName(),
                "Enfoque en técnica y recuperación", 25, Intensity.LIGHT, RecoverySuggestion.LIGHT_ACTIVITY);
    }

    private Routine getStandardRecommendation(FatigueLevel fatigue, UUID athleteId, SportType sportType) {
        switch (fatigue) {
            case HIGH:
                return Routine.create(athleteId, "Recuperación - " + sportType.getDisplayName(), "Entrenamiento ligero",
                        30, Intensity.LIGHT, RecoverySuggestion.LIGHT_ACTIVITY);
            case MEDIUM:
                return Routine.create(athleteId, "Mantenimiento - " + sportType.getDisplayName(),
                        "Entrenamiento moderado", 45, Intensity.MODERATE, RecoverySuggestion.MODERATE_WORKOUT);
            case LOW:
                return Routine.create(athleteId, "Intenso - " + sportType.getDisplayName(), "Alta intensidad", 60,
                        Intensity.HIGH, RecoverySuggestion.INCREASE_INTENSITY);
            default:
                return Routine.create(athleteId, "Normal - " + sportType.getDisplayName(), "Entrenamiento estándar", 50,
                        Intensity.MODERATE, RecoverySuggestion.MODERATE_WORKOUT);
        }
    }
}