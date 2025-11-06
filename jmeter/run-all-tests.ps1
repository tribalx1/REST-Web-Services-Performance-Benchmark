# PowerShell script to run all JMeter tests for all variants
# Usage: .\run-all-tests.ps1

$ErrorActionPreference = "Stop"

# Configuration
$VARIANTS = @(
    @{Name="variant-a"; Port=8081; Label="JAX-RS"},
    @{Name="variant-c"; Port=8082; Label="Spring-MVC"},
    @{Name="variant-d"; Port=8083; Label="Spring-Data-REST"}
)

$SCENARIOS = @(
    @{Name="scenario-1-read-heavy"; File="scenario-1-read-heavy.jmx"; Label="READ-heavy"},
    @{Name="scenario-2-join-filter"; File="scenario-2-join-filter.jmx"; Label="JOIN-filter"},
    @{Name="scenario-3-mixed"; File="scenario-3-mixed.jmx"; Label="MIXED"}
)

$JMETER_HOME = $env:JMETER_HOME
if (-not $JMETER_HOME) {
    Write-Host "ERROR: JMETER_HOME environment variable not set" -ForegroundColor Red
    Write-Host "Please set it to your JMeter installation directory"
    exit 1
}

$JMETER_BIN = Join-Path $JMETER_HOME "bin\jmeter.bat"
if (-not (Test-Path $JMETER_BIN)) {
    Write-Host "ERROR: JMeter executable not found at $JMETER_BIN" -ForegroundColor Red
    exit 1
}

# Create results directory
$RESULTS_DIR = "results"
if (-not (Test-Path $RESULTS_DIR)) {
    New-Item -ItemType Directory -Path $RESULTS_DIR | Out-Null
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "REST Benchmark - JMeter Test Execution" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$summaryFile = Join-Path $RESULTS_DIR "summary-$timestamp.csv"

# Create summary CSV header
"Variant,Scenario,Port,Samples,Errors,ErrorRate,AvgResponseTime,MinResponseTime,MaxResponseTime,P50,P90,P95,P99,Throughput" | Out-File -FilePath $summaryFile -Encoding UTF8

foreach ($variant in $VARIANTS) {
    Write-Host "========================================" -ForegroundColor Yellow
    Write-Host "Testing: $($variant.Label) (Port $($variant.Port))" -ForegroundColor Yellow
    Write-Host "========================================" -ForegroundColor Yellow
    Write-Host ""
    
    # Check if application is running
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:$($variant.Port)/actuator/health" -TimeoutSec 5 -ErrorAction Stop
        Write-Host "✓ Application is running" -ForegroundColor Green
    }
    catch {
        Write-Host "✗ Application not responding on port $($variant.Port)" -ForegroundColor Red
        Write-Host "  Please start $($variant.Name) before running tests" -ForegroundColor Red
        continue
    }
    
    foreach ($scenario in $SCENARIOS) {
        Write-Host ""
        Write-Host "Running: $($scenario.Label)..." -ForegroundColor Cyan
        
        $jtlFile = Join-Path $RESULTS_DIR "$($variant.Name)-$($scenario.Name)-$timestamp.jtl"
        $reportDir = Join-Path $RESULTS_DIR "$($variant.Name)-$($scenario.Name)-$timestamp-report"
        
        # Run JMeter test
        $jmeterArgs = @(
            "-n",
            "-t", $scenario.File,
            "-JHOST=localhost",
            "-JPORT=$($variant.Port)",
            "-l", $jtlFile,
            "-e",
            "-o", $reportDir
        )
        
        Write-Host "  Command: jmeter $($jmeterArgs -join ' ')" -ForegroundColor Gray
        
        $startTime = Get-Date
        & $JMETER_BIN $jmeterArgs
        $endTime = Get-Date
        $duration = ($endTime - $startTime).TotalSeconds
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "  ✓ Test completed in $([math]::Round($duration, 2)) seconds" -ForegroundColor Green
            Write-Host "  Report: $reportDir\index.html" -ForegroundColor Gray
        }
        else {
            Write-Host "  ✗ Test failed with exit code $LASTEXITCODE" -ForegroundColor Red
        }
        
        # Wait between tests
        Write-Host "  Waiting 60 seconds before next test..." -ForegroundColor Gray
        Start-Sleep -Seconds 60
    }
    
    Write-Host ""
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "All tests completed!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Results directory: $RESULTS_DIR" -ForegroundColor Cyan
Write-Host "Summary file: $summaryFile" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Review HTML reports in results/ directory" -ForegroundColor White
Write-Host "2. Check Grafana dashboards for detailed metrics" -ForegroundColor White
Write-Host "3. Compare results across variants" -ForegroundColor White
