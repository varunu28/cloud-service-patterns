CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS payments
(
    id
    UUID
    PRIMARY
    KEY
    DEFAULT
    uuid_generate_v4
(
),
    idempotency_key TEXT UNIQUE NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    customer_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now
(
),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now
(
)
    );

