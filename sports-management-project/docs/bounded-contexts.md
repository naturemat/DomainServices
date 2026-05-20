# Bounded Contexts

## Overview

This project is divided into two bounded contexts following DDD principles:

1. **Training Context** - Operational domain
2. **Performance Context** - Analytical domain

---

## Training Context

**Responsibilities:**
- Registration and management of athletes
- Registration and management of training sessions
- Fatigue calculation based on recent sessions
- Recommendation of personalized routines
- Generation of recovery suggestions

**Aggregates:**
- **Athlete** (Aggregate Root): Entity managing athlete data
- **TrainingSession** (Aggregate Root): Entity managing session data with embedded Routine

**Repositories (Ports - Outbound):**
- `AthleteRepository` → PostgreSQL
- `TrainingSessionRepository` → PostgreSQL
- `RoutineRepository` → PostgreSQL

**Domain Services:**
- `FatigueCalculationService`: Calculates fatigue level using formula `(duration/10) × intensityMultiplier`
- `RoutineRecommendationService`: Recommends routines based on fatigue + sport type
- `RecoverySuggestionService`: Suggests recovery actions based on fatigue level

**Technology:**
- PostgreSQL for transactional data (athletes, sessions, routines)
- MongoDB for fatigue metrics (via Training Context)

**Boundary:** Contains all business logic for training operations

---

## Performance Context

**Responsibilities:**
- Historical storage of fatigue metrics per athlete
- Trend analysis and performance insights
- Time-series data for fatigue progression

**Aggregates:**
- **FatigueMetrics** (Aggregate Root): Historical record of fatigue calculations

**Repositories (Ports - Outbound):**
- `FatigueMetricsRepository` → MongoDB

**Technology:**
- MongoDB for flexible schema (time-series-like documents)

**Purpose:**
Clear separation between:
- Operational data (Training Context - athletes, sessions, routines)
- Analytical data (Performance Context - metrics, trends)

**Benefits:**
- Independent scalability of operational vs analytical workloads
- Query optimization by data type
- Flexibility in metrics schema evolution
- No cross-context transactions (eventual consistency)

---

## Context Integration

### Integration Pattern: Domain Service Orchestration

The Application Layer orchestrates both contexts without direct knowledge between them.

### Training → Performance Flow
When a training session is registered:

```
1. POST /api/v1/training/sessions (REST)
         ↓
2. RegisterTrainingSessionUseCase (Application)
         ↓
3. TrainingSession persisted in PostgreSQL (Training Context)
         ↓
4. FatigueCalculationService calculates fatigue
         ↓
5. FatigueMetrics stored in MongoDB (Performance Context)
```

### Performance → Training Flow
When generating a routine:

```
1. GET /api/v1/training/routines/{athleteId} (REST)
         ↓
2. GenerateRoutineUseCase (Application)
         ↓
3. FatigueMetricsRepository queries MongoDB (Performance Context)
         ↓
4. FatigueCalculationService + RoutineRecommendationService (Training Context)
         ↓
5. Routine returned with recovery suggestions
```

### Coupling Strategy
- **Low Coupling**: Contexts communicate via Application Layer
- **Eventual Consistency**: MongoDB writes happen after PostgreSQL commits
- **No Shared Database**: Each context owns its data store

---

## Mapping to Ubiquitous Language Terms

| Bounded Context | Entities | Value Objects | Services |
|----------------|----------|---------------|----------|
| **Training** | Athlete, TrainingSession, Routine | Intensity, SportType, SessionId | FatigueCalculationService, RoutineRecommendationService, RecoverySuggestionService |
| **Performance** | FatigueMetrics | FatigueLevel | (Read-only queries via repositories) |

## Use Case Examples

### Training Context: Register Session
```
Scenario: Athlete registers a training session (60 min, HIGH intensity, GYM)

1. Create TrainingSession with duration=60, intensity=HIGH
2. Calculate fatigue: (60/10) × 3 = 18 points → MEDIUM (15-29)
3. Recommend routine: MEDIUM + GYM → "Gym Maintenance Routine"
4. Suggest recovery: MEDIUM + GYM → LIGHT_ACTIVITY
5. Persist to PostgreSQL (Training Context)
6. Store fatigue metrics in MongoDB (Performance Context)
```

### Training Context: Generate Routine
```
Scenario: Generate routine for athlete with accumulated fatigue

1. Query recent sessions within 72-hour window (Training)
2. Calculate total fatigue points (Training)
3. Determine FatigueLevel: LOW/MEDIUM/HIGH (Training)
4. Query historical FatigueMetrics (Performance)
5. Recommend routine based on fatigue + sport type (Training)
6. Return Routine with RecoverySuggestion (Training)
```

### Performance Context: View Fatigue Trends
```
Scenario: Analytics - View athlete's fatigue history

1. Query last 30 days from MongoDB (Performance)
2. Aggregate daily fatigue averages
3. Calculate trend (improving/declining/stable)
4. No direct knowledge of Training Context entities
```

---

## Technology Stack per Context

| Context | Database | Purpose |
|---------|----------|---------|
| Training | PostgreSQL | Transactional data, ACID compliance |
| Performance | MongoDB | Time-series metrics, flexible schema |