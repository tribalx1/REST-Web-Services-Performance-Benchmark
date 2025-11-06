# REST Performance Benchmark - Complete Setup Script
# This script sets up the complete benchmarking environment

$ErrorActionPreference = "Stop"

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  REST Performance Benchmark - Complete Setup" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

# Step 1: Build Maven Project
Write-Host "`n[1/5] Building Maven Project..." -ForegroundColor Yellow
Write-Host "      Compiling all variants (A, C, D) and shared model..." -ForegroundColor Gray

try {
    .\mvnw.cmd clean install -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "      [ERROR] Maven build failed!" -ForegroundColor Red
        exit 1
    }
    Write-Host "      [OK] Maven build successful" -ForegroundColor Green
} catch {
    Write-Host "      [ERROR] $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 2: Generate JMeter Test Data
Write-Host "`n[2/5] Generating JMeter Test Data..." -ForegroundColor Yellow
Write-Host "      Creating CSV files for load testing..." -ForegroundColor Gray

try {
    Push-Location jmeter
    python generate_jmeter_data.py
    if ($LASTEXITCODE -ne 0) {
        Write-Host "      [ERROR] Failed to generate test data!" -ForegroundColor Red
        Pop-Location
        exit 1
    }
    Pop-Location
    Write-Host "      [OK] Test data generated" -ForegroundColor Green
} catch {
    Write-Host "      [ERROR] $($_.Exception.Message)" -ForegroundColor Red
    Pop-Location
    exit 1
}

# Step 3: Start Docker Infrastructure
Write-Host "`n[3/5] Starting Docker Infrastructure..." -ForegroundColor Yellow
Write-Host "      Starting PostgreSQL, Prometheus, Grafana, InfluxDB..." -ForegroundColor Gray

try {
    Push-Location monitoring
    docker-compose up -d
    if ($LASTEXITCODE -ne 0) {
        Write-Host "      [ERROR] Failed to start Docker services!" -ForegroundColor Red
        Pop-Location
        exit 1
    }
    Pop-Location
    Write-Host "      [OK] Docker services started" -ForegroundColor Green
    Write-Host "      Waiting 30 seconds for services to initialize..." -ForegroundColor Gray
    Start-Sleep -Seconds 30
} catch {
    Write-Host "      [ERROR] $($_.Exception.Message)" -ForegroundColor Red
    Pop-Location
    exit 1
}

# Step 4: Setup Database
Write-Host "`n[4/5] Setting up Database..." -ForegroundColor Yellow
Write-Host "      Creating schema and loading test data..." -ForegroundColor Gray

try {
    Push-Location database
    
    # Check if data files exist
    if (-not (Test-Path "categories.sql")) {
        Write-Host "      Generating database test data..." -ForegroundColor Gray
        python generate_data.py
        if ($LASTEXITCODE -ne 0) {
            Write-Host "      [ERROR] Failed to generate database data!" -ForegroundColor Red
            Pop-Location
            exit 1
        }
    }
    
    # Load schema and data
    Write-Host "      Loading schema..." -ForegroundColor Gray
    $env:PGPASSWORD = "postgres"
    psql -U postgres -h localhost -d postgres -c "DROP DATABASE IF EXISTS benchmark;"
    psql -U postgres -h localhost -d postgres -c "CREATE DATABASE benchmark;"
    psql -U postgres -h localhost -d benchmark -f schema.sql
    
    if (Test-Path "categories.sql") {
        Write-Host "      Loading data..." -ForegroundColor Gray
        psql -U postgres -h localhost -d benchmark -f categories.sql
        psql -U postgres -h localhost -d benchmark -f items.sql
    }
    
    Pop-Location
    Write-Host "      [OK] Database ready" -ForegroundColor Green
} catch {
    Write-Host "      [WARNING] Database setup issue: $($_.Exception.Message)" -ForegroundColor Yellow
    Write-Host "      You may need to setup database manually" -ForegroundColor Yellow
    Pop-Location
}

# Step 5: Verify Setup
Write-Host "`n[5/5] Verifying Setup..." -ForegroundColor Yellow

$allOk = $true

# Check JARs
Write-Host "      Checking built artifacts..." -ForegroundColor Gray
$jarA = Test-Path "variant-a-jaxrs\target\variant-a-jaxrs-1.0.0-SNAPSHOT.jar"
$jarC = Test-Path "variant-c-springmvc\target\variant-c-springmvc-1.0.0-SNAPSHOT.jar"
$jarD = Test-Path "variant-d-springdata\target\variant-d-springdata-1.0.0-SNAPSHOT.jar"

if ($jarA) { Write-Host "        [OK] Variant A JAR exists" -ForegroundColor Green }
else { Write-Host "        [MISSING] Variant A JAR" -ForegroundColor Red; $allOk = $false }

if ($jarC) { Write-Host "        [OK] Variant C JAR exists" -ForegroundColor Green }
else { Write-Host "        [MISSING] Variant C JAR" -ForegroundColor Red; $allOk = $false }

if ($jarD) { Write-Host "        [OK] Variant D JAR exists" -ForegroundColor Green }
else { Write-Host "        [MISSING] Variant D JAR" -ForegroundColor Red; $allOk = $false }

# Check Docker services
Write-Host "      Checking Docker services..." -ForegroundColor Gray
try {
    $dockerStatus = docker-compose -f monitoring\docker-compose.yml ps --services --filter "status=running"
    $services = @("postgres", "prometheus", "grafana", "influxdb")
    foreach ($service in $services) {
        if ($dockerStatus -contains $service) {
            Write-Host "        [OK] $service is running" -ForegroundColor Green
        } else {
            Write-Host "        [DOWN] $service is not running" -ForegroundColor Red
            $allOk = $false
        }
    }
} catch {
    Write-Host "        [ERROR] Could not check Docker services" -ForegroundColor Red
    $allOk = $false
}

# Check CSV files
Write-Host "      Checking JMeter test data..." -ForegroundColor Gray
$csvFiles = @("category_ids.csv", "item_ids.csv", "category_payloads.csv", "item_payloads_1kb.csv", "item_payloads_5kb.csv")
foreach ($csv in $csvFiles) {
    if (Test-Path "jmeter\$csv") {
        Write-Host "        [OK] $csv" -ForegroundColor Green
    } else {
        Write-Host "        [MISSING] $csv" -ForegroundColor Red
        $allOk = $false
    }
}

Write-Host "`n============================================================" -ForegroundColor Cyan
if ($allOk) {
    Write-Host "  [SUCCESS] Setup Complete!" -ForegroundColor Green
    Write-Host "============================================================" -ForegroundColor Cyan
    
    Write-Host "`nNext Steps:" -ForegroundColor Yellow
    Write-Host "  1. Start a variant:" -ForegroundColor White
    Write-Host "     Variant A (JAX-RS):      java -jar variant-a-jaxrs\target\variant-a-jaxrs-1.0.0-SNAPSHOT.jar" -ForegroundColor Gray
    Write-Host "     Variant C (Spring MVC):  mvn -pl variant-c-springmvc spring-boot:run" -ForegroundColor Gray
    Write-Host "     Variant D (Spring Data): mvn -pl variant-d-springdata spring-boot:run" -ForegroundColor Gray
    
    Write-Host "`n  2. Access monitoring:" -ForegroundColor White
    Write-Host "     Grafana:    http://localhost:3000 (admin/admin)" -ForegroundColor Gray
    Write-Host "     Prometheus: http://localhost:9090" -ForegroundColor Gray
    Write-Host "     InfluxDB:   http://localhost:8086" -ForegroundColor Gray
    
    Write-Host "`n  3. Test endpoints:" -ForegroundColor White
    Write-Host "     curl http://localhost:8081/actuator/health  # Variant A" -ForegroundColor Gray
    Write-Host "     curl http://localhost:8082/actuator/health  # Variant C" -ForegroundColor Gray
    Write-Host "     curl http://localhost:8083/actuator/health  # Variant D" -ForegroundColor Gray
    
    Write-Host "`n  4. Run load tests:" -ForegroundColor White
    Write-Host "     cd jmeter" -ForegroundColor Gray
    Write-Host "     # Use JMeter GUI or command line" -ForegroundColor Gray
    
} else {
    Write-Host "  [WARNING] Setup completed with issues" -ForegroundColor Yellow
    Write-Host "============================================================" -ForegroundColor Cyan
    Write-Host "`nPlease check the errors above and fix manually." -ForegroundColor Yellow
}

Write-Host ""
