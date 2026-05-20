# Ubiquitous Language

> **Code is the source of truth** - All terms in this document match the actual code in the domain layer.

## Quick Reference

| Category | Terms |
|----------|-------|
| **Entities** | Athlete, TrainingSession, FatigueMetrics |
| **Value Objects** | Intensity, SportType, FatigueLevel, SessionId, Routine, RecoverySuggestion |
| **Domain Services** | FatigueCalculationService, RoutineRecommendationService, RecoverySuggestionService |
| **Policies** | FatigueRules |
| **Ports** | AthleteRepository, TrainingSessionRepository, RoutineRepository, FatigueMetricsRepository |

---

## Domain Terms

### 1. Athlete
**Definition**: Person who performs physical training. Each athlete has a sport profile defining their discipline.

**Attributes**:
- `id`: Unique identifier (UUID)
- `name`: Athlete's name
- `sportType`: Sport type (GYM or FOOTBALL)
- `birthDate`: Birth date

**Code Example**:
```java
// Entity in domain/model/entities/Athlete.java
public class Athlete {
    private UUID id;
    private String name;
    private SportType sportType;
    private LocalDate birthDate;
    
    // Factory method
    public static Athlete create(String name, SportType sportType, LocalDate birthDate) {
        return new Athlete(UUID.randomUUID(), name, sportType, birthDate);
    }
}
```

---

### 2. TrainingSession
**Definition**: Event that records a physical activity performed by an athlete.

**Attributes**:
- `sessionId`: Unique session identifier (Value Object)
- `athleteId`: Athlete ID reference
- `sessionDate`: Date and time of session
- `durationMinutes`: Duration in minutes
- `intensity`: Training intensity
- `caloriesBurned`: Calories burned (optional)
- `fatigueLevel`: Calculated fatigue level
- `recommendedRoutine`: Recommended routine
- `recoverySuggestion`: Recovery suggestion

**Code Example**:
```java
// Entity in domain/model/entities/TrainingSession.java
public class TrainingSession {
    private SessionId sessionId;
    private UUID athleteId;
    private LocalDateTime sessionDate;
    private Integer durationMinutes;
    private Intensity intensity;
    private Integer caloriesBurned;
    private FatigueLevel fatigueLevel;
    
    public static TrainingSession create(UUID athleteId, LocalDateTime sessionDate, 
                                         Integer durationMinutes, Intensity intensity, Integer caloriesBurned) {
        // Validation logic
        // Factory method
    }
}
```

---

### 3. FatigueLevel
**Definition**: Fatigue level calculated from recent training sessions.

**Values**: 
- `LOW`: 0-14 points
- `MEDIUM`: 15-29 points  
- `HIGH`: 30+ points

**Calculation Formula**: `(durationMinutes / 10) × intensityMultiplier`

**Code Example**:
```java
// Value Object in domain/model/valueobjects/FatigueLevel.java
public enum FatigueLevel {
    LOW(0, 14),
    MEDIUM(15, 29),
    HIGH(30, Integer.MAX_VALUE);
    
    private final int minPoints;
    private final int maxPoints;
    
    public static FatigueLevel fromPoints(int points) {
        for (FatigueLevel level : values()) {
            if (points >= level.minPoints && points <= level.maxPoints) {
                return level;
            }
        }
        return HIGH;
    }
}
```

---

### 4. Intensity
**Definition**: Effort level of a training session.

**Values and Multipliers**:
- `LIGHT` (Ligera): multiplier 1x
- `MODERATE` (Moderada): multiplier 2x
- `HIGH` (Alta): multiplier 3x
- `EXTREME` (Extrema): multiplier 4x

**Code Example**:
```java
// Value Object in domain/model/valueobjects/Intensity.java
public enum Intensity {
    LIGHT("Ligera", 1),
    MODERATE("Moderada", 2),
    HIGH("Alta", 3),
    EXTREME("Extrema", 4);
    
    private final String description;
    private final int fatigueMultiplier;
    
    public int getFatigueMultiplier() {
        return fatigueMultiplier;
    }
}
```

---

### 5. Routine
**Definition**: Training plan recommended based on current fatigue level.

**Attributes**:
- `id`: Unique identifier
- `athleteId`: Athlete ID reference
- `name`: Routine name
- `description`: Description
- `recommendedDurationMinutes`: Recommended duration
- `recommendedIntensity`: Recommended intensity
- `recoverySuggestion`: Recovery suggestion

**Code Example**:
```java
// Entity in domain/model/entities/Routine.java
public class Routine {
    private UUID id;
    private UUID athleteId;
    private String name;
    private String description;
    private Integer recommendedDurationMinutes;
    private Intensity recommendedIntensity;
    private RecoverySuggestion recoverySuggestion;
    
    public static Routine create(UUID athleteId, String name, String description,
                                 Integer duration, Intensity intensity, RecoverySuggestion recovery) {
        return new Routine(UUID.randomUUID(), athleteId, name, description, 
                          duration, intensity, recovery);
    }
}
```

---

### 6. RecoverySuggestion
**Definition**: Rest or activity recommendation based on fatigue level and sport type.

**Values**:
- `ABSOLUTE_REST`: Complete rest (high fatigue)
- `LIGHT_ACTIVITY`: Light activity (gym, medium fatigue)
- `ACTIVE_RECOVERY`: Active recovery (football, medium fatigue)
- `MODERATE_WORKOUT`: Moderate workout
- `INCREASE_INTENSITY`: Increase intensity (low fatigue)

---

### 7. SportType
**Definition**: Sport discipline practiced by the athlete.

**Values**:
- `GYM`: Gym / Fitness
- `FOOTBALL`: Football

---

### 8. FatigueMetrics
**Definition**: Historical record of athlete's fatigue level at a specific moment.

**Storage**: MongoDB (NoSQL database)

---

### 9. SessionId
**Definition**: Value object that uniquely identifies each training session.

**Type**: UUID wrapper

---

## Domain Services

### 1. FatigueCalculationService
**Responsibility**: Calculate fatigue level based on recent training sessions.

**Code Example**:
```java
// Domain Service in domain/services/FatigueCalculationService.java
public class FatigueCalculationService {
    private final FatigueRules fatigueRules;
    
    public FatigueLevel calculateFatigue(List<TrainingSession> recentSessions, LocalDateTime currentTime) {
        if (recentSessions == null || recentSessions.isEmpty()) {
            return FatigueLevel.LOW;
        }
        
        int totalFatiguePoints = 0;
        
        for (TrainingSession session : recentSessions) {
            if (isWithinRecoveryWindow(session.getSessionDate(), currentTime)) {
                totalFatiguePoints += calculateSessionFatiguePoints(session);
            }
        }
        
        return determineFatigueLevel(totalFatiguePoints);
    }
    
    private int calculateSessionFatiguePoints(TrainingSession session) {
        int basePoints = session.getDurationMinutes() / 10;
        int intensityMultiplier = session.getIntensity().getFatigueMultiplier();
        return basePoints * intensityMultiplier;
    }
}
```

### 2. RoutineRecommendationService
**Responsibility**: Recommend a training routine based on fatigue level and sport type.

**Code Example**:
```java
// Domain Service in domain/services/RoutineRecommendationService.java
public class Routine recommendRoutine(UUID athleteId, FatigueLevel fatigueLevel, SportType sportType) {
    return switch (fatigueLevel) {
        case HIGH -> createHighFatigueRoutine(athleteId, sportType);
        case MEDIUM -> createMediumFatigueRoutine(athleteId, sportType);
        case LOW -> createLowFatigueRoutine(athleteId, sportType);
    };
}

private Routine createMediumFatigueRoutine(UUID athleteId, SportType sportType) {
    String name = sportType == SportType.GYM 
        ? "Rutina de Mantenimiento Gym" 
        : "Rutina de Mantenimiento Fútbol";
    // ...
}
```

### 3. RecoverySuggestionService
**Responsibility**: Suggest recovery actions based on fatigue level.

**Code Example**:
```java
public RecoverySuggestion getSuggestion(FatigueLevel fatigueLevel, SportType sportType) {
    return switch (fatigueLevel) {
        case HIGH -> RecoverySuggestion.ABSOLUTE_REST;
        case MEDIUM -> sportType == SportType.GYM 
            ? RecoverySuggestion.LIGHT_ACTIVITY 
            : RecoverySuggestion.ACTIVE_RECOVERY;
        case LOW -> RecoverySuggestion.INCREASE_INTENSITY;
    };
}
```

---

## Domain Rules

### Recovery Window
Sessions within the last 72 hours contribute to fatigue calculation.

### Threshold Definition
- LOW: 0-14 points
- MEDIUM: 15-29 points
- HIGH: 30+ points

---

## Architecture Mapping

```
domain/
├── model/entities/         → Entities (Athlete, TrainingSession, Routine)
├── model/valueobjects/   → Value Objects (Intensity, SportType, FatigueLevel)
├── model/enums/          → Enums (RecoverySuggestion)
├── services/             → Domain Services (FatigueCalculationService, etc.)
├── policies/             → Policies (FatigueRules)
└── ports/out/            → Interfaces (AthleteRepository, etc.)

application/              → Use Cases (RegisterTrainingSessionUseCase, etc.)
infrastructure/          → Adapters (REST Controllers, JPA Repositories)
```

---

## Complete Domain Summary

### Entities (3)
| Entity | Type | File Location | Purpose |
|--------|------|---------------|---------|
| **Athlete** | Aggregate Root | domain/model/entities/Athlete.java | Person who trains |
| **TrainingSession** | Aggregate Root | domain/model/entities/TrainingSession.java | Training event with fatigue |
| **FatigueMetrics** | Entity | domain/model/entities/FatigueMetrics.java | Historical fatigue (MongoDB) |

### Value Objects (6)
| VO | Type | File Location | Values |
|----|------|---------------|--------|
| **Intensity** | Enum | domain/model/valueobjects/Intensity.java | LIGHT(1x), MODERATE(2x), HIGH(3x), EXTREME(4x) |
| **SportType** | Enum | domain/model/valueobjects/SportType.java | GYM, FOOTBALL |
| **FatigueLevel** | Enum | domain/model/valueobjects/FatigueLevel.java | LOW(0-14), MEDIUM(15-29), HIGH(30+) |
| **SessionId** | Value Object | domain/model/valueobjects/SessionId.java | UUID wrapper |
| **Routine** | Value Object | domain/model/valueobjects/Routine.java | Recommended training plan |
| **RecoverySuggestion** | Enum | domain/model/enums/RecoverySuggestion.java | ABSOLUTE_REST, LIGHT_ACTIVITY, ACTIVE_RECOVERY, INCREASE_INTENSITY |

### Domain Services (3)
| Service | File Location | Responsibility |
|---------|---------------|----------------|
| **FatigueCalculationService** | domain/services/FatigueCalculationService.java | Calculate fatigue from sessions |
| **RoutineRecommendationService** | domain/services/RoutineRecommendationService.java | Recommend routine based on fatigue |
| **RecoverySuggestionService** | domain/services/RecoverySuggestionService.java | Suggest recovery action |

### Policies (1)
| Policy | File Location | Purpose |
|--------|---------------|---------|
| **FatigueRules** | domain/policies/FatigueRules.java | Fatigue thresholds and recovery window |

### Ports (4)
| Port | File Location | Implementation |
|------|---------------|-----------------|
| **AthleteRepository** | domain/ports/out/AthleteRepository.java | infrastructure/persistence/postgres |
| **TrainingSessionRepository** | domain/ports/out/TrainingSessionRepository.java | infrastructure/persistence/postgres |
| **RoutineRepository** | domain/ports/out/RoutineRepository.java | infrastructure/persistence/postgres |
| **FatigueMetricsRepository** | domain/ports/out/FatigueMetricsRepository.java | infrastructure/persistence/mongo |