@echo off
title Campus Lost and Found System
cls
echo =============================================================
echo    Campus Lost and Found Management System
echo    Skill Development Lab-I
echo =============================================================
echo Compiling Java source files...
javac campuslostfound/*.java
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %errorlevel%
)
echo [SUCCESS] Compilation successful!
echo.
echo Starting application...
java campuslostfound.Main
pause
