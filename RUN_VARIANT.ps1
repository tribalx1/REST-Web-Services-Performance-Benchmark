# Quick script to run any variant
# Usage: .\RUN_VARIANT.ps1 A|C|D

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet('A','C','D','a','c','d')]
    [string]$Variant
)

$Variant = $Variant.ToUpper()

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Starting Variant $Variant" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

switch ($Variant) {
    'A' {
        Write-Host "`nVariant A: JAX-RS (Jersey) + JPA/Hibernate" -ForegroundColor Yellow
        Write-Host "Port: 8081" -ForegroundColor Gray
        Write-Host "Starting..." -ForegroundColor Gray
        $jar = "variant-a-jaxrs\target\variant-a-jaxrs-1.0.0-SNAPSHOT.jar"
        if (Test-Path $jar) {
            Push-Location variant-a-jaxrs\target
            java -jar variant-a-jaxrs-1.0.0-SNAPSHOT.jar
            Pop-Location
        } else {
            Write-Host "[ERROR] JAR not found. Run '.\mvnw.cmd clean install' first!" -ForegroundColor Red
            exit 1
        }
    }
    'C' {
        Write-Host "`nVariant C: Spring Boot + @RestController + JPA/Hibernate" -ForegroundColor Yellow
        Write-Host "Port: 8082" -ForegroundColor Gray
        Write-Host "Starting..." -ForegroundColor Gray
        Push-Location variant-c-springmvc
        ..\mvnw.cmd spring-boot:run
        Pop-Location
    }
    'D' {
        Write-Host "`nVariant D: Spring Boot + Spring Data REST" -ForegroundColor Yellow
        Write-Host "Port: 8083" -ForegroundColor Gray
        Write-Host "Starting..." -ForegroundColor Gray
        Push-Location variant-d-springdata
        ..\mvnw.cmd spring-boot:run
        Pop-Location
    }
}
