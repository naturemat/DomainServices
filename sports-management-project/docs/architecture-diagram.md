# Architecture Diagram - Hexagonal (Ports & Adapters)

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
|  +------------------+  +-------------------+  +------------------------+  |
|  |    ENTITIES      |  |  VALUE OBJECTS    |  |      ENUMS            |  |
|  | - Athlete        |  | - FatigueLevel    |  | - RecoverySuggestion  |  |
|  | - TrainingSession|  | - Intensity       |  +------------------------+  |
|  | - Routine        |  | - SportType       |                              |
|  | - SportProfile   |  | - SessionId       |                              |
|  +------------------+  +-------------------+                              |
|                                                                          |
|  +------------------------+  +------------------+  +-----------------+   |
|  |    DOMAIN SERVICES    |  |      PORTS      |  |    POLICIES     |   |
|  | - FatigueCalculation  |  | (Outbound)     |  | - FatigueRules  |   |
|  |   Service             |  | - AthleteRepo  |  +-----------------+   |
|  | - RoutineRecommenda-  |  | - TrainingSession|                        |
|  |   tionService         |  |   Repo          |                        |
|  | - RecoverySuggestion  |  | - RoutineRepo  |                        |
|  |   Service             |  | - FatigueMetrics|                        |
|  +------------------------+  |   Repo         |                        |
|                             +------------------+                        |
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
- **Entities**: Objects with identity (Athlete, TrainingSession, Routine)
- **Value Objects**: Immutable objects without identity (FatigueLevel, Intensity, SportType)
- **Domain Services**: Stateless business logic (FatigueCalculationService, RoutineRecommendationService, RecoverySuggestionService)
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

### Domain Model Overview

```
┌─────────────────────┐     ┌─────────────────────┐
│      ATHLETE        │     │  TRAINING SESSION   │
│  (Aggregate Root)   │     │  (Aggregate Root)    │
├─────────────────────┤     ├─────────────────────┤
│ - id: UUID          │────▶│ - sessionId: VO     │
│ - name: String      │     │ - athleteId: UUID   │
│ - sportType: VO     │     │ - sessionDate       │
│ - birthDate         │     │ - durationMinutes   │
└─────────────────────┘     │ - intensity: VO     │
                            │ - fatigueLevel: VO  │
                            │ - recoverySugg: VO  │
                            │ - routine: VO       │
                            └─────────────────────┘
```

### Entities (3)

| Entity | Responsibility |
|--------|----------------|
| **Athlete** | Person who trains - owns training sessions |
| **TrainingSession** | Individual training event with calculated fatigue |
| **FatigueMetrics** | Historical record of athlete's fatigue (MongoDB) |

### Value Objects (6)

| Value Object | Values |
|--------------|--------|
| **Intensity** | LIGHT(1x), MODERATE(2x), HIGH(3x), EXTREME(4x) |
| **FatigueLevel** | LOW(0-14), MEDIUM(15-29), HIGH(30+) |
| **SportType** | GYM, FOOTBALL |
| **RecoverySuggestion** | ABSOLUTE_REST, LIGHT_ACTIVITY, ACTIVE_RECOVERY, INCREASE_INTENSITY |
| **SessionId** | UUID wrapper |
| **Routine** | Recommended training plan |

### Domain Services (3)

| Service | Method | Returns |
|---------|--------|---------|
| **FatigueCalculationService** | `calculateFatigue(sessions, now)` | FatigueLevel |
| **RoutineRecommendationService** | `recommendRoutine(athleteId, fatigue, sport)` | Routine |
| **RecoverySuggestionService** | `getSuggestion(fatigue, sport)` | RecoverySuggestion |

### Hexagonal Architecture - Inside View

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