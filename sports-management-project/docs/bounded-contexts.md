# Bounded Contexts

## Overview

This project has a single bounded context following DDD principles:

1. **Training Context** - Operational domain

---

## Training Context

**Package**: `com.sportsclub.training.domain`

**Responsibilities:**
- Registration and management of athletes
- Registration and management of training sessions
- Fatigue calculation based on recent sessions
- Recommendation of personalized routines
- Generation of recovery suggestions

**Aggregates:**
- **Athlete** (Aggregate Root): Person who trains (name, birthDate, sportType with validation)
- **TrainingSession** (Aggregate Root): Training event with calculated fatigue contribution
- **Routine** (Aggregate Root): Recommended training plan with intensity, duration, recovery suggestion

**Value Objects:**
- `SessionId` (UUID wrapper)
- `Intensity` (LIGHT, MODERATE, HIGH, EXTREME) — contains `getFatigueMultiplier()` and `calculateCalories()`
- `SportType` (GYM, FOOTBALL)
- `FatigueLevel` (LOW, MEDIUM, HIGH) — contains `getRecoverySuggestion(SportType)`
- `RecoverySuggestion` (ABSOLUTE_REST, LIGHT_ACTIVITY, ACTIVE_RECOVERY, MODERATE_WORKOUT, INCREASE_INTENSITY) — contains `getRecommendedIntensity()`, `getBaseDurationMinutes()`, `buildRoutineName()`, `buildDescription()`

**Repositories (Ports - Outbound):**
- `AthleteRepository` -> PostgreSQL (primary), MongoDB (secondary)
- `TrainingSessionRepository` -> PostgreSQL
- `RoutineRepository` -> PostgreSQL

**Domain Services:**
- `FatigueCalculator`: Sums fatigue contribution from sessions within recovery window, delegates classification to `FatigueConfiguration` policy
- `RoutineRecommender`: Orchestrates across AthleteRepository, FatigueCalculator, RecoverySuggester, session data, and FatigueConfiguration to build a context-appropriate routine
- `RecoverySuggester`: Evaluates consecutive low days, absolute rest policy, and delegates base suggestion to `FatigueLevel.getRecoverySuggestion(SportType)`

**Policies:**
- `FatigueConfiguration`: Configurable thresholds (HIGH=30, MEDIUM=15), recovery window (72h), volume threshold (300min/week), youth max duration (40min)

**Technology:**
- PostgreSQL for transactional data (athletes, sessions, routines)
- MongoDB as secondary adapter for AthleteRepository (read scalability)

---

## Context Integration

### Domain Service Orchestration

The application layer orchestrates domain services, policies, and repositories.

### Session Registration Flow

```
POST /api/v1/training/sessions (REST)
         │
RegisterTrainingSessionUseCase (Application)
         │
   ┌─────┴──────┐
   │ Athlete    │  AthleteRepository.findById()
   │ exists?    │
   └─────┬──────┘
         │ yes
   ┌─────┴──────────────┐
   │ TrainingSession    │  TrainingSession.create() with validation
   │ created            │
   └─────┬──────────────┘
         │
   ┌─────┴──────────────┐
   │ FatigueCalculator  │  calculateFatigue(recentSessions)
   └─────┬──────────────┘
         │ fatigueLevel
   ┌─────┴──────────────┐
   │ RoutineRecommender │  recommendRoutine(athleteId, sportType)
   │ (internally calls  │  → fatigueCalculator, recoverySuggester,
   │  RecoverySuggester)│  → athleteRepository, sessionRepository
   └─────┬──────────────┘
         │ routine + recoverySuggestion
   ┌─────┴──────────────┐
   │ Persist session    │  trainingSessionRepository.save()
   │ and routine        │  routineRepository.save()
   └─────┬──────────────┘
         │
   Return RegisterSessionResponse
```

### Routine Generation Flow

```
GET /api/v1/training/routines/{athleteId} (REST)
         │
   ┌─────┴──────────────┐
   │ RoutineRecommender │  recommendRoutine(athleteId, sportType)
   │                    │
   │ 1. AthleteRepository.findById()
   │ 2. TrainingSessionRepository.findRecentByAthleteId()
   │ 3. FatigueCalculator.calculateFatigue()
   │ 4. RecoverySuggester.suggest()
   │ 5. Build Routine using RecoverySuggestion VO
   └─────┬──────────────┘
         │
   Return Routine (name, description, duration, intensity, recoverySuggestion)
```

---

## Mapping to Ubiquitous Language Terms

| Category | Elements |
|----------|----------|
| **Entities** | Athlete, TrainingSession, Routine |
| **Value Objects** | SessionId, Intensity, SportType, FatigueLevel, RecoverySuggestion |
| **Domain Services** | FatigueCalculator, RoutineRecommender, RecoverySuggester |
| **Policies** | FatigueConfiguration |
| **Ports** | AthleteRepository, TrainingSessionRepository, RoutineRepository |

## Identity Definition

| Entity | Identity | Type |
|--------|----------|------|
| **Athlete** | `UUID id` | Aggregate Root |
| **TrainingSession** | `SessionId sessionId` | Aggregate Root |
| **Routine** | `UUID id` | Aggregate Root |

---

## Use Case Examples

### Register Session
```
Scenario: Athlete registers a training session (60 min, HIGH intensity, GYM)

1. AthleteRepository.findById() → athlete exists
2. TrainingSession.create(60, HIGH) → session with caloriesBurned
3. FatigueCalculator.calculateFatigue(sessions) → (60/10) × 3 = 18 points → MEDIUM
4. RoutineRecommender.recommendRoutine(GYM)
   a. fatigue = MEDIUM
   b. RecoverySuggester.suggest(MEDIUM, GYM, sessions) → LIGHT_ACTIVITY
   c. Routine: name="Recuperación Ligera - Gimnasio", duration=25, intensity=LIGHT
5. Persist and return response
```

### Generate Routine
```
Scenario: Generate routine for athlete with accumulated fatigue

1. Query recent sessions within recovery window
2. Calculate total fatigue points using FatigueCalculator
3. Determine FatigueLevel: LOW/MEDIUM/HIGH
4. Call RecoverySuggester for recovery context
5. Build Routine with appropriate name, description, duration, intensity
6. Return Routine
```

---

## Technology Stack

| Storage | Purpose |
|---------|---------|
| PostgreSQL | Transactional data, ACID compliance (athletes, sessions, routines) |
| MongoDB | Secondary adapter for AthleteRepository (alternate read path) |
