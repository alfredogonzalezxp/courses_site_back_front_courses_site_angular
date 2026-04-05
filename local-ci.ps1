# Local CI/CD Pipeline Simulation Script
# Mirrors .github/workflows/main.yml

$ErrorActionPreference = "Stop"
$StartTime = Get-Date

Write-Host "`n=== [STAGE 1] Backend CI (Maven) ===" -ForegroundColor Cyan
try {
    Push-Location courseback
    # Environment variables for local DB if running tests that require it
    $env:DB_URL = "jdbc:postgresql://localhost:5433/appdbang"
    $env:DB_USER = "adminbckang"
    $env:DB_PASSWORD = "StrongPassw0rd-2029!"
    
    Write-Host "Running: ./mvnw clean test"
    ./mvnw clean test
    Pop-Location
} catch {
    Write-Host "`n[!] Backend CI Failed!" -ForegroundColor Red
    exit 1
}

Write-Host "`n=== [STAGE 2] Frontend CI (Node/Vitest) ===" -ForegroundColor Cyan
try {
    Push-Location coursefront
    Write-Host "Running: npm test -- --reporters=verbose --watch=false"
    npm test -- --reporters=verbose --watch=false
    Pop-Location
} catch {
    Write-Host "`n[!] Frontend CI Failed!" -ForegroundColor Red
    exit 1
}

Write-Host "`n=== [STAGE 3] Docker Build Validation ===" -ForegroundColor Cyan
try {
    Write-Host "Running: docker compose build"
    docker compose build
} catch {
    Write-Host "`n[!] Docker Build Validation Failed!" -ForegroundColor Red
    exit 1
}

$EndTime = Get-Date
$Duration = $EndTime - $StartTime

Write-Host "`n===============================================" -ForegroundColor Green
Write-Host " SUCCESS: LOCAL CI/CD PIPELINE COMPLETE" -ForegroundColor Green
Write-Host " Duration: $($Duration.Minutes)m $($Duration.Seconds)s"
Write-Host "===============================================" -ForegroundColor Green
