# Ubiquitous Language

> **Code is the source of truth** - All terms in this document match the actual code in the domain layer.

## Contexts

| Context | Package | Purpose |
|---------|---------|---------|
| **Training** | `com.sportsclub.training.domain` | Operational domain (athletes, sessions, routines, fatigue calculation) |
| **Performance** | `com.sportsclub.performance.domain` | Common value objects (FatigueLevel) and exceptions |
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

---

## Entities (Training Context)

| Entity | Key Field | Purpose |
|--------|-----------|---------|
| **Athlete** | `id: UUID` | Person who performs training |
| **TrainingSession** | `sessionId: SessionId` | Completed training event |
| **Routine** | `id: UUID` | Recommended training plan |
| **SportProfile** | `athleteId: UUID` | Athlete's sport-specific state |
| **AthleteProfile** | `athleteId: UUID` | Athlete's metadata (age, weekly load) |

---

## Value Objects

| VO | Type | Values / Behavior |
|----|------|-------------------|
| **SessionId** | `record` | Wraps UUID for type safety |
| **Intensity** | `enum` | LIGHT, MODERATE, HIGH, EXTREME. Has `getFatigueMultiplier()` and `calculateCalories()` |
| **SportType** | `enum` | GYM ("Gimnasio"), FOOTBALL ("Fútbol") |
| **FatigueLevel** | `enum` (Shared) | LOW(1,"Baja"), MEDIUM(2,"Media"), HIGH(3,"Alta"). Has `isHigherThan()`, `isLowerThan()` |

---

## Domain Services

| Service | Input | Output | Responsibility |
|---------|-------|--------|----------------|
| **FatigueCalculator** | `List<TrainingSession>`, `LocalDateTime` | `FatigueLevel` | Calculate fatigue from recent sessions |
| **RecoverySuggester** | `FatigueLevel`, `SportType` | `RecoverySuggestion` | Suggest recovery based on fatigue and sport |
| **RoutineRecommender** | `UUID athleteId`, `SportType` | `Routine` | Recommend routine considering fatigue, age, competition, weekly load |

---

## Policies

| Policy | Configurable Values | Business Rules |
|--------|--------------------|----------------|
| **FatigueConfiguration** | recoveryWindowHours(72), highThreshold(30), mediumThreshold(15), restDayReduction(1) | `isHighFatigue()`, `classifyFatigue()`, `needsAbsoluteRest()` |

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

| Rule | Formula / Threshold |
|------|---------------------|
| **Recovery Window** | Only sessions within last 72 hours affect fatigue |
| **Fatigue Points** | `(durationMinutes / 10) × intensityMultiplier` |
| **Fatigue Classification** | ≥30 = HIGH, 15-29 = MEDIUM, <15 = LOW |
| **Calories Formula** | `durationMinutes × 5.0 × intensityMultiplier` (LIGHT=1.0, MODERATE=1.5, HIGH=2.0, EXTREME=2.5) |
| **Absolute Rest** | `fatiguePoints ≥ 30 AND sessionsThisWeek ≥ 5` |

### Calculation Formula
`(durationMinutes / 10) × intensityMultiplier`

---

## Exceptions (Shared)

| Exception | Extends | Use Case |
|-----------|---------|----------|
| **DomainException** | `RuntimeException` (abstract) | Base for all domain exceptions |
| **AthleteNotFoundException** | `DomainException` | Athlete not found in repository |
| **InvalidSearchParameterException** | `DomainException` | Invalid search input |

---

## Complete Domain Summary

### Training Context Entities (5)
| Entity | Type | File Location |
|--------|------|---------------|
| **Athlete** | Aggregate Root | `training/domain/model/entity/Athlete.java` |
| **TrainingSession** | Aggregate Root | `training/domain/model/entity/TrainingSession.java` |
| **Routine** | Aggregate Root | `training/domain/model/entity/Routine.java` |
| **SportProfile** | Entity | `training/domain/model/entity/SportProfile.java` |
| **AthleteProfile** | Entity | `training/domain/model/entity/AthleteProfile.java` |

### Performance Context Entities (1)
| Entity | Type | File Location |
|--------|------|---------------|
| **FatigueMetrics** | Aggregate Root | `performance/domain/model/entity/FatigueMetrics.java` |

### Value Objects (4)
| VO | Context | File Location | Values |
|----|---------|---------------|--------|
| **SessionId** | Training | `training/domain/model/valueobject/SessionId.java` | UUID wrapper |
| **Intensity** | Training | `training/domain/model/valueobject/Intensity.java` | LIGHT, MODERATE, HIGH, EXTREME |
| **SportType** | Training | `training/domain/model/valueobject/SportType.java` | GYM, FOOTBALL |
| **FatigueLevel** | Shared | `shared/domain/model/FatigueLevel.java` | LOW, MEDIUM, HIGH |

### Enums (1)
| Enum | Context | File Location | Values |
|------|---------|---------------|--------|
| **RecoverySuggestion** | Training | `training/domain/model/enums/RecoverySuggestion.java` | ABSOLUTE_REST, LIGHT_ACTIVITY, ACTIVE_RECOVERY, MODERATE_WORKOUT, INCREASE_INTENSITY |

### Domain Services (3)
| Service | Context | File Location | Responsibility |
|---------|---------|---------------|----------------|
| **FatigueCalculator** | Training | `training/domain/service/FatigueCalculator.java` | Calculate fatigue from recent sessions |
| **RecoverySuggester** | Training | `training/domain/service/RecoverySuggester.java` | Suggest recovery based on fatigue and sport |
| **RoutineRecommender** | Training | `training/domain/service/RoutineRecommender.java` | Recommend routine considering fatigue, age, competition, weekly load |

### Policies (1)
| Policy | Context | File Location | Purpose |
|--------|---------|---------------|---------|
| **FatigueConfiguration** | Training | `training/domain/policy/FatigueConfiguration.java` | Fatigue thresholds, classification, and business rules |
