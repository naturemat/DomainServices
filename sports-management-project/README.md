# Sports Management System

Academic project implementing Hexagonal Architecture (Ports & Adapters) with Domain-Driven Design (DDD) to manage training sessions, calculate accumulated fatigue, recommend personalized routines, and suggest recovery plans.

## Core Business Domain

**The System Solves**: Fatigue management for athletes

### Bounded Context

| Context | Package | Purpose |
|---------|---------|---------|
| **Training** | `com.sportsclub.training.domain` | Operational domain (athletes, sessions, routines, fatigue, recovery) |

### Entities

| Entity | Type | Identity | Description |
|--------|------|----------|-------------|
| **Athlete** | Aggregate Root | `UUID id` | Person who trains (GYM or FOOTBALL) with name, birthDate, sportType |
| **TrainingSession** | Aggregate Root | `SessionId sessionId` | Individual training event with calculated fatigue contribution |
| **Routine** | Aggregate Root | `UUID id` | Recommended training plan with intensity, duration, and recovery suggestion |

### Value Objects

| Value Object | Values / Behavior |
|--------------|-------------------|
| **SessionId** | UUID wrapper (Java record) |
| **Intensity** | LIGHT(1x), MODERATE(2x), HIGH(3x), EXTREME(4x). Has `getFatigueMultiplier()` and `calculateCalories()` |
| **SportType** | GYM, FOOTBALL. Has `getDisplayName()` |
| **FatigueLevel** | LOW(1), MEDIUM(2), HIGH(3). Has `isHigherThan()`, `isLowerThan()`, `getRecoverySuggestion(SportType)` |
| **RecoverySuggestion** | ABSOLUTE_REST, LIGHT_ACTIVITY, ACTIVE_RECOVERY, MODERATE_WORKOUT, INCREASE_INTENSITY. Has `getRecommendedIntensity()`, `getBaseDurationMinutes()`, `buildRoutineName()`, `buildDescription()` |

### Domain Services

| Service | Responsibility | Collaborators |
|---------|----------------|---------------|
| **FatigueCalculator** | Calculates fatigue level from recent sessions within recovery window | FatigueConfiguration (Policy), TrainingSession (VO behavior `getFatigueContribution()`) |
| **RoutineRecommender** | Recommends routine by orchestrating fatigue calculation, recovery suggestion, athlete context, and training load metrics | AthleteRepository, FatigueCalculator, RecoverySuggester, TrainingSessionRepository, FatigueConfiguration |
| **RecoverySuggester** | Suggests recovery action based on fatigue, sport type, consecutive low days, and absolute rest policy | FatigueConfiguration (Policy), FatigueLevel (VO `getRecoverySuggestion()`) |

### Policies

| Policy | Configuration | Rules |
|--------|---------------|-------|
| **FatigueConfiguration** | recoveryWindowHours(72), highThreshold(30), mediumThreshold(15), restDayReduction(1), volumeThreshold(300min/week), youthMaxDuration(40min) | `classifyFatigue()`, `needsAbsoluteRest()`, `adjustDurationForVolume()`, `adjustDurationForAge()`, `applyRestReduction()` |

### Business Rules
- Sessions within **72-hour window** count toward fatigue
- Fatigue thresholds: LOW(0-14), MEDIUM(15-29), HIGH(30+)
- Fatigue points: `(durationMinutes / 10) × intensityMultiplier`
- Absolute rest needed when `fatiguePoints >= 30 AND sessionsThisWeek >= 5`
- Youth athletes (< 18) have max routine duration of 40 minutes
- High weekly volume (> 300 min) reduces routine duration by 20%

---

## Quick Start

```bash
# Backend
mvn clean install
mvn spring-boot:run

# Frontend
cd frontend && npm install && npm run dev
```

Access: Backend `http://localhost:8080`, Frontend `http://localhost:5173`

## Documentation

For detailed documentation, see the `/docs` folder:

| Document | Description |
|----------|-------------|
| [bounded-contexts.md](docs/bounded-contexts.md) | Training bounded context, entities, services, and data flow |
| [architecture-diagram.md](docs/architecture-diagram.md) | Hexagonal layers, data flow, deep dive |
| [ubiquitous-language.md](docs/ubiquitous-language.md) | Domain vocabulary with code examples |

## Architecture (Hexagonal)

```
┌─────────────────────────────────────────┐
│         INFRASTRUCTURE (Adapters)       │
│    REST Controllers │ PostgreSQL │ MongoDB │
└─────────────────┬───────────────────────┘
                  ▼
┌─────────────────────────────────────────┐
│         PORTS (Interfaces)              │
│  AthleteRepo │ SessionRepo │ RoutineRepo│
└─────────────────┬───────────────────────┘
                  ▼
┌─────────────────────────────────────────┐
│         DOMAIN (Core - Pure Logic)      │
│   Athlete, TrainingSession, Routine,     │
│   Services, Policies, Value Objects      │
│   (ZERO Spring annotations)             │
└─────────────────────────────────────────┘
```

- **Training Context**: Athletes, sessions, routines, domain services (FatigueCalculator, RoutineRecommender, RecoverySuggester), policies (FatigueConfiguration)

## API Endpoints

- `GET /api/v1/athletes` - List athletes
- `POST /api/v1/athletes` - Register athlete
- `GET /api/v1/training/sessions` - List sessions
- `POST /api/v1/training/sessions` - Register session
- `GET /api/v1/training/routines/{athleteId}` - Generate routine
