package com.sportsclub.training.domain.model.valueobject;

public enum RecoverySuggestion {
    ABSOLUTE_REST("Descanso absoluto - No entrenar"),
    LIGHT_ACTIVITY("Actividad ligera - Caminata, estiramientos"),
    ACTIVE_RECOVERY("Recuperación activa - Ejercicios suaves"),
    MODERATE_WORKOUT("Entrenamiento moderado"),
    INCREASE_INTENSITY("Incrementar intensidad - El atleta está recuperado");

    private final String description;

    RecoverySuggestion(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Intensity getRecommendedIntensity() {
        return switch (this) {
            case ABSOLUTE_REST, LIGHT_ACTIVITY, ACTIVE_RECOVERY -> Intensity.LIGHT;
            case MODERATE_WORKOUT -> Intensity.MODERATE;
            case INCREASE_INTENSITY -> Intensity.HIGH;
        };
    }

    public int getBaseDurationMinutes() {
        return switch (this) {
            case ABSOLUTE_REST -> 0;
            case LIGHT_ACTIVITY -> 25;
            case ACTIVE_RECOVERY -> 30;
            case MODERATE_WORKOUT -> 45;
            case INCREASE_INTENSITY -> 60;
        };
    }

    public String buildRoutineName(SportType sportType) {
        return switch (this) {
            case ABSOLUTE_REST -> "Descanso - " + sportType.getDisplayName();
            case LIGHT_ACTIVITY -> "Recuperación Ligera - " + sportType.getDisplayName();
            case ACTIVE_RECOVERY -> "Recuperación Activa - " + sportType.getDisplayName();
            case MODERATE_WORKOUT -> "Mantenimiento - " + sportType.getDisplayName();
            case INCREASE_INTENSITY -> "Progresión - " + sportType.getDisplayName();
        };
    }

    public String buildDescription(FatigueLevel fatigue) {
        return switch (this) {
            case ABSOLUTE_REST -> "El atleta necesita descanso. Fatiga " + fatigue.getDescription().toLowerCase();
            case LIGHT_ACTIVITY -> "Actividad ligera para promover recuperación activa";
            case ACTIVE_RECOVERY -> "Ejercicios de recuperación para reducir fatiga muscular";
            case MODERATE_WORKOUT -> "Entrenamiento de mantenimiento con carga moderada";
            case INCREASE_INTENSITY -> "El atleta está recuperado, aumentar intensidad progresivamente";
        };
    }
}
