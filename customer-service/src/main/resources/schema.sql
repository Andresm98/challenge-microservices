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