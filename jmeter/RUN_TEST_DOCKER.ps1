# JMeter Testing with Docker - SUPER SIMPLE
# Usage: .\RUN_TEST_DOCKER.ps1 C
# (C = Variant C, use A or D for others)

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet('A','C','D')]
    [string]$Variant
)

# Port mapping
$ports = @{
    'A' = 8081
    'C' = 8082
    'D' = 8083
}

$port = $ports[$Variant]
$variantName = "variant-$($Variant.ToLower())"

Write-Host "" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  JMeter Test - Variant $Variant (Port $port)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if app is running
Write-Host "[1/4] Checking if Variant $Variant is running..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:$port/actuator/health" -TimeoutSec 5 -UseBasicParsing -ErrorAction Stop
    Write-Host "      [OK] Application is UP and running!" -ForegroundColor Green
}
catch {
    Write-Host "      [ERROR] Application NOT running on port $port" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please start it first:" -ForegroundColor Yellow
    Write-Host "  cd .." -ForegroundColor Gray
    Write-Host "  .\RUN_VARIANT.ps1 $Variant" -ForegroundColor Gray
    exit 1
}

# Create results directory
Write-Host "[2/4] Setting up results folder..." -ForegroundColor Yellow
$resultsDir = "results"
if (-not (Test-Path $resultsDir)) {
    New-Item -ItemType Directory -Path $resultsDir | Out-Null
}
Write-Host "      [OK] Results folder ready" -ForegroundColor Green

# Timestamp for files
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$jtlFile = "$resultsDir/$variantName-test-$timestamp.jtl"
$reportDir = "$resultsDir/$variantName-report-$timestamp"

# Pull JMeter Docker image
Write-Host "[3/4] Pulling JMeter Docker image (first time only)..." -ForegroundColor Yellow
docker pull justb4/jmeter:latest 2>&1 | Out-Null
Write-Host "      [OK] JMeter image ready" -ForegroundColor Green

# Run the test
Write-Host "[4/4] Running JMeter test..." -ForegroundColor Yellow
Write-Host "      This will take 2-5 minutes..." -ForegroundColor Gray
Write-Host ""

# For Windows, we need to use absolute paths
$currentPath = (Get-Location).Path

docker run --rm `
    -v "${currentPath}:/jmeter" `
    justb4/jmeter:latest `
    -n `
    -t "/jmeter/scripts/read_heavy_test.jmx" `
    "-JHOST=host.docker.internal" `
    "-JPORT=$port" `
    -l "/jmeter/$jtlFile" `
    -e `
    -o "/jmeter/$reportDir"

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  [SUCCESS] TEST COMPLETED!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Results saved to:" -ForegroundColor Cyan
    Write-Host "  $reportDir" -ForegroundColor White
    Write-Host ""
    Write-Host "Open the report:" -ForegroundColor Yellow
    Write-Host "  1. Go to folder: jmeter\$reportDir" -ForegroundColor Gray
    Write-Host "  2. Open file: index.html" -ForegroundColor Gray
    Write-Host ""
    Write-Host "In the report, look for these numbers:" -ForegroundColor Yellow
    Write-Host "  - Throughput = RPS (requests/second)" -ForegroundColor White
    Write-Host "  - 50th pct = p50 (milliseconds)" -ForegroundColor White
    Write-Host "  - 95th pct = p95 (milliseconds)" -ForegroundColor White
    Write-Host "  - 99th pct = p99 (milliseconds)" -ForegroundColor White
    Write-Host "  - Error % = Error rate" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "[ERROR] Test failed!" -ForegroundColor Red
    Write-Host "Error code: $LASTEXITCODE" -ForegroundColor Red
}
