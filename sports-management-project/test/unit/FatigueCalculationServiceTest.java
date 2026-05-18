package test.unit;

import domain.model.entities.TrainingSession;
import domain.model.valueobjects.FatigueLevel;
import domain.model.valueobjects.Intensity;
import domain.model.valueobjects.SessionId;
import domain.policies.FatigueRules;
import domain.services.FatigueCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class FatigueCalculationServiceTest {

    private FatigueCalculationService fatigueCalculationService;
    private FatigueRules fatigueRules;

    @BeforeEach
    void setUp() {
        fatigueRules = new FatigueRules(72, 30, 15, 1);
        fatigueCalculationService = new FatigueCalculationService(fatigueRules);
    }

    @Test
    void testCalculateFatigue_WithNoSessions_ReturnsLow() {
        List<TrainingSession> noSessions = List.of();
        LocalDateTime currentTime = LocalDateTime.now();

        FatigueLevel result = fatigueCalculationService.calculateFatigue(noSessions, currentTime);

        assertEquals(FatigueLevel.LOW, result);
    }

    @Test
    void testCalculateFatigue_HighIntensitySessions_ReturnsHighFatigue() {
        UUID athleteId = UUID.randomUUID();
        LocalDateTime currentTime = LocalDateTime.now();

        TrainingSession session1 = new TrainingSession(
            SessionId.generate(), athleteId,
            currentTime.minusHours(24), 60, Intensity.HIGH, 500,
            currentTime.minusHours(24)
        );

        TrainingSession session2 = new TrainingSession(
            SessionId.generate(), athleteId,
            currentTime.minusHours(48), 60, Intensity.HIGH, 500,
            currentTime.minusHours(48)
        );

        List<TrainingSession> sessions = List.of(session1, session2);
        FatigueLevel result = fatigueCalculationService.calculateFatigue(sessions, currentTime);

        assertEquals(FatigueLevel.HIGH, result);
    }

    @Test
    void testCalculateFatigue_MediumIntensitySessions_ReturnsLowFatigue() {
        UUID athleteId = UUID.randomUUID();
        LocalDateTime currentTime = LocalDateTime.now();

        TrainingSession session = new TrainingSession(
            SessionId.generate(), athleteId,
            currentTime.minusHours(24), 45, Intensity.MODERATE, 300,
            currentTime.minusHours(24)
        );

        List<TrainingSession> sessions = List.of(session);
        FatigueLevel result = fatigueCalculationService.calculateFatigue(sessions, currentTime);

        assertEquals(FatigueLevel.LOW, result);
    }

    @Test
    void testCalculateFatigue_LightIntensitySessions_ReturnsLowFatigue() {
        UUID athleteId = UUID.randomUUID();
        LocalDateTime currentTime = LocalDateTime.now();

        TrainingSession session = new TrainingSession(
            SessionId.generate(), athleteId,
            currentTime.minusHours(24), 30, Intensity.LIGHT, 150,
            currentTime.minusHours(24)
        );

        List<TrainingSession> sessions = List.of(session);
        FatigueLevel result = fatigueCalculationService.calculateFatigue(sessions, currentTime);

        assertEquals(FatigueLevel.LOW, result);
    }

    @Test
    void testApplyRestReduction_WithRestDays_LowersFatigue() {
        FatigueLevel highFatigue = FatigueLevel.HIGH;

        FatigueLevel result = fatigueCalculationService.applyRestReduction(highFatigue, 1);

        assertEquals(FatigueLevel.MEDIUM, result);
    }

    @Test
    void testApplyRestReduction_WithManyRestDays_ReturnsLow() {
        FatigueLevel highFatigue = FatigueLevel.HIGH;

        FatigueLevel result = fatigueCalculationService.applyRestReduction(highFatigue, 5);

        assertEquals(FatigueLevel.LOW, result);
    }

    @Test
    void testCalculateFatigue_OutsideRecoveryWindow_ReturnsLow() {
        UUID athleteId = UUID.randomUUID();
        LocalDateTime currentTime = LocalDateTime.now();

        TrainingSession oldSession = new TrainingSession(
            SessionId.generate(), athleteId,
            currentTime.minusHours(96), 60, Intensity.HIGH, 500,
            currentTime.minusHours(96)
        );

        List<TrainingSession> sessions = List.of(oldSession);
        FatigueLevel result = fatigueCalculationService.calculateFatigue(sessions, currentTime);

        assertEquals(FatigueLevel.LOW, result);
    }
}