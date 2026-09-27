# 🏦 Microservices Platform

Esta plataforma es una solución bancaria basada en microservicios reactivos, diseñada para gestionar clientes, cuentas y movimientos financieros con alta concurrencia y escalabilidad.



## 🛠️ Stack Tecnológico
* **Java 17** con **Spring Boot 4.0.2** (WebFlux).
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

```cmd
# 1. Clonar el repositorio
git clone https://github.com/Andresm98/challenge-microservices
cd challenge-microservices

# 2. Solo la primera vez: crear .env si aún no existe y reemplazar sus valores de ejemplo
if not exist .env copy .env.example .env
# 3. Levantar servicios, seguridad, métricas y logging centralizado
docker compose up --build
```

### Inspeccionar PostgreSQL

Abre pgAdmin en `http://localhost:5050` e inicia sesión con `PGADMIN_DEFAULT_EMAIL` y `PGADMIN_DEFAULT_PASSWORD` del `.env` (si no se definieron, el usuario local es `admin@example.com` / `local-dev-pgadmin-password`). Registra los servidores usando **Add New Server**:

| Campo | Customer DB | Account DB |
| --- | --- | --- |
| Host name/address | `customer-postgres` | `account-postgres` |
| Port | `5432` | `5432` |
| Maintenance database | `customer_db` | `account_db` |
| Username/password | `CUSTOMER_DB_USER` / `CUSTOMER_DB_PASSWORD` | `ACCOUNT_DB_USER` / `ACCOUNT_DB_PASSWORD` |

Dentro de Docker se usa el puerto `5432` para ambas bases; `5432` y `5433` son los puertos publicados en el host. El puerto de pgAdmin está enlazado solo a `localhost`. Cambia la contraseña de desarrollo antes de compartir el entorno.

### Grafana y eventos Kafka

- **Grafana:** abre `http://localhost:3000`. El usuario es `GRAFANA_ADMIN_USER` (por defecto `admin`) y la contraseña es `GRAFANA_ADMIN_PASSWORD` del `.env`; Compose exige que esta variable esté definida. Loki ya está provisionado como datasource. Para investigar el fallback de clientes, ve a **Explore → Loki** y consulta `{container="account-service"} |= "customer-service no disponible"`.
- **Kafka / AKHQ:** abre `http://localhost:8083`. El Compose no configura autenticación para AKHQ; úsalo solo en desarrollo local. Los eventos de movimientos se publican en el topic `movement-events`.
- **Dashboard de microservicios:** abre `http://localhost:3000/d/microservices-ops/microservices-operacion` (o la carpeta **Microservices**). Incluye volumen de logs separado por servicio, errores recientes de account/customer, logs completos por servicio y un contador de intentos de fallback de customer-service en account-service.
- El dashboard también presenta las cuatro señales doradas de SRE por servicio: **tráfico** (HTTP requests/s), **errores** (ratio HTTP 5xx), **latencia** (p95 HTTP) y **saturación** (CPU del proceso y heap JVM). Prometheus queda en `http://localhost:9090`; los scrapes se realizan cada 15 segundos y se excluye `/actuator/**` de las métricas de tráfico/error/latencia para que las sondas no contaminen los resultados.

RedisInsight no está incluido: es un visor para Redis, y este proyecto no define un servicio Redis. Para Kafka, el visor disponible es AKHQ.

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
| Reporte completo | `GET /api/v1/reports/users` | Todos los clientes con sus cuentas y movimientos |

### Seguridad y operación

- Todos los endpoints de `account-service` requieren HTTP Basic; solo health e info son públicos. Configura `ACCOUNT_API_USER` y `ACCOUNT_API_PASSWORD`; en producción sirve el API detrás de TLS. `customer-service` no se modifica en este alcance y conserva su seguridad actual.
- `POST /api/v1/movements` requiere `Idempotency-Key`. Reutilizar la misma clave devuelve el movimiento previo sin volver a aplicar el saldo.
- `customer-service` tiene timeout configurable (`CUSTOMER_SERVICE_TIMEOUT`), retry y circuit breaker. El último nombre recibido correctamente se guarda en PostgreSQL local y sirve como fallback durante una caída.
- Los movimientos y sus eventos se guardan juntos en PostgreSQL mediante outbox; Kafka se reintenta de forma asíncrona. La entrega es al menos una vez, por lo que los consumidores deben deduplicar por la clave Kafka (id del movimiento).
- Logs JSON de contenedores se envían por Grafana Alloy a Loki; Grafana queda en `http://localhost:3000`. Métricas Prometheus: `/actuator/prometheus`; health: `/actuator/health`.

Los cambios de datos de cliente no se aceptan desde `account-service`: el contrato de este servicio solo consulta clientes. La caché se refresca al completar una lectura remota; una cola de escrituras offline requiere definir qué cambios puede iniciar account y formalizar ese comando en el contrato de customer.

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

## ✒️ Autor

**Santiago Andres Moreta** – Software Engineer | Cloud Engineer
