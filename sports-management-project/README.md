# Sistema de Gestión Deportiva Multidisciplinaria

Sistema académico que implementa arquitectura hexagonal (Ports & Adapters) con Domain-Driven Design para gestionar sesiones de entrenamiento, calcular fatiga acumulada, recomendar rutinas personalizadas y sugerir recuperación.

## Requisitos Previos

- Java 17+
- Maven 3.8+
- Cuenta en [Neon](https://neon.tech) (PostgreSQL cloud)
- Cuenta en [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) (MongoDB cloud)

## Inicio Rápido

### 1. Configurar credenciales de bases de datos

El proyecto usa servicios cloud. Tienes dos opciones:

#### Opción A: Usar archivo .env (desarrollo local)
```bash
# Copia el archivo de ejemplo
cp .env.example .env

# Edita .env con tus credenciales de Neon y MongoDB Atlas
```

#### Opción B: Variables de entorno del sistema
Exporta las siguientes variables:
```bash
export POSTGRES_JDBC_URL="jdbc:postgresql://..."
export POSTGRES_USERNAME="tu_usuario"
export POSTGRES_PASSWORD="tu_password"
export MONGODB_URI="mongodb+srv://..."
export MONGODB_DATABASE="sports_metrics"
```

### ¿Dónde obtener las credenciales?

**Neon (PostgreSQL):**
1. Ve a https://console.neon.tech
2. Crea un proyecto nuevo o usa uno existente
3. En "Connection Details" selecciona tu lenguaje (Java/Spring)
4. Copia la connection string y reemplaza `dbname` con `sports_db`

**MongoDB Atlas:**
1. Ve a https://www.mongodb.com/cloud/atlas
2. Crea un cluster gratuito (Free Tier)
3. En "Database" > "Connect" > "Connect your application"
4. Copia la connection string y reemplaza `<password>` con tu password

### 2. Compilar y ejecutar

```bash
mvn clean install
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## Uso de la API

### Registrar Sesión de Entrenamiento

**Endpoint:** `POST /api/v1/training/sessions`

**Request:**
```json
{
  "athleteId": "550e8400-e29b-41d4-a716-446655440000",
  "sessionDate": "2024-01-15T10:30:00",
  "durationMinutes": 60,
  "intensity": "HIGH",
  "caloriesBurned": 500
}
```

**Response:**
```json
{
  "sessionId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "athleteId": "550e8400-e29b-41d4-a716-446655440000",
  "sessionDate": "2024-01-15T10:30:00",
  "durationMinutes": 60,
  "intensity": "HIGH",
  "fatigueLevel": "HIGH",
  "recommendedRoutine": {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "athleteId": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Rutina de Recuperación",
    "description": "Entrenamiento ligero para recuperación activa",
    "recommendedDurationMinutes": 30,
    "recommendedIntensity": "LIGHT",
    "recoverySuggestion": "LIGHT_ACTIVITY"
  },
  "recoverySuggestion": "ABSOLUTE_REST",
  "createdAt": "2024-01-15T10:30:00"
}
```

### Generar Rutina Recomendada

**Endpoint:** `GET /api/v1/training/routines/{athleteId}`

**Response:**
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "athleteId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Rutina de Mantenimiento Gym",
  "description": "Entrenamiento moderado para mantener condición",
  "recommendedDurationMinutes": 45,
  "recommendedIntensity": "MODERATE",
  "recoverySuggestion": "MODERATE_WORKOUT"
}
```

## Arquitectura

El proyecto sigue una arquitectura hexagonal con las siguientes capas:

```
domain/           # lógica de negocio pura (sin Spring)
application/      # casos de uso y DTOs
infrastructure/   # implementaciones de repositorios, controllers
shared/          # excepciones comunes
docs/            # documentación del dominio
```

### Domain Services

1. **FatigueCalculationService**: Calcula nivel de fatiga basado en sesiones recientes
2. **RoutineRecommendationService**: Recomienda rutinas según nivel de fatiga
3. **RecoverySuggestionService**: Sugiere recuperación según deporte y fatiga

## Documentación Adicional

- [Lenguaje Ubicuo](docs/ubiquitous-language.md)
- [Diagrama de Arquitectura](docs/architecture-diagram.md)
- [Bounded Contexts](docs/bounded-contexts.md)

## Archivos Sensibles - NO subir a Git

Los siguientes archivos están ignorados en `.gitignore` y contienen datos sensibles:

- `.env` - Credenciales locales de bases de datos
- `application-local.yml` - Configuraciones locales
- `.opencode/` - Archivos de OpenSpec workflow
- `openspec/` - Archivos de OpenSpec

**Para GitHub:** El archivo `application-example.yml` está diseñado para ser subido como referencia.

## Errores Comunes a Evitar

1. **NO** inyectar repositorios en Domain Services (viola independencia del dominio)
2. **NO** usar anotaciones Spring (@Service, @Component) en la capa domain/
3. **NO** acoplar entidades de dominio con entidades JPA (usar mappers)
4. **SÍ** mantener los puertos como interfaces en domain/ports/out/
5. **SÍ** usar Value Objects para conceptos que no tienen identidad propia