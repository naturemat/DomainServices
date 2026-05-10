# Diagrama de Arquitectura - Hexagonal (Ports & Adapters)

## Visión General

```
+---------------------------------------------------------------------------+
|                           APPLICATION LAYER                               |
|  +------------------------+  +------------------------------------+      |
|  |     Use Cases          |  |              DTOs                   |      |
|  | - RegisterTraining     |  | - RegisterSessionRequest/Response  |      |
|  |   SessionUseCase       |  | - RoutineResponse                  |      |
|  | - GenerateRoutine      |  | - FatigueMetricsDTO                |      |
|  +------------------------+  +------------------------------------+      |
+---------------------------------------------------------------------------+
                                |
                                v
+---------------------------------------------------------------------------+
|                              DOMAIN LAYER                                 |
|  +------------------+  +-------------------+  +------------------------+  |
|  |    ENTITIES      |  |  VALUE OBJECTS    |  |      ENUMS            |  |
|  | - Athlete        |  | - FatigueLevel    |  | - RecoverySuggestion  |  |
|  | - TrainingSession|  | - Intensity      |  +------------------------+  |
|  | - Routine        |  | - SportType       |                              |
|  | - SportProfile   |  | - SessionId      |                              |
|  +------------------+  +-------------------+                              |
|                                                                          |
|  +------------------------+  +------------------+  +-----------------+   |
|  |    DOMAIN SERVICES    |  |      PORTS      |  |    POLICIES     |   |
|  | - FatigueCalculation  |  | (Outbound)     |  | - FatigueRules  |   |
|  |   Service             |  | - AthleteRepo   |  +-----------------+   |
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

## Descripción de Componentes

### Domain Layer (Capa de Dominio)
Contiene la lógica de negocio pura, sin dependencias de frameworks:
- **Entities**: objetos con identidad (Athlete, TrainingSession, Routine)
- **Value Objects**: objetos inmutables sin identidad (FatigueLevel, Intensity)
- **Domain Services**: lógica de negocio stateless (FatigueCalculationService)
- **Ports**: interfaces que definen los contratos para la infraestructura
- **Policies**: reglas de negocio configurables (FatigueRules)

### Application Layer (Capa de Aplicación)
Orquesta la ejecución de casos de uso:
- **Use Cases**: coordinación de repositorios y servicios de dominio
- **DTOs**: objetos para transferencia de datos entrada/salida

### Infrastructure Layer (Capa de Infraestructura)
Implementaciones concretas de los puertos:
- **PostgreSQL Adapter**: implementación JPA de repositorios transaccionales
- **MongoDB Adapter**: implementación de métricas de fatiga
- **REST Adapter**: controladores Spring MVC
- **Config**: configuraciones de bases de datos

## Flujo de Datos

1. **Registro de Sesión**:
   - REST Controller recibe request
   - Use Case valida atleta existe
   - Domain Services calculan fatiga
   - Repositorios persisten en PostgreSQL + MongoDB

2. **Generación de Rutina**:
   - REST Controller solicita rutina por athleteId
   - Use Case obtiene métricas de fatiga desde MongoDB
   - Domain Service recomienda rutina
   - Respuesta retorna al cliente