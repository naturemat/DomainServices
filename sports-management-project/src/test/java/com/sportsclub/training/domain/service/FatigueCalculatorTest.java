package com.sportsclub.training.domain.service;

import com.sportsclub.training.domain.model.entity.TrainingSession;
import com.sportsclub.training.domain.model.valueobject.Intensity;
import com.sportsclub.training.domain.model.valueobject.SessionId;
import com.sportsclub.training.domain.policy.FatigueConfiguration;
import com.sportsclub.training.domain.model.valueobject.FatigueLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class FatigueCalculatorTest {
    private FatigueCalculator fatigueCalculator;
    private FatigueConfiguration fatigueConfiguration;

    @BeforeEach
    void setUp() { fatigueConfiguration = new FatigueConfiguration(72, 30, 15, 1); fatigueCalculator = new FatigueCalculator(fatigueConfiguration); }

    @Test
    void testCalculateFatigue_WithNoSessions_ReturnsLow() { assertEquals(FatigueLevel.LOW, fatigueCalculator.calculateFatigue(List.of(), LocalDateTime.now())); }

    @Test
    void testCalculateFatigue_HighIntensitySessions_ReturnsHighFatigue() {
        UUID athleteId = UUID.randomUUID();
        LocalDateTime currentTime = LocalDateTime.now();
        List<TrainingSession> sessions = List.of(
            new TrainingSession(SessionId.generate(), athleteId, currentTime.minusHours(24), 60, Intensity.HIGH, 500, currentTime.minusHours(24)),
            new TrainingSession(SessionId.generate(), athleteId, currentTime.minusHours(48), 60, Intensity.HIGH, 500, currentTime.minusHours(48))
        );
        assertEquals(FatigueLevel.HIGH, fatigueCalculator.calculateFatigue(sessions, currentTime));
    }

    @Test
    void testApplyRestReduction_WithRestDays_LowersFatigue() { assertEquals(FatigueLevel.MEDIUM, fatigueCalculator.applyRestReduction(FatigueLevel.HIGH, 1)); }
    @Test
    void testApplyRestReduction_WithManyRestDays_ReturnsLow() { assertEquals(FatigueLevel.LOW, fatigueCalculator.applyRestReduction(FatigueLevel.HIGH, 5)); }
}