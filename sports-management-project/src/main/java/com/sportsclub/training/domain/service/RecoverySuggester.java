package com.sportsclub.training.domain.service;

import com.sportsclub.training.domain.model.enums.RecoverySuggestion;
import com.sportsclub.training.domain.model.valueobject.SportType;
import com.sportsclub.shared.domain.model.FatigueLevel;

public class RecoverySuggester {
    public RecoverySuggestion getSuggestion(FatigueLevel fatigueLevel, SportType sportType) {
        if (fatigueLevel == null || sportType == null)
            throw new IllegalArgumentException("Fatigue level and sport type cannot be null");
        return switch (fatigueLevel) {
            case HIGH -> RecoverySuggestion.ABSOLUTE_REST;
            case MEDIUM ->
                sportType == SportType.GYM ? RecoverySuggestion.LIGHT_ACTIVITY : RecoverySuggestion.ACTIVE_RECOVERY;
            case LOW -> RecoverySuggestion.INCREASE_INTENSITY;
        };
    }

    public RecoverySuggestion adjustForConsecutiveLow(FatigueLevel currentFatigue, int consecutiveLowDays,
            SportType sportType) {
        if (consecutiveLowDays >= 3 && currentFatigue == FatigueLevel.LOW) {
            return RecoverySuggestion.INCREASE_INTENSITY;
        }
        return getSuggestion(currentFatigue, sportType);
    }
}