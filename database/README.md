# Database Setup

## Prerequisites

- PostgreSQL 14 or higher
- Python 3.7+ (for data generation)

## Setup Steps

### 1. Create Database

```bash
# Login to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE benchmark;

# Connect to database
\c benchmark

# Exit
\q
```

### 2. Create Schema

```bash
psql -U postgres -d benchmark -f schema.sql
```

### 3. Generate Test Data

```bash
python generate_data.py
```

This will create:
- `categories.csv` - 2,000 categories (CAT0001..CAT2000)
- `items.csv` - ~100,000 items (~50 items per category)

### 4. Load Data

```bash
psql -U postgres -d benchmark -f load_data.sql
```

## Database Configuration

Default connection settings for all variants:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/benchmark
spring.datasource.username=postgres
spring.datasource.password=postgres

# HikariCP Configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

## Verify Data

```sql
-- Check counts
SELECT COUNT(*) FROM category;  -- Should be 2000
SELECT COUNT(*) FROM item;      -- Should be ~100000

-- Check distribution
SELECT 
    MIN(cnt) as min_items,
    MAX(cnt) as max_items,
    ROUND(AVG(cnt), 2) as avg_items
FROM (
    SELECT category_id, COUNT(*) as cnt 
    FROM item 
    GROUP BY category_id
) AS distribution;

-- Check sample data
SELECT c.code, c.name, COUNT(i.id) as item_count
FROM category c
LEFT JOIN item i ON c.id = i.category_id
GROUP BY c.id, c.code, c.name
ORDER BY c.id
LIMIT 10;
```

## Cleanup

To reset the database:

```bash
psql -U postgres -d benchmark -f schema.sql
python generate_data.py
psql -U postgres -d benchmark -f load_data.sql
```
