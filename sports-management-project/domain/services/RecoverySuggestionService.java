package domain.services;

import domain.model.enums.RecoverySuggestion;
import domain.model.valueobjects.FatigueLevel;
import domain.model.valueobjects.SportType;

public class RecoverySuggestionService {

    public RecoverySuggestion getSuggestion(FatigueLevel fatigueLevel, SportType sportType) {
        if (fatigueLevel == null || sportType == null) {
            throw new IllegalArgumentException("Fatigue level and sport type cannot be null");
        }

        return switch (fatigueLevel) {
            case HIGH -> getHighFatigueSuggestion(sportType);
            case MEDIUM -> getMediumFatigueSuggestion(sportType);
            case LOW -> RecoverySuggestion.INCREASE_INTENSITY;
        };
    }

    private RecoverySuggestion getHighFatigueSuggestion(SportType sportType) {
        return switch (sportType) {
            case GYM -> RecoverySuggestion.ABSOLUTE_REST;
            case FOOTBALL -> RecoverySuggestion.ABSOLUTE_REST;
        };
    }

    private RecoverySuggestion getMediumFatigueSuggestion(SportType sportType) {
        return switch (sportType) {
            case GYM -> RecoverySuggestion.LIGHT_ACTIVITY;
            case FOOTBALL -> RecoverySuggestion.ACTIVE_RECOVERY;
        };
    }

    public RecoverySuggestion adjustForConsecutiveLow(FatigueLevel currentFatigue, int consecutiveLowDays) {
        if (consecutiveLowDays >= 3 && currentFatigue == FatigueLevel.LOW) {
            return RecoverySuggestion.INCREASE_INTENSITY;
        }
        return getSuggestion(currentFatigue, null);
    }
}