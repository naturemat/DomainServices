# Architecture Diagram - Hexagonal (Ports & Adapters)

## Contexts Structure

```
com.sportsclub/
├── training/
│   └── domain/
│       ├── model/
│       │   ├── entity/      (Athlete, TrainingSession, Routine, SportProfile)
│       │   ├── valueobject/ (SessionId, Intensity, SportType)
│       │   └── enums/       (RecoverySuggestion)
│       ├── service/         (FatigueCalculator, RoutineRecommender, RecoverySuggester)
│       ├── policy/          (FatigueRules)
│       └── port/out/        (AthleteRepository, TrainingSessionRepository, RoutineRepository)
├── performance/
│   └── domain/
│       ├── model/entity/    (FatigueMetrics)
│       └── port/out/        (FatigueMetricsRepository)
└── shared/
    └── domain/model/       (FatigueLevel)
```

## Overview

```
+---------------------------------------------------------------------------+
|                           APPLICATION LAYER                               |
|  +------------------------+  +------------------------------------+          |
|  |     Use Cases          |  |              DTOs                   |        |
|  | - RegisterTraining     |  | - RegisterSessionRequest/Response  |        |
|  |   SessionUseCase       |  | - RoutineResponse                  |        |
|  | - GenerateRoutine      |  | - FatigueMetricsDTO                |        |
|  | - RegisterAthlete      |  |                                    |        |
|  +------------------------+  +------------------------------------+        |
+---------------------------------------------------------------------------+
                                  |
                                  v
+---------------------------------------------------------------------------+
|                              DOMAIN LAYER                                 |
|                                                                          |
|  ┌─────────────────────────────┐  ┌───────────────────────────────┐    |
|  |    TRAINING CONTEXT          │  │    PERFORMANCE CONTEXT        │    |
|  │  com.sportsclub.training    │  │  com.sportsclub.performance   │    |
|  │                             │  │                               │    |
|  │  ENTITIES:                  │  │  ENTITIES:                    │    |
|  │  - Athlete (Agg Root)       │  │  - FatigueMetrics (Agg Root) │    |
|  │  - TrainingSession (Agg Root)  │  │                               │    |
|  │  - Routine (Agg Root)       │  │  PORTS:                       │    |
|  │  - SportProfile             │  │  - FatigueMetricsRepository  │    |
|  │                             │  │                               │    |
|  │  VALUE OBJECTS:             │  │                               │    |
|  │  - SessionId (VO)           │  │                               │    |
|  │  - Intensity (VO)           │  │                               │    |
|  │  - SportType (VO)           │  │                               │    |
|  │                             │  │                               │    |
|  │  ENUMS:                     │  │                               │    |
|  │  - RecoverySuggestion       │  │                               │    |
|  │                             │  │                               │    |
|  │  DOMAIN SERVICES:           │  │                               │    |
|  │  - FatigueCalculator       │  │                               │    |
|  │  - RoutineRecommender      │  │                               │    |
|  │  - RecoverySuggester        │  │                               │    |
|  │                             │  │                               │    |
|  │  POLICIES:                  │  │                               │    |
|  │  - FatigueRules             │  │                               │    |
|  │                             │  │                               │    |
|  │  PORTS:                     │  │                               │    |
|  │  - AthleteRepository       │  │                               │    |
|  │  - TrainingSessionRepository│  │                               │    |
|  │  - RoutineRepository        │  │                               │    |
|  └─────────────────────────────┘  └───────────────────────────────┘    |
|                                                                          |
|  ┌─────────────────────────────────────────────────────────────────┐     |
|  │                    SHARED (com.sportsclub.shared)               │     |
|  │                      FatigueLevel (Enum)                        │     |
|  └─────────────────────────────────────────────────────────────────┘     |
+---------------------------------------------------------------------------+
              ^                                         |
              |                          ADAPTERS (INFRASTRUCTURE)
      +-------+-------+                    +-----------+-----------+
      |              |                    |                       |
 +---+---+   +------+------+         +----+----+           +----+---+
 | POSTGRESQL |   | MONGODB     |         | REST  |           |  CONFIG  |
 | Adapter    |   | Adapter     |         | Adapter|           |          |
 +------------+   +-------------+         +--------+           +----------+

```

## Component Description

### Domain Layer
Contains pure business logic without framework dependencies:
- **Entities**: Objects with identity (Athlete, TrainingSession, Routine, SportProfile, FatigueMetrics)
- **Value Objects**: Immutable objects without identity (SessionId, Intensity, SportType, FatigueLevel)
- **Enums**: Fixed sets of values (RecoverySuggestion)
- **Domain Services**: Stateless business logic (FatigueCalculator, RoutineRecommender, RecoverySuggester)
- **Ports**: Interfaces that define contracts for infrastructure
- **Policies**: Configurable business rules (FatigueRules)

### Application Layer
Orchestrates use case execution:
- **Use Cases**: Coordination of repositories and domain services
- **DTOs**: Objects for data transfer input/output

### Infrastructure Layer
Concrete implementations of ports:
- **PostgreSQL Adapter**: JPA implementation of transactional repositories
- **MongoDB Adapter**: Implementation for fatigue metrics
- **REST Adapter**: Spring MVC controllers
- **Config**: Database configurations, domain service wiring

## Data Flow

### Session Registration Flow:
1. REST Controller receives request
2. Use Case validates athlete exists
3. Domain Services calculate fatigue
4. Repositories persist in PostgreSQL + MongoDB

### Routine Generation Flow:
1. REST Controller requests routine by athleteId
2. Use Case obtains fatigue metrics from MongoDB
3. Domain Service recommends routine
4. Response returns to client

## Hexagonal Architecture Principles Applied

| Principle | Implementation |
|-----------|----------------|
| **Dependency Inversion** | Domain services don't depend on Spring; injected via config |
| **Ports & Adapters** | Domain has interfaces (ports), infrastructure implements (adapters) |
| **Pure Domain** | No @Service, @Component annotations in domain/ layer |
| **Single Responsibility** | Each service has one clear responsibility |
| **Ubiquitous Language** | Code uses domain terms, no technical jargon in names |

---

## Core Business Domain - Deep Dive

### Bounded Contexts Structure

```
┌───────────────────────────────────────────────────────────────────────────┐
│                        TRAINING CONTEXT                                    │
│                        Package: com.sportsclub.training.domain             │
│                                                                           │
│  ┌──────────────┐     ┌──────────────┐     ┌──────────────┐             │
│  │   ATHLETE    │     │  TRAINING    │     │   ROUTINE    │             │
│  │ (Agg Root)   │     │  SESSION     │     │ (Agg Root)   │             │
│  │              │     │ (Agg Root)   │     │              │             │
│  │ identity:    │     │ identity:    │     │ identity:   │             │
│  │ UUID id      │     │ SessionId    │     │ UUID id      │             │
│  │              │     │ (UUID VO)    │     │              │             │
│  └──────────────┘     └──────────────┘     └──────────────┘             │
│         │                   │                                              │
│         └───────────────────┼──────────────────────────────────┐          │
│                             │                                  │          │
│  ┌──────────────┐          │                            ┌──────────────┐  │
│  │SPORT PROFILE │          │                            │   POLICY     │  │
│  │              │          │                            │ FatigueRules │  │
│  │ identity:    │          │                            │              │  │
│  │ UUID id      │          │                            │ thresholds:  │  │
│  └──────────────┘          │                            │ HIGH=30      │  │
│                            │                            │ MEDIUM=15    │  │
│                            │                            │ window=72h   │  │
└────────────────────────────┼────────────────────────────┴──────────────┘  │
                             │                                              │
  ┌──────────────────────────┼──────────────────────────────────────────────┤
  │                 │         │                                    │        │
  │                 │         │                                    │        │
  │                 ▼         │                                    ▼        │
  │  ┌────────────────┐       │                       ┌────────────────┐   │
  │  │DOMAIN SERVICES │       │                       │ VALUE OBJECTS  │   │
  │  │                │       │                       │                │   │
  │  │FatigueCalculator       │                       │ SessionId      │   │
  │  │RoutineRecommender      │                       │ Intensity      │   │
  │  │RecoverySuggester       │                       │ SportType      │   │
  │  └────────────────┘       │                       └────────────────┘   │
  │                           │                                              │
  │  ┌────────────────┐       │                       ┌────────────────┐   │
  │  │     PORTS      │       │                       │     ENUMS      │   │
  │  │                │       │                       │                │   │
  │  │AthleteRepository       │                       │RecoverySugges-│   │
  │  │TrainingSessionRepo     │                       │tion           │   │
  │  │RoutineRepository       │                       └────────────────┘   │
  │  └────────────────┘       │                                              │
  └───────────────────────────┼──────────────────────────────────────────────┘
                               │
┌──────────────────────────────┼──────────────────────────────────────────────┤
│                        PERFORMANCE CONTEXT                                │
│                        Package: com.sportsclub.performance.domain          │
│                                                                          │
│  ┌──────────────────┐      ┌──────────────────────┐                    │
│  │  FATIGUE METRICS  │      │    PORTS             │                    │
│  │  (Agg Root)       │      │                      │                    │
│  │                   │      │ FatigueMetrics      │                    │
│  │ identity:         │      │   Repository        │                    │
│  │ UUID id           │      └──────────────────────┘                    │
│  └──────────────────┘                                                     │
└───────────────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                           SHARED                                          │
│                    Package: com.sportsclub.shared.domain.model            │
│                         FatigueLevel (Enum)                              │
└───────────────────────────────────────────────────────────────────────────┘
```

### Entities Summary

| Context | Entity | Type | Identity | Package Location |
|---------|--------|------|----------|------------------|
| Training | **Athlete** | Aggregate Root | `UUID id` | `training/domain/model/entity/Athlete.java` |
| Training | **TrainingSession** | Aggregate Root | `SessionId sessionId` | `training/domain/model/entity/TrainingSession.java` |
| Training | **Routine** | Aggregate Root | `UUID id` | `training/domain/model/entity/Routine.java` |
| Training | **SportProfile** | Entity | `UUID id` | `training/domain/model/entity/SportProfile.java` |
| Performance | **FatigueMetrics** | Aggregate Root | `UUID id` | `performance/domain/model/entity/FatigueMetrics.java` |

### Value Objects Summary

| VO | Context | Package Location | Values |
|----|---------|------------------|--------|
| **SessionId** | Training | `training/domain/model/valueobject/SessionId.java` | UUID wrapper (Java record) |
| **Intensity** | Training | `training/domain/model/valueobject/Intensity.java` | LIGHT(1x), MODERATE(2x), HIGH(3x), EXTREME(4x) |
| **SportType** | Training | `training/domain/model/valueobject/SportType.java` | GYM, FOOTBALL |
| **FatigueLevel** | Shared | `shared/domain/model/FatigueLevel.java` | LOW(1), MEDIUM(2), HIGH(3) |

### Enums Summary

| Enum | Context | Package Location | Values |
|------|---------|------------------|--------|
| **RecoverySuggestion** | Training | `training/domain/model/enums/RecoverySuggestion.java` | ABSOLUTE_REST, LIGHT_ACTIVITY, ACTIVE_RECOVERY, MODERATE_WORKOUT, INCREASE_INTENSITY |

### Domain Services Summary

| Service | Context | Package Location | Responsibility |
|---------|---------|------------------|----------------|
| **FatigueCalculator** | Training | `training/domain/service/FatigueCalculator.java` | Calculate fatigue from sessions within 72h window |
| **RoutineRecommender** | Training | `training/domain/service/RoutineRecommender.java` | Recommend routine based on fatigue + sport type |
| **RecoverySuggester** | Training | `training/domain/service/RecoverySuggester.java` | Suggest recovery action |

### Policies Summary

| Policy | Context | Package Location | Purpose |
|--------|---------|------------------|---------|
| **FatigueRules** | Training | `training/domain/policy/FatigueRules.java` | Fatigue thresholds (HIGH=30, MEDIUM=15) and recovery window (72h) |

### Ports Summary

| Port | Context | Package Location | Implementation |
|------|---------|------------------|----------------|
| **AthleteRepository** | Training | `training/domain/port/out/AthleteRepository.java` | `PostgresAthleteRepository` |
| **TrainingSessionRepository** | Training | `training/domain/port/out/TrainingSessionRepository.java` | `PostgresTrainingSessionRepository` |
| **RoutineRepository** | Training | `training/domain/port/out/RoutineRepository.java` | `PostgresRoutineRepository` |
| **FatigueMetricsRepository** | Performance | `performance/domain/port/out/FatigueMetricsRepository.java` | `MongoFatigueMetricsRepository` |

### Hexagonal Architecture - Inside View

```
┌─────────────────────────────────────────────────────────────────────┐
│                         DOMAIN CORE                                 │
│                                                                     │
│   ┌──────────────┐  ┌──────────────┐  ┌────────────────────────┐   │
│   │   ENTITIES   │  │ VALUE OBJECTS│  │    DOMAIN SERVICES    │   │
│   │              │  │              │  │                        │   │
│   │ Athlete      │  │ SessionId    │  │ FatigueCalculator     │   │
│   │ Training     │  │ Intensity    │  │                       │   │
│   │   Session    │  │ SportType    │  │ RoutineRecommender   │   │
│   │ Routine      │  │ FatigueLevel │  │                       │   │
│   │ SportProfile │  │              │  │ RecoverySuggester    │   │
│   │ Fatigue      │  │              │  │                       │   │
│   │   Metrics    │  └──────────────┘  └────────────────────────┘   │
│   └──────────────┘                                               │
│                             │                                      │
│   ┌──────────────┐  ┌──────────────┐  ┌────────────────────────┐ │
│   │   POLICIES   │  │     ENUMS     │  │        PORTS          │ │
│   │              │  │              │  │    (Outbound)         │ │
│   │ FatigueRules │  │RecoverySugge-│  │                        │ │
│   │ (thresholds, │  │stion         │  │ AthleteRepository    │ │
│   │   window)    │  └──────────────┘  │ TrainingSessionRepo   │ │
│   └──────────────┘                    │ RoutineRepository     │ │
│                                        │ FatigueMetricsRepo    │ │
│                                        └────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
```
┌─────────────────────────────────────────────────────────────────────┐
│                         DOMAIN CORE                                 │
│                                                                     │
│   ┌──────────────┐  ┌──────────────┐  ┌────────────────────────┐   │
│   │   ENTITIES   │  │ VALUE OBJECTS│  │    DOMAIN SERVICES    │   │
│   │              │  │              │  │                        │   │
│   │ Athlete      │  │ Intensity    │  │ FatigueCalculation    │   │
│   │ Training     │  │ SportType    │  │ Service               │   │
│   │   Session    │  │ FatigueLevel │  │                       │   │
│   │ Fatigue      │  │ Recovery     │  │ RoutineRecommendation │   │
│   │   Metrics    │  │   Suggestion │  │ Service               │   │
│   │              │  │ SessionId    │  │                       │   │
│   │              │  │ Routine      │  │ RecoverySuggestion    │   │
│   │              │  │              │  │ Service               │   │
│   └──────────────┘  └──────────────┘  └────────────────────────┘   │
│                                                                     │
│   ┌──────────────┐  ┌──────────────┐                               │
│   │   POLICIES   │  │    PORTS     │                               │
│   │              │  │ (Outbound)  │                               │
│   │ FatigueRules │  │              │                               │
│   │ (thresholds, │  │ AthleteRepo  │                               │
│   │   window)    │  │ SessionRepo  │                               │
│   └──────────────┘  │ RoutineRepo  │                               │
│                     │ MetricsRepo  │                               │
│                     └──────────────┘                               │
└─────────────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┴───────────────┐
              ▼                               ▼
┌─────────────────────────────────┐  ┌─────────────────────────────────┐
│      APPLICATION LAYER          │  │     INFRASTRUCTURE LAYER        │
│  ┌───────────────────────────┐  │  │  ┌─────────┐ ┌─────────┐      │
│  │  USE CASES                │  │  │  │  REST   │ │  DBs    │      │
│  │                           │  │  │  │Adapters │ │Adapters │      │
│  │ RegisterAthlete           │  │  │  │         │ │         │      │
│  │ RegisterTrainingSession  │  │  │  │Ctrl     │ │Postgres │      │
│  │ GenerateRoutine           │  │  │  │         │ │MongoDB  │      │
│  │ SearchAthletes            │  │  │  └─────────┘ └─────────┘      │
│  │ SearchSessions            │  │  └─────────────────────────────────┘
│  └───────────────────────────┘  │
│  ┌───────────────────────────┐  │
│  │         DTOs              │  │
│  │ Request / Response        │  │
│  └───────────────────────────┘  │
└─────────────────────────────────┘
```

### Ports (Interfaces - Domain defines)

```java
// domain/ports/out/AthleteRepository.java
public interface AthleteRepository {
    Optional<Athlete> findById(UUID id);
    List<Athlete> findAll();
    List<Athlete> findByNameContaining(String name);
    Athlete save(Athlete athlete);
}

// domain/ports/out/TrainingSessionRepository.java
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
| **Dependency flows inward** | Infrastructure → Ports → Domain |
| **Use cases orchestrate** | Application layer coordinates without domain knowing |