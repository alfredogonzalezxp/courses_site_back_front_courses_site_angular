# Setup Git Pre-Push Hook
# This script installs a local hook to run validation before pushing code.

$HookPath = ".git/hooks/pre-push"
$HookContent = @"
#!/usr/bin/env pwsh
Write-Host "`nRunning Local CI/CD Validation before push..." -ForegroundColor Cyan
./local-ci.ps1
if (`$LASTEXITCODE -ne 0) {
    Write-Host "`n[!] Push cancelled: Local validation failed." -ForegroundColor Red
    exit 1
}
exit 0
"@

if (-Not (Test-Path ".git")) {
    Write-Host "[!] Error: No .git directory found. Run this from the project root." -ForegroundColor Red
    exit 1
}

# Create the hooks directory if it doesn't exist
if (-Not (Test-Path ".git/hooks")) {
    New-Item -ItemType Directory -Path ".git/hooks" | Out-Null
}

# Write the hook file
Set-Content -Path $HookPath -Value $HookContent -Encoding UTF8

Write-Host "`n[+] Git Pre-Push hook installed successfully!" -ForegroundColor Green
Write-Host "[i] Location: $HookPath"
Write-Host "[i] Note: You can skip this check by using 'git push --no-verify' if absolutely necessary."
