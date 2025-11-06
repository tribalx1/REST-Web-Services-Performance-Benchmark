# JMeter Load Testing

## Overview

This directory contains JMeter test plans for benchmarking REST API variants.

## Test Scenarios

### 1. READ-heavy (with relations)
**File**: `scenario-1-read-heavy.jmx`

Load distribution:
- 50% GET /items?page=&size=50
- 20% GET /items?categoryId=...&page=&size=
- 20% GET /categories/{id}/items?page=&size=
- 10% GET /categories?page=&size=

Concurrency: 50 → 100 → 200 threads
Duration: 10 min/level, ramp-up 60s

### 2. JOIN-filter targeted
**File**: `scenario-2-join-filter.jmx`

Load distribution:
- 70% GET /items?categoryId=...&page=&size=
- 30% GET /items/{id}

Concurrency: 60 → 120 threads
Duration: 8 min/level, ramp-up 60s

### 3. MIXED (reads + writes)
**File**: `scenario-3-mixed.jmx`

Load distribution:
- 40% GET /items?page=...
- 20% POST /items (1 KB)
- 10% PUT /items/{id} (1 KB)
- 10% DELETE /items/{id}
- 10% POST /categories (0.5-1 KB)
- 10% PUT /categories/{id}

Concurrency: 50 → 100 threads
Duration: 10 min/level

## Prerequisites

### 1. Install JMeter

Download Apache JMeter 5.6+ from https://jmeter.apache.org/

```bash
# Windows
choco install jmeter

# macOS
brew install jmeter

# Linux
wget https://dlcdn.apache.org//jmeter/binaries/apache-jmeter-5.6.3.tgz
tar xzf apache-jmeter-5.6.3.tgz
```

### 2. Install InfluxDB Backend Listener Plugin

JMeter 5.5+ includes the InfluxDB backend listener by default.

For older versions:
```bash
# Download from JMeter Plugins Manager or manually
wget https://repo1.maven.org/maven2/kg/apc/jmeter-plugins-influxdb2-listener/2.7/jmeter-plugins-influxdb2-listener-2.7.jar
# Place in $JMETER_HOME/lib/ext/
```

### 3. Generate Test Data CSVs

```bash
cd ../database
python generate_test_data_for_jmeter.py
```

This creates:
- `category_ids.csv` - Random category IDs for parameterization
- `item_ids.csv` - Random item IDs for parameterization
- `payloads.csv` - Sample JSON payloads for POST/PUT

## Running Tests

### GUI Mode (for debugging)

```bash
jmeter -t scenario-1-read-heavy.jmx
```

### CLI Mode (for actual benchmarking)

```bash
# Scenario 1 - READ-heavy
jmeter -n -t scenario-1-read-heavy.jmx \
  -JHOST=localhost \
  -JPORT=8082 \
  -l results/variant-c-scenario1.jtl \
  -e -o results/variant-c-scenario1-report

# Scenario 2 - JOIN-filter
jmeter -n -t scenario-2-join-filter.jmx \
  -JHOST=localhost \
  -JPORT=8082 \
  -l results/variant-c-scenario2.jtl \
  -e -o results/variant-c-scenario2-report

# Scenario 3 - MIXED
jmeter -n -t scenario-3-mixed.jmx \
  -JHOST=localhost \
  -JPORT=8082 \
  -l results/variant-c-scenario3.jtl \
  -e -o results/variant-c-scenario3-report
```

### Test Each Variant

```bash
# Test Variant A (JAX-RS) - Port 8081
jmeter -n -t scenario-1-read-heavy.jmx -JHOST=localhost -JPORT=8081 \
  -l results/variant-a-scenario1.jtl -e -o results/variant-a-scenario1-report

# Test Variant C (Spring MVC) - Port 8082
jmeter -n -t scenario-1-read-heavy.jmx -JHOST=localhost -JPORT=8082 \
  -l results/variant-c-scenario1.jtl -e -o results/variant-c-scenario1-report

# Test Variant D (Spring Data REST) - Port 8083
jmeter -n -t scenario-1-read-heavy.jmx -JHOST=localhost -JPORT=8083 \
  -l results/variant-d-scenario1.jtl -e -o results/variant-d-scenario1-report
```

## Configuration

### Test Plan Variables

Each test plan uses these variables (set via -J parameters):

- `HOST`: Target hostname (default: localhost)
- `PORT`: Target port (8081/8082/8083)
- `THREADS_LEVEL1`: First concurrency level
- `THREADS_LEVEL2`: Second concurrency level
- `THREADS_LEVEL3`: Third concurrency level (scenario 1 only)
- `RAMPUP`: Ramp-up time in seconds
- `DURATION`: Duration per level in seconds

### InfluxDB Backend Listener

Configured in each test plan:

```
Implementation: org.apache.jmeter.visualizers.backend.influxdb.InfluxdbBackendListenerClient

Parameters:
- influxdbUrl: http://localhost:8086/api/v2/write?org=benchmark&bucket=jmeter&precision=ms
- influxdbToken: benchmark-token-12345
- application: variant-[a|c|d]
- testName: scenario-[1|2|3]
```

## Results Analysis

### JTL Files

Tab-separated results file containing:
- Timestamp
- Elapsed time
- Label
- Response code
- Response message
- Thread name
- Data type
- Success flag
- Failure message
- Bytes
- Latency
- Connect time

### HTML Reports

Generated with `-e -o` options, includes:
- Response time over time
- Throughput over time
- Response time percentiles
- Transactions per second
- Error rate

### Viewing in Grafana

Import JMeter dashboard and select:
- Application: variant-[a|c|d]
- Test name: scenario-[1|2|3]

## Best Practices

1. **Warm-up**: Run a 2-minute warm-up before actual test
2. **Isolation**: Test one variant at a time
3. **Monitoring**: Keep Grafana open during tests
4. **Cool-down**: Wait 5 minutes between tests
5. **Verification**: Check application logs for errors
6. **GC**: Monitor GC activity during tests
7. **Resources**: Ensure adequate system resources

## Troubleshooting

### Connection Refused

- Verify application is running: `curl http://localhost:PORT/actuator/health`
- Check firewall settings
- Verify PORT matches variant

### High Error Rate

- Check application logs
- Verify database connection
- Check system resources (CPU, memory)
- Reduce thread count

### InfluxDB Not Receiving Data

- Verify InfluxDB is running: `curl http://localhost:8086/health`
- Check token and bucket name
- Verify backend listener configuration

## Comparison Script

Run all scenarios for all variants:

```bash
# Windows PowerShell
.\run-all-tests.ps1

# Linux/macOS
./run-all-tests.sh
```

This will:
1. Test each variant (A, C, D)
2. Run all scenarios (1, 2, 3)
3. Generate reports
4. Export results to CSV
5. Create comparison charts
