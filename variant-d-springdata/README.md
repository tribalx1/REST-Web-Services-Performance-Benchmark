# Variant D - Spring Data REST

## Overview

This variant uses **Spring Data REST** to automatically expose REST endpoints from Spring Data repositories. Endpoints are generated with minimal code, following HATEOAS principles with HAL format.

## Port

- **8083**

## Key Features

- Automatic REST endpoint generation
- HAL (Hypertext Application Language) format by default
- HATEOAS links included in responses
- Built-in pagination and sorting support
- Projections for customizing response payloads

## Endpoints

Spring Data REST automatically generates the following endpoints:

### Categories

- `GET /categories` - List all categories (paginated)
- `GET /categories/{id}` - Get category by ID
- `GET /categories/{id}/items` - Get items for a category (relational)
- `POST /categories` - Create category
- `PUT /categories/{id}` - Update category
- `PATCH /categories/{id}` - Partial update
- `DELETE /categories/{id}` - Delete category
- `GET /categories/search/by-code?code={code}` - Find by code

### Items

- `GET /items` - List all items (paginated)
- `GET /items/{id}` - Get item by ID
- `GET /items/{id}/category` - Get category for an item
- `POST /items` - Create item
- `PUT /items/{id}` - Update item
- `PATCH /items/{id}` - Partial update
- `DELETE /items/{id}` - Delete item
- `GET /items/search/by-sku?sku={sku}` - Find by SKU
- `GET /items/search/by-category?categoryId={id}` - Filter by category

### Pagination & Sorting

All collection endpoints support:
- `?page=0` - Page number (0-indexed)
- `?size=20` - Page size
- `?sort=id,asc` - Sort by field and direction

### Projections

Use projections to customize response payloads:
- `?projection=categoryExcerpt` - Simplified category view with item count
- `?projection=itemExcerpt` - Simplified item view with nested category info

## HAL Format

Responses include HATEOAS links:

```json
{
  "_embedded": {
    "items": [...]
  },
  "_links": {
    "self": { "href": "http://localhost:8083/items?page=0&size=20" },
    "next": { "href": "http://localhost:8083/items?page=1&size=20" },
    "profile": { "href": "http://localhost:8083/profile/items" }
  },
  "page": {
    "size": 20,
    "totalElements": 100000,
    "totalPages": 5000,
    "number": 0
  }
}
```

## Plain JSON (Optional)

To get plain JSON instead of HAL, add header:
```
Accept: application/json
```

Or configure in `application.yml` (currently disabled):
```yaml
spring:
  data:
    rest:
      default-media-type: application/json
```

## Running

```bash
cd variant-d-springdata
mvn spring-boot:run
```

Or with JAR:
```bash
mvn package
java -jar target/variant-d-springdata-1.0.0-SNAPSHOT.jar
```

## Configuration

Key configuration in `application.yml`:
- Port: 8083
- Base path: `/`
- Default page size: 20
- Max page size: 100
- HAL format enabled by default

## Testing

Browse the API:
```bash
# Get API root
curl http://localhost:8083/

# List categories
curl http://localhost:8083/categories

# List items with pagination
curl http://localhost:8083/items?page=0&size=50

# Filter items by category
curl http://localhost:8083/items/search/by-category?categoryId=1&page=0&size=20

# Get category with items
curl http://localhost:8083/categories/1/items

# With projection
curl http://localhost:8083/items?projection=itemExcerpt
```

## Monitoring

- Actuator: http://localhost:8083/actuator
- Health: http://localhost:8083/actuator/health
- Metrics: http://localhost:8083/actuator/metrics
- Prometheus: http://localhost:8083/actuator/prometheus
