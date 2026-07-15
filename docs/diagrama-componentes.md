# Diagrama de Componentes - AnalytiCore

## Vista General

```mermaid
graph TB
    subgraph "Usuario"
        U[Usuario Web]
    end

    subgraph "Render Cloud - Oregon"
        subgraph "Frontend"
            FE[React SPA]
            NG[Nginx]
        end

        subgraph "Servicio de Submisión"
            PY[Python Flask API]
        end

        subgraph "Servicio de Análisis"
            JV[Java Spring Boot API]
        end

        subgraph "Persistencia"
            DB[(PostgreSQL)]
        end
    end

    U -->|1. Envía texto| FE
    FE --> NG
    NG -->|2. POST /api/jobs| PY
    PY -->|3. Crea job PENDIENTE| DB
    PY -->|4. POST /api/analyze| JV
    JV -->|5. PROCESANDO| DB
    JV -->|6. Análisis + COMPLETADO| DB
    PY -->|7. Retorna jobId| FE
    FE -->|8. GET /api/jobs/id polling| PY
    PY -->|9. Consulta estado| DB
    PY -->|10. Resultados| FE
```

## Flujo de Datos

| Paso | Origen | Destino | Acción |
|------|--------|---------|--------|
| 1 | Usuario | Frontend | Introduce texto y envía |
| 2 | Frontend | Python | POST `/api/jobs` con el texto |
| 3 | Python | PostgreSQL | Crea registro con estado `PENDIENTE` |
| 4 | Python | Java | POST `/api/analyze` con `jobId` |
| 5 | Java | PostgreSQL | Actualiza estado a `PROCESANDO` |
| 6 | Java | PostgreSQL | Guarda sentimiento, keywords, `COMPLETADO` |
| 7 | Python | Frontend | Devuelve `jobId` |
| 8 | Frontend | Python | Polling GET `/api/jobs/{jobId}` cada 2s |
| 9-10 | Python | Frontend | Retorna estado y resultados |

## Contenedores Docker

| Componente | Imagen | Puerto |
|------------|--------|--------|
| Frontend | `node:20` + `nginx:alpine` | 80 |
| Python Service | `python:3.11-slim` | 5000 |
| Java Service | `eclipse-temurin:17` | 8080 |
| PostgreSQL | Render Managed | 5432 |

## Patrones Cloud Aplicados

- **Empaquetado Docker**: Cada componente en su propia imagen
- **APIs RESTful**: Comunicación exclusiva vía HTTP/JSON
- **Stateless**: Todo el estado en PostgreSQL
- **Configuración externa**: `DATABASE_URL`, `JAVA_SERVICE_URL` vía variables de entorno
- **Arquitectura limpia**: Capas separadas en cada servicio
