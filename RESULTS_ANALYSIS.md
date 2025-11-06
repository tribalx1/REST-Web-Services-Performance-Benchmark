# REST Performance Benchmark - Results Analysis

**Date:** _______________  
**Tester:** _______________  
**Environment:** _______________

---

## T0 — Configuration matérielle & logicielle

| Élément | Valeur |
|---------|--------|
| Machine (CPU, cœurs, RAM) | |
| OS / Kernel | |
| Java version | |
| Docker/Compose versions | |
| PostgreSQL version | |
| JMeter version | |
| Prometheus / Grafana / InfluxDB | |
| JVM flags (Xms/Xmx, GC) | |
| HikariCP (min/max/timeout) | min=10, max=20, timeout=30s |

---

## T1 — Scénarios

| Scénario | Mix | Threads (paliers) | Ramp up | Durée/palier | Payload |
|----------|-----|-------------------|---------|--------------|---------|
| READ heavy (relation) | 50% items list, 20% items by category, 20% cat→items, 10% cat list | 50→100→200 | 60s | 10 min | – |
| JOIN-filter | 70% items?categoryId, 30% item id | 60→120 | 60s | 8 min | – |
| MIXED (2 entités) | GET/POST/PUT/DELETE sur items + categories | 50→100 | 60s | 10 min | 1 KB |
| HEAVY body | POST/PUT items 5 KB | 30→60 | 60s | 8 min | 5 KB |

---

## T2 — Résultats JMeter (par scénario et variante)

### Scenario 1: READ-heavy

| Mesure | A : Jersey | C : @RestController | D : Spring Data REST |
|--------|------------|---------------------|----------------------|
| RPS | | | |
| p50 (ms) | | | |
| p95 (ms) | | | |
| p99 (ms) | | | |
| Err % | | | |

### Scenario 2: JOIN-filter

| Mesure | A : Jersey | C : @RestController | D : Spring Data REST |
|--------|------------|---------------------|----------------------|
| RPS | | | |
| p50 (ms) | | | |
| p95 (ms) | | | |
| p99 (ms) | | | |
| Err % | | | |

### Scenario 3: MIXED (2 entités)

| Mesure | A : Jersey | C : @RestController | D : Spring Data REST |
|--------|------------|---------------------|----------------------|
| RPS | | | |
| p50 (ms) | | | |
| p95 (ms) | | | |
| p99 (ms) | | | |
| Err % | | | |

### Scenario 4: HEAVY-body

| Mesure | A : Jersey | C : @RestController | D : Spring Data REST |
|--------|------------|---------------------|----------------------|
| RPS | | | |
| p50 (ms) | | | |
| p95 (ms) | | | |
| p99 (ms) | | | |
| Err % | | | |

---

## T3 — Ressources JVM (Prometheus)

| Variante | CPU proc. (%) moy/pic | Heap (Mo) moy/pic | GC time (ms/s) moy/pic | Threads actifs moy/pic | Hikari (actifs/max) |
|----------|----------------------|-------------------|------------------------|------------------------|---------------------|
| A : Jersey | | | | | |
| C : @RestController | | | | | |
| D : Spring Data REST | | | | | |

---

## T4 — Détails par endpoint (scénario JOIN-filter)

### GET /items?categoryId=

| Variante | RPS | p95 (ms) | Err % | Observations (JOIN, N+1, projection) |
|----------|-----|----------|-------|--------------------------------------|
| A | | | | |
| C | | | | |
| D | | | | |

### GET /categories/{id}/items

| Variante | RPS | p95 (ms) | Err % | Observations (JOIN, N+1, projection) |
|----------|-----|----------|-------|--------------------------------------|
| A | | | | |
| C | | | | |
| D | | | | |

---

## T5 — Détails par endpoint (scénario MIXED)

### GET /items

| Variante | RPS | p95 (ms) | Err % | Observations |
|----------|-----|----------|-------|--------------|
| A | | | | |
| C | | | | |
| D | | | | |

### POST /items

| Variante | RPS | p95 (ms) | Err % | Observations |
|----------|-----|----------|-------|--------------|
| A | | | | |
| C | | | | |
| D | | | | |

### PUT /items/{id}

| Variante | RPS | p95 (ms) | Err % | Observations |
|----------|-----|----------|-------|--------------|
| A | | | | |
| C | | | | |
| D | | | | |

### DELETE /items/{id}

| Variante | RPS | p95 (ms) | Err % | Observations |
|----------|-----|----------|-------|--------------|
| A | | | | |
| C | | | | |
| D | | | | |

### GET /categories

| Variante | RPS | p95 (ms) | Err % | Observations |
|----------|-----|----------|-------|--------------|
| A | | | | |
| C | | | | |
| D | | | | |

### POST /categories

| Variante | RPS | p95 (ms) | Err % | Observations |
|----------|-----|----------|-------|--------------|
| A | | | | |
| C | | | | |
| D | | | | |

---

## T6 — Incidents / erreurs

| Run | Variante | Type d'erreur (HTTP/DB/timeout) | % | Cause probable | Action corrective |
|-----|----------|----------------------------------|---|----------------|-------------------|
| | | | | | |
| | | | | | |
| | | | | | |

---

## T7 — Synthèse & conclusion

| Critère | Meilleure variante | Écart | Commentaires |
|---------|-------------------|-------|--------------|
| Débit global (RPS) | | | |
| Latence p95 | | | |
| Stabilité (erreurs) | | | |
| Empreinte CPU/RAM | | | |
| Facilité d'expo relationnelle | | | |

---

## Analyse Détaillée

### 1. Impact du N+1 Query Problem

**Observations:**
- Avec USE_JOIN_FETCH=true: 
- Avec USE_JOIN_FETCH=false: 
- Différence de performance: 

### 2. Coût de l'abstraction Spring Data REST

**Observations:**
- HAL format overhead: 
- Exposition automatique vs manuelle: 
- Flexibilité vs performance: 

### 3. Performance des différentes stacks

**JAX-RS (Variant A):**
- Points forts: 
- Points faibles: 
- Cas d'usage recommandés: 

**Spring MVC (Variant C):**
- Points forts: 
- Points faibles: 
- Cas d'usage recommandés: 

**Spring Data REST (Variant D):**
- Points forts: 
- Points faibles: 
- Cas d'usage recommandés: 

### 4. Comportement sous charge

**READ-heavy:**
- 

**JOIN-filter:**
- 

**MIXED:**
- 

**HEAVY-body:**
- 

---

## Recommandations

### Pour applications READ-heavy avec relations:
**Variante recommandée:** ___________  
**Justification:**

### Pour applications avec forte écriture:
**Variante recommandée:** ___________  
**Justification:**

### Pour exposition rapide de CRUD:
**Variante recommandée:** ___________  
**Justification:**

### Pour performance maximale:
**Variante recommandée:** ___________  
**Justification:**

---

## Graphiques et Captures

*Insérer ici:*
- Screenshots Grafana des 3 variantes
- Graphiques JMeter de latence
- Graphiques de consommation CPU/RAM
- Graphiques de connexions HikariCP

---

## Conclusion Générale

### Résumé des résultats:


### Leçons apprises:


### Améliorations possibles:


---

**Signature:** _______________  
**Date:** _______________
