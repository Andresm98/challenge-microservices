# 🏦 Banking Microservices Platform

Esta plataforma es una solución bancaria basada en microservicios reactivos, diseñada para gestionar clientes, cuentas y movimientos financieros con alta concurrencia y escalabilidad.



## 🛠️ Stack Tecnológico
* **Java 17** con **Spring Boot 3.3.4** (WebFlux).
* **Programación Reactiva:** Project Reactor para flujos no bloqueantes.
* **Persistencia:** Spring Data **R2DBC** con PostgreSQL 16.
* **Mensajería:** **Apache Kafka** para arquitectura dirigida por eventos (Event-Driven).
* **Monitoreo:** **AKHQ.io** para visualización y gestión de tópicos de Kafka.
* **Documentación:** **OpenAPI 3.0** (Enfoque Contract-First).
* **Infraestructura:** Docker & Docker Compose con **Multi-stage builds** para optimización de imágenes.

---

## 🏗️ Arquitectura y Diseño
El proyecto implementa **Arquitectura Hexagonal**, separando estrictamente la lógica de negocio (Domain) de los adaptadores de entrada (REST Controllers) y salida (R2DBC, Kafka, WebClient).



### Componentes:
1.  **Customer Service (Puerto 8081):** Gestión de Clientes (CRUD completo).
2.  **Account Service (Puerto 8082):** * Gestión de Cuentas y Movimientos.
    * **Validación de Saldo (F3):** Lógica reactiva atómica para impedir retiros que excedan el saldo disponible.
    * **Reportes Integrados (F4):** Consolidación de datos entre microservicios mediante comunicación no bloqueante.
3.  **AKHQ Service (Puerto 8083):** Interfaz gráfica para auditar eventos en tiempo real.

---

## 🚀 Despliegue con Docker (Automatizado) (F7)
El despliegue está totalmente automatizado mediante **Docker Multi-stage builds**. Docker se encarga de compilar el código fuente con Maven y levantar la infraestructura sin requerir dependencias locales (Java/Maven).

```bash
# 1. Clonar el repositorio
git clone https://github.com/Andresm98/challenge-microservices
cd challenge-microservices

# 2. Levantar el ecosistema completo (Bases de datos, Kafka, Microservicios)
docker-compose up --build
```

## 📖 Documentación de la API (OpenAPI)

Se entrega la especificación técnica completa bajo el estándar **OpenAPI 3.0**.

- **Ruta del contrato:** `docs/openapi.yaml`
- **Visualización:** Se puede importar este archivo en [Swagger Editor](https://editor.swagger.io/) para ver el detalle de los modelos y endpoints.

---

## 🧪 Pruebas de Integración (Postman)  (F1, F2)

Se incluye una colección completa con casos de éxito y error (ej. saldo insuficiente).

- **Archivo:** `postman/Banking_Platform.postman_collection.json`
- **Instrucciones:**
    1. Importar la colección en Postman.
    2. Ejecutar en el siguiente orden recomendado:
        - `POST /customers` → Crear cliente
        - `POST /accounts` → Crear cuenta con el `customerId` generado
        - `POST /movements` → Realizar depósitos/retiros
        - `GET /reports/{clientId}` → Generar estado de cuenta detallado

---

## 📊 Endpoints Principales (F1, F2)

| Servicio  | Endpoint                                 | Descripción                                     |
|-----------|-----------------------------------------|-------------------------------------------------|
| Customer  | `GET /api/v1/customers`                 | Lista todos los clientes                        |
| Account   | `POST /api/v1/accounts`                 | Crea una cuenta vinculada a un cliente         |
| Movement  | `POST /api/v1/movements`                | Registro de transacciones con validación de saldo |
| Report    | `GET /api/v1/reports/{id}?startDate=...&endDate=...` | Reporte consolidado de movimientos |

---

## 🧪 Pruebas y CI/CD (GitHub Actions)

Este proyecto cuenta con pruebas automatizadas y flujo de integración continua:

### Pruebas Unitarias (F5) e Integración (F6)

El proyecto cuenta con pruebas automatizadas para garantizar la calidad y correcto funcionamiento de los microservicios.

### Tipos de pruebas

1. **Unitarias:**  
   Validan la lógica de negocio aislada (dominio, servicios, validaciones).

2. **Integración HTTP (WebTestClient):**
    - Ejemplo: `AccountIntegrationTest`
    - Valida endpoints REST, persistencia y flujo completo de creación de cuentas.

3. **Integración Kafka (KafkaSmokeTest):**
    - Valida envío y recepción de mensajes en topics de Kafka
    - Usa `KafkaTemplate` y listener temporal con `BlockingQueue`.

#### Comando para ejecutar pruebas localmente:

```bash
# Ejecutar todas las pruebas
# Linux / macOS
./mvnw clean test

# Windows (CMD o PowerShell)
mvnw.cmd clean test
```

---

## 📖 Decisiones de Diseño y Trade-Offs

Durante el desarrollo se tomaron decisiones arquitectónicas considerando **rendimiento, escalabilidad y resiliencia**, aunque algunas no se implementaron completamente y quedan documentadas como **deuda técnica**.

### Observabilidad
- **Decisión:** Logging centralizado, métricas y tracing distribuido.
- **Trade-off:** Se priorizó implementar la funcionalidad central antes de centralizar observabilidad.
- **Ubicación planificada:** `./infrastructure/config/observability/`
- **Beneficio esperado:** Permite detectar cuellos de botella y analizar comportamiento bajo carga. 
- **Path:** Ver la estructura completa del proyecto en [README.md](./account-service/src/main/java/com/anax/account/infrastructure/config/observability/README.MD)

### Seguridad
- **Decisión:** Autenticación stateless y control de acceso por roles.
- **Trade-off:** No se implementó integración con JWT/OAuth2 para no retrasar la entrega funcional.
- **Ubicación planificada:** `./infrastructure/config/security/`
- **Beneficio esperado:** Seguridad consistente y escalable, sin acoplarse al dominio. 
- **Path:** Ver la estructura completa del proyecto en [README.md](./account-service/src/main/java/com/anax/account/infrastructure/config/security/README.MD)
 

### Resiliencia
- **Decisión:** Circuit breakers, reintentos y bulkheads para manejar fallos parciales.
- **Trade-off:** Se documentó la estrategia pero no se implementaron librerías externas como Resilience4j.
- **Ubicación planificada:** `./infrastructure/config/resilience/`
- **Beneficio esperado:** Evita fallos en cascada y mantiene estabilidad bajo carga. 
- **Path:** Ver la estructura completa del proyecto en [README.md](./account-service/src/main/java/com/anax/account/infrastructure/config/resilience/README.MD)

> Nota: Estas decisiones reflejan un diseño preparado para producción,
> pero la implementación completa se considera deuda técnica para mantener el foco en la funcionalidad central y el cumplimiento de los requisitos.


## ✒️ Autor

**Santiago Andres Moreta** – Software Engineer | Cloud Engineer
