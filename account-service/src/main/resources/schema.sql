DROP TABLE IF EXISTS movements;
DROP TABLE IF EXISTS accounts;



CREATE TABLE accounts
(
    id              SERIAL PRIMARY KEY,
    account_number  VARCHAR(50)    NOT NULL UNIQUE,
    account_type    VARCHAR(50)    NOT NULL,
    initial_balance DECIMAL(15, 2) NOT NULL,
    status          BOOLEAN        NOT NULL,
    customer_id     INTEGER        NOT NULL
);


CREATE TABLE movements
(
    id            SERIAL PRIMARY KEY,
    date          TIMESTAMP      NOT NULL,
    movement_type VARCHAR(20)    NOT NULL,
    value         DECIMAL(15, 2) NOT NULL,
    balance       DECIMAL(15, 2) NOT NULL,
    account_id    INT REFERENCES accounts (id)
);



insert into accounts (account_number, account_type, initial_balance, status, customer_id) values
('478758', 'Ahorros', 2000.00, TRUE, 1),
('225487', 'Corriente', 100.00, TRUE, 2),
('495878', 'Ahorros', 0.00, TRUE, 3),
('496825', 'Ahorros', 540.00, TRUE, 2);


-- insert into movements (date, movement_type, value, balance, account_id) values
-- ('2026-01-20 14:30:00', 'Retiro', 2000.00, 2000.00, 1),
-- ('2026-01-21 10:00:00', 'Depósito', 500.00, 1500.00, 1);