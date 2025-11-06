-- =============================================
-- REST Benchmark Database Schema
-- PostgreSQL 14+
-- =============================================

-- Drop existing tables if they exist
DROP TABLE IF EXISTS item CASCADE;
DROP TABLE IF EXISTS category CASCADE;

-- Create Category table
CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(32) UNIQUE NOT NULL,
    name VARCHAR(128) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create Item table
CREATE TABLE item (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(64) UNIQUE NOT NULL,
    name VARCHAR(128) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    stock INT NOT NULL,
    category_id BIGINT NOT NULL REFERENCES category(id),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create indexes
CREATE INDEX idx_item_category ON item(category_id);
CREATE INDEX idx_item_updated_at ON item(updated_at);
CREATE INDEX idx_category_code ON category(code);
CREATE INDEX idx_item_sku ON item(sku);

-- Display table information
\d category
\d item
