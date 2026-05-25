package com.sportsclub.training.domain.model.valueobject;

public enum Intensity {
    LIGHT("Ligera"), MODERATE("Moderada"), HIGH("Alta"), EXTREME("Extrema");

    private final String description;

    Intensity(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getFatigueMultiplier() {
        return switch (this) {
            case LIGHT -> 1;
            case MODERATE -> 2;
            case HIGH -> 3;
            case EXTREME -> 4;
        };
    }

    public int calculateCalories(int durationMinutes) {
        double baseCalories = durationMinutes * 5.0;
        double multiplier = switch (this) {
            case LIGHT -> 1.0;
            case MODERATE -> 1.5;
            case HIGH -> 2.0;
            case EXTREME -> 2.5;
        };
        return (int) Math.round(baseCalories * multiplier);
    }
}