# Bounded Contexts

## Training Context (Contexto de Entrenamiento)

### Responsabilidades
- Registro y gestión de sesiones de entrenamiento
- Cálculo de fatiga basada en sesiones recientes
- Recomendación de rutinas personalizadas
- Generación de sugerencias de recuperación

### Entidades Principales
- **Athlete**: Atleta registrado en el sistema
- **TrainingSession**: Sesión de entrenamiento realizada
- **Routine**: Rutina recomendada basada en fatiga

### Repositorios
- `AthleteRepository` (PostgreSQL)
- `TrainingSessionRepository` (PostgreSQL)
- `RoutineRepository` (PostgreSQL)

### Servicios de Dominio
- `FatigueCalculationService`: Calcula nivel de fatiga
- `RoutineRecommendationService`: Recomienda rutinas
- `RecoverySuggestionService`: Sugiere recuperación

### Tecnología
- PostgreSQL para datos transaccionales
- MongoDB para métricas de fatiga

---

## Performance Context (Contexto de Rendimiento)

### Responsabilidades
- Almacenamiento histórico de métricas de fatiga
- Análisis de tendencias de rendimiento
- Persistencia de recomendaciones generadas

### Entidades
- **FatigueMetrics**: Métricas de fatiga almacenadas

### Repositorios
- `FatigueMetricsRepository` (MongoDB)

### Propósito
Separación clara entre datos operacionales (entrenamiento) y datos analíticos (métricas), permitiendo:
- Escalabilidad independiente
- Optimización de consultas por tipo de dato
- Flexibilidad en el esquema de métricas

---

## Integración entre Contextos

### Training → Performance
Cuando se registra una sesión de entrenamiento:
1. Se persiste la sesión en PostgreSQL (Training Context)
2. Se calcula la fatiga y se guarda en MongoDB (Performance Context)

### Performance → Training
Al generar una rutina:
1. Se consultan las últimas métricas de fatiga (Performance Context)
2. Se genera la rutina basada en el historial (Training Context)

### Comunicación
La comunicación es via Domain Services en la capa de aplicación, que orquesta ambos contextos sin que ellos se conozcan directamente (principio de baja acoplamiento).