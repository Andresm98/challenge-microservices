DROP TABLE IF EXISTS movements;
DROP TABLE IF EXISTS accounts;

CREATE TABLE accounts
(
    id              SERIAL PRIMARY KEY,
    account_number  VARCHAR(50)    NOT NULL,
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