package com.sportsclub.training.domain.service;

import com.sportsclub.training.domain.model.entity.Routine;
import com.sportsclub.training.domain.model.enums.RecoverySuggestion;
import com.sportsclub.training.domain.model.valueobject.Intensity;
import com.sportsclub.training.domain.model.valueobject.SportType;
import com.sportsclub.shared.domain.model.FatigueLevel;
import java.util.UUID;

public class RoutineRecommender {
    public Routine recommendRoutine(UUID athleteId, FatigueLevel fatigueLevel, SportType sportType) {
        if (fatigueLevel == null || sportType == null) throw new IllegalArgumentException("Fatigue level and sport type cannot be null");
        return switch (fatigueLevel) {
            case HIGH -> Routine.create(athleteId, "Rutina de Recuperación", "Entrenamiento ligero para recuperación activa", 30, Intensity.LIGHT, RecoverySuggestion.LIGHT_ACTIVITY);
            case MEDIUM -> Routine.create(athleteId, sportType == SportType.GYM ? "Rutina de Mantenimiento Gym" : "Rutina de Mantenimiento Fútbol", "Entrenamiento moderado para mantener condición", 45, Intensity.MODERATE, RecoverySuggestion.MODERATE_WORKOUT);
            case LOW -> Routine.create(athleteId, sportType == SportType.GYM ? "Rutina Intensa Gym" : "Rutina Intensa Fútbol", "Entrenamiento completo para máximo rendimiento", 60, Intensity.HIGH, RecoverySuggestion.INCREASE_INTENSITY);
        };
    }
}