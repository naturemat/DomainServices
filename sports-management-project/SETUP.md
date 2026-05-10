# Guía de Configuración - Sports Management System

## Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- Git

## Paso 1: Clonar el repositorio

```bash
git clone <repository-url>
cd sports-management-system
```

## Paso 2: Obtener credenciales de bases de datos

### Neon (PostgreSQL)
1. Ve a https://console.neon.tech
2. Crea un proyecto nuevo
3. En "Connection Details", selecciona "Java" y copia la cadena de conexión
4. Asegúrate de que el nombre de la base de datos sea `sports_db`

### MongoDB Atlas
1. Ve a https://www.mongodb.com/cloud/atlas
2. Crea un cluster gratuito
3. En "Database" > "Connect" > "Connect your application"
4. Copia la cadena de conexión y reemplaza `<password>` con tu password

## Paso 3: Configurar variables de entorno

### Opción A: Usar archivo .env (recomendado para desarrollo)
```bash
# Copia el archivo de ejemplo
copy .env.example .env

# Edita el archivo .env con tus credenciales
```

### Opción B: Variables de entorno del sistema
```bash
# Windows (PowerShell)
$env:POSTGRES_JDBC_URL="jdbc:postgresql://..."
$env:POSTGRES_USERNAME="tu_usuario"
$env:POSTGRES_PASSWORD="tu_password"
$env:MONGODB_URI="mongodb+srv://..."
$env:MONGODB_DATABASE="sports_metrics"

# Linux/Mac (bash)
export POSTGRES_JDBC_URL="jdbc:postgresql://..."
export POSTGRES_USERNAME="tu_usuario"
export POSTGRES_PASSWORD="tu_password"
export MONGODB_URI="mongodb+srv://..."
export MONGODB_DATABASE="sports_metrics"
```

## Paso 4: Compilar el proyecto

```bash
mvn clean compile
```

## Paso 5: Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## Verificar que funciona

```bash
# Probar que la API responde
curl http://localhost:8080/api/v1/training/routines/00000000-0000-0000-0000-000000000000
```

Debería devolver un error 404 (porque no existe el atleta) pero sin errores de conexión a la base de datos.

## Solución de problemas

### Error de conexión a PostgreSQL
- Verifica que la URL de Neon sea correcta
- Asegúrate de que el cluster esté activo
- Verifica el usuario y password

### Error de conexión a MongoDB
- Verifica que el cluster de Atlas esté activo
- Asegúrate de que tu IP esté en la lista de allowlist de Atlas
- Verifica el password en la URI

### Error "No se encontró .env"
- Asegúrate de que el archivo se llame exactamente `.env` (sin extensión)
- El archivo debe estar en la raíz del proyecto

## Notas de seguridad

- **NUNCA** subas el archivo `.env` a Git - ya está en `.gitignore`
- **NUNCA** pongas credenciales reales en `application.yml`
- Usa `application-example.yml` como referencia para otros desarrolladores
- Para producción, considera usar herramientas como Docker Secrets o Kubernetes Secrets