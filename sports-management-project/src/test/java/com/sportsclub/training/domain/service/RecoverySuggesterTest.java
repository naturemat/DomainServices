package com.sportsclub.training.domain.service;

import com.sportsclub.training.domain.model.enums.RecoverySuggestion;
import com.sportsclub.training.domain.model.valueobject.SportType;
import com.sportsclub.shared.domain.model.FatigueLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecoverySuggesterTest {
    private RecoverySuggester recoverySuggester;

    @BeforeEach
    void setUp() { recoverySuggester = new RecoverySuggester(); }

    @Test
    void testGetSuggestion_HighFatigueGym_ReturnsAbsoluteRest() { assertEquals(RecoverySuggestion.ABSOLUTE_REST, recoverySuggester.getSuggestion(FatigueLevel.HIGH, SportType.GYM)); }
    @Test
    void testGetSuggestion_HighFatigueFootball_ReturnsAbsoluteRest() { assertEquals(RecoverySuggestion.ABSOLUTE_REST, recoverySuggester.getSuggestion(FatigueLevel.HIGH, SportType.FOOTBALL)); }
    @Test
    void testGetSuggestion_MediumFatigueGym_ReturnsLightActivity() { assertEquals(RecoverySuggestion.LIGHT_ACTIVITY, recoverySuggester.getSuggestion(FatigueLevel.MEDIUM, SportType.GYM)); }
    @Test
    void testGetSuggestion_MediumFatigueFootball_ReturnsActiveRecovery() { assertEquals(RecoverySuggestion.ACTIVE_RECOVERY, recoverySuggester.getSuggestion(FatigueLevel.MEDIUM, SportType.FOOTBALL)); }
    @Test
    void testGetSuggestion_LowFatigue_ReturnsIncreaseIntensity() { assertEquals(RecoverySuggestion.INCREASE_INTENSITY, recoverySuggester.getSuggestion(FatigueLevel.LOW, SportType.GYM)); }
    @Test
    void testGetSuggestion_NullFatigue_ThrowsException() { assertThrows(IllegalArgumentException.class, () -> recoverySuggester.getSuggestion(null, SportType.GYM)); }
    @Test
    void testGetSuggestion_NullSportType_ThrowsException() { assertThrows(IllegalArgumentException.class, () -> recoverySuggester.getSuggestion(FatigueLevel.LOW, null)); }
    @Test
    void testAdjustForConsecutiveLow_ThreeConsecutiveLow_ReturnsIncreaseIntensity() { assertEquals(RecoverySuggestion.INCREASE_INTENSITY, recoverySuggester.adjustForConsecutiveLow(FatigueLevel.LOW, 3)); }
}