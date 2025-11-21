@echo off
title Vehicle Renter Launcher (Windows)

echo Checking Docker...
docker --version >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    echo Docker not installed or Docker Desktop not running.
    pause
    exit /b
)

echo Starting PostgreSQL database container...
docker compose up -d

echo Waiting for Postgres service...
:waitloop
powershell -command "(New-Object Net.Sockets.TcpClient).Connect('localhost',5432)" 2>$nul
IF %ERRORLEVEL% NEQ 0 (
    timeout /t 1 >nul
    goto waitloop
)

echo Database ready!

echo Running Vehicle Renter App...
mvn clean javafx:run

echo JavaFX application has closed.
echo Stopping containers...

docker compose down

echo Done.
pause
