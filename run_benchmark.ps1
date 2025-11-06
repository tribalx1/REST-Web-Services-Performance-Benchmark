# Run Benchmark Script
# Usage: .\run_benchmark.ps1 [variant] [scenario] [threads] [rampup] [duration]
# Example: .\run_benchmark.ps1 A read_heavy 50 60 600

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet('A', 'C', 'D')]
    [string]$variant,
    
    [Parameter(Mandatory=$true)]
    [ValidateSet('read_heavy', 'join_filter', 'mixed', 'heavy_body')]
    [string]$scenario,
    
    [int]$threads = 50,
    [int]$rampup = 60,
    [int]$duration = 600
)

# Set port based on variant
$port = switch ($variant) {
    'A' { 8081 }
    'C' { 8082 }
    'D' { 8083 }
}

# Set test plan based on scenario
$testPlan = "read_heavy_test.jmx"  # Default
if ($scenario -eq "join_filter") {
    $testPlan = "join_filter_test.jmx"
} elseif ($scenario -eq "mixed") {
    $testPlan = "mixed_test.jmx"
} elseif ($scenario -eq "heavy_body") {
    $testPlan = "heavy_body_test.jmx"
}

# Create results directory
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$resultsDir = "./jmeter/results/${variant}_${scenario}_${timestamp}"
New-Item -ItemType Directory -Force -Path $resultsDir

Write-Host "Starting benchmark for Variant $variant ($port) - Scenario: $scenario"
Write-Host "Threads: $threads, Ramp-up: ${rampup}s, Duration: ${duration}s"

# Run the test using Docker
$env:JMETER_TEST_PLAN=$testPlan
$env:JMETER_THREADS=$threads
$env:JMETER_RAMPUP=$rampup
$env:JMETER_DURATION=$duration
$env:JMETER_PORT=$port

docker-compose up -d influxdb grafana

Write-Host "Waiting for InfluxDB and Grafana to be ready..."
Start-Sleep -Seconds 10

Write-Host "Starting JMeter test..."
docker-compose run --rm jmeter-master \
  -n \
  -t /tests/$testPlan \
  -JTHREADS=$threads \
  -JRAMPUP=$rampup \
  -JDURATION=$duration \
  -JHOST=host.docker.internal \
  -JPORT=$port \
  -l $resultsDir/result.jtl \
  -j $resultsDir/jmeter.log \
  -e -o $resultsDir/report

Write-Host "Test completed. Results saved to $resultsDir"
Write-Host "View the report at: $resultsDir/report/index.html"
Write-Host "Access Grafana at: http://localhost:3000 (admin/admin)"
