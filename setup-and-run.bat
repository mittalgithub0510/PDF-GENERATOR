@echo off
echo ========================================
echo PDF Generator - Setup and Run Script
echo ========================================
echo.

REM Check if Maven is installed
where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Maven is already installed
    goto :run_project
)

echo [INFO] Maven is not installed. Installing Maven...
echo.

REM Create temp directory for Maven
set MAVEN_VERSION=3.9.11
set MAVEN_HOME=%USERPROFILE%\.m2\apache-maven-%MAVEN_VERSION%
set MAVEN_URL=https://dlcdn.apache.org/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip

if exist "%MAVEN_HOME%" (
    echo [INFO] Maven already downloaded at %MAVEN_HOME%
    goto :set_path
)

echo [INFO] Downloading Maven %MAVEN_VERSION%...
powershell -Command "& {Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%TEMP%\maven.zip'}"

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to download Maven
    echo [INFO] Please install Maven manually from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo [INFO] Extracting Maven...
powershell -Command "& {Expand-Archive -Path '%TEMP%\maven.zip' -DestinationPath '%USERPROFILE%\.m2' -Force}"
del "%TEMP%\maven.zip"

:set_path
echo [INFO] Setting up Maven in PATH for this session...
set PATH=%MAVEN_HOME%\bin;%PATH%

REM Verify Maven installation
mvn -version
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven installation failed
    echo [INFO] Please install Maven manually and add it to your PATH
    pause
    exit /b 1
)

echo.
echo [SUCCESS] Maven installed successfully!
echo.
echo [NOTE] To make Maven permanent, add this to your System PATH:
echo %MAVEN_HOME%\bin
echo.

:run_project
echo ========================================
echo Building and Running the Project...
echo ========================================
echo.

REM Clean and compile
call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Build failed
    pause
    exit /b 1
)

echo.
echo [SUCCESS] Build completed successfully!
echo.
echo Starting the PDF Generator application...
echo.

REM Run the application
call mvn exec:java
