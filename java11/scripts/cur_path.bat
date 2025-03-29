@echo off
chcp 65001
setlocal

rem 현재 bat 파일 위치를 기준으로 경로 설정
set CURRENT_DIR=%~dp0

echo CURRENT_DIR %CURRENT_DIR%

if %ERRORLEVEL% EQU 0 (
    echo 성공
) else (
    echo 실패
)

endlocal