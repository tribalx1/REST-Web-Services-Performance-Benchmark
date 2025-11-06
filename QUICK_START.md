# Quick Start Guide

This guide will help you set up and run the REST performance benchmark in under 30 minutes.

## Prerequisites

- **Java 17** - [Download](https://adoptium.net/)
- **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
- **PostgreSQL 14+** - [Download](https://www.postgresql.org/download/)
- **Docker & Docker Compose** - [Download](https://www.docker.com/products/docker-desktop/)
- **Python 3.7+** - For data generation
- **JMeter 5.6+** (optional) - For load testing

## Step 1: Clone and Build (5 min)

```bash
# Navigate to project
cd c:\benchmark

# Build all modules
mvn clean install -DskipTests

# This compiles:
# - shared-model
# - variant-c-springmvc
# - variant-d-springdata
# - variant-a-jaxrs (when implemented)
```

## Step 2: Start Infrastructure (5 min)

```bash
# Start PostgreSQL, Prometheus, Grafana, InfluxDB
cd monitoring
docker-compose up -d

# Verify services are running
docker-compose ps

# Wait for services to be ready (~30 seconds)
```

Access points:
- PostgreSQL: `localhost:5432`
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin)
- InfluxDB: http://localhost:8086

## Step 3: Setup Database (5 min)

```bash
# Navigate to database directory
cd ..\database

# Generate test data (2,000 categories, ~100,000 items)
python generate_data.py

# Load data into PostgreSQL
psql -U postgres -d benchmark -f schema.sql
psql -U postgres -d benchmark -f load_data.sql

# Verify data loaded
psql -U postgres -d benchmark -c "SELECT COUNT(*) FROM category;"
# Should return: 2000

psql -U postgres -d benchmark -c "SELECT COUNT(*) FROM item;"
# Should return: ~100000
```

## Step 4: Download JMX Exporter (2 min)

```bash
cd ..\monitoring

# Download JMX Prometheus Exporter
curl -LO https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.20.0/jmx_prometheus_javaagent-0.20.0.jar
```

## Step 5: Run Variants (2 min each)

### Variant C - Spring MVC (Port 8082)

```bash
cd ..\variant-c-springmvc

# Option 1: Using Maven
mvn spring-boot:run

# Option 2: Using JAR with JMX monitoring
mvn package
java -javaagent:..\monitoring\jmx_prometheus_javaagent-0.20.0.jar=8092:..\monitoring\jmx-config.yml ^
     -jar target\variant-c-springmvc-1.0.0-SNAPSHOT.jar
```

### Variant D - Spring Data REST (Port 8083)

```bash
cd ..\variant-d-springdata

# Option 1: Using Maven
mvn spring-boot:run

# Option 2: Using JAR with JMX monitoring
mvn package
java -javaagent:..\monitoring\jmx_prometheus_javaagent-0.20.0.jar=8093:..\monitoring\jmx-config.yml ^
     -jar target\variant-d-springdata-1.0.0-SNAPSHOT.jar
```

## Step 6: Verify Applications (2 min)

### Variant C - Spring MVC

```bash
# Health check
curl http://localhost:8082/actuator/health

# Test endpoints
curl http://localhost:8082/categories?page=0&size=10
curl http://localhost:8082/items?page=0&size=10
curl http://localhost:8082/items?categoryId=1&page=0&size=10
curl http://localhost:8082/categories/1/items?page=0&size=10

# Prometheus metrics
curl http://localhost:8082/actuator/prometheus
```

### Variant D - Spring Data REST

```bash
# Health check
curl http://localhost:8083/actuator/health

# Test endpoints (HAL format)
curl http://localhost:8083/
curl http://localhost:8083/categories?page=0&size=10
curl http://localhost:8083/items?page=0&size=10
curl http://localhost:8083/items/search/by-category?categoryId=1&page=0&size=10

# Prometheus metrics
curl http://localhost:8083/actuator/prometheus
```

## Step 7: Configure Grafana (5 min)

1. **Open Grafana**: http://localhost:3000 (admin/admin)

2. **Add Prometheus Data Source**:
   - Configuration → Data Sources → Add data source
   - Select "Prometheus"
   - URL: `http://prometheus:9090`
   - Click "Save & Test"

3. **Add InfluxDB Data Source**:
   - Configuration → Data Sources → Add data source
   - Select "InfluxDB"
   - Query Language: Flux
   - URL: `http://influxdb:8086`
   - Organization: `benchmark`
   - Token: `benchmark-token-12345`
   - Default Bucket: `jmeter`
   - Click "Save & Test"

4. **Import Dashboards**:
   - Create → Import
   - Import JVM Dashboard (ID: 4701)
   - Import Spring Boot Dashboard (ID: 6756)

## Step 8: Run Simple Load Test (5 min)

### Using curl in a loop (simple smoke test)

```bash
# PowerShell - 100 requests to Variant C
1..100 | ForEach-Object {
    Invoke-WebRequest -Uri "http://localhost:8082/items?page=0&size=20" | Out-Null
    Write-Host "Request $_"
}

# Check response times in Grafana
```

### Using JMeter (proper load test)

```bash
cd ..\jmeter

# Install JMeter if not already installed
# Download from: https://jmeter.apache.org/download_jmeter.cgi

# Set JMETER_HOME environment variable
$env:JMETER_HOME = "C:\path\to\apache-jmeter-5.6.3"

# Run a simple test (GUI mode for first time)
& "$env:JMETER_HOME\bin\jmeter.bat" -t scenario-1-read-heavy.jmx

# Or run in CLI mode
& "$env:JMETER_HOME\bin\jmeter.bat" -n -t scenario-1-read-heavy.jmx `
    -JHOST=localhost -JPORT=8082 `
    -l results\test1.jtl -e -o results\test1-report
```

## Common Issues & Solutions

### Issue: Port already in use

```bash
# Find process using port
netstat -ano | findstr :8082

# Kill process (replace PID)
taskkill /PID <pid> /F
```

### Issue: Database connection refused

```bash
# Check if PostgreSQL is running
docker-compose ps

# Restart if needed
docker-compose restart postgres

# Check connection
psql -U postgres -d benchmark -c "SELECT 1;"
```

### Issue: Maven build fails

```bash
# Clean and rebuild
mvn clean install -DskipTests -U

# If still fails, check Java version
java -version  # Should be 17
```

### Issue: Out of memory

```bash
# Increase JVM heap size
java -Xmx2g -Xms1g -javaagent:... -jar app.jar

# Or set MAVEN_OPTS
$env:MAVEN_OPTS = "-Xmx2g"
mvn spring-boot:run
```

### Issue: Prometheus not scraping

1. Check Prometheus targets: http://localhost:9090/targets
2. Verify application is running on correct port
3. Check JMX agent is attached: look for startup log message
4. For Spring variants: ensure actuator endpoints are enabled

## Next Steps

### 1. Compare Variants

Start all variants (ports 8081, 8082, 8083) and compare:
- Throughput
- Latency (p50, p95, p99)
- Resource usage (CPU, memory)
- GC behavior

### 2. Run Full Benchmark Suite

```bash
cd jmeter
.\run-all-tests.ps1
```

This runs all scenarios against all variants and generates comparison reports.

### 3. Analyze Results

1. Open Grafana dashboards
2. Review JMeter HTML reports in `jmeter/results/`
3. Fill out analysis template: `docs/ANALYSIS_TEMPLATE.md`

### 4. Experiment with Configurations

Test impact of:
- Connection pool size
- JVM heap size
- JOIN FETCH vs N+1 queries (toggle `USE_JOIN_FETCH` env var)
- Pagination size

## Useful Commands

```bash
# Check application status
curl http://localhost:8082/actuator/health

# Monitor JVM metrics
curl http://localhost:8082/actuator/metrics/jvm.memory.used

# Check database connections
curl http://localhost:8082/actuator/metrics/hikaricp.connections.active

# View application logs
# (Check terminal where application is running)

# Stop all Docker services
cd monitoring
docker-compose down

# Stop with volume cleanup
docker-compose down -v
```

## Directory Structure Reference

```
benchmark/
├── shared-model/          # JPA entities & DTOs
├── variant-c-springmvc/   # Spring MVC implementation
├── variant-d-springdata/  # Spring Data REST implementation
├── variant-a-jaxrs/       # JAX-RS implementation (TBD)
├── database/              # SQL scripts & data generation
├── jmeter/                # Load test plans
├── monitoring/            # Prometheus, Grafana, Docker configs
├── docs/                  # Analysis templates & documentation
└── README.md             # Project overview
```

## Support

For issues or questions:
1. Check `README.md` in each module
2. Review `monitoring/README.md` for monitoring setup
3. Check `jmeter/README.md` for load testing guide
4. Review `database/README.md` for database setup

---

**Estimated Total Time**: ~30 minutes for first-time setup

Happy benchmarking! 🚀
