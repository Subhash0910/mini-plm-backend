@echo off
REM Quick Fix Script for Mini PLM Backend Build (Windows)
REM Date: January 16, 2026
REM Usage: Run this file directly

echo ======================================
echo Mini PLM Backend - Quick Fix Script
echo ======================================
echo.

REM Navigate to backend directory
echo [1/4] Navigating to backend directory...
cd /d "%~dp0mini-plm-backend" || exit /b 1
echo     [OK] Location: %cd%
echo.

REM Clean Maven cache
echo [2/4] Cleaning Maven cache...
call mvn clean
if %errorlevel% neq 0 (
    echo     [ERROR] Maven clean failed
    pause
    exit /b 1
)
echo     [OK] Maven cache cleaned
echo.

REM Build with annotation processing
echo [3/4] Building with annotation processing...
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo     [ERROR] Build failed. Please check errors above.
    pause
    exit /b 1
)
echo     [OK] Build successful!
echo.

REM Start application
echo [4/4] Starting Spring Boot application...
echo     [INFO] Starting on http://localhost:8080
echo.
call mvn spring-boot:run

pause
