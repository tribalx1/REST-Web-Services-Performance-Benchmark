# REST Performance Benchmark - Setup Guide

## Prerequisites

- Docker Desktop for Windows
- At least 8GB of free RAM
- At least 2 CPU cores

## Getting Started

1. **Start the monitoring stack**:
   ```powershell
   docker-compose up -d influxdb grafana prometheus
   ```

2. **Start the application variant** (A, C, or D):
   ```powershell
   .\RUN_VARIANT.ps1 A  # For Variant A (JAX-RS)
   # OR
   .\RUN_VARIANT.ps1 C  # For Variant C (Spring MVC)
   # OR
   .\RUN_VARIANT.ps1 D  # For Variant D (Spring Data REST)
   ```

3. **Run a benchmark test**:
   ```powershell
   .\run_benchmark.ps1 A read_heavy 50 60 600
   ```
   Parameters:
   - `A`: Variant (A, C, or D)
   - `read_heavy`: Test scenario (read_heavy, join_filter, mixed, heavy_body)
   - `50`: Number of threads
   - `60`: Ramp-up time in seconds
   - `600`: Test duration in seconds

## Accessing Monitoring Tools

- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090
- **InfluxDB**: http://localhost:8086 (admin/admin123)

## Test Scenarios

1. **READ-heavy**:
   - 50% GET /items
   - 20% GET /items?categoryId=X
   - 20% GET /categories/X/items
   - 10% GET /categories

2. **JOIN-filter**:
   - 70% GET /items?categoryId=X
   - 30% GET /items/X

3. **MIXED**:
   - 40% GET /items
   - 20% POST /items
   - 10% PUT /items/X
   - 10% DELETE /items/X
   - 10% POST /categories
   - 10% PUT /categories/X

4. **HEAVY-body**:
   - 50% POST /items (5KB payload)
   - 50% PUT /items/X (5KB payload)

## Viewing Results

Test results are saved in the `jmeter/results` directory with timestamps. Each test run includes:
- HTML report
- JTL file with raw results
- JMeter log file

## Stopping Everything

```powershell
docker-compose down -v
```

## Troubleshooting

- If you get connection errors, make sure the application variant is running
- Check Docker logs for any container issues: `docker-compose logs -f`
- Increase Docker Desktop resources if tests are failing due to resource constraints
