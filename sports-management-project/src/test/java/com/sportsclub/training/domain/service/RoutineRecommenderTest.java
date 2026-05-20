package com.sportsclub.training.domain.service;

import com.sportsclub.training.domain.model.entity.Routine;
import com.sportsclub.training.domain.model.enums.RecoverySuggestion;
import com.sportsclub.training.domain.model.valueobject.Intensity;
import com.sportsclub.training.domain.model.valueobject.SportType;
import com.sportsclub.shared.domain.model.FatigueLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class RoutineRecommenderTest {
    private RoutineRecommender routineRecommender;

    @BeforeEach
    void setUp() { routineRecommender = new RoutineRecommender(); }

    @Test
    void testRecommendRoutine_HighFatigue_ReturnsLightRoutine() {
        Routine routine = routineRecommender.recommendRoutine(UUID.randomUUID(), FatigueLevel.HIGH, SportType.GYM);
        assertNotNull(routine);
        assertEquals("Rutina de Recuperación", routine.getName());
        assertEquals(Intensity.LIGHT, routine.getRecommendedIntensity());
    }

    @Test
    void testRecommendRoutine_MediumFatigue_ReturnsModerateRoutine() {
        Routine routine = routineRecommender.recommendRoutine(UUID.randomUUID(), FatigueLevel.MEDIUM, SportType.GYM);
        assertNotNull(routine);
        assertEquals("Rutina de Mantenimiento Gym", routine.getName());
        assertEquals(Intensity.MODERATE, routine.getRecommendedIntensity());
    }

    @Test
    void testRecommendRoutine_LowFatigue_ReturnsHighIntensityRoutine() {
        Routine routine = routineRecommender.recommendRoutine(UUID.randomUUID(), FatigueLevel.LOW, SportType.GYM);
        assertNotNull(routine);
        assertEquals("Rutina Intensa Gym", routine.getName());
        assertEquals(Intensity.HIGH, routine.getRecommendedIntensity());
    }

    @Test
    void testRecommendRoutine_NullFatigue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> routineRecommender.recommendRoutine(UUID.randomUUID(), null, SportType.GYM));
    }

    @Test
    void testRecommendRoutine_NullSportType_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> routineRecommender.recommendRoutine(UUID.randomUUID(), FatigueLevel.LOW, null));
    }
}