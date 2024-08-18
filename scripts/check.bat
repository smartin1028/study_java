@echo off
set "original_dir=%cd%"
echo original_dir=%original_dir%
cd /d %~dp0
set "curr_dir=%cd%"
echo curr_dir=%curr_dir%


call java_check_script.bat "D:\dev\bin\jdks\temurin-17.0.12 - 복사본"
if %errorlevel% neq 0 (
    echo 오류가 발생했습니다. 원래 위치로 돌아갑니다.
    cd /d "%original_dir%"
    exit /b %errorlevel%
)
