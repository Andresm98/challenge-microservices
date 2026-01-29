# 📖 Seguridad

## Propósito
Este paquete está reservado para configuraciones de seguridad necesarias en una
arquitectura de microservicios distribuida y escalable.

Las preocupaciones de seguridad se aíslan intencionalmente de la lógica de negocio
y del dominio para mantener la separación de responsabilidades.

## Consideraciones

- **Autenticación**
    - Autenticación stateless usando JWT
    - Autenticación usando OAuth
    - Validación de tokens en la capa de infraestructura

- **Autorización**
    - Control de acceso basado en roles (RBAC)
    - Políticas de autorización a nivel de endpoint

- **Gestión de Secretos**
    - Configuración externalizada
    - Secrets basados en entorno (sin credenciales hardcodeadas)

## Tecnologías Planeadas

- Spring Security
- OAuth2 / JWT
- Proveedor de identidad externo (p.ej., Keycloak)

## Relación con Requerimientos No Funcionales

- **Escalabilidad**: mecanismos stateless adecuados para escalado horizontal
- **Seguridad**: control de acceso centralizado y consistente entre servicios
