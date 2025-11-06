# REST Performance Benchmark - Execution Guide

Complete step-by-step guide to run the performance benchmark.

---

## Prerequisites Check

Before starting, ensure you have:

- ✅ Java 17 installed (`java -version`)
- ✅ Maven installed or use `mvnw.cmd` wrapper
- ✅ Docker and Docker Compose installed
- ✅ Python 3.7+ installed
- ✅ PostgreSQL client tools (psql) installed
- ✅ JMeter 5.6+ (optional, for load testing)
- ✅ At least 8GB RAM available
- ✅ Ports available: 5432, 8081, 8082, 8083, 3000, 8086, 9090

---

## Quick Start (Automated)

### Option 1: Full Automated Setup

```powershell
# Run the complete setup script
.\setup-and-build.ps1
```

This script will:
1. Build all Maven modules
2. Generate JMeter test data
3. Start Docker infrastructure
4. Setup database with test data
5. Verify the setup

### Option 2: Manual Setup

Follow the steps below for more control.

---

## Step-by-Step Manual Setup

### Step 1: Build the Project (5 min)

```powershell
# Using Maven wrapper (recommended)
.\mvnw.cmd clean install -DskipTests

# Or using Maven if installed
mvn clean install -DskipTests
```

**Expected Output:**
```
[INFO] REST Performance Benchmark ................. SUCCESS
[INFO] Shared Model ............................... SUCCESS
[INFO] Variant A - JAX-RS Jersey .................. SUCCESS
[INFO] Variant C - Spring MVC ..................... SUCCESS
[INFO] Variant D - Spring Data REST ............... SUCCESS
[INFO] BUILD SUCCESS
```

**Verify JARs created:**
```powershell
# Check all JARs exist
ls variant-a-jaxrs\target\*.jar
ls variant-c-springmvc\target\*.jar
ls variant-d-springdata\target\*.jar
```

---

### Step 2: Generate Test Data (2 min)

```powershell
cd jmeter
python generate_jmeter_data.py
cd ..
```

**Files created:**
- `category_ids.csv` - 1000 random category IDs
- `item_ids.csv` - 1000 random item IDs
- `category_payloads.csv` - JSON payloads for categories
- `item_payloads_1kb.csv` - ~1KB item payloads
- `item_payloads_5kb.csv` - ~5KB item payloads

---

### Step 3: Start Infrastructure (5 min)

```powershell
cd monitoring
docker-compose up -d
```

**Services started:**
- PostgreSQL on port 5432
- Prometheus on port 9090
- Grafana on port 3000
- InfluxDB on port 8086

**Verify services:**
```powershell
docker-compose ps
```

All services should show status "Up".

**Wait for initialization:**
```powershell
Start-Sleep -Seconds 30
```

---

### Step 4: Setup Database (5 min)

```powershell
cd ..\database

# Generate database test data
python generate_data.py
```

**Load schema and data:**
```powershell
# Set PostgreSQL password
$env:PGPASSWORD = "postgres"

# Create database
psql -U postgres -h localhost -d postgres -c "DROP DATABASE IF EXISTS benchmark;"
psql -U postgres -h localhost -d postgres -c "CREATE DATABASE benchmark;"

# Load schema
psql -U postgres -h localhost -d benchmark -f schema.sql

# Load data (if files exist)
psql -U postgres -h localhost -d benchmark -f categories.sql
psql -U postgres -h localhost -d benchmark -f items.sql
```

**Verify data loaded:**
```powershell
psql -U postgres -h localhost -d benchmark -c "SELECT COUNT(*) FROM category;"
# Expected: 2000

psql -U postgres -h localhost -d benchmark -c "SELECT COUNT(*) FROM item;"
# Expected: ~100000
```

---

## Running the Variants

### Variant A - JAX-RS (Jersey) - Port 8081

```powershell
# Option 1: Quick run
.\RUN_VARIANT.ps1 A

# Option 2: Manual run
java -jar variant-a-jaxrs\target\variant-a-jaxrs-1.0.0-SNAPSHOT.jar

# Option 3: With JMX monitoring
java -javaagent:monitoring\jmx_prometheus_javaagent-0.20.0.jar=8091:monitoring\jmx-config.yml `
     -jar variant-a-jaxrs\target\variant-a-jaxrs-1.0.0-SNAPSHOT.jar
```

**Test endpoints:**
```powershell
curl http://localhost:8081/actuator/health
curl http://localhost:8081/categories?page=0&size=10
curl http://localhost:8081/items?page=0&size=10
```

---

### Variant C - Spring MVC - Port 8082

```powershell
# Option 1: Quick run
.\RUN_VARIANT.ps1 C

# Option 2: Manual run
cd variant-c-springmvc
mvn spring-boot:run

# Option 3: Using JAR
mvn package
java -jar target\variant-c-springmvc-1.0.0-SNAPSHOT.jar
```

**Test endpoints:**
```powershell
curl http://localhost:8082/actuator/health
curl http://localhost:8082/categories?page=0&size=10
curl http://localhost:8082/items?page=0&size=10
curl http://localhost:8082/items?categoryId=1&page=0&size=10
```

---

### Variant D - Spring Data REST - Port 8083

```powershell
# Option 1: Quick run
.\RUN_VARIANT.ps1 D

# Option 2: Manual run
cd variant-d-springdata
mvn spring-boot:run

# Option 3: Using JAR
mvn package
java -jar target\variant-d-springdata-1.0.0-SNAPSHOT.jar
```

**Test endpoints:**
```powershell
curl http://localhost:8083/actuator/health
curl http://localhost:8083/
curl http://localhost:8083/categories?page=0&size=10
curl http://localhost:8083/items?page=0&size=10
curl http://localhost:8083/items/search/by-category?categoryId=1
```

---

## Testing N+1 Query Prevention

Each variant supports toggling JOIN FETCH optimization:

```powershell
# Disable JOIN FETCH (test N+1 problem)
$env:USE_JOIN_FETCH = "false"
.\RUN_VARIANT.ps1 C

# Enable JOIN FETCH (optimized)
$env:USE_JOIN_FETCH = "true"
.\RUN_VARIANT.ps1 C
```

Monitor the difference in:
- Query count (check application logs)
- Response time
- Database load

---

## Running Load Tests with JMeter

### Setup JMeter

1. Download JMeter from https://jmeter.apache.org/download_jmeter.cgi
2. Extract to a location (e.g., `C:\apache-jmeter-5.6.3`)
3. Set environment variable:

```powershell
$env:JMETER_HOME = "C:\apache-jmeter-5.6.3"
```

### Run Tests

**Important:** Only run tests against ONE variant at a time!

#### Test Variant A (JAX-RS) - Port 8081

```powershell
# 1. Start Variant A
.\RUN_VARIANT.ps1 A

# 2. In another terminal, run tests
cd jmeter
# Configure for port 8081 and run your test scenarios
```

#### Test Variant C (Spring MVC) - Port 8082

```powershell
# 1. Stop other variants
# 2. Start Variant C
.\RUN_VARIANT.ps1 C

# 3. Run tests on port 8082
```

#### Test Variant D (Spring Data REST) - Port 8083

```powershell
# 1. Stop other variants  
# 2. Start Variant D
.\RUN_VARIANT.ps1 D

# 3. Run tests on port 8083
```

### Test Scenarios

Create JMeter test plans for:

1. **READ-heavy** - Focus on GET requests with relations
2. **JOIN-filter** - Test category filtering
3. **MIXED** - Combination of GET/POST/PUT/DELETE
4. **HEAVY-body** - Large payload (5KB) POST/PUT

---

## Monitoring and Metrics

### Grafana Dashboards

1. Open http://localhost:3000
2. Login: `admin` / `admin`
3. Add data sources:
   - Prometheus: `http://prometheus:9090`
   - InfluxDB: `http://influxdb:8086` (org: benchmark, token: benchmark-token-12345)
4. Import dashboards:
   - JVM Micrometer (ID: 4701)
   - Spring Boot (ID: 6756)

### Prometheus Queries

Visit http://localhost:9090 and try queries:

```promql
# JVM Memory Usage
jvm_memory_used_bytes{application="variant-c-springmvc"}

# HTTP Request Rate
rate(http_server_requests_seconds_count[1m])

# HikariCP Active Connections
hikaricp_connections_active{pool="HikariCP-VariantC"}

# GC Time
rate(jvm_gc_pause_seconds_sum[1m])
```

### Application Metrics Endpoints

```powershell
# Variant A
curl http://localhost:8081/actuator/prometheus

# Variant C
curl http://localhost:8082/actuator/prometheus

# Variant D
curl http://localhost:8083/actuator/prometheus
```

---

## Performance Testing Workflow

### 1. Baseline Test (No Load)

```powershell
# Start variant
.\RUN_VARIANT.ps1 C

# Make simple requests
for ($i=1; $i -le 100; $i++) {
    curl -s http://localhost:8082/items?page=0&size=20 | Out-Null
    Write-Host "Request $i"
}
```

### 2. Load Test (JMeter)

Use JMeter to simulate concurrent users and measure:
- Throughput (requests/second)
- Response time (p50, p95, p99)
- Error rate
- Resource consumption

### 3. Stress Test

Gradually increase load until:
- Error rate increases
- Response time degrades
- Resources saturate

### 4. Collect Metrics

Export from:
- JMeter summary reports
- Grafana dashboards (screenshots)
- Prometheus metrics (CSV export)

---

## Collecting Results

### Export JMeter Results

JMeter generates:
- `.jtl` files (raw results)
- HTML reports (aggregate statistics)
- CSV files (time series data)

### Export Grafana Dashboards

1. Open dashboard in Grafana
2. Share → Export → Save to file → Export for external sharing
3. Take screenshots of key graphs

### Query Prometheus Data

```powershell
# Example: Export CPU usage
curl "http://localhost:9090/api/v1/query_range?query=process_cpu_usage&start=2024-01-01T00:00:00Z&end=2024-01-01T01:00:00Z&step=15s"
```

---

## Filling the Analysis Template

Use `RESULTS_ANALYSIS.md` to document:

1. **T0** - Configuration details
2. **T1** - Test scenarios executed
3. **T2** - JMeter results per scenario
4. **T3** - JVM resource consumption
5. **T4** - Endpoint-specific results
6. **T5** - Mixed scenario breakdown
7. **T6** - Errors and incidents
8. **T7** - Final analysis and recommendations

---

## Troubleshooting

### Port Already in Use

```powershell
# Find process using port
netstat -ano | findstr :8082

# Kill process (replace PID)
taskkill /PID <pid> /F
```

### Database Connection Refused

```powershell
# Check if PostgreSQL is running
docker-compose -f monitoring\docker-compose.yml ps postgres

# Restart if needed
docker-compose -f monitoring\docker-compose.yml restart postgres
```

### OutOfMemoryError

```powershell
# Increase JVM heap
java -Xms2g -Xmx4g -jar variant-c-springmvc\target\variant-c-springmvc-1.0.0-SNAPSHOT.jar
```

### Maven Build Fails

```powershell
# Clean and rebuild
.\mvnw.cmd clean install -DskipTests -U

# Check Java version
java -version  # Must be 17
```

---

## Cleanup

### Stop All Services

```powershell
# Stop Docker services
cd monitoring
docker-compose down

# Optional: Remove volumes
docker-compose down -v
```

### Reset Database

```powershell
$env:PGPASSWORD = "postgres"
psql -U postgres -h localhost -d postgres -c "DROP DATABASE IF EXISTS benchmark;"
```

---

## Summary Checklist

Before starting tests:
- [ ] All variants built successfully
- [ ] Docker services running
- [ ] Database loaded with test data
- [ ] JMeter CSV files generated
- [ ] Grafana configured with data sources
- [ ] One variant running and responding to health checks

During tests:
- [ ] Only ONE variant running at a time
- [ ] Monitoring dashboards open
- [ ] JMeter Backend Listener configured for InfluxDB
- [ ] Recording screenshots/metrics

After tests:
- [ ] Export JMeter results
- [ ] Export Grafana dashboards
- [ ] Fill RESULTS_ANALYSIS.md
- [ ] Save all evidence (screenshots, CSV, logs)

---

**Good luck with your performance benchmark!** 🚀
