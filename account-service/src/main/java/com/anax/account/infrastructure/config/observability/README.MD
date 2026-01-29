# 📖 Observabilidad

## Propósito
Este módulo está destinado a soportar preocupaciones de observabilidad como monitoreo,
análisis de rendimiento y resolución de problemas en un entorno de microservicios
distribuidos.

Aunque no está completamente implementado, este paquete define el espacio
arquitectónico donde se ubicarán las configuraciones relacionadas con la observabilidad.

## Consideraciones

- **Logging**
    - Logging centralizado y estructurado
    - Identificadores de correlación (traceId / spanId)
    - Separación entre logs de negocio y logs técnicos

- **Métricas**
    - Latencia, throughput y tasas de error
    - Exposición a través de endpoints de Actuator
    - Integración con sistemas de monitoreo (p.ej., Prometheus)
    - Prometheus y Grafana para análisis visual de métricas en tiempo real
  
- **Tracing Distribuido**
    - Trazado de requests de extremo a extremo entre microservicios
    - Propagación de contexto vía HTTP y mensajería (Kafka)

## Tecnologías Planeadas

- Spring Boot Actuator
- Micrometer
- OpenTelemetry (Jaeger / Zipkin)

## Relación con Requerimientos No Funcionales

- **Rendimiento**: permite detectar cuellos de botella y dependen
