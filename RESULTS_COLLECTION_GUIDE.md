# Guide de Collecte des Résultats - Benchmark REST

## 📋 Ordre d'Exécution

### Étape 1: Tests JMeter (T2, T4, T5)

Pour chaque variante (A, C, D), exécutez les 4 scénarios:

```powershell
# Démarrer une variante
.\RUN_VARIANT.ps1 C  # Changez pour A ou D

# Dans un autre terminal, lancez les tests JMeter
cd jmeter

# Scénario 1: READ-heavy
jmeter -n -t scenario-1-read-heavy.jmx `
  -JHOST=localhost -JPORT=8082 `
  -l results/C-read-heavy.jtl `
  -e -o results/C-read-heavy-report

# Scénario 2: JOIN-filter
jmeter -n -t scenario-2-join-filter.jmx `
  -JHOST=localhost -JPORT=8082 `
  -l results/C-join-filter.jtl `
  -e -o results/C-join-filter-report

# Scénario 3: MIXED
jmeter -n -t scenario-3-mixed.jmx `
  -JHOST=localhost -JPORT=8082 `
  -l results/C-mixed.jtl `
  -e -o results/C-mixed-report

# Scénario 4: HEAVY-body
jmeter -n -t scenario-4-heavy-body.jmx `
  -JHOST=localhost -JPORT=8082 `
  -l results/C-heavy-body.jtl `
  -e -o results/C-heavy-body-report
```

### Étape 2: Collecter Métriques JVM via Prometheus (T3)

Pendant que les tests tournent, ouvrez Grafana ou Prometheus:

**Grafana**: http://localhost:3000 (admin/admin)

**Requêtes Prometheus** à exécuter sur http://localhost:9090 :

```promql
# CPU Process Usage (%)
rate(process_cpu_usage[5m]) * 100

# Heap Memory (Mo)
jvm_memory_used_bytes{area="heap"} / 1024 / 1024

# GC Time (ms/s)
rate(jvm_gc_pause_seconds_sum[1m]) * 1000

# Threads actifs
jvm_threads_live

# HikariCP connections actives
hikaricp_connections_active

# HikariCP connections max
hikaricp_connections_max
```

---

## 📊 T2 - Extraction depuis JMeter Report

Ouvrez le rapport HTML: `jmeter/results/C-read-heavy-report/index.html`

### Dans "Statistics" tab:

| Métrique JMeter | Colonne Tableau T2 |
|-----------------|-------------------|
| Throughput (req/sec) | RPS |
| 50th pct | p50 (ms) |
| 95th pct | p95 (ms) |
| 99th pct | p99 (ms) |
| Error % | Err % |

### Template T2:

```
READ-heavy | Variant C
- RPS: _____ req/s
- p50: _____ ms
- p95: _____ ms
- p99: _____ ms
- Err%: _____%

JOIN-filter | Variant C
- RPS: _____ req/s
- p50: _____ ms
- p95: _____ ms
- p99: _____ ms
- Err%: _____%

MIXED | Variant C
- RPS: _____ req/s
- p50: _____ ms
- p95: _____ ms
- p99: _____ ms
- Err%: _____%

HEAVY-body | Variant C
- RPS: _____ req/s
- p50: _____ ms
- p95: _____ ms
- p99: _____ ms
- Err%: _____%
```

Répétez pour Variants A et D.

---

## 🔍 T3 - Métriques JVM (Prometheus/Grafana)

### Méthode 1: Via Grafana Dashboard

1. Créez un dashboard avec ces panels
2. Sélectionnez la période du test
3. Notez moyenne (avg) et pic (max)

### Méthode 2: Via Prometheus Query

Pendant le test, exécutez sur http://localhost:9090/graph :

```promql
# CPU moyen sur 10 min
avg_over_time(process_cpu_usage{application="variant-c-springmvc"}[10m]) * 100

# CPU pic
max_over_time(process_cpu_usage{application="variant-c-springmvc"}[10m]) * 100

# Heap moyen (Mo)
avg_over_time(jvm_memory_used_bytes{area="heap",application="variant-c-springmvc"}[10m]) / 1024 / 1024

# Heap pic (Mo)
max_over_time(jvm_memory_used_bytes{area="heap",application="variant-c-springmvc"}[10m]) / 1024 / 1024
```

### Template T3:

```
Variant A (JAX-RS):
- CPU: avg ___%, pic ___% 
- Heap: avg ___ Mo, pic ___ Mo
- GC time: avg ___ ms/s, pic ___ ms/s
- Threads: avg ___, pic ___
- Hikari: actifs ___, max 20

Variant C (Spring MVC):
- CPU: avg ___%, pic ___% 
- Heap: avg ___ Mo, pic ___ Mo
- GC time: avg ___ ms/s, pic ___ ms/s
- Threads: avg ___, pic ___
- Hikari: actifs ___, max 20

Variant D (Spring Data REST):
- CPU: avg ___%, pic ___% 
- Heap: avg ___ Mo, pic ___ Mo
- GC time: avg ___ ms/s, pic ___ ms/s
- Threads: avg ___, pic ___
- Hikari: actifs ___, max 20
```

---

## 📍 T4 - Détails par Endpoint (JOIN-filter)

Dans le rapport JMeter `C-join-filter-report/index.html`:

Allez dans l'onglet **"Statistics"** et filtrez par Transaction Name:

### Endpoints à chercher:

1. `GET /items?categoryId=X` ou `GET /items/by-category/X`
2. `GET /categories/{id}/items`

Pour chaque endpoint, notez:
- Throughput → RPS
- 95th pct → p95
- Error % → Err %

### Template T4:

```
Scénario JOIN-filter

GET /items?categoryId=
- Variant A: RPS ___, p95 ___ ms, Err ___%, Notes: ___
- Variant C: RPS ___, p95 ___ ms, Err ___%, Notes: ___
- Variant D: RPS ___, p95 ___ ms, Err ___%, Notes: ___

GET /categories/{id}/items:
- Variant A: RPS ___, p95 ___ ms, Err ___%, Notes: ___
- Variant C: RPS ___, p95 ___ ms, Err ___%, Notes: ___
- Variant D: RPS ___, p95 ___ ms, Err ___%, Notes: ___
```

**Observations à noter**:
- Utilisation de JOIN FETCH? (Regardez les logs Hibernate si `show_sql=true`)
- Problème N+1? (Nombre de requêtes SQL pour 1 requête HTTP)
- Format réponse? (JSON simple vs HAL/HATEOAS)

---

## 📍 T5 - Détails par Endpoint (MIXED)

Dans le rapport JMeter `C-mixed-report/index.html`:

### Endpoints MIXED:

1. GET /items
2. POST /items
3. PUT /items/{id}
4. DELETE /items/{id}
5. GET /categories
6. POST /categories

Pour chaque endpoint × variante:
- Throughput → RPS
- 95th pct → p95
- Error % → Err %

### Template T5:

```
Scénario MIXED

GET /items:
- A: RPS ___, p95 ___ ms, Err ___%
- C: RPS ___, p95 ___ ms, Err ___%
- D: RPS ___, p95 ___ ms, Err ___%

POST /items:
- A: RPS ___, p95 ___ ms, Err ___%
- C: RPS ___, p95 ___ ms, Err ___%
- D: RPS ___, p95 ___ ms, Err ___%

PUT /items/{id}:
- A: RPS ___, p95 ___ ms, Err ___%
- C: RPS ___, p95 ___ ms, Err ___%
- D: RPS ___, p95 ___ ms, Err ___%

DELETE /items/{id}:
- A: RPS ___, p95 ___ ms, Err ___%
- C: RPS ___, p95 ___ ms, Err ___%
- D: RPS ___, p95 ___ ms, Err ___%

GET /categories:
- A: RPS ___, p95 ___ ms, Err ___%
- C: RPS ___, p95 ___ ms, Err ___%
- D: RPS ___, p95 ___ ms, Err ___%

POST /categories:
- A: RPS ___, p95 ___ ms, Err ___%
- C: RPS ___, p95 ___ ms, Err ___%
- D: RPS ___, p95 ___ ms, Err ___%
```

---

## ⚠️ T6 - Incidents / Erreurs

### Où chercher les erreurs:

1. **Rapports JMeter**: Section "Errors" dans les rapports HTML
2. **Logs applications**: 
   ```powershell
   # Dans le terminal où tourne la variante
   # Chercher les exceptions, stack traces
   ```
3. **Logs PostgreSQL**:
   ```powershell
   docker logs benchmark-postgres | findstr ERROR
   ```

### Template T6:

```
Run 1 | Variant C | Timeout HTTP | 2.3% | Connection pool saturé | Augmenter HikariCP max
Run 2 | Variant D | 500 Internal | 0.8% | NullPointerException | Fix code ligne X
```

---

## 📊 T7 - Synthèse

### Calcul des écarts:

```
Écart % = ((Meilleur - Moins bon) / Moins bon) * 100
```

**Exemple**:
- Variant C: 850 RPS
- Variant D: 650 RPS
- Écart: ((850-650)/650)*100 = 30.8% plus rapide

### Template T7:

```
DÉBIT GLOBAL (RPS):
- Meilleure variante: ___
- Écart: ___% plus rapide que ___
- Commentaires: ___

LATENCE P95:
- Meilleure variante: ___
- Écart: ___ ms de différence vs ___
- Commentaires: ___

STABILITÉ (erreurs):
- Meilleure variante: ___
- Écart: ___% vs ___% d'erreurs
- Commentaires: ___

EMPREINTE CPU/RAM:
- Meilleure variante: ___
- Écart: ___% moins de CPU, ___ Mo moins de RAM
- Commentaires: ___

FACILITÉ D'APPROCHE RELATIONNELLE:
- Meilleure variante: ___
- Observations: JOIN FETCH natif? N+1 évité? HAL overhead?
- Commentaires: ___
```

---

## 🎯 Checklist Complète

- [ ] T0: Configuration documentée ✅ (déjà fait)
- [ ] T1: Scénarios définis ✅ (déjà définis)
- [ ] T2: Tests JMeter exécutés pour les 3 variantes × 4 scénarios
- [ ] T3: Métriques JVM collectées via Prometheus
- [ ] T4: Détails JOIN-filter extraits
- [ ] T5: Détails MIXED extraits
- [ ] T6: Incidents documentés
- [ ] T7: Synthèse comparative rédigée

---

## 🚀 Commandes Rapides

```powershell
# Démarrer les services
cd monitoring
docker-compose up -d

# Variante A
.\RUN_VARIANT.ps1 A
# Test sur http://localhost:8081

# Variante C
.\RUN_VARIANT.ps1 C
# Test sur http://localhost:8082

# Variante D
.\RUN_VARIANT.ps1 D
# Test sur http://localhost:8083

# Arrêter proprement
# Ctrl+C dans le terminal de la variante

# Vérifier les services
docker ps
curl http://localhost:8082/actuator/health
```
