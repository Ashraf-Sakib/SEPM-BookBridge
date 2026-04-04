@echo off
REM BookBridge - Rebuild and Restart Script
REM This script rebuilds the application with the fixed code and restarts it

echo.
echo ========================================
echo BookBridge - Rebuild and Restart Script
echo ========================================
echo.

REM Set JAVA_HOME to Java 25 (required for Spring Boot 3.3.5)
echo [*] Setting Java environment...
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
echo Java Home: %JAVA_HOME%
echo.

REM Stop any running Java processes
echo [1/4] Stopping any running Java processes...
taskkill /F /IM java.exe >nul 2>&1
timeout /t 2 >nul
echo Done!
echo.

REM Navigate to project directory
cd /d "C:\Users\User\Desktop\SEPM Project\SEPM-BookBridge"

REM Clean build
echo [2/4] Clean building the project...
echo This may take 2-5 minutes...
call mvnw.cmd clean install -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)
echo Done!
echo.

REM Check if JAR was created
if not exist "target\BookBridge-0.0.1-SNAPSHOT.jar" (
    echo ERROR: JAR file not created!
    pause
    exit /b 1
)
echo.

REM Start the application
echo [3/4] Starting BookBridge application...
echo The application is starting... please wait
echo.
echo Once you see "Started BookBridgeApplication in X seconds"
echo the application is ready at http://localhost:8082
echo.
"%JAVA_EXE%" -jar target\BookBridge-0.0.1-SNAPSHOT.jar --spring.profiles.active=local --server.port=8082

REM If we reach here, application stopped
echo.
echo [4/4] Application stopped
pause

