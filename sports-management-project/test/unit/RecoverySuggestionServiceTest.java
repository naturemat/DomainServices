package test.unit;

import domain.model.enums.RecoverySuggestion;
import domain.model.valueobjects.FatigueLevel;
import domain.model.valueobjects.SportType;
import domain.services.RecoverySuggestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecoverySuggestionServiceTest {

    private RecoverySuggestionService recoverySuggestionService;

    @BeforeEach
    void setUp() {
        recoverySuggestionService = new RecoverySuggestionService();
    }

    @Test
    void testGetSuggestion_HighFatigueGym_ReturnsAbsoluteRest() {
        RecoverySuggestion suggestion = recoverySuggestionService.getSuggestion(FatigueLevel.HIGH, SportType.GYM);

        assertEquals(RecoverySuggestion.ABSOLUTE_REST, suggestion);
    }

    @Test
    void testGetSuggestion_HighFatigueFootball_ReturnsAbsoluteRest() {
        RecoverySuggestion suggestion = recoverySuggestionService.getSuggestion(FatigueLevel.HIGH, SportType.FOOTBALL);

        assertEquals(RecoverySuggestion.ABSOLUTE_REST, suggestion);
    }

    @Test
    void testGetSuggestion_MediumFatigueGym_ReturnsLightActivity() {
        RecoverySuggestion suggestion = recoverySuggestionService.getSuggestion(FatigueLevel.MEDIUM, SportType.GYM);

        assertEquals(RecoverySuggestion.LIGHT_ACTIVITY, suggestion);
    }

    @Test
    void testGetSuggestion_MediumFatigueFootball_ReturnsActiveRecovery() {
        RecoverySuggestion suggestion = recoverySuggestionService.getSuggestion(FatigueLevel.MEDIUM, SportType.FOOTBALL);

        assertEquals(RecoverySuggestion.ACTIVE_RECOVERY, suggestion);
    }

    @Test
    void testGetSuggestion_LowFatigue_ReturnsIncreaseIntensity() {
        RecoverySuggestion suggestion = recoverySuggestionService.getSuggestion(FatigueLevel.LOW, SportType.GYM);

        assertEquals(RecoverySuggestion.INCREASE_INTENSITY, suggestion);
    }

    @Test
    void testGetSuggestion_NullFatigue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            recoverySuggestionService.getSuggestion(null, SportType.GYM)
        );
    }

    @Test
    void testGetSuggestion_NullSportType_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            recoverySuggestionService.getSuggestion(FatigueLevel.LOW, null)
        );
    }

    @Test
    void testAdjustForConsecutiveLow_ThreeConsecutiveLow_ReturnsIncreaseIntensity() {
        RecoverySuggestion suggestion = recoverySuggestionService.adjustForConsecutiveLow(FatigueLevel.LOW, 3);

        assertEquals(RecoverySuggestion.INCREASE_INTENSITY, suggestion);
    }

    @Test
    void testAdjustForConsecutiveLow_LessThanThree_ReturnsNormalSuggestion() {
        RecoverySuggestion suggestion = recoverySuggestionService.adjustForConsecutiveLow(FatigueLevel.LOW, 2);

        assertNotNull(suggestion);
    }
}