package com.sportsclub.training.domain.service;

import com.sportsclub.training.domain.model.entity.TrainingSession;
import com.sportsclub.training.domain.model.valueobject.FatigueLevel;
import com.sportsclub.training.domain.model.valueobject.RecoverySuggestion;
import com.sportsclub.training.domain.model.valueobject.Intensity;
import com.sportsclub.training.domain.model.valueobject.SportType;
import com.sportsclub.training.domain.policy.FatigueConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class RecoverySuggesterTest {
    private RecoverySuggester recoverySuggester;

    @BeforeEach
    void setUp() {
        recoverySuggester = new RecoverySuggester(new FatigueConfiguration());
    }

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void testHighFatigue_ReturnsAbsoluteRest() {
        assertEquals(RecoverySuggestion.ABSOLUTE_REST,
                recoverySuggester.suggest(FatigueLevel.HIGH, SportType.GYM, List.of(), now));
    }

    @Test
    void testMediumFatigueInGym_ReturnsLightActivity() {
        assertEquals(RecoverySuggestion.LIGHT_ACTIVITY,
                recoverySuggester.suggest(FatigueLevel.MEDIUM, SportType.GYM, List.of(), now));
    }

    @Test
    void testMediumFatigueInFootball_ReturnsActiveRecovery() {
        assertEquals(RecoverySuggestion.ACTIVE_RECOVERY,
                recoverySuggester.suggest(FatigueLevel.MEDIUM, SportType.FOOTBALL, List.of(), now));
    }

    @Test
    void testLowFatigue_ReturnsIncreaseIntensity() {
        assertEquals(RecoverySuggestion.INCREASE_INTENSITY,
                recoverySuggester.suggest(FatigueLevel.LOW, SportType.GYM, List.of(), now));
    }

    @Test
    void testNullFatigue_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> recoverySuggester.suggest(null, SportType.GYM, List.of(), now));
    }

    @Test
    void testNullSportType_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> recoverySuggester.suggest(FatigueLevel.LOW, null, List.of(), now));
    }

    @Test
    void testConsecutiveLowDays_ReturnsIncreaseIntensity() {
        TrainingSession low1 = TrainingSession.create(
                UUID.randomUUID(), LocalDateTime.now().minusHours(24), 30,
                Intensity.LIGHT, 100);
        TrainingSession low2 = TrainingSession.create(
                UUID.randomUUID(), LocalDateTime.now().minusHours(48), 30,
                Intensity.LIGHT, 100);
        TrainingSession low3 = TrainingSession.create(
                UUID.randomUUID(), LocalDateTime.now().minusHours(72), 30,
                Intensity.LIGHT, 100);
        List<TrainingSession> sessions = List.of(low1, low2, low3);

        assertEquals(RecoverySuggestion.INCREASE_INTENSITY,
                recoverySuggester.suggest(FatigueLevel.LOW, SportType.GYM, sessions, now));
    }

    @Test
    void testHighFatiguePoints_ReturnsAbsoluteRest() {
        TrainingSession s1 = TrainingSession.create(
                UUID.randomUUID(), LocalDateTime.now().minusHours(2), 60, Intensity.HIGH, 400);
        TrainingSession s2 = TrainingSession.create(
                UUID.randomUUID(), LocalDateTime.now().minusHours(26), 60, Intensity.HIGH, 400);
        TrainingSession s3 = TrainingSession.create(
                UUID.randomUUID(), LocalDateTime.now().minusHours(50), 60, Intensity.HIGH, 400);
        TrainingSession s4 = TrainingSession.create(
                UUID.randomUUID(), LocalDateTime.now().minusHours(74), 60, Intensity.HIGH, 400);
        TrainingSession s5 = TrainingSession.create(
                UUID.randomUUID(), LocalDateTime.now().minusHours(98), 60, Intensity.HIGH, 400);
        List<TrainingSession> sessions = List.of(s1, s2, s3, s4, s5);

        assertEquals(RecoverySuggestion.ABSOLUTE_REST,
                recoverySuggester.suggest(FatigueLevel.LOW, SportType.GYM, sessions, now));
    }

}
