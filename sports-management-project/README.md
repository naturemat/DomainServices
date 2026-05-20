# Sports Management System

Academic project implementing Hexagonal Architecture (Ports & Adapters) with Domain-Driven Design (DDD) to manage training sessions, calculate accumulated fatigue, recommend personalized routines, and suggest recovery plans.

## Core Business Domain

**The System Solves**: Fatigue management for athletes

### Entities (Domain Model)

| Entity | Type | Description |
|--------|------|-------------|
| **Athlete** | Aggregate Root | Person who trains (GYM or FOOTBALL) |
| **TrainingSession** | Aggregate Root | Individual training event with calculated fatigue |
| **FatigueMetrics** | Entity | Historical fatigue record (MongoDB) |

### Value Objects

| Value Object | Values |
|--------------|--------|
| **Intensity** | LIGHT(1x), MODERATE(2x), HIGH(3x), EXTREME(4x) |
| **FatigueLevel** | LOW(0-14), MEDIUM(15-29), HIGH(30+) |
| **SportType** | GYM, FOOTBALL |
| **RecoverySuggestion** | ABSOLUTE_REST, LIGHT_ACTIVITY, ACTIVE_RECOVERY, INCREASE_INTENSITY |

### Domain Services (3)

1. **FatigueCalculationService** - Calculates fatigue: `(duration/10) × intensityMultiplier`
2. **RoutineRecommendationService** - Recommends routine based on fatigue + sport type
3. **RecoverySuggestionService** - Suggests recovery action

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
│  AthleteRepo │ SessionRepo │ MetricsRepo │
└─────────────────┬───────────────────────┘
                  ▼
┌─────────────────────────────────────────┐
│         DOMAIN (Core - Pure Logic)      │
│   Entities │ Services │ Value Objects   │
│   (ZERO Spring annotations)             │
└─────────────────────────────────────────┘
```

- **Domain Layer**: Pure business logic (zero Spring annotations)
- **Application Layer**: Use cases with `@Transactional`
- **Infrastructure Layer**: REST adapters, PostgreSQL, MongoDB

## API Endpoints

- `GET /api/v1/athletes` - List athletes
- `POST /api/v1/athletes` - Register athlete
- `GET /api/v1/training/sessions` - List sessions
- `POST /api/v1/training/sessions` - Register session
- `GET /api/v1/training/routines/{athleteId}` - Generate routine