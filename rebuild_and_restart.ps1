# BookBridge - Rebuild and Restart Script (PowerShell)
# This script rebuilds the application with the fixed code and restarts it

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "BookBridge - Rebuild and Restart Script" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Set JAVA_HOME environment variable (required for Spring Boot 3.3.5)
Write-Host "[*] Setting Java environment..." -ForegroundColor Yellow
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"
$javaExe = Join-Path $env:JAVA_HOME 'bin\java.exe'
if (-not (Test-Path $javaExe)) {
    Write-Host "ERROR: Java not found at $env:JAVA_HOME" -ForegroundColor Red
    Write-Host "Please update the script with the correct JDK path." -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
$env:SPRING_PROFILES_ACTIVE = 'local'
Write-Host "Java Home: $env:JAVA_HOME" -ForegroundColor Cyan
Write-Host ""

# Stop any running Java processes
Write-Host "[1/4] Stopping any running Java processes..." -ForegroundColor Yellow
taskkill /F /IM java.exe 2>&1 | Out-Null
Start-Sleep -Seconds 2
Write-Host "Done!" -ForegroundColor Green
Write-Host ""

# Navigate to project directory
$projectPath = "C:\Users\User\Desktop\SEPM Project\SEPM-BookBridge"
Set-Location $projectPath

# Clean build
Write-Host "[2/4] Clean building the project..." -ForegroundColor Yellow
Write-Host "This may take 2-5 minutes, please wait..." -ForegroundColor Cyan
Write-Host ""

& .\mvnw.cmd clean install -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Build failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host ""
Write-Host "Build completed successfully!" -ForegroundColor Green
Write-Host ""

# Check if JAR was created
$jarPath = "target\BookBridge-0.0.1-SNAPSHOT.jar"
if (-not (Test-Path $jarPath)) {
    Write-Host "ERROR: JAR file not created at $jarPath" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "[3/4] Starting BookBridge application..." -ForegroundColor Yellow
Write-Host ""
Write-Host "The application is starting... please wait" -ForegroundColor Cyan
Write-Host ""
Write-Host "Once you see 'Started BookBridgeApplication in X seconds'" -ForegroundColor Cyan
Write-Host "the application is ready at http://localhost:8082" -ForegroundColor Cyan
Write-Host ""

# Start the application
& $javaExe -jar $jarPath --spring.profiles.active=local --server.port=8082

# If we reach here, application stopped
Write-Host ""
Write-Host "[4/4] Application stopped" -ForegroundColor Yellow
Read-Host "Press Enter to exit"

