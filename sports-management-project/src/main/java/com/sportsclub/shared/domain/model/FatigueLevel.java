package com.sportsclub.shared.domain.model;

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

    public static FatigueLevel fromValue(int value) {
        for (FatigueLevel level : values()) {
            if (level.value == value)
                return level;
        }
        return MEDIUM;
    }
}