# Setup Git Pre-Push Hook
# This script installs a local hook to run validation before pushing code.

$HookPath = ".git/hooks/pre-push"
$HookContent = @"
#!/bin/sh
# Git for Windows runs hooks using Bash (MinGW).
# We use Bash to trigger the PowerShell CI script safely.

echo ""
echo "Running Local CI/CD Validation before push..."
# Call pwsh with NoProfile and ExecutionPolicy Bypass for maximum compatibility.
pwsh -NoProfile -ExecutionPolicy Bypass -File "./local-ci.ps1"

if [ `$? -ne 0 ]; then
    echo ""
    echo "[!] Push cancelled: Local validation failed."
    exit 1
fi

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
