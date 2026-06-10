package com.sportsclub.training.domain.model.valueobject;

public enum FatigueLevel {
    LOW(1, "Baja"), MEDIUM(2, "Media"), HIGH(3, "Alta");

    private final int value;
    private final String description;

    FatigueLevel(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public boolean isHigherThan(FatigueLevel other) {
        return this.value > other.value;
    }

    public boolean isLowerThan(FatigueLevel other) {
        return this.value < other.value;
    }

    public RecoverySuggestion getRecoverySuggestion(SportType sportType) {
        return switch (this) {
            case HIGH -> RecoverySuggestion.ABSOLUTE_REST;
            case MEDIUM ->
                sportType == SportType.GYM ? RecoverySuggestion.LIGHT_ACTIVITY
                        : RecoverySuggestion.ACTIVE_RECOVERY;
            case LOW -> RecoverySuggestion.INCREASE_INTENSITY;
        };
    }

    public static FatigueLevel fromValue(int value) {
        if (value <= LOW.value) return LOW;
        if (value >= HIGH.value) return HIGH;
        return MEDIUM;
    }
}
