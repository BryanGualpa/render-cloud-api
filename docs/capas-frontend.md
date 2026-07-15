# Diagrama de Capas - Frontend (React)

## Arquitectura Limpia

```mermaid
graph TB
    subgraph "Capa de Presentación"
        APP[App.jsx - Componente principal]
        CSS[Estilos CSS]
    end

    subgraph "Capa de Aplicación"
        SUB[handleSubmit - Enviar análisis]
        POLL[pollJob - Consultar estado]
        STATE[Estado React - useState/useEffect]
    end

    subgraph "Capa de Infraestructura"
        API[Fetch API - Cliente HTTP]
        ENV[VITE_API_URL - Config externa]
    end

    APP --> SUB
    APP --> POLL
    SUB --> STATE
    POLL --> STATE
    SUB --> API
    POLL --> API
    API --> ENV
```

## Responsabilidades por Capa

| Capa | Archivos | Responsabilidad |
|------|----------|-----------------|
| **Presentación** | `App.jsx`, `App.css`, `index.css` | UI, formulario, visualización de resultados |
| **Aplicación** | Lógica en `App.jsx` | Orquestar envío, polling, manejo de estado |
| **Infraestructura** | `fetch()`, variables de entorno | Comunicación REST con Python Service |

## Principios

- Sin estado persistente en el cliente (stateless)
- Configuración externa via `VITE_API_URL`
- Separación visual (CSS) de lógica (JSX)
- Polling desacoplado del renderizado
