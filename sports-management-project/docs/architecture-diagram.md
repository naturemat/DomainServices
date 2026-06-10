# Architecture Diagram - Hexagonal (Ports & Adapters)

## Package Structure

```
com.sportsclub/
├── training/
│   └── domain/
│       ├── model/
│       │   ├── entity/      (Athlete, TrainingSession, Routine)
│       │   └── valueobject/ (SessionId, Intensity, SportType, FatigueLevel, RecoverySuggestion)
│       ├── service/         (FatigueCalculator, RoutineRecommender, RecoverySuggester)
│       ├── policy/          (FatigueConfiguration)
│       ├── exception/       (DomainException, AthleteNotFoundException, InvalidSearchParameterException)
│       └── port/out/        (AthleteRepository, TrainingSessionRepository, RoutineRepository)
├── application/             (Use Cases, DTOs)
└── infrastructure/          (REST, PostgreSQL, MongoDB adapters)
```

## Overview

```
+---------------------------------------------------------------------------+
|                           APPLICATION LAYER                               |
|  +------------------------+  +------------------------------------+       |
|  |     Use Cases          |  |              DTOs                  |       |
|  | - RegisterTraining     |  | - RegisterSessionRequest/Response |       |
|  |   SessionUseCase       |  | - RoutineResponse                 |       |
|  | - RegisterAthlete      |  |                                    |       |
|  +------------------------+  +------------------------------------+       |
+---------------------------------------------------------------------------+
                                  |
                                  v
+---------------------------------------------------------------------------+
|                              DOMAIN LAYER                                 |
|                                                                           |
|  +-----------------------------+                                          |
|  |    TRAINING CONTEXT         |                                          |
|  |  com.sportsclub.training    |                                          |
|  |                             |                                          |
|  |  ENTITIES:                  |                                          |
|  |  - Athlete (Agg Root)       |                                          |
|  |  - TrainingSession (Agg Root)|                                         |
|  |  - Routine (Agg Root)       |                                          |
|  |                             |                                          |
|  |  VALUE OBJECTS:             |                                          |
|  |  - SessionId (record)       |                                          |
|  |  - Intensity (enum)        |                                          |
|  |  - SportType (enum)        |                                          |
|  |  - FatigueLevel (enum)     |                                          |
|  |  - RecoverySuggestion (enum)|                                         |
|  |                             |                                          |
|  |  DOMAIN SERVICES:           |                                          |
|  |  - FatigueCalculator       |                                          |
|  |  - RoutineRecommender      |                                          |
|  |  - RecoverySuggester       |                                          |
|  |                             |                                          |
|  |  POLICIES:                  |                                          |
|  |  - FatigueConfiguration     |                                          |
|  |                             |                                          |
|  |  PORTS:                     |                                          |
|  |  - AthleteRepository       |                                          |
|  |  - TrainingSessionRepository|                                          |
|  |  - RoutineRepository       |                                          |
|  |                             |                                          |
|  |  EXCEPTIONS:                |                                          |
|  |  - DomainException          |                                          |
|  |  - AthleteNotFoundException |                                          |
|  |  - InvalidSearchParam...    |                                          |
|  +-----------------------------+                                          |
+---------------------------------------------------------------------------+
              ^
              |
      +-------+-------+
      |              |
 +---------+   +----+----+
 |POSTGRES |   | MONGODB |
 | Adapter |   | Adapter |
 +---------+   +---------+
 +--------+   +----------+
 |  REST  |   |  CONFIG  |
 |Adapter |   |          |
 +--------+   +----------+

```

## Component Description

### Domain Layer
Contains pure business logic without framework dependencies:
- **Entities**: Objects with identity (Athlete, TrainingSession, Routine)
- **Value Objects**: Immutable objects without identity (SessionId, Intensity, SportType, FatigueLevel, RecoverySuggestion) — contain behavior (e.g., `getFatigueMultiplier()`, `getRecoverySuggestion(SportType)`)
- **Domain Services**: Stateless orchestration logic (FatigueCalculator, RoutineRecommender, RecoverySuggester)
- **Policies**: Configurable business rules (FatigueConfiguration with thresholds, duration adjustments)
- **Ports**: Interfaces that define contracts for infrastructure
- **Exceptions**: Domain-specific exceptions (DomainException, AthleteNotFoundException)

### Application Layer
Orchestrates use case execution:
- **Use Cases**: Coordination of repositories and domain services
- **DTOs**: Objects for data transfer input/output

### Infrastructure Layer
Concrete implementations of ports:
- **PostgreSQL Adapter**: JPA implementation of all repositories
- **MongoDB Adapter**: Secondary implementation of AthleteRepository
- **REST Adapter**: Spring MVC controllers
- **Config**: Database configurations, domain service wiring (DomainServiceConfig)

## Data Flow

### Session Registration Flow:

```
1. REST Controller receives POST /api/v1/training/sessions
         │
2. RegisterTrainingSessionUseCase.execute(request)
         │
3. AthleteRepository.findById(athleteId)              → Athlete or throw
         │
4. TrainingSession.create(...)                         → Session with validation
         │
5. TrainingSessionRepository.findRecentByAthleteId()   → List of sessions
         │
6. FatigueCalculator.calculateFatigue(sessions, now)   → FatigueLevel
         │         │
         │  TrainingSession.getFatigueContribution()   → fatigue points each
         │  FatigueConfiguration.classifyFatigue()     → classify total
         │
7. RoutineRecommender.recommendRoutine(athleteId, sportType)
         │         │
         │  a) FatigueCalculator.calculateFatigue()
         │  b) RecoverySuggester.suggest(fatigue, sport, sessions, now)
         │  c) FatigueConfiguration.adjustDurationForVolume()
         │  d) FatigueConfiguration.adjustDurationForAge()
         │  e) RecoverySuggestion.buildRoutineName(sportType)
         │  f) RecoverySuggestion.getRecommendedIntensity()
         │
8. TrainingSessionRepository.save(session)             → persist
   RoutineRepository.save(routine)                     → persist
         │
9. Return RegisterSessionResponse
```

### Routine Generation Flow:

```
1. REST Controller receives GET /api/v1/training/routines/{athleteId}
         │
2. RoutineRecommender.recommendRoutine(athleteId, sportType)
         │
   a. AthleteRepository.findById(athleteId)
   b. TrainingSessionRepository.findRecentByAthleteId(athleteId, window)
   c. FatigueCalculator.calculateFatigue(sessions, now)
   d. RecoverySuggester.suggest(fatigue, sportType, sessions, now)
   e. Build Routine from RecoverySuggestion VO metrics
         │
3. Return Routine
```

## Hexagonal Architecture Principles Applied

| Principle | Implementation |
|-----------|----------------|
| **Dependency Inversion** | Domain services don't depend on Spring; injected via DomainServiceConfig |
| **Ports & Adapters** | Domain has interfaces (ports), infrastructure implements (adapters) |
| **Pure Domain** | No @Service, @Component annotations in domain/ layer |
| **Single Responsibility** | Each service has one clear orchestration role |
| **Ubiquitous Language** | Code uses domain terms (FatigueLevel, RecoverySuggestion, TrainingSession) |
| **Rich VOs** | Value Objects encapsulate behavior (Intensity.getFatigueMultiplier(), RecoverySuggestion.buildRoutineName()) |

---

## Core Business Domain - Deep Dive

### Entities

| Entity | Type | Identity | File |
|--------|------|----------|------|
| **Athlete** | Aggregate Root | `UUID id` | `entity/Athlete.java` |
| **TrainingSession** | Aggregate Root | `SessionId sessionId` | `entity/TrainingSession.java` |
| **Routine** | Aggregate Root | `UUID id` | `entity/Routine.java` |

### Value Objects

| VO | File | Behavior |
|----|------|----------|
| **SessionId** | `valueobject/SessionId.java` | UUID wrapper (Java record) |
| **Intensity** | `valueobject/Intensity.java` | `getFatigueMultiplier()` (1,2,3,4), `calculateCalories(duration)` |
| **SportType** | `valueobject/SportType.java` | `getDisplayName()` |
| **FatigueLevel** | `valueobject/FatigueLevel.java` | `isHigherThan()`, `isLowerThan()`, `getRecoverySuggestion(SportType)` |
| **RecoverySuggestion** | `valueobject/RecoverySuggestion.java` | `getRecommendedIntensity()`, `getBaseDurationMinutes()`, `buildRoutineName(SportType)`, `buildDescription(FatigueLevel)` |

### Domain Services

| Service | File | Collaborators | Responsibility |
|---------|------|---------------|----------------|
| **FatigueCalculator** | `service/FatigueCalculator.java` | FatigueConfiguration, TrainingSession | Sum fatigue contribution, classify via policy |
| **RoutineRecommender** | `service/RoutineRecommender.java` | AthleteRepository, FatigueCalculator, RecoverySuggester, TrainingSessionRepository, FatigueConfiguration | Orchestrate full routine recommendation |
| **RecoverySuggester** | `service/RecoverySuggester.java` | FatigueConfiguration, FatigueLevel | Check consecutive low days, absolute rest policy, delegate to VO |

### Policies

| Policy | File | Methods |
|--------|------|---------|
| **FatigueConfiguration** | `policy/FatigueConfiguration.java` | `classifyFatigue()`, `needsAbsoluteRest()`, `adjustDurationForVolume()`, `adjustDurationForAge()`, `applyRestReduction()` |

### Ports

| Port | File | Implementation(s) |
|------|------|-------------------|
| **AthleteRepository** | `port/out/AthleteRepository.java` | PostgresAthleteRepository (@Primary), MongoAthleteRepository |
| **TrainingSessionRepository** | `port/out/TrainingSessionRepository.java` | PostgresTrainingSessionRepository |
| **RoutineRepository** | `port/out/RoutineRepository.java` | PostgresRoutineRepository |

### Hexagonal Architecture Inside View

```
+---------------------------------------------------------------------+
|                         DOMAIN CORE                                 |
|                                                                     |
|   +--------------+  +--------------+  +------------------------+   |
|   |   ENTITIES   |  | VALUE OBJECTS|  |    DOMAIN SERVICES    |   |
|   |              |  |              |  |                        |   |
|   | Athlete      |  | SessionId    |  | FatigueCalculator     |   |
|   | Training     |  | Intensity    |  |                        |   |
|   |   Session    |  | SportType    |  | RoutineRecommender    |   |
|   | Routine      |  | FatigueLevel |  |                        |   |
|   |              |  | Recovery     |  | RecoverySuggester     |   |
|   |              |  |   Suggestion |  |                        |   |
|   +--------------+  +--------------+  +------------------------+   |
|                                                                     |
|   +-------------------+  +---------------------------+              |
|   |     POLICIES      |  |        PORTS              |             |
|   |                   |  |     (Outbound)            |             |
|   | FatigueConfig     |  |                           |             |
|   | (thresholds,      |  | AthleteRepository        |             |
|   |  window, volume,  |  | TrainingSessionRepository |             |
|   |  youth cap)       |  | RoutineRepository         |             |
|   +-------------------+  +---------------------------+              |
+---------------------------------------------------------------------+
                              |
               +---------------+---------------+
               v                               v
+---------------------------------+  +---------------------------------+
|      APPLICATION LAYER          |  |     INFRASTRUCTURE LAYER        |
|  +---------------------------+  |  |  +---------+ +---------+      |
|  |  USE CASES                |  |  |  |  REST   | |  DBs    |      |
|  |                           |  |  |  |Adapters | |Adapters |      |
|  | RegisterAthlete           |  |  |  |         | |         |      |
|  | RegisterTrainingSession  |  |  |  |Ctrl     | |Postgres |      |
|  | GenerateRoutine           |  |  |  |         | |MongoDB  |      |
|  | SearchAthletes            |  |  |  +---------+ +---------+      |
|  | SearchSessions            |  |  +---------------------------------+
|  +---------------------------+  |
|  +---------------------------+  |
|  |         DTOs              |  |
|  | Request / Response        |  |
|  +---------------------------+  |
+---------------------------------+
```

### Ports (Interfaces - Domain defines)

```java
// domain/port/out/AthleteRepository.java
public interface AthleteRepository {
    Optional<Athlete> findById(UUID id);
    List<Athlete> findAll();
    List<Athlete> findByNameContaining(String name);
    Athlete save(Athlete athlete);
}

// domain/port/out/TrainingSessionRepository.java
public interface TrainingSessionRepository {
    TrainingSession save(TrainingSession session);
    List<TrainingSession> findByAthleteId(UUID athleteId);
    List<TrainingSession> findRecentByAthleteId(UUID athleteId, LocalDateTime since);
}
```

### Adapter Implementation (Infrastructure)

```java
// infrastructure/persistence/postgres/repository/AthleteRepositoryImpl.java
@Repository
public class AthleteRepositoryImpl implements AthleteRepository {
    // JPA implementation - Spring handles transaction
}
```

### Key Hexagonal Properties

| Property | Implementation |
|----------|----------------|
| **Domain is isolated** | No Spring annotations in domain/ |
| **Ports are inward-facing** | Domain defines interfaces, infrastructure implements |
| **Adapters are outward-facing** | Infrastructure implements domain ports |
| **Dependency flows inward** | Infrastructure -> Ports -> Domain |
| **Use cases orchestrate** | Application layer coordinates without domain knowing |
