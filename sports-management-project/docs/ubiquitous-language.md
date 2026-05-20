# Ubiquitous Language

> **Code is the source of truth** - All terms in this document match the actual code in the domain layer.

## Contexts

| Context | Package | Purpose |
|---------|---------|---------|
| **Training** | `com.sportsclub.training.domain` | Operational domain (athletes, sessions, routines) |
| **Performance** | `com.sportsclub.performance.domain` | Analytical domain (fatigue metrics) |
| **Shared** | `com.sportsclub.shared.domain.model` | Common value objects (FatigueLevel) |

## Quick Reference

| Category | Terms | Context |
|----------|-------|---------|
| **Entities** | Athlete, TrainingSession, Routine, SportProfile | Training |
| **Entities** | FatigueMetrics | Performance |
| **Value Objects** | SessionId, Intensity, SportType | Training |
| **Value Objects** | FatigueLevel | Shared |
| **Enums** | RecoverySuggestion | Training |
| **Domain Services** | FatigueCalculator, RoutineRecommender, RecoverySuggester | Training |
| **Policies** | FatigueRules | Training |
| **Ports** | AthleteRepository, TrainingSessionRepository, RoutineRepository | Training |
| **Ports** | FatigueMetricsRepository | Performance |

---

## Domain Terms

### 1. Athlete
**Context**: Training (`com.sportsclub.training.domain.model.entity.Athlete`)
**Definition**: Person who performs physical training. Each athlete has a sport profile defining their discipline.

**Attributes**:
- `id`: Unique identifier (UUID)
- `name`: Athlete's name
- `sportType`: Sport type (GYM or FOOTBALL)
- `birthDate`: Birth date
- `createdAt`: Creation timestamp

**Code Example**:
```java
// src/main/java/com/sportsclub/training/domain/model/entity/Athlete.java
public class Athlete {
    private final UUID id;
    private final String name;
    private final SportType sportType;
    private final LocalDate birthDate;
    private final LocalDate createdAt;
}
```

---

### 2. TrainingSession
**Context**: Training (`com.sportsclub.training.domain.model.entity.TrainingSession`)
**Definition**: Event that records a physical activity performed by an athlete.

**Attributes**:
- `sessionId`: Unique session identifier (Value Object - SessionId)
- `athleteId`: Athlete ID reference
- `sessionDate`: Date and time of session
- `durationMinutes`: Duration in minutes
- `intensity`: Training intensity
- `caloriesBurned`: Calories burned
- `createdAt`: Creation timestamp

**Code Example**:
```java
// src/main/java/com/sportsclub/training/domain/model/entity/TrainingSession.java
public class TrainingSession {
    private final SessionId sessionId;
    private final UUID athleteId;
    private final LocalDateTime sessionDate;
    private final int durationMinutes;
    private final Intensity intensity;
    private final int caloriesBurned;
    private final LocalDateTime createdAt;
}
```

---

### 3. Routine
**Context**: Training (`com.sportsclub.training.domain.model.entity.Routine`)
**Definition**: Training plan recommended based on current fatigue level. Entity (not value object).

**Attributes**:
- `id`: Unique identifier (UUID)
- `athleteId`: Athlete ID reference
- `name`: Routine name
- `description`: Description
- `recommendedDurationMinutes`: Recommended duration
- `recommendedIntensity`: Recommended intensity
- `recoverySuggestion`: Recovery suggestion
- `createdAt`: Creation timestamp

**Code Example**:
```java
// src/main/java/com/sportsclub/training/domain/model/entity/Routine.java
public class Routine {
    private final UUID id;
    private final UUID athleteId;
    private final String name;
    private final String description;
    private final int recommendedDurationMinutes;
    private final Intensity recommendedIntensity;
    private final RecoverySuggestion recoverySuggestion;
    private final LocalDateTime createdAt;
}
```

---

### 4. SportProfile
**Context**: Training (`com.sportsclub.training.domain.model.entity.SportProfile`)
**Definition**: Athlete's sport-specific profile with current fatigue information.

**Attributes**:
- `id`: Unique identifier (UUID)
- `athleteId`: Athlete ID reference
- `sportType`: Sport type
- `currentFatigueLevel`: Current fatigue level
- `totalSessionsThisWeek`: Sessions this week
- `totalMinutesThisWeek`: Minutes this week

---

### 5. FatigueLevel
**Context**: Shared (`com.sportsclub.shared.domain.model.FatigueLevel`)
**Definition**: Fatigue level calculated from recent training sessions.

**Values**:
- `LOW`: value=1 (Baja)
- `MEDIUM`: value=2 (Media)
- `HIGH`: value=3 (Alta)

**Code Example**:
```java
// src/main/java/com/sportsclub/shared/domain/model/FatigueLevel.java
public enum FatigueLevel {
    LOW(1, "Baja"), MEDIUM(2, "Media"), HIGH(3, "Alta");

    private final int value;
    private final String description;

    public static FatigueLevel fromValue(int value) { ... }
    public boolean isHigherThan(FatigueLevel other) { ... }
    public boolean isLowerThan(FatigueLevel other) { ... }
}
```

---

### 6. SessionId
**Context**: Training (`com.sportsclub.training.domain.model.valueobject.SessionId`)
**Definition**: Value object that wraps UUID to uniquely identify each training session.

**Type**: Java record wrapping UUID

---

### 7. SportType
**Context**: Training (`com.sportsclub.training.domain.model.valueobject.SportType`)
**Definition**: Sport discipline practiced by the athlete.

**Values**:
- `GYM`: "Gimnasio"
- `FOOTBALL`: "Fútbol"

---

### 8. RecoverySuggestion
**Context**: Training (`com.sportsclub.training.domain.model.enums.RecoverySuggestion`)
**Definition**: Rest or activity recommendation based on fatigue level and sport type.

**Values**:
- `ABSOLUTE_REST`: "Descanso absoluto - No entrenar"
- `LIGHT_ACTIVITY`: "Actividad ligera - Caminata, estiramientos"
- `ACTIVE_RECOVERY`: "Recuperación activa - Ejercicios suaves"
- `MODERATE_WORKOUT`: "Entrenamiento moderado"
- `INCREASE_INTENSITY`: "Incrementar intensidad - El atleta está recuperado"

---

### 9. FatigueMetrics
**Context**: Performance (`com.sportsclub.performance.domain.model.entity.FatigueMetrics`)
**Definition**: Historical record of athlete's fatigue level at a specific moment.

**Storage**: MongoDB (NoSQL database)

**Attributes**:
- `id`: Unique identifier (UUID)
- `athleteId`: Athlete ID reference
- `fatigueLevel`: Calculated fatigue level
- `calculatedAt`: Timestamp of calculation

---

## Domain Services

### 1. FatigueCalculator
**Context**: Training (`com.sportsclub.training.domain.service.FatigueCalculator`)
**Location**: `src/main/java/com/sportsclub/training/domain/service/FatigueCalculator.java`
**Responsibility**: Calculate fatigue level based on recent training sessions within the recovery window.

**Methods**:
- `calculateFatigue(List<TrainingSession> recentSessions, LocalDateTime currentTime)`: Returns FatigueLevel
- `applyRestReduction(FatigueLevel currentFatigue, int restDays)`: Applies rest reduction

**Code Example**:
```java
// src/main/java/com/sportsclub/training/domain/service/FatigueCalculator.java
public class FatigueCalculator {
    private final FatigueRules fatigueRules;

    public FatigueLevel calculateFatigue(List<TrainingSession> recentSessions, LocalDateTime currentTime) {
        // Returns FatigueLevel.LOW, MEDIUM, or HIGH based on thresholds
    }
}
```

### 2. RoutineRecommender
**Context**: Training (`com.sportsclub.training.domain.service.RoutineRecommender`)
**Location**: `src/main/java/com/sportsclub/training/domain/service/RoutineRecommender.java`
**Responsibility**: Recommend a training routine based on fatigue level and sport type.

**Code Example**:
```java
// src/main/java/com/sportsclub/training/domain/service/RoutineRecommender.java
public class RoutineRecommender {
    public Routine recommendRoutine(UUID athleteId, FatigueLevel fatigueLevel, SportType sportType) {
        return switch (fatigueLevel) {
            case HIGH -> Routine.create(..., "Rutina de Recuperación", ...);
            case MEDIUM -> Routine.create(..., sportType == SportType.GYM ? "Rutina de Mantenimiento Gym" : "Rutina de Mantenimiento Fútbol", ...);
            case LOW -> Routine.create(..., sportType == SportType.GYM ? "Rutina Intensa Gym" : "Rutina Intensa Fútbol", ...);
        };
    }
}
```

### 3. RecoverySuggester
**Context**: Training (`com.sportsclub.training.domain.service.RecoverySuggester`)
**Location**: `src/main/java/com/sportsclub/training/domain/service/RecoverySuggester.java`
**Responsibility**: Suggest recovery actions based on fatigue level and sport type.

**Code Example**:
```java
// src/main/java/com/sportsclub/training/domain/service/RecoverySuggester.java
public class RecoverySuggester {
    public RecoverySuggestion getSuggestion(FatigueLevel fatigueLevel, SportType sportType) {
        return switch (fatigueLevel) {
            case HIGH -> RecoverySuggestion.ABSOLUTE_REST;
            case MEDIUM -> sportType == SportType.GYM ? RecoverySuggestion.LIGHT_ACTIVITY : RecoverySuggestion.ACTIVE_RECOVERY;
            case LOW -> RecoverySuggestion.INCREASE_INTENSITY;
        };
    }
}
```

---

## Domain Policies

### FatigueRules
**Context**: Training (`com.sportsclub.training.domain.policy.FatigueRules`)
**Location**: `src/main/java/com/sportsclub/training/domain/policy/FatigueRules.java`
**Definition**: Configurable business rules for fatigue calculation.

**Default Values**:
- `recoveryWindowHours`: 72 hours
- `highFatigueThreshold`: 30 points
- `mediumFatigueThreshold`: 15 points
- `restDayReductionRate`: 1 point per day

**Code Example**:
```java
// src/main/java/com/sportsclub/training/domain/policy/FatigueRules.java
public class FatigueRules {
    public FatigueRules() { this(72, 30, 15, 1); } // defaults
    public int getRecoveryWindowHours() { return recoveryWindowHours; }
    public int getHighFatigueThreshold() { return highFatigueThreshold; }
    public int getMediumFatigueThreshold() { return mediumFatigueThreshold; }
    public int getRestDayReductionRate() { return restDayReductionRate; }
}
```

---

## Domain Repositories (Ports)

### Training Context Ports
| Port | Location | Implementation |
|------|----------|----------------|
| **AthleteRepository** | `training.domain.port.out.AthleteRepository` | `PostgresAthleteRepository` |
| **TrainingSessionRepository** | `training.domain.port.out.TrainingSessionRepository` | `PostgresTrainingSessionRepository` |
| **RoutineRepository** | `training.domain.port.out.RoutineRepository` | `PostgresRoutineRepository` |

### Performance Context Ports
| Port | Location | Implementation |
|------|----------|----------------|
| **FatigueMetricsRepository** | `performance.domain.port.out.FatigueMetricsRepository` | `MongoFatigueMetricsRepository` |

---

## Domain Rules

### Recovery Window
Sessions within the last 72 hours contribute to fatigue calculation.

### Threshold Definition
- `LOW`: 0-14 points (value=1)
- `MEDIUM`: 15-29 points (value=2)
- `HIGH`: 30+ points (value=3)

### Calculation Formula
`(durationMinutes / 10) × intensityMultiplier`

---

## Complete Domain Summary

### Training Context Entities (4)
| Entity | Type | File Location |
|--------|------|---------------|
| **Athlete** | Aggregate Root | `training/domain/model/entity/Athlete.java` |
| **TrainingSession** | Aggregate Root | `training/domain/model/entity/TrainingSession.java` |
| **Routine** | Aggregate Root | `training/domain/model/entity/Routine.java` |
| **SportProfile** | Entity | `training/domain/model/entity/SportProfile.java` |

### Performance Context Entities (1)
| Entity | Type | File Location |
|--------|------|---------------|
| **FatigueMetrics** | Aggregate Root | `performance/domain/model/entity/FatigueMetrics.java` |

### Value Objects (4)
| VO | Context | File Location | Values |
|----|---------|---------------|--------|
| **SessionId** | Training | `training/domain/model/valueobject/SessionId.java` | UUID wrapper |
| **Intensity** | Training | `training/domain/model/valueobject/Intensity.java` | LIGHT(1x), MODERATE(2x), HIGH(3x), EXTREME(4x) |
| **SportType** | Training | `training/domain/model/valueobject/SportType.java` | GYM, FOOTBALL |
| **FatigueLevel** | Shared | `shared/domain/model/FatigueLevel.java` | LOW(1), MEDIUM(2), HIGH(3) |

### Enums (1)
| Enum | Context | File Location | Values |
|------|---------|---------------|--------|
| **RecoverySuggestion** | Training | `training/domain/model/enums/RecoverySuggestion.java` | ABSOLUTE_REST, LIGHT_ACTIVITY, ACTIVE_RECOVERY, MODERATE_WORKOUT, INCREASE_INTENSITY |

### Domain Services (3)
| Service | Context | File Location | Responsibility |
|---------|---------|---------------|----------------|
| **FatigueCalculator** | Training | `training/domain/service/FatigueCalculator.java` | Calculate fatigue from sessions |
| **RoutineRecommender** | Training | `training/domain/service/RoutineRecommender.java` | Recommend routine based on fatigue |
| **RecoverySuggester** | Training | `training/domain/service/RecoverySuggester.java` | Suggest recovery action |

### Policies (1)
| Policy | Context | File Location | Purpose |
|--------|---------|---------------|---------|
| **FatigueRules** | Training | `training/domain/policy/FatigueRules.java` | Fatigue thresholds and recovery window |