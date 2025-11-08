# 📊 Benchmark Tables - Summary of Filled Data

**Date**: November 8, 2025  
**Tests Completed**: Variant A & C (READ-heavy scenario)  
**Status**: Partial - Variant D pending

---

## ✅ What's Been Filled

### T0 — Configuration (100% Complete)
- ✅ All system information filled
- CPU, RAM, OS, Java, Docker versions documented

### T2 — Results by Scenario (20% Complete)
**Filled for READ-heavy scenario:**
- ✅ Variant A (Jersey): 122.25 RPS, p50=140ms, p95=251ms, p99=331ms, 0% errors
- ✅ Variant C (Spring MVC): 90.74 RPS, p50=142ms, p95=431ms, p99=814ms, 0% errors
- ❌ Variant D: Not tested yet
- ❌ Other scenarios (JOIN-filter, MIXED, HEAVY-body): Not tested yet

### T5 — Endpoint Details (30% Complete)
**Filled for GET requests:**
- ✅ GET /items - A: 61.14 RPS, p95=305ms | C: 45.39 RPS, p95=564ms
- ✅ GET /categories - A: 61.47 RPS, p95=210ms | C: 45.50 RPS, p95=230ms
- ❌ POST, PUT, DELETE operations: Not tested yet
- ❌ Variant D: Not tested yet

### T7 — Synthesis (50% Complete)
**Filled with A vs C comparison:**
- ✅ Throughput: A wins (+34.7%)
- ✅ Latency p95: A wins (-41.8%)
- ✅ Latency p99: A wins (-59.3%)
- ✅ Stability: Both equal (0% errors)
- ❌ CPU/RAM metrics: Need Grafana data
- ❌ Complete analysis: Need Variant D

---

## 🔴 What's Still Missing

### T1 — Scenarios Definition
- ⚠️ Need to verify actual test configuration matches planned scenarios
- Current test used: 20 threads, 30s ramp-up, 5 min duration
- Original plan: 50→100→200 threads, 60s ramp-up, 10 min duration

### T2 — Complete Scenario Results
**Need to run these tests:**
- ❌ JOIN-filter scenario (items?categoryId queries)
- ❌ MIXED scenario (GET/POST/PUT/DELETE mix)
- ❌ HEAVY-body scenario (5KB payloads)
- ❌ All scenarios for Variant D

### T3 — JVM Resources (0% Complete)
**Need from Grafana/Prometheus:**
- ❌ CPU usage (avg/peak) for each variant
- ❌ Heap memory (avg/peak) for each variant
- ❌ GC time (avg/peak) for each variant
- ❌ Active threads (avg/peak) for each variant
- ❌ HikariCP connections (active/max) for each variant

**How to get this data:**
1. Open Grafana: http://localhost:3000 (admin/admin)
2. While test is running, note the metrics from dashboards
3. Or query Prometheus: http://localhost:9090
   - `process_cpu_usage`
   - `jvm_memory_used_bytes{area="heap"}`
   - `jvm_gc_pause_seconds_sum`
   - `jvm_threads_live`
   - `hikaricp_connections_active`

### T4 — JOIN-filter Details (0% Complete)
**Need to test:**
- ❌ GET /items?categoryId= endpoint performance
- ❌ GET /categories/{id}/items endpoint performance
- ❌ All three variants
- ❌ Observations on JOIN strategy, N+1 queries, projections

### T5 — MIXED Scenario Details (30% Complete)
**Still need:**
- ❌ POST /items performance
- ❌ PUT /items/{id} performance
- ❌ DELETE /items/{id} performance
- ❌ POST /categories performance
- ❌ Variant D for all operations

### T6 — Incidents/Errors (0% Complete)
**Good news:** No errors in current tests (0%)
- But should document any issues encountered during testing
- Example: Variant C had latency spikes (max 10.4s)

---

## 🎯 Next Steps to Complete Tables

### Immediate Priority (High Value):

1. **Get Grafana Metrics for A & C** (15 minutes)
   - Open Grafana while variants are running
   - Screenshot or note down the values
   - Fill T3 table

2. **Test Variant D** (30 minutes)
   - Stop other variants
   - Start Variant D: `.\RUN_VARIANT.ps1 D`
   - Run JMeter test: `.\RUN_TEST_DOCKER.ps1 D`
   - Fill corresponding cells in T2, T5, T7

### Medium Priority:

3. **Create/Run JOIN-filter Test** (1 hour)
   - Need to create a JMeter test for this scenario
   - Test all three variants
   - Fill T4 table

4. **Create/Run MIXED Test** (1 hour)
   - Need JMeter test with POST/PUT/DELETE operations
   - Test all three variants
   - Complete T5 table

### Lower Priority:

5. **Create/Run HEAVY-body Test** (1 hour)
   - Test with 5KB payloads
   - Fill remaining T2 rows

---

## 📈 Key Findings So Far

### Performance Winner: Variant A (Jersey)
- **+34.7% better throughput** (122 vs 91 RPS)
- **~2x better p95 latency** (251ms vs 431ms)
- **~2.5x better p99 latency** (331ms vs 814ms)
- **More consistent** (max latency: 1.7s vs 10.4s)

### Why is Jersey faster?
Possible reasons (need to verify with JVM metrics):
1. **Lower overhead**: JAX-RS is lighter than Spring MVC
2. **Simpler request pipeline**: Fewer interceptors/filters
3. **Better memory usage**: Less object creation
4. **Optimized serialization**: Jersey's JSON handling

### Concerns with Variant C:
- Latency spikes up to **10.4 seconds** (vs 1.7s for A)
- Could indicate GC pauses or connection pool issues
- Need to check Grafana for memory/GC metrics

---

## 🛠️ Quick Commands Reference

### Start a variant:
```powershell
.\RUN_VARIANT.ps1 A  # or C or D
```

### Run JMeter test:
```powershell
cd jmeter
.\RUN_TEST_DOCKER.ps1 A  # or C or D
```

### Check if variant is running:
```powershell
curl http://localhost:8081/actuator/health  # Variant A
curl http://localhost:8082/actuator/health  # Variant C
curl http://localhost:8083/actuator/health  # Variant D
```

### Access monitoring:
- Grafana: http://localhost:3000 (admin/admin)
- Prometheus: http://localhost:9090
- JMeter Reports: `jmeter\results\variant-X-report-XXXXX\index.html`

### Stop all variants:
```powershell
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force
```

---

## 📊 Data Sources

| Table | Data Source | Location |
|-------|-------------|----------|
| T0 | System info | Already filled |
| T1 | Test config | JMX files + manual documentation |
| T2 | JMeter results | `jmeter/results/*/statistics.json` |
| T3 | Grafana/Prometheus | http://localhost:3000 (during test) |
| T4 | JMeter results | JOIN-filter test results |
| T5 | JMeter results | MIXED test results |
| T6 | Logs + JMeter | Error logs + JMeter error reports |
| T7 | Analysis | Calculated from T2-T6 |

---

## 💡 Tips for Getting Good Data

1. **Let tests run completely** - Don't interrupt
2. **One variant at a time** - Avoid memory issues
3. **Open Grafana BEFORE starting test** - Capture full metrics
4. **Screenshot Grafana dashboards** - For documentation
5. **Check JMeter HTML reports** - More detailed than JSON
6. **Run tests multiple times** - Verify consistency
7. **Document anomalies** - Note any spikes or errors in T6

---

## ✅ Completion Status

- [x] T0 Configuration: **100%**
- [ ] T1 Scenarios: **50%** (need to verify actual configs)
- [ ] T2 Results: **20%** (1 scenario, 2 variants)
- [ ] T3 JVM Resources: **0%** (need Grafana data)
- [ ] T4 JOIN Details: **0%** (need to create/run test)
- [ ] T5 MIXED Details: **30%** (GET only, 2 variants)
- [ ] T6 Incidents: **0%** (no errors to report yet)
- [ ] T7 Synthesis: **50%** (partial A vs C comparison)

**Overall Progress: ~30%**

---

## 🎯 Recommended Next Action

**Option 1 - Quick Win (30 min):**
Run Variant D test to get A vs C vs D comparison:
```powershell
# Stop current variants
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force

# Start Variant D
.\RUN_VARIANT.ps1 D

# In new terminal - run test
cd jmeter
.\RUN_TEST_DOCKER.ps1 D
```

**Option 2 - Complete Analysis (2-3 hours):**
1. Get Grafana metrics for A & C (rerun with monitoring)
2. Test Variant D
3. Create and run JOIN-filter scenario
4. Create and run MIXED scenario
5. Complete all tables

**My Recommendation:** Start with Option 1 to get Variant D data, then move to complete analysis if needed for your project.
