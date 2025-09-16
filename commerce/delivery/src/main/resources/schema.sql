CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS addresses (
    address_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country    VARCHAR(100) NOT NULL,
    city       VARCHAR(100) NOT NULL,
    street     VARCHAR(255) NOT NULL,
    house      VARCHAR(5)   NOT NULL,
    flat       VARCHAR(5)
    );

CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_address_id UUID NOT NULL,
    to_address_id   UUID NOT NULL,
    order_id        UUID NOT NULL,
    delivery_state  VARCHAR(20) NOT NULL,
    delivery_weight DOUBLE PRECISION NOT NULL,
    delivery_volume DOUBLE PRECISION NOT NULL,
    fragile         BOOLEAN NOT NULL,
    FOREIGN KEY (from_address_id) REFERENCES addresses(address_id),
    FOREIGN KEY (to_address_id) REFERENCES addresses(address_id)
    );