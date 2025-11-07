CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT,
    full_name TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE banks (
    id SERIAL PRIMARY KEY,
    name TEXT,
    api_url TEXT,
    capital NUMERIC(18,2),
    initial_capital NUMERIC(18,2),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE consent_statuses (
    id SERIAL PRIMARY KEY,
    name TEXT
);

CREATE TABLE consents (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    bank_id INT REFERENCES banks(id),
    consent_external_id TEXT UNIQUE NOT NULL,
    consent_type_id INT,
    status_id INT REFERENCES consent_statuses(id),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    role TEXT UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE user_roles (
    user_id INT REFERENCES users(id),
    role_id INT REFERENCES roles(role_id)
);

CREATE TABLE IF NOT EXISTS accounts (
     id SERIAL PRIMARY KEY,
     user_id INT REFERENCES users(id),
    bank_id INT REFERENCES banks(id),
    account_number TEXT NOT NULL,
    name TEXT,
    balance NUMERIC(18,2),
    currency TEXT,
    type TEXT,
     created_at TIMESTAMPTZ DEFAULT now()

);

CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    account_id INT REFERENCES accounts(id),
    bank_id INT REFERENCES banks(id),
    date TIMESTAMPTZ DEFAULT now(),
    amount NUMERIC(18,2),
    category TEXT,
    description TEXT,
    created_at TIMESTAMPTZ DEFAULT now()

);

  

CREATE TABLE IF NOT EXISTS goals (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    account_id INT REFERENCES accounts(id),
    name TEXT,
    target_amount NUMERIC(18,2),
    current_amount NUMERIC(18,2) DEFAULT 0,
    deadline DATE,
    created_at TIMESTAMPTZ DEFAULT now()

);

CREATE TABLE IF NOT EXISTS assistant_queries (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    question TEXT,
    answer TEXT,
    created_at TIMESTAMPTZ DEFAULT now()

);