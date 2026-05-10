package domain.services;

import domain.model.entities.Routine;
import domain.model.enums.RecoverySuggestion;
import domain.model.valueobjects.FatigueLevel;
import domain.model.valueobjects.Intensity;
import domain.model.valueobjects.SportType;
import java.util.UUID;

public class RoutineRecommendationService {

    public Routine recommendRoutine(UUID athleteId, FatigueLevel fatigueLevel, SportType sportType) {
        if (fatigueLevel == null || sportType == null) {
            throw new IllegalArgumentException("Fatigue level and sport type cannot be null");
        }

        return switch (fatigueLevel) {
            case HIGH -> createHighFatigueRoutine(athleteId, sportType);
            case MEDIUM -> createMediumFatigueRoutine(athleteId, sportType);
            case LOW -> createLowFatigueRoutine(athleteId, sportType);
        };
    }

    private Routine createHighFatigueRoutine(UUID athleteId, SportType sportType) {
        String name = "Rutina de Recuperación";
        String description = "Entrenamiento ligero para recuperación activa";
        int duration = 30;
        Intensity intensity = Intensity.LIGHT;
        RecoverySuggestion recovery = RecoverySuggestion.LIGHT_ACTIVITY;

        return Routine.create(athleteId, name, description, duration, intensity, recovery);
    }

    private Routine createMediumFatigueRoutine(UUID athleteId, SportType sportType) {
        String name = sportType == SportType.GYM
            ? "Rutina de Mantenimiento Gym"
            : "Rutina de Mantenimiento Fútbol";
        String description = "Entrenamiento moderado para mantener condición";
        int duration = 45;
        Intensity intensity = Intensity.MODERATE;
        RecoverySuggestion recovery = RecoverySuggestion.MODERATE_WORKOUT;

        return Routine.create(athleteId, name, description, duration, intensity, recovery);
    }

    private Routine createLowFatigueRoutine(UUID athleteId, SportType sportType) {
        String name = sportType == SportType.GYM
            ? "Rutina Intensa Gym"
            : "Rutina Intensa Fútbol";
        String description = "Entrenamiento completo para máximo rendimiento";
        int duration = 60;
        Intensity intensity = Intensity.HIGH;
        RecoverySuggestion recovery = RecoverySuggestion.INCREASE_INTENSITY;

        return Routine.create(athleteId, name, description, duration, intensity, recovery);
    }
}