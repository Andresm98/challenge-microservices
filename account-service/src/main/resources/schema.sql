CREATE TABLE IF NOT EXISTS accounts
(
    id              SERIAL PRIMARY KEY,
    account_number  VARCHAR(50)    NOT NULL UNIQUE,
    account_type    VARCHAR(50)    NOT NULL,
    initial_balance DECIMAL(15, 2) NOT NULL,
    status          BOOLEAN        NOT NULL,
    customer_id     INTEGER        NOT NULL
);


CREATE TABLE IF NOT EXISTS movements
(
    id            SERIAL PRIMARY KEY,
    date          TIMESTAMP      NOT NULL,
    movement_type VARCHAR(20)    NOT NULL,
    value         DECIMAL(15, 2) NOT NULL,
    balance       DECIMAL(15, 2) NOT NULL,
    account_id    INT REFERENCES accounts (id)
);

CREATE TABLE IF NOT EXISTS customer_cache
(
    id             BIGINT PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    gender         VARCHAR(20),
    age            INTEGER,
    identification VARCHAR(20),
    address        VARCHAR(200),
    phone          VARCHAR(20),
    status         BOOLEAN
);

ALTER TABLE customer_cache ADD COLUMN IF NOT EXISTS gender VARCHAR(20);
ALTER TABLE customer_cache ADD COLUMN IF NOT EXISTS age INTEGER;
ALTER TABLE customer_cache ADD COLUMN IF NOT EXISTS identification VARCHAR(20);
ALTER TABLE customer_cache ADD COLUMN IF NOT EXISTS address VARCHAR(200);
ALTER TABLE customer_cache ADD COLUMN IF NOT EXISTS phone VARCHAR(20);
ALTER TABLE customer_cache ADD COLUMN IF NOT EXISTS status BOOLEAN;

CREATE TABLE IF NOT EXISTS movement_outbox
(
    id          BIGSERIAL PRIMARY KEY,
    movement_id BIGINT    NOT NULL UNIQUE REFERENCES movements (id),
    created_at  TIMESTAMP NOT NULL,
    published   BOOLEAN   NOT NULL DEFAULT FALSE
);

ALTER TABLE movements ADD COLUMN IF NOT EXISTS idempotency_key VARCHAR(128);
CREATE UNIQUE INDEX IF NOT EXISTS movements_idempotency_key_uq ON movements (idempotency_key)
    WHERE idempotency_key IS NOT NULL;

insert into accounts (account_number, account_type, initial_balance, status, customer_id) values
('478758', 'Ahorros', 2000.00, TRUE, 1),
('225487', 'Corriente', 100.00, TRUE, 2),
('495878', 'Ahorros', 0.00, TRUE, 3),
('496825', 'Ahorros', 540.00, TRUE, 2)
ON CONFLICT (account_number) DO NOTHING;


-- insert into movements (date, movement_type, value, balance, account_id) values
-- ('2026-01-20 14:30:00', 'Retiro', 2000.00, 2000.00, 1),
-- ('2026-01-21 10:00:00', 'Depósito', 500.00, 1500.00, 1);