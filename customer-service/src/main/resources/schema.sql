CREATE TABLE IF NOT EXISTS customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    gender VARCHAR(20),
    age INT,
    identification VARCHAR(20) UNIQUE,
    address VARCHAR(200),
    phone VARCHAR(20),
    password VARCHAR(50),
    status BOOLEAN DEFAULT TRUE
    );

INSERT INTO customers
(name, gender, age, identification, address, phone, password, status)
VALUES
    ('Jose Lema', 'Masculino', 30, 'ID123456', 'Av. Siempre Viva 742', '098254785', '1234', TRUE),
    ('Marianela Montalvo', 'Femenino', 25, 'ID66666', 'Amazonas y NNUU', '097548965', '5678', TRUE),
    ('Juan Osorio', 'Masculino', 66, 'ID77777', '13 junio y Equinoccial', '098874587', '1245', TRUE)
ON CONFLICT (identification) DO NOTHING;

CREATE TABLE IF NOT EXISTS customer_event_outbox
(
    id          BIGSERIAL PRIMARY KEY,
    event_id    VARCHAR(36)  NOT NULL UNIQUE,
    event_type  VARCHAR(20)  NOT NULL,
    customer_id BIGINT       NOT NULL,
    payload     TEXT         NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    published   BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS customer_movement_activity
(
    event_id      VARCHAR(100) PRIMARY KEY,
    movement_id   BIGINT       NOT NULL UNIQUE,
    customer_id   BIGINT       NOT NULL,
    account_id    BIGINT       NOT NULL,
    movement_type VARCHAR(20)  NOT NULL,
    value         DECIMAL(15, 2) NOT NULL,
    balance       DECIMAL(15, 2) NOT NULL,
    occurred_at   TIMESTAMP    NOT NULL
);