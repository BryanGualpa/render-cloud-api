# Diagrama de Capas - Python Service (Submisión)

## Arquitectura Limpia

```mermaid
graph TB
    subgraph "Capa de Presentación"
        ROUTES[routes.py - Blueprint Flask]
    end

    subgraph "Capa de Aplicación"
        CREATE[CreateJobUseCase]
        GET[GetJobUseCase]
    end

    subgraph "Capa de Dominio"
        JOB[Job Entity]
        REPO_INT[JobRepository Interface]
    end

    subgraph "Capa de Infraestructura"
        PG[PostgresJobRepository]
        JAVA[JavaAnalysisClient]
        CONN[connection.py]
    end

    ROUTES --> CREATE
    ROUTES --> GET
    CREATE --> REPO_INT
    CREATE --> JAVA
    GET --> REPO_INT
    REPO_INT -.-> PG
    PG --> CONN
    PG --> JOB
    JAVA -->|HTTP POST| JAVA_SVC[Servicio Java]
    CONN --> DB[(PostgreSQL)]
```

## Estructura de Carpetas

```
python-service/app/
├── domain/
│   ├── entities/job.py          # Entidad de dominio
│   └── repositories/job_repository.py  # Interfaz
├── application/
│   └── use_cases/
│       ├── create_job_use_case.py
│       └── get_job_use_case.py
├── infrastructure/
│   ├── database/
│   │   ├── connection.py
│   │   └── postgres_job_repository.py
│   └── http/java_analysis_client.py
├── presentation/
│   └── api/routes.py
└── main.py
```

## Responsabilidades

| Capa | Responsabilidad |
|------|-----------------|
| **Dominio** | Entidad `Job`, contrato `JobRepository` |
| **Aplicación** | Casos de uso: crear job, consultar job |
| **Infraestructura** | PostgreSQL, cliente HTTP hacia Java |
| **Presentación** | Endpoints REST Flask |
