package com.sportsclub.training.domain.service;

import com.sportsclub.training.domain.model.entity.Athlete;
import com.sportsclub.training.domain.model.entity.Routine;
import com.sportsclub.training.domain.model.valueobject.FatigueLevel;
import com.sportsclub.training.domain.model.valueobject.Intensity;
import com.sportsclub.training.domain.model.valueobject.SportType;
import com.sportsclub.training.domain.policy.FatigueConfiguration;
import com.sportsclub.training.domain.port.out.AthleteRepository;
import com.sportsclub.training.domain.port.out.TrainingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoutineRecommenderTest {
    private RoutineRecommender routineRecommender;
    private AthleteRepository athleteRepository;
    private FatigueCalculator fatigueCalculator;
    private TrainingSessionRepository sessionRepository;

    @BeforeEach
    void setUp() {
        athleteRepository = mock(AthleteRepository.class);
        fatigueCalculator = mock(FatigueCalculator.class);
        sessionRepository = mock(TrainingSessionRepository.class);
        RecoverySuggester recoverySuggester = new RecoverySuggester(new FatigueConfiguration());
        routineRecommender = new RoutineRecommender(athleteRepository, fatigueCalculator, recoverySuggester,
                sessionRepository, new FatigueConfiguration());
    }

    @Test
    void testHighFatigue_ReturnsLightRoutine() {
        UUID athleteId = UUID.randomUUID();
        Athlete athlete = Athlete.create("Test", SportType.GYM, LocalDate.of(2000, 1, 1));
        when(athleteRepository.findById(athleteId)).thenReturn(Optional.of(athlete));
        when(sessionRepository.findRecentByAthleteId(any(), any())).thenReturn(List.of());
        when(fatigueCalculator.calculateFatigue(any(), any())).thenReturn(FatigueLevel.HIGH);

        Routine routine = routineRecommender.recommendRoutine(athleteId, SportType.GYM);

        assertNotNull(routine);
        assertEquals(Intensity.LIGHT, routine.getRecommendedIntensity());
    }

    @Test
    void testMediumFatigueInGym_ReturnsLightIntensity() {
        UUID athleteId = UUID.randomUUID();
        Athlete athlete = Athlete.create("Test", SportType.GYM, LocalDate.of(2000, 1, 1));
        when(athleteRepository.findById(athleteId)).thenReturn(Optional.of(athlete));
        when(sessionRepository.findRecentByAthleteId(any(), any())).thenReturn(List.of());
        when(fatigueCalculator.calculateFatigue(any(), any())).thenReturn(FatigueLevel.MEDIUM);

        Routine routine = routineRecommender.recommendRoutine(athleteId, SportType.GYM);

        assertNotNull(routine);
        assertEquals(Intensity.LIGHT, routine.getRecommendedIntensity());
    }

    @Test
    void testLowFatigue_ReturnsHighIntensityRoutine() {
        UUID athleteId = UUID.randomUUID();
        Athlete athlete = Athlete.create("Test", SportType.GYM, LocalDate.of(2000, 1, 1));
        when(athleteRepository.findById(athleteId)).thenReturn(Optional.of(athlete));
        when(sessionRepository.findRecentByAthleteId(any(), any())).thenReturn(List.of());
        when(fatigueCalculator.calculateFatigue(any(), any())).thenReturn(FatigueLevel.LOW);

        Routine routine = routineRecommender.recommendRoutine(athleteId, SportType.GYM);

        assertNotNull(routine);
        assertEquals(Intensity.HIGH, routine.getRecommendedIntensity());
    }

    @Test
    void testYouthWithHighFatigue_ReturnsLightActivity() {
        UUID athleteId = UUID.randomUUID();
        Athlete youth = Athlete.create("Young", SportType.FOOTBALL, LocalDate.of(2010, 6, 1));
        when(athleteRepository.findById(athleteId)).thenReturn(Optional.of(youth));
        when(sessionRepository.findRecentByAthleteId(any(), any())).thenReturn(List.of());
        when(fatigueCalculator.calculateFatigue(any(), any())).thenReturn(FatigueLevel.HIGH);

        Routine routine = routineRecommender.recommendRoutine(athleteId, SportType.FOOTBALL);

        assertNotNull(routine);
        assertEquals(Intensity.LIGHT, routine.getRecommendedIntensity());
    }

    @Test
    void testYouthWithMediumFatigue_AppliesDurationCap() {
        UUID athleteId = UUID.randomUUID();
        Athlete youth = Athlete.create("Young", SportType.FOOTBALL, LocalDate.of(2010, 6, 1));
        when(athleteRepository.findById(athleteId)).thenReturn(Optional.of(youth));
        when(sessionRepository.findRecentByAthleteId(any(), any())).thenReturn(List.of());
        when(fatigueCalculator.calculateFatigue(any(), any())).thenReturn(FatigueLevel.MEDIUM);

        Routine routine = routineRecommender.recommendRoutine(athleteId, SportType.FOOTBALL);

        assertTrue(routine.getRecommendedDurationMinutes() <= 40);
    }

    @Test
    void testYouthWithLowFatigue_DurationCappedAtYouthMax() {
        UUID athleteId = UUID.randomUUID();
        Athlete youth = Athlete.create("Young", SportType.GYM, LocalDate.of(2010, 6, 1));
        when(athleteRepository.findById(athleteId)).thenReturn(Optional.of(youth));
        when(sessionRepository.findRecentByAthleteId(any(), any())).thenReturn(List.of());
        when(fatigueCalculator.calculateFatigue(any(), any())).thenReturn(FatigueLevel.LOW);

        Routine routine = routineRecommender.recommendRoutine(athleteId, SportType.GYM);

        assertTrue(routine.getRecommendedDurationMinutes() <= 40);
    }

    @Test
    void testAthleteNotFound_FallsBackToDefaultAge() {
        UUID athleteId = UUID.randomUUID();
        when(athleteRepository.findById(athleteId)).thenReturn(Optional.empty());
        when(sessionRepository.findRecentByAthleteId(any(), any())).thenReturn(List.of());
        when(fatigueCalculator.calculateFatigue(any(), any())).thenReturn(FatigueLevel.LOW);

        Routine routine = routineRecommender.recommendRoutine(athleteId, SportType.GYM);

        assertNotNull(routine);
        assertEquals(Intensity.HIGH, routine.getRecommendedIntensity());
    }
}
