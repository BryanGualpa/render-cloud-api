# AnalytiCore - Prototipo de Arquitectura Orientada a Servicios en la Nube

Plataforma de análisis de sentimiento y extracción de palabras clave, desplegada en **Render** con arquitectura políglota (React, Python, Java) y PostgreSQL.

## Estructura del Repositorio

```
├── frontend/           # React + Nginx (interfaz de usuario)
├── python-service/     # Servicio de Submisión (Flask)
├── java-service/       # Servicio de Análisis (Spring Boot)
└── docs/               # Diagramas e informe ejecutivo
```

## Arquitectura

```
Usuario → Frontend (React/Nginx)
              ↓ REST
         Python Service (submisión)
              ↓ REST interna          ↕ PostgreSQL
         Java Service (análisis)  ←——→  (estado y resultados)
              ↑
         Frontend consulta estado vía Python (polling)
```

## Despliegue en Render

### 1. Base de datos PostgreSQL
- **New +** → **Postgres** → Free → Oregon
- Copiar **Internal Database URL**

### 2. Servicio Java (desplegar primero)
- **New +** → **Web Service** → Repo → Root Directory: `java-service`
- Runtime: **Docker** | Region: Oregon | Free
- Environment: `DATABASE_URL` = Internal Database URL

### 3. Servicio Python
- **New +** → **Web Service** → Root Directory: `python-service`
- Runtime: **Docker** | Region: Oregon | Free
- Environment:
  - `DATABASE_URL` = Internal Database URL
  - `JAVA_SERVICE_URL` = URL interna del servicio Java (ej: `https://analyticore-java.onrender.com`)

### 4. Frontend
- **New +** → **Web Service** → Root Directory: `frontend`
- Runtime: **Docker** | Region: Oregon | Free
- Environment (build arg):
  - `VITE_API_URL` = URL pública del servicio Python

> **Nota:** En plan Free, la primera petición puede tardar ~50 segundos.

## Desarrollo Local

```bash
# Con Docker Compose
docker-compose up --build
```

- Frontend: http://localhost:3000
- Python API: http://localhost:5000
- Java API: http://localhost:8080

## Endpoints

| Servicio | Endpoint | Descripción |
|----------|----------|-------------|
| Python | `POST /api/jobs` | Enviar texto para análisis |
| Python | `GET /api/jobs/{id}` | Consultar estado y resultados |
| Java | `POST /api/analyze` | Iniciar análisis (interno) |
| Todos | `GET /health` | Health check |

## Documentación

- [Diagrama de Componentes](docs/diagrama-componentes.md)
- [Capas - Frontend](docs/capas-frontend.md)
- [Capas - Python Service](docs/capas-python-service.md)
- [Capas - Java Service](docs/capas-java-service.md)
- [Informe Ejecutivo](docs/informe-ejecutivo.md)
