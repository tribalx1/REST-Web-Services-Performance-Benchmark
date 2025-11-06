# 🚀 START HERE - REST Performance Benchmark

## Quick Start (5 Minutes)

### 1. Build Everything
```powershell
.\mvnw.cmd clean install -DskipTests
```

### 2. Generate Test Data
```powershell
cd jmeter
python generate_jmeter_data.py
cd ..
```

### 3. Start Services
```powershell
cd monitoring
docker-compose up -d
cd ..
```

### 4. Test a Variant
```powershell
# Start Variant C (Spring MVC)
.\RUN_VARIANT.ps1 C

# In another terminal, test it:
curl http://localhost:8082/actuator/health
curl http://localhost:8082/categories?page=0&size=10
```

---

## 📚 Complete Documentation

| Document | Purpose |
|----------|---------|
| **FINAL_PROJECT_STATUS.md** | Complete project status and what's been implemented |
| **EXECUTION_GUIDE.md** | Step-by-step guide for running tests |
| **RESULTS_ANALYSIS.md** | Template for documenting performance results |
| **QUICK_START.md** | 30-minute setup guide |

---

## 🎯 Three Variants to Compare

### Variant A - JAX-RS (Port 8081)
```powershell
.\RUN_VARIANT.ps1 A
```
- Manual REST endpoint implementation
- Jersey + Grizzly HTTP server
- Direct EntityManager usage

### Variant C - Spring MVC (Port 8082)
```powershell
.\RUN_VARIANT.ps1 C
```
- Spring @RestController
- Spring Data JPA repositories
- Service layer architecture

### Variant D - Spring Data REST (Port 8083)
```powershell
.\RUN_VARIANT.ps1 D
```
- Automatic REST endpoint generation
- HAL/HATEOAS format
- Zero controller code

---

## 📊 What to Test

1. **Throughput** - Requests per second
2. **Latency** - p50, p95, p99 response times
3. **Resources** - CPU, RAM, GC behavior
4. **N+1 Queries** - Impact of JOIN FETCH
5. **Abstraction Cost** - Manual vs auto-generated endpoints

---

## 🔍 Key Files

### For Development
- `pom.xml` - Parent build configuration
- `shared-model/` - Common entities and DTOs
- `variant-*/` - Implementation variants

### For Testing
- `jmeter/` - Test data CSVs
- `database/` - Schema and data generation
- `monitoring/` - Docker infrastructure

### For Running
- `RUN_VARIANT.ps1` - Quick launcher
- `setup-and-build.ps1` - Automated setup

---

## ✅ Prerequisites

- Java 17
- Python 3.7+
- Docker & Docker Compose
- PostgreSQL client (psql)
- JMeter 5.6+ (optional)

---

## 🆘 Need Help?

1. Read **EXECUTION_GUIDE.md** for detailed instructions
2. Check **FINAL_PROJECT_STATUS.md** for implementation details
3. See troubleshooting section in **EXECUTION_GUIDE.md**

---

## 📈 Next Steps

1. ✅ Build complete (you are here!)
2. ⏳ Setup infrastructure (Docker, PostgreSQL)
3. ⏳ Run variants and verify endpoints
4. ⏳ Execute load tests with JMeter
5. ⏳ Collect metrics from Grafana/Prometheus
6. ⏳ Fill RESULTS_ANALYSIS.md template
7. ⏳ Generate final report

---

**Ready to start testing!** 🎉

Run `.\setup-and-build.ps1` for automated setup or follow **EXECUTION_GUIDE.md** for manual steps.
