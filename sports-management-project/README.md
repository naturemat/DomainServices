# Sistema de Gestión Deportiva Multidisciplinaria

Sistema académico que implementa arquitectura hexagonal (Ports & Adapters) con Domain-Driven Design para gestionar sesiones de entrenamiento, calcular fatiga acumulada, recomendar rutinas personalizadas y sugerir recuperación.

## Requisitos Previos

- Java 17+
- Maven 3.8+
- Node.js 18+
- npm 9+
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

## Frontend (React + Vite)

### 1. Instalar dependencias

```bash
cd frontend
npm install
```

### 2. Configurar variables de entorno

El frontend ya viene configurado para conectarse al backend local. Si necesitas modificar la URL:

```bash
# Crea o-edita el archivo frontend/.env
VITE_API_URL=http://localhost:8080
```

### 3. Ejecutar el frontend

```bash
cd frontend
npm run dev
```

La aplicación estará disponible en `http://localhost:5173`

### 4. Configuración de CORS

El backend ya está configurado para permitir conexiones desde el frontend (localhost:5173). Si experimentas errores de CORS, verifica que el backend esté ejecutándose antes que el frontend.

## Ejecutar Ambos Servicios

Para desarrollo completo, necesitas ejecutar backend y frontend en terminal separada:

**Terminal 1 - Backend:**
```bash
mvn spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
cd frontend
npm run dev
```

Luego accede a `http://localhost:5173`

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

## Problemas Comunes

### Error: CORS policy blocked

**Síntoma:** `Access to fetch at 'http://localhost:8080' from origin 'http://localhost:5173' has been blocked by CORS policy`

**Solución:**
1. Verifica que el backend esté ejecutándose (`mvn spring-boot:run`)
2. Confirma que el archivo `infrastructure/config/WebConfig.java` existe y tiene la configuración CORS
3. Reinicia el backend después de cambios en configuración

### Error: Connection refused al backend

**Síntoma:** `Failed to fetch` o `net::ERR_CONNECTION_REFUSED`

**Solución:**
1. Verifica que el backend esté corriendo en puerto 8080
2. Confirma que la URL en `frontend/.env` es `http://localhost:8080`
3. Revisa que no haya otro proceso ocupando el puerto 8080

### Error: JWT token no almacenado

**Síntoma:** Sesión no persiste al recargar la página

**Solución:**
1. Verifica que el backend devuelve un token en la respuesta de login/register
2. Revisa la consola del navegador para errores de localStorage
3. Asegúrate de que el login sea exitoso (revisa respuesta de red)

### Error: Base de datos no conecta

**Síntoma:** `Unable to obtain connection from database` o errores de conexión

**Solución:**
1. Verifica que el archivo `.env` tenga las credenciales correctas
2. Confirma que Neon y MongoDB Atlas estén activos (no en pausa)
3. Verifica que la URL de conexión contenga `?sslmode=require` para Neon

### Frontend no carga después de construir

**Síntoma:** Página en blanco o errores de módulo

**Solución:**
```bash
cd frontend
rm -rf node_modules dist
npm install
npm run dev
```

### Cambios en el backend no se reflejan

**Síntoma:** El frontend recibe datos antigos

**Solución:**
1. Limpia la caché del navegador (Ctrl+Shift+R)
2. Verifica los endpoints en la pestaña Network de DevTools
3. Confirma que el backend reinicia correctamente

## Probar la API con Postman

### Paso 1: Iniciar el backend

```bash
mvn spring-boot:run
```

El backend estará disponible en `http://localhost:8080`

---

### Endpoints Disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/athletes` | Obtener todos los atletas |
| GET | `/api/v1/athletes/{id}` | Obtener un atleta por ID |
| POST | `/api/v1/athletes` | Registrar un nuevo atleta |
| GET | `/api/v1/training/sessions` | Obtener todas las sesiones |
| GET | `/api/v1/training/ping` | Verificar disponibilidad |
| POST | `/api/v1/training/sessions` | Registrar sesión de entrenamiento |
| GET | `/api/v1/training/routines/{athleteId}` | Generar rutina recomendada |

---

### Valores Enum

**SportType (para atletas):**
- `GYM` - Gimnasio
- `FOOTBALL` - Fútbol

**Intensity (para sesiones):**
- `LIGHT` - Ligera
- `MODERATE` - Moderada
- `HIGH` - Alta
- `EXTREME` - Extrema

---

### Paso 2: Registrar un Atleta

**Crear una nueva solicitud en Postman:**

1. **Método:** POST
2. **URL:** `http://localhost:8080/api/v1/athletes`
3. **Headers:**
   - `Content-Type: application/json`

**Body (JSON):**
```json
{
  "name": "Juan Pérez",
  "sportType": "GYM",
  "birthDate": "1995-03-15"
}
```

**Respuesta:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Juan Pérez",
  "sportType": "GYM",
  "birthDate": "1995-03-15"
}
```

---

### Paso 3: Obtener Todos los Atletas

1. **Método:** GET
2. **URL:** `http://localhost:8080/api/v1/athletes`

**Respuesta:** Array con todos los atletas registrados.

---

### Paso 4: Obtener un Atleta por ID

1. **Método:** GET
2. **URL:** `http://localhost:8080/api/v1/athletes/550e8400-e29b-41d4-a716-446655440000`

**Respuesta:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Juan Pérez",
  "sportType": "GYM",
  "birthDate": "1995-03-15"
}
```

---

### Paso 5: Registrar Sesión de Entrenamiento

1. **Método:** POST
2. **URL:** `http://localhost:8080/api/v1/training/sessions`
3. **Headers:**
   - `Content-Type: application/json`

**Body (JSON):**
```json
{
  "athleteId": "550e8400-e29b-41d4-a716-446655440000",
  "sessionDate": "2026-05-16T10:30:00",
  "durationMinutes": 60,
  "intensity": "HIGH",
  "caloriesBurned": 500
}
```

**Respuesta:**
```json
{
  "sessionId": "...",
  "athleteId": "550e8400-e29b-41d4-a716-446655440000",
  "sessionDate": "2026-05-16T10:30:00",
  "durationMinutes": 60,
  "intensity": "HIGH",
  "fatigueLevel": "HIGH",
  "recoverySuggestion": "ABSOLUTE_REST",
  "recommendedRoutine": {
    "id": "...",
    "name": "Rutina de Recuperación",
    "description": "Entrenamiento ligero para recuperación activa",
    "recommendedDurationMinutes": 30,
    "recommendedIntensity": "LIGHT",
    "recoverySuggestion": "LIGHT_ACTIVITY"
  },
  "createdAt": "2026-05-16T10:30:00"
}
```

---

### Paso 6: Obtener Todas las Sesiones

1. **Método:** GET
2. **URL:** `http://localhost:8080/api/v1/training/sessions`

**Respuesta:** Array con todas las sesiones registradas.

---

### Paso 7: Generar Rutina Recomendada

1. **Método:** GET
2. **URL:** `http://localhost:8080/api/v1/training/routines/550e8400-e29b-41d4-a716-446655440000`

**Respuesta:**
```json
{
  "id": "...",
  "athleteId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Rutina de Mantenimiento Gym",
  "description": "Entrenamiento moderado para mantener condición",
  "recommendedDurationMinutes": 45,
  "recommendedIntensity": "MODERATE",
  "recoverySuggestion": "MODERATE_WORKOUT"
}
```

---

### Paso 8: Verificar Salud del Servicio

1. **Método:** GET
2. **URL:** `http://localhost:8080/api/v1/training/ping`

**Respuesta:** `pong`

---

### Probar con Diferentes Intensidades

**Intensidad LIGHT:**
```json
{
  "athleteId": "550e8400-e29b-41d4-a716-446655440000",
  "sessionDate": "2026-05-16T09:00:00",
  "durationMinutes": 30,
  "intensity": "LIGHT"
}
```
**Resultado:** `fatigueLevel: LOW`, `recoverySuggestion: LIGHT_ACTIVITY`

**Intensidad MODERATE:**
```json
{
  "athleteId": "550e8400-e29b-41d4-a716-446655440000",
  "sessionDate": "2026-05-16T09:00:00",
  "durationMinutes": 45,
  "intensity": "MODERATE"
}
```
**Resultado:** `fatigueLevel: MEDIUM`, `recoverySuggestion: MODERATE_WORKOUT`

**Intensidad EXTREME:**
```json
{
  "athleteId": "550e8400-e29b-41d4-a716-446655440000",
  "sessionDate": "2026-05-16T14:00:00",
  "durationMinutes": 120,
  "intensity": "EXTREME"
}
```
**Resultado:** `fatigueLevel: EXTREME`, `recoverySuggestion: ABSOLUTE_REST`

---

### Notas Adicionales

- Todos los endpoints de tipo POST requieren header `Content-Type: application/json`
- Los IDs son formatos UUID (ej: `550e8400-e29b-41d4-a716-446655440000`)
- Las fechas usan formato ISO 8601: `YYYY-MM-DDTHH:MM:SS`
- Los campos marcados con `@NotNull` o `@NotBlank` son obligatorios |
