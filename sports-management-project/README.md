# Sports Management System

Academic project implementing Hexagonal Architecture (Ports & Adapters) with Domain-Driven Design (DDD) to manage training sessions, calculate accumulated fatigue, recommend personalized routines, and suggest recovery plans.

## Core Business Domain

**The System Solves**: Fatigue management for athletes

### Bounded Contexts

| Context | Package | Purpose |
|---------|---------|---------|
| **Training** | `com.sportsclub.training.domain` | Operational domain (athletes, sessions, routines) |
| **Performance** | `com.sportsclub.performance.domain` | Analytical domain (fatigue metrics) |

### Entities

| Entity | Context | Type | Identity | Description |
|--------|---------|------|----------|-------------|
| **Athlete** | Training | Aggregate Root | `UUID id` | Person who trains (GYM or FOOTBALL) |
| **TrainingSession** | Training | Aggregate Root | `SessionId sessionId` | Individual training event with calculated fatigue |
| **Routine** | Training | Aggregate Root | `UUID id` | Recommended training plan |
| **SportProfile** | Training | Entity | `UUID id` | Athlete's sport-specific profile |
| **FatigueMetrics** | Performance | Aggregate Root | `UUID id` | Historical fatigue record (MongoDB) |

### Value Objects & Enums

| Value Object/Enum | Context | Values |
|--------------------|---------|--------|
| **SessionId** | Training | UUID wrapper (Java record) |
| **Intensity** | Training | LIGHT(1x), MODERATE(2x), HIGH(3x), EXTREME(4x) |
| **SportType** | Training | GYM, FOOTBALL |
| **FatigueLevel** | Shared | LOW(1), MEDIUM(2), HIGH(3) |
| **RecoverySuggestion** | Training | ABSOLUTE_REST, LIGHT_ACTIVITY, ACTIVE_RECOVERY, MODERATE_WORKOUT, INCREASE_INTENSITY |

### Domain Services

| Service | Context | File Location | Description |
|---------|---------|---------------|-------------|
| **FatigueCalculator** | Training | `training/domain/service/` | Calculates fatigue: `(duration/10) × intensityMultiplier` |
| **RoutineRecommender** | Training | `training/domain/service/` | Recommends routine based on fatigue + sport type |
| **RecoverySuggester** | Training | `training/domain/service/` | Suggests recovery action |

### Policies

| Policy | Context | Description |
|--------|---------|-------------|
| **FatigueRules** | Training | Thresholds (HIGH=30, MEDIUM=15) and 72h recovery window |

### Business Rules
- Sessions within **72-hour window** count toward fatigue
- Fatigue thresholds: LOW(0-14), MEDIUM(15-29), HIGH(30+)

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
| [bounded-contexts.md](docs/bounded-contexts.md) | Training & Performance bounded contexts |
| [architecture-diagram.md](docs/architecture-diagram.md) | Hexagonal layers, data flow, deep dive |
| [ubiquitous-language.md](docs/ubiquitous-language.md) | Domain vocabulary with code examples |

## Architecture (Hexagonal)

```
┌─────────────────────────────────────────┐
│         INFRASTRUCTURE (Adapters)       │
│   REST Controllers │ PostgreSQL │ MongoDB │
└─────────────────┬───────────────────────┘
                  ▼
┌─────────────────────────────────────────┐
│         PORTS (Interfaces)              │
│  AthleteRepo │ SessionRepo │ RoutineRepo│
│  FatigueMetricsRepo                     │
└─────────────────┬───────────────────────┘
                  ▼
┌─────────────────────────────────────────┐
│         DOMAIN (Core - Pure Logic)      │
│   Training Context: Athlete, TrainingSession, │
│   Routine, SportProfile, Services, Policies │
│   Performance Context: FatigueMetrics     │
│   (ZERO Spring annotations)             │
└─────────────────────────────────────────┘
```

- **Training Context**: Athletes, sessions, routines, FatigueCalculator, RoutineRecommender, RecoverySuggester
- **Performance Context**: FatigueMetrics, FatigueMetricsRepository
- **Shared**: FatigueLevel enum

## API Endpoints

- `GET /api/v1/athletes` - List athletes
- `POST /api/v1/athletes` - Register athlete
- `GET /api/v1/training/sessions` - List sessions
- `POST /api/v1/training/sessions` - Register session
- `GET /api/v1/training/routines/{athleteId}` - Generate routine