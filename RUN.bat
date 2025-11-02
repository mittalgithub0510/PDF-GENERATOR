@echo off
echo ========================================
echo PDF Generator - Quick Run
echo ========================================
echo.

REM Check if Maven is installed
where mvn >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven is not installed!
    echo.
    echo Please choose one of the following options:
    echo.
    echo Option 1: Run setup-and-run.bat to automatically install Maven
    echo Option 2: Install Maven manually:
    echo    1. Download from: https://maven.apache.org/download.cgi
    echo    2. Extract to C:\Program Files\Apache\Maven
    echo    3. Add to PATH: C:\Program Files\Apache\Maven\bin
    echo.
    pause
    exit /b 1
)

echo [OK] Maven found!
echo.
echo Building project...
call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Build failed!
    pause
    exit /b 1
)

echo.
echo [SUCCESS] Build completed!
echo.
echo Starting PDF Generator...
echo.
call mvn exec:java
