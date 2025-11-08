# Tableaux de Benchmark - REST Performance Benchmark

## T0 — Configuration matérielle & logicielle

| Élément | Valeur |
|---------|--------|
| Machine (CPU, cœurs, RAM) | Intel Core i5-6200U @ 2.30GHz, 2 cores, 4 logical processors, 7.8 GB RAM |
| OS / Kernel | Microsoft Windows 10 Pro, Build 19045 |
| Java version | Java 23 (build 23+37-2369) - HotSpot 64-Bit Server VM |
| Docker/Compose versions | Docker 28.0.1, Docker Compose v2.33.1 |
| PostgreSQL version | PostgreSQL 14.19 (Alpine Linux, Docker container) |
| JMeter version | JMeter 5.6+ (à vérifier avec votre installation) |
| Prometheus / Grafana / InfluxDB | Prometheus:latest, Grafana:latest, InfluxDB:2.7-alpine |
| JVM flags (Xms/Xmx, GC) | Default Spring Boot settings (-Xmx calculated by JVM, G1GC by default on Java 23) |
| HikariCP (min/max/timeout) | min-idle: 10, max-pool-size: 20, connection-timeout: 30000ms, idle-timeout: 600000ms |

---

## T1 — Scénarios

| Scénario | Mix | Threads (paliers) | Ramp-up | Durée/palier | Payload |
|----------|-----|-------------------|---------|--------------|---------|
| READ-heavy (relation) | 50% items list, 20% items by category, 20% cat→items, 10% cat list | 50→100→200 | 60s | 10 min | -- |
| JOIN-filter | 70% items?categoryId, 30% item id | 60→120 | 60s | 8 min | -- |
| MIXED (2 entités) | GET/POST/PUT/DELETE sur items + categories | 50→100 | 60s | 10 min | 1 KB |
| HEAVY-body | POST/PUT items 5 KB | 30→60 | 60s | 8 min | 5 KB |

---

## T2 — Résultats par scénario et variante

**À remplir après les tests JMeter**

| Scénario | Mesure | A : Jersey | C : @RestController | D : Spring Data REST |
|----------|--------|------------|---------------------|----------------------|
| READ-heavy | RPS | ___ | ___ | ___ |
| READ-heavy | p50 (ms) | ___ | ___ | ___ |
| READ-heavy | p95 (ms) | ___ | ___ | ___ |
| READ-heavy | p99 (ms) | ___ | ___ | ___ |
| READ-heavy | Err % | ___ | ___ | ___ |
| JOIN-filter | RPS | ___ | ___ | ___ |
| JOIN-filter | p50 (ms) | ___ | ___ | ___ |
| JOIN-filter | p95 (ms) | ___ | ___ | ___ |
| JOIN-filter | p99 (ms) | ___ | ___ | ___ |
| JOIN-filter | Err % | ___ | ___ | ___ |
| MIXED (2 entités) | RPS | ___ | ___ | ___ |
| MIXED (2 entités) | p50 (ms) | ___ | ___ | ___ |
| MIXED (2 entités) | p95 (ms) | ___ | ___ | ___ |
| MIXED (2 entités) | p99 (ms) | ___ | ___ | ___ |
| MIXED (2 entités) | Err % | ___ | ___ | ___ |
| HEAVY-body | RPS | ___ | ___ | ___ |
| HEAVY-body | p50 (ms) | ___ | ___ | ___ |
| HEAVY-body | p95 (ms) | ___ | ___ | ___ |
| HEAVY-body | p99 (ms) | ___ | ___ | ___ |
| HEAVY-body | Err % | ___ | ___ | ___ |

---

## T3 — Ressources JVM (Prometheus)

**À remplir en consultant Prometheus/Grafana pendant les tests**

| Variante | CPU proc. (%) moy/pic | Heap (Mo) moy/pic | GC time (ms/s) moy/pic | Threads actifs moy/pic | Hikari (actifs/max) |
|----------|----------------------|-------------------|------------------------|------------------------|---------------------|
| A : Jersey | ___/___% | ___/___ Mo | ___/___ ms/s | ___/___ | ___/20 |
| C : @RestController | ___/___% | ___/___ Mo | ___/___ ms/s | ___/___ | ___/20 |
| D : Spring Data REST | ___/___% | ___/___ Mo | ___/___ ms/s | ___/___ | ___/20 |

### Métriques Prometheus à consulter :

- **CPU**: `process_cpu_usage` ou `system_cpu_usage`
- **Heap**: `jvm_memory_used_bytes{area="heap"}`
- **GC time**: `jvm_gc_pause_seconds_sum`
- **Threads**: `jvm_threads_live`
- **HikariCP**: `hikaricp_connections_active`, `hikaricp_connections_max`

---

## T4 — Détails par endpoint (scénario JOIN-filter)

**À remplir avec les résultats JMeter pour le scénario JOIN-filter**

| Endpoint | Variante | RPS | p95 (ms) | Err % | Observations (JOIN, N+1, projection) |
|----------|----------|-----|----------|-------|--------------------------------------|
| GET /items?categoryId= | A | ___ | ___ | ___% | ___ |
|  | C | ___ | ___ | ___% | ___ |
|  | D | ___ | ___ | ___% | ___ |
| GET /categories/{id}/items | A | ___ | ___ | ___% | ___ |
|  | C | ___ | ___ | ___% | ___ |
|  | D | ___ | ___ | ___% | ___ |

---

## T5 — Détails par endpoint (scénario MIXED)

**À remplir avec les résultats JMeter pour le scénario MIXED**

| Endpoint | Variante | RPS | p95 (ms) | Err % | Observations |
|----------|----------|-----|----------|-------|--------------|
| GET /items | A | ___ | ___ | ___% | ___ |
|  | C | ___ | ___ | ___% | ___ |
|  | D | ___ | ___ | ___% | ___ |
| POST /items | A | ___ | ___ | ___% | ___ |
|  | C | ___ | ___ | ___% | ___ |
|  | D | ___ | ___ | ___% | ___ |
| PUT /items/{id} | A | ___ | ___ | ___% | ___ |
|  | C | ___ | ___ | ___% | ___ |
|  | D | ___ | ___ | ___% | ___ |
| DELETE /items/{id} | A | ___ | ___ | ___% | ___ |
|  | C | ___ | ___ | ___% | ___ |
|  | D | ___ | ___ | ___% | ___ |
| GET /categories | A | ___ | ___ | ___% | ___ |
|  | C | ___ | ___ | ___% | ___ |
|  | D | ___ | ___ | ___% | ___ |
| POST /categories | A | ___ | ___ | ___% | ___ |
|  | C | ___ | ___ | ___% | ___ |
|  | D | ___ | ___ | ___% | ___ |

---

## T6 — Incidents / erreurs

**À remplir au fur et à mesure des tests**

| Run | Variante | Type d'erreur (HTTP/DB/timeout) | % | Cause probable | Action corrective |
|-----|----------|----------------------------------|---|----------------|-------------------|
| ___ | ___ | ___ | ___% | ___ | ___ |
| ___ | ___ | ___ | ___% | ___ | ___ |

---

## T7 — Synthèse & conclusion

**À remplir après l'analyse complète de tous les tests**

| Critère | Meilleure variante | Écart (justifier) | Commentaires |
|---------|-------------------|-------------------|--------------|
| Débit global (RPS) | ___ | ___% vs ___ | ___ |
| Latence p95 | ___ | ___ms vs ___ | ___ |
| Stabilité (erreurs) | ___ | ___% vs ___% | ___ |
| Empreinte CPU/RAM | ___ | ___% vs ___% | ___ |
| Facilité d'appro relationnelle | ___ | JOIN FETCH / N+1 / HAL | ___ |

---

## Notes pour remplir les tableaux

### Comment obtenir les données :

1. **T0 (Configuration)** : ✅ Déjà rempli ci-dessus

2. **T2-T5 (Résultats de performance)** :
   - Exécutez les tests JMeter pour chaque scénario
   - Ouvrez les rapports HTML générés par JMeter
   - Consultez les fichiers `.jtl` pour les métriques détaillées
   - Formule: `jmeter/results/scenario-X-variant-Y/`

3. **T3 (Ressources JVM)** :
   - Accédez à Prometheus: http://localhost:9090
   - Ou Grafana: http://localhost:3000
   - Utilisez les requêtes PromQL listées ci-dessus
   - Prenez les valeurs moyennes et pics pendant la durée du test

4. **T6 (Incidents)** :
   - Consultez les logs des applications
   - Vérifiez les erreurs dans les rapports JMeter
   - Examinez les logs Docker: `docker logs benchmark-postgres`

5. **T7 (Synthèse)** :
   - Comparez tous les résultats des tableaux précédents
   - Calculez les écarts en pourcentage
   - Identifiez les patterns (ex: Spring Data REST + HAL overhead)

### Commandes utiles pour la collecte de données :

```powershell
# Démarrer un variant
.\RUN_VARIANT.ps1 A  # ou C ou D

# Vérifier l'état
curl http://localhost:8081/actuator/health  # Variant A
curl http://localhost:8082/actuator/health  # Variant C
curl http://localhost:8083/actuator/health  # Variant D

# Voir les métriques Prometheus
curl http://localhost:8082/actuator/prometheus

# Consulter les logs
docker logs benchmark-postgres
docker logs -f variant-c-springmvc  # si lancé en Docker

# Lancer JMeter (mode CLI)
cd jmeter
jmeter -n -t scenario-1-read-heavy.jmx -JHOST=localhost -JPORT=8082 \
  -l results/variant-c-read-heavy.jtl \
  -e -o results/variant-c-read-heavy-report
```

### Endpoints disponibles par variante :

**Variant A (JAX-RS) - Port 8081:**
- GET /api/categories
- GET /api/categories/{id}
- GET /api/items
- GET /api/items/{id}
- GET /api/items/by-category/{categoryId}
- POST /api/categories
- POST /api/items

**Variant C (Spring MVC) - Port 8082:**
- GET /categories
- GET /categories/{id}
- GET /categories/{id}/items
- GET /items
- GET /items/{id}
- GET /items?categoryId={id}
- POST /categories
- POST /items
- PUT /items/{id}
- DELETE /items/{id}

**Variant D (Spring Data REST) - Port 8083:**
- GET /categories
- GET /categories/{id}
- GET /items
- GET /items/{id}
- GET /items/search/by-category?categoryId={id}
- POST /categories
- POST /items
- PUT /items/{id}
- DELETE /items/{id}

(Tous avec format HAL/HATEOAS)
