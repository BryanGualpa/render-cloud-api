# Informe Ejecutivo - AnalytiCore

## Problema de Negocio

AnalytiCore es una startup de análisis de datos que necesita validar la viabilidad técnica de ofrecer un servicio en línea de análisis de sentimiento y extracción de palabras clave. El reto es construir un prototipo funcional en la nube que demuestre capacidad de procesar textos de usuarios de forma confiable, sin invertir en infraestructura propia.

## Solución Propuesta

Se desarrolló un prototipo con **arquitectura orientada a servicios** desplegado en **Render**, compuesto por tres microservicios políglotas:

- **Frontend (React + Nginx)**: Interfaz web donde el usuario envía textos y consulta resultados.
- **Servicio de Submisión (Python)**: Recibe solicitudes, valida datos, persiste trabajos en PostgreSQL y orquesta el análisis.
- **Servicio de Análisis (Java)**: Procesa el texto, calcula sentimiento y extrae palabras clave, actualizando resultados en la base de datos.

El flujo es asíncrono con polling: el usuario envía un texto, recibe un identificador de trabajo (`jobId`) y consulta periódicamente hasta obtener los resultados.

## Valor de la Solución

El prototipo demuestra que es posible ofrecer análisis de texto en la nube con una arquitectura modular, donde cada componente puede evolucionar de forma independiente. La externalización del estado a PostgreSQL garantiza que ningún servicio pierda datos ante reinicios o escalado.

## Beneficios de la Arquitectura

| Beneficio | Descripción |
|-----------|-------------|
| **Escalabilidad** | Cada servicio escala de forma independiente según su carga (más instancias Java si hay muchos análisis). |
| **Mantenibilidad** | Arquitectura limpia con capas separadas facilita cambios sin afectar otros componentes. |
| **Flexibilidad del equipo** | Tecnologías políglotas (React, Python, Java) permiten que equipos especializados trabajen en paralelo. |
| **Resiliencia** | Servicios stateless + base de datos centralizada = recuperación rápida ante fallos. |
| **Costo** | Despliegue en plan Free de Render permite validar el concepto sin inversión inicial. |

## Conclusión

AnalytiCore demuestra que una arquitectura cloud orientada a servicios, con comunicación REST y persistencia externalizada, es una base sólida y escalable para convertir el análisis de texto en un producto comercial.
