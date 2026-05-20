package com.sportsclub.training.domain.model.enums;

public enum RecoverySuggestion {
    ABSOLUTE_REST("Descanso absoluto - No entrenar"),
    LIGHT_ACTIVITY("Actividad ligera - Caminata, estiramientos"),
    ACTIVE_RECOVERY("Recuperación activa - Ejercicios suaves"),
    MODERATE_WORKOUT("Entrenamiento moderado"),
    INCREASE_INTENSITY("Incrementar intensidad - El atleta está recuperado");

    private final String description;
    RecoverySuggestion(String description) { this.description = description; }
    public String getDescription() { return description; }
}