package test.unit;

import domain.model.entities.Routine;
import domain.model.enums.RecoverySuggestion;
import domain.model.valueobjects.FatigueLevel;
import domain.model.valueobjects.Intensity;
import domain.model.valueobjects.SportType;
import domain.services.RoutineRecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class RoutineRecommendationServiceTest {

    private RoutineRecommendationService routineRecommendationService;

    @BeforeEach
    void setUp() {
        routineRecommendationService = new RoutineRecommendationService();
    }

    @Test
    void testRecommendRoutine_HighFatigue_ReturnsLightRoutine() {
        UUID athleteId = UUID.randomUUID();
        FatigueLevel highFatigue = FatigueLevel.HIGH;

        Routine routine = routineRecommendationService.recommendRoutine(athleteId, highFatigue, SportType.GYM);

        assertNotNull(routine);
        assertEquals("Rutina de Recuperación", routine.getName());
        assertEquals(Intensity.LIGHT, routine.getRecommendedIntensity());
    }

    @Test
    void testRecommendRoutine_HighFatigueGym_ReturnsRestSuggestion() {
        UUID athleteId = UUID.randomUUID();

        Routine routine = routineRecommendationService.recommendRoutine(athleteId, FatigueLevel.HIGH, SportType.GYM);

        assertEquals(RecoverySuggestion.LIGHT_ACTIVITY, routine.getRecoverySuggestion());
    }

    @Test
    void testRecommendRoutine_MediumFatigue_ReturnsModerateRoutine() {
        UUID athleteId = UUID.randomUUID();
        FatigueLevel mediumFatigue = FatigueLevel.MEDIUM;

        Routine routine = routineRecommendationService.recommendRoutine(athleteId, mediumFatigue, SportType.GYM);

        assertNotNull(routine);
        assertEquals("Rutina de Mantenimiento Gym", routine.getName());
        assertEquals(Intensity.MODERATE, routine.getRecommendedIntensity());
    }

    @Test
    void testRecommendRoutine_MediumFatigueFootball_ReturnsFootballRoutine() {
        UUID athleteId = UUID.randomUUID();

        Routine routine = routineRecommendationService.recommendRoutine(athleteId, FatigueLevel.MEDIUM, SportType.FOOTBALL);

        assertNotNull(routine);
        assertEquals("Rutina de Mantenimiento Fútbol", routine.getName());
    }

    @Test
    void testRecommendRoutine_LowFatigue_ReturnsHighIntensityRoutine() {
        UUID athleteId = UUID.randomUUID();
        FatigueLevel lowFatigue = FatigueLevel.LOW;

        Routine routine = routineRecommendationService.recommendRoutine(athleteId, lowFatigue, SportType.GYM);

        assertNotNull(routine);
        assertEquals("Rutina Intensa Gym", routine.getName());
        assertEquals(Intensity.HIGH, routine.getRecommendedIntensity());
    }

    @Test
    void testRecommendRoutine_LowFatigue_ReturnsIncreaseIntensity() {
        UUID athleteId = UUID.randomUUID();

        Routine routine = routineRecommendationService.recommendRoutine(athleteId, FatigueLevel.LOW, SportType.FOOTBALL);

        assertEquals(RecoverySuggestion.INCREASE_INTENSITY, routine.getRecoverySuggestion());
    }

    @Test
    void testRecommendRoutine_NullFatigue_ThrowsException() {
        UUID athleteId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () ->
            routineRecommendationService.recommendRoutine(athleteId, null, SportType.GYM)
        );
    }

    @Test
    void testRecommendRoutine_NullSportType_ThrowsException() {
        UUID athleteId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () ->
            routineRecommendationService.recommendRoutine(athleteId, FatigueLevel.LOW, null)
        );
    }
}