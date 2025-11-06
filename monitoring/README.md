# Monitoring Setup

## Overview

This directory contains monitoring configuration for:
- **Prometheus**: Metrics collection from JVM and application
- **Grafana**: Visualization and dashboards
- **InfluxDB v2**: JMeter test results storage
- **JMX Exporter**: JVM metrics export to Prometheus

## Quick Start

### 1. Start Infrastructure

```bash
cd monitoring
docker-compose up -d
```

This starts:
- PostgreSQL on port 5432
- Prometheus on port 9090
- Grafana on port 3000
- InfluxDB on port 8086

### 2. Download JMX Exporter

```bash
# Download JMX Prometheus Exporter
curl -LO https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.20.0/jmx_prometheus_javaagent-0.20.0.jar
```

### 3. Run Variant with Monitoring

```bash
# Example: Variant C (Spring MVC)
cd ../variant-c-springmvc
mvn package
java -javaagent:../monitoring/jmx_prometheus_javaagent-0.20.0.jar=8092:../monitoring/jmx-config.yml \
     -jar target/variant-c-springmvc-1.0.0-SNAPSHOT.jar
```

Ports for JMX metrics:
- Variant A (JAX-RS): 8091
- Variant C (Spring MVC): 8092
- Variant D (Spring Data REST): 8093

### 4. Access Monitoring Tools

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **InfluxDB**: http://localhost:8086 (admin/adminpassword)

## Prometheus Configuration

Edit `prometheus.yml` to add or modify scrape targets. Current configuration:

- **Variant A**: JMX exporter on port 8091
- **Variant C**: Spring Actuator on port 8082/actuator/prometheus
- **Variant D**: Spring Actuator on port 8083/actuator/prometheus

## JMX Exporter Configuration

The `jmx-config.yml` exports:

### JVM Metrics
- Thread count, daemon threads, peak threads
- Heap and non-heap memory usage
- GC collection count and time
- Memory pool usage
- CPU load

### HikariCP Metrics
- Active/idle/total connections
- Threads awaiting connection
- Connection creation rate
- Connection timeout rate

### Hibernate Metrics
- Entity operations (insert, update, delete, load, fetch)
- Query execution count
- Cache hit/miss/put counts
- Session operations

## Grafana Dashboards

### Setup Prometheus Data Source

1. Login to Grafana (http://localhost:3000)
2. Go to Configuration > Data Sources
3. Add Prometheus data source
   - URL: http://prometheus:9090
   - Access: Server (default)
4. Save & Test

### Setup InfluxDB Data Source

1. Add InfluxDB data source
2. Configure:
   - Query Language: Flux
   - URL: http://influxdb:8086
   - Organization: benchmark
   - Token: benchmark-token-12345
   - Default Bucket: jmeter
3. Save & Test

### Import Dashboards

Import pre-configured dashboards from `grafana/dashboards/`:

1. JVM Metrics Dashboard
2. HikariCP Connection Pool Dashboard
3. Hibernate Statistics Dashboard
4. JMeter Performance Dashboard
5. Comparative Analysis Dashboard

## InfluxDB Setup for JMeter

JMeter Backend Listener configuration:

```
Backend Listener Implementation: org.apache.jmeter.visualizers.backend.influxdb.InfluxdbBackendListenerClient

Parameters:
- influxdbUrl: http://localhost:8086/api/v2/write?org=benchmark&bucket=jmeter&precision=ms
- influxdbToken: benchmark-token-12345
- application: variant-[a|c|d]
- testName: [scenario-name]
- nodeName: localhost
```

## Stopping Infrastructure

```bash
docker-compose down          # Stop containers
docker-compose down -v       # Stop and remove volumes (clean slate)
```

## Monitoring Checklist

Before running benchmarks:

- [ ] PostgreSQL is running and database is loaded
- [ ] Prometheus is running and scraping targets
- [ ] Grafana is accessible with dashboards configured
- [ ] InfluxDB is ready for JMeter data
- [ ] JMX Exporter JAR is downloaded
- [ ] Application started with JMX agent

## Troubleshooting

### Prometheus not scraping metrics

Check targets at http://localhost:9090/targets - all should be "UP"

### Grafana shows no data

- Verify Prometheus data source is configured
- Check that application is running with JMX agent
- For Spring variants, check /actuator/prometheus endpoint

### InfluxDB connection issues

- Verify InfluxDB is running: `docker ps | grep influxdb`
- Check token and organization name match
- Verify bucket "jmeter" exists

### High memory usage

Adjust Prometheus retention or reduce scrape frequency in `prometheus.yml`
