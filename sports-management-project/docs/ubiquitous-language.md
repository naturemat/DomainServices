# Ubiquitous Language

> **Code is the source of truth** - All terms in this document match the actual code in the domain layer.

## Context

| Context | Package | Purpose |
|---------|---------|---------|
| **Training** | `com.sportsclub.training.domain` | Operational domain (athletes, sessions, routines, fatigue calculation, recovery) |

---

## Quick Reference

| Category | Terms | Location |
|----------|-------|----------|
| **Entities** | Athlete, TrainingSession, Routine | `training/domain/model/entity/` |
| **Value Objects** | SessionId, Intensity, SportType, FatigueLevel, RecoverySuggestion | `training/domain/model/valueobject/` |
| **Domain Services** | FatigueCalculator, RoutineRecommender, RecoverySuggester | `training/domain/service/` |
| **Policies** | FatigueConfiguration | `training/domain/policy/` |
| **Ports** | AthleteRepository, TrainingSessionRepository, RoutineRepository | `training/domain/port/out/` |
| **Exceptions** | DomainException, AthleteNotFoundException, InvalidSearchParameterException | `training/domain/exception/` |

---

## Entities

| Entity | Key Field | Purpose | File |
|--------|-----------|---------|------|
| **Athlete** | `id: UUID` | Person who performs training. Validates name not blank, birthDate in past, sportType not null | `entity/Athlete.java` |
| **TrainingSession** | `sessionId: SessionId` | Completed training event with fatigue contribution calculation | `entity/TrainingSession.java` |
| **Routine** | `id: UUID` | Recommended training plan with intensity, duration, and recovery suggestion | `entity/Routine.java` |

---

## Value Objects

| VO | Type | Behavior | File |
|----|------|----------|------|
| **SessionId** | `record` | Wraps UUID for type safety | `valueobject/SessionId.java` |
| **Intensity** | `enum` | LIGHT, MODERATE, HIGH, EXTREME. Has `getFatigueMultiplier()` (1,2,3,4) and `calculateCalories(duration)` | `valueobject/Intensity.java` |
| **SportType** | `enum` | GYM ("Gimnasio"), FOOTBALL ("Fútbol"). Has `getDisplayName()` | `valueobject/SportType.java` |
| **FatigueLevel** | `enum` | LOW(1,"Baja"), MEDIUM(2,"Media"), HIGH(3,"Alta"). Has `isHigherThan()`, `isLowerThan()`, `getRecoverySuggestion(SportType)` | `valueobject/FatigueLevel.java` |
| **RecoverySuggestion** | `enum` | ABSOLUTE_REST, LIGHT_ACTIVITY, ACTIVE_RECOVERY, MODERATE_WORKOUT, INCREASE_INTENSITY. Has `getRecommendedIntensity()`, `getBaseDurationMinutes()`, `buildRoutineName(SportType)`, `buildDescription(FatigueLevel)` | `valueobject/RecoverySuggestion.java` |

---

## Domain Services

| Service | Input | Output | Responsibility | File |
|---------|-------|--------|----------------|------|
| **FatigueCalculator** | `List<TrainingSession>`, `LocalDateTime` | `FatigueLevel` | Calculates fatigue by summing `session.getFatigueContribution()` within recovery window, delegates classification to `FatigueConfiguration.classifyFatigue()` | `service/FatigueCalculator.java` |
| **RecoverySuggester** | `FatigueLevel`, `SportType`, `List<TrainingSession>`, `LocalDateTime` | `RecoverySuggestion` | Orchestrates: counts consecutive low days, checks `FatigueConfiguration.needsAbsoluteRest()` policy, delegates base suggestion to `FatigueLevel.getRecoverySuggestion(SportType)` | `service/RecoverySuggester.java` |
| **RoutineRecommender** | `UUID athleteId`, `SportType` | `Routine` | Orchestrates across 5 collaborators: AthleteRepository (get athlete), FatigueCalculator (fatigue), RecoverySuggester (suggestion), session data (weekly volume), FatigueConfiguration (duration adjustments). Builds routine from` RecoverySuggestion VO` (name, intensity, duration) | `service/RoutineRecommender.java` |

---

## Policies

| Policy | Configurable Values | Methods | File |
|--------|--------------------|---------|------|
| **FatigueConfiguration** | recoveryWindowHours(72), highThreshold(30), mediumThreshold(15), restDayReduction(1), volumeThreshold(300), youthMaxDuration(40) | `classifyFatigue()`, `needsAbsoluteRest()`, `adjustDurationForVolume()`, `adjustDurationForAge()`, `applyRestReduction()` | `policy/FatigueConfiguration.java` |

---

## Domain Rules

| Rule | Formula / Threshold |
|------|---------------------|
| **Recovery Window** | Only sessions within last `recoveryWindowHours` (72) affect fatigue |
| **Fatigue Points** | `(durationMinutes / 10) × intensityMultiplier` (from `TrainingSession.getFatigueContribution()`) |
| **Fatigue Classification** | >=30 = HIGH, 15-29 = MEDIUM, <15 = LOW (from `FatigueConfiguration`) |
| **Absolute Rest** | `fatiguePoints >= highThreshold AND sessionsThisWeek >= 5` |
| **Volume Adjustment** | Base duration reduced by 20% if `weeklyMinutes > volumeThreshold` (300) |
| **Youth Cap** | Routine duration capped at `youthMaxDuration` (40) for athletes < 18 years |

### Calculation Formula
`fatiguePoints = sum of (durationMinutes / 10) × intensityMultiplier for each session in recovery window`

### Calorie Formula
`calories = durationMinutes × 5.0 × intensityMultiplier` (LIGHT=1.0, MODERATE=1.5, HIGH=2.0, EXTREME=2.5)

---

## Repositories (Ports)

| Port | Implementation(s) |
|------|-------------------|
| **AthleteRepository** | `PostgresAthleteRepository` (@Primary), `MongoAthleteRepository` |
| **TrainingSessionRepository** | `PostgresTrainingSessionRepository` |
| **RoutineRepository** | `PostgresRoutineRepository` |

---

## Exceptions

| Exception | Extends | Use Case |
|-----------|---------|----------|
| **DomainException** | `RuntimeException` (abstract) | Base for all domain exceptions |
| **AthleteNotFoundException** | `DomainException` | Athlete not found in repository |
| **InvalidSearchParameterException** | `DomainException` | Invalid search input |

All located in `training/domain/exception/`.

---

## Complete File Summary

### Entities (3)
| File | Type |
|------|------|
| `training/domain/model/entity/Athlete.java` | Aggregate Root |
| `training/domain/model/entity/TrainingSession.java` | Aggregate Root |
| `training/domain/model/entity/Routine.java` | Aggregate Root |

### Value Objects (5)
| File | Description |
|------|-------------|
| `training/domain/model/valueobject/SessionId.java` | UUID wrapper |
| `training/domain/model/valueobject/Intensity.java` | LIGHT, MODERATE, HIGH, EXTREME |
| `training/domain/model/valueobject/SportType.java` | GYM, FOOTBALL |
| `training/domain/model/valueobject/FatigueLevel.java` | LOW, MEDIUM, HIGH |
| `training/domain/model/valueobject/RecoverySuggestion.java` | ABSOLUTE_REST, LIGHT_ACTIVITY, ACTIVE_RECOVERY, MODERATE_WORKOUT, INCREASE_INTENSITY |

### Domain Services (3)
| File | Responsibility |
|------|----------------|
| `training/domain/service/FatigueCalculator.java` | Calculate fatigue from recent sessions |
| `training/domain/service/RecoverySuggester.java` | Suggest recovery based on fatigue, sport, and session context |
| `training/domain/service/RoutineRecommender.java` | Recommend routine considering fatigue, recovery, age, and training load |

### Policies (1)
| File | Purpose |
|------|---------|
| `training/domain/policy/FatigueConfiguration.java` | Fatigue thresholds, classification, duration adjustments, and business rules |

### Exceptions (3)
| File | Purpose |
|------|---------|
| `training/domain/exception/DomainException.java` | Base domain exception |
| `training/domain/exception/AthleteNotFoundException.java` | Athlete not found |
| `training/domain/exception/InvalidSearchParameterException.java` | Invalid search input |
