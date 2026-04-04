@echo off
REM Quick Fix Script - Sets Java and Rebuilds

setlocal enabledelayedexpansion
cls

echo.
echo ==========================================
echo   BookBridge - Quick Rebuild ^& Restart
echo ==========================================
echo.

echo [*] Setting Java 25 environment...
set "JAVA_HOME=C:\Program Files\Java\jdk-25"
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo ERROR: Java not found at "%JAVA_HOME%"
    echo Please update the script with the correct JDK path.
    pause
    exit /b 1
)
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "SPRING_PROFILES_ACTIVE=local"
echo     JAVA_HOME: %JAVA_HOME%
echo.

echo [*] Stopping any running Java processes...
taskkill /F /IM java.exe >nul 2>&1
timeout /t 2 >nul
echo     Done!
echo.

echo [*] Navigating to project directory...
cd /d "C:\Users\User\Desktop\SEPM Project\SEPM-BookBridge"
echo     Location: %cd%
echo.

echo [*] Starting clean build (with Java 25)...
echo     This will take 2-5 minutes, please wait...
echo.
call mvnw.cmd clean install -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Build failed!
    echo.
    pause
    exit /b 1
)

echo.
echo [*] Build successful! Starting application...
echo.
"%JAVA_EXE%" -jar target\BookBridge-0.0.1-SNAPSHOT.jar --spring.profiles.active=local --server.port=8082

echo.
echo [*] Application stopped
pause

