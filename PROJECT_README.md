# REST API Performance Benchmarking Suite

## 📋 Project Overview
This project is a comprehensive benchmarking suite designed to compare the performance of three different Java REST API implementations:
1. **Variant A**: JAX-RS (Jersey) implementation
2. **Variant C**: Spring MVC with @RestController
3. **Variant D**: Spring Data REST

The benchmark evaluates various performance metrics including response times, throughput, and resource utilization under different load scenarios.

## 🏗️ Project Structure
```
benchmark-main/
├── database/           # Database schema and data initialization
├── jmeter/             # JMeter test plans and data
│   ├── scripts/        # JMeter test scenarios
│   └── results/        # Test execution results
├── monitoring/         # Monitoring tools configuration
│   ├── grafana/        # Grafana dashboards and provisioning
│   └── prometheus/     # Prometheus configuration
├── variant-a-jaxrs/    # JAX-RS implementation
├── variant-c-springmvc # Spring MVC implementation
└── variant-d-springdata # Spring Data REST implementation
```

## 🛠️ Technology Stack

### Backend Implementations
1. **Variant A (JAX-RS)**
   - Java 17
   - Jersey (JAX-RS 3.1.0)
   - Hibernate 6.0
   - Maven

2. **Variant C (Spring MVC)**
   - Java 17
   - Spring Boot 3.0.0
   - Spring Data JPA
   - Hibernate 6.0
   - Maven

3. **Variant D (Spring Data REST)**
   - Java 17
   - Spring Boot 3.0.0
   - Spring Data REST
   - Hibernate 6.0
   - Maven

### Database
- **PostgreSQL 14**
  - Tables: category, item
  - ~2,000 categories
  - ~100,000 items

### Testing & Monitoring
- **JMeter 5.6.2** - Load testing
- **InfluxDB 2.7** - Time-series database for metrics
- **Grafana 10.2.0** - Visualization and dashboards
- **Prometheus 2.47.0** - Metrics collection

## 🚀 Getting Started

### Prerequisites
- Docker Desktop for Windows
- At least 8GB free RAM
- At least 2 CPU cores
- Java 17 JDK
- Maven 3.8+

### Quick Start
1. **Start the monitoring stack**:
   ```powershell
   docker-compose up -d influxdb grafana prometheus
   ```

2. **Build the project**:
   ```powershell
   .\setup-and-build.ps1
   ```

3. **Run a variant** (A, C, or D):
   ```powershell
   .\RUN_VARIANT.ps1 A  # For Variant A (JAX-RS)
   ```

4. **Run a benchmark**:
   ```powershell
   .\run_benchmark.ps1 A read_heavy 50 60 600
   ```

## 📊 Test Scenarios

### 1. READ-heavy Workload
- **Objective**: Test read performance with high concurrency
- **Operations**:
  - 50% GET /items
  - 20% GET /items?categoryId=X
  - 20% GET /categories/X/items
  - 10% GET /categories

### 2. JOIN-filter Workload
- **Objective**: Test complex queries with joins
- **Operations**:
  - 70% GET /items?categoryId=X
  - 30% GET /items/X

### 3. MIXED Workload
- **Objective**: Test mixed read/write operations
- **Operations**:
  - 40% GET /items
  - 20% POST /items
  - 10% PUT /items/X
  - 10% DELETE /items/X
  - 10% POST /categories
  - 10% PUT /categories/X

### 4. HEAVY-body Workload
- **Objective**: Test with large payloads
- **Operations**:
  - 50% POST /items (5KB payload)
  - 50% PUT /items/X (5KB payload)

## 📈 Performance Metrics
The benchmark collects the following metrics:
- **Throughput**: Requests per second (RPS)
- **Response Times**: p50, p95, p99 percentiles
- **Error Rate**: Percentage of failed requests
- **Resource Usage**:
  - CPU utilization
  - Memory consumption
  - Garbage collection metrics
  - Database connection pool stats

## 📊 Monitoring Setup
1. **Grafana**: http://localhost:3000 (admin/admin)
   - Pre-configured dashboards for:
     - Application metrics
     - JVM metrics
     - Database performance

2. **Prometheus**: http://localhost:9090
   - Raw metrics collection
   - Alert rules

3. **InfluxDB**: http://localhost:8086 (admin/admin123)
   - Time-series data storage
   - Query interface at http://localhost:8086

## 📝 Test Execution
1. **Single Test**:
   ```powershell
   .\run_benchmark.ps1 A read_heavy 50 60 600
   # Parameters: variant scenario threads rampup duration
   ```

2. **Full Test Suite**:
   ```powershell
   .\run_full_benchmark.ps1
   ```

## 📂 Results
Test results are stored in the `jmeter/results` directory with the following structure:
```
results/
├── [variant]_[scenario]_[timestamp]/
│   ├── result.jtl        # Raw JMeter results
│   ├── jmeter.log        # JMeter execution log
│   └── report/           # HTML report
│       ├── index.html    # Dashboard
│       └── statistics.json
```

## 🧪 Test Data
The database is pre-populated with:
- 2,000 categories
- 100,000 items (~50 items per category)

## 📚 Documentation
- [BENCHMARK_GUIDE.md](BENCHMARK_GUIDE.md) - Detailed usage instructions
- `database/` - Database schema and data loading scripts
- `monitoring/` - Monitoring setup and configuration

## 🤝 Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📧 Contact
For questions or feedback, please open an issue in the repository.
