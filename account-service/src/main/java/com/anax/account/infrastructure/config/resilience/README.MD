# 📖 Resiliencia

## Propósito
Este paquete define el espacio arquitectónico para mecanismos de resiliencia
orientados a manejar fallos parciales, latencia y problemas de estabilidad
en la comunicación entre microservicios.

El objetivo es prevenir fallos en cascada y garantizar degradación controlada.

## Consideraciones

- **Tolerancia a Fallos**
    - Circuit breakers para aislar dependencias fallidas
    - Timeouts para evitar consumo excesivo de recursos

- **Reintentos**
    - Reintentos controlados con estrategias de backoff
    - Evitar tormentas de reintentos

- **Bulkheads / Compartimentación**
    - Aislamiento de recursos críticos
    - Protección contra sobrecarga

## Tecnologías Planeadas

- Resilience4j
- Operadores de resiliencia en programación reactiva

## Relación con Requerimientos No Funcionales

- **Resiliencia**: mayor estabilidad ante escenarios de fallo
- **Escalabilidad**: uso controlado de recursos bajo alta concurrencia
