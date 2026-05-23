CREATE TABLE IF NOT EXISTS inventories (
    id BIGSERIAL PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    order_id BIGINT NOT NULL,
    inventory_count INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS inventory_record (
    id BIGSERIAL PRIMARY KEY,
    inventory_id BIGINT NOT NULL,
    record_type VARCHAR(50) NOT NULL,
    inventory_count INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_inventory FOREIGN KEY(inventory_id) REFERENCES inventories(id) ON DELETE CASCADE
);