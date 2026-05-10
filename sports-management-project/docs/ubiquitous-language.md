# Lenguaje Ubicuo

## Términos del Dominio

### 1. Atleta (Athlete)
Representa a la persona que realiza entrenamiento físico. Cada atleta tiene un perfil deportivo que define su disciplina (GYM o FOOTBALL).

### 2. Sesión de Entrenamiento (TrainingSession)
Evento que registra una actividad física realizada por un atleta. Incluye fecha, duración, intensidad y calorías quemadas.

### 3. Fatiga (FatigueLevel)
Nivel de agotamiento del atleta calculado a partir de las sesiones recientes. Puede ser: LOW (Baja), MEDIUM (Media), HIGH (Alta).

### 4. Intensidad (Intensity)
Nivel de esfuerzo de una sesión de entrenamiento: LIGHT (Ligera), MODERATE (Moderada), HIGH (Alta), EXTREME (Extrema).

### 5. Rutina (Routine)
Plan de entrenamiento recomendado basado en el nivel de fatiga actual del atleta. Incluye duración sugerida, intensidad recomendada y sugerencia de recuperación.

### 6. Recuperación (RecoverySuggestion)
Recomendación de descanso o actividad basada en el nivel de fatiga y tipo de deporte: ABSOLUTE_REST, LIGHT_ACTIVITY, ACTIVE_RECOVERY, MODERATE_WORKOUT, INCREASE_INTENSITY.

### 7. Perfil Deportivo (SportProfile)
Información contextual del atleta que incluye tipo de deporte, nivel de fatiga actual y estadísticas semanales (sesiones y minutos).

### 8. Tipo de Deporte (SportType)
Disciplina que practica el atleta: GYM (Gimnasio) o FOOTBALL (Fútbol).

### 9. Métricas de Fatiga (FatigueMetrics)
Registro histórico del nivel de fatiga de un atleta en un momento específico, almacenado en MongoDB.

### 10. Identificador de Sesión (SessionId)
Value object que identifica de forma única cada sesión de entrenamiento.