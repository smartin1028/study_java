@echo off

set "JAVA_HOME=%JAVA_HOME%"

set "param01=%1"

if not defined param01 (
    echo 인자값이 없습니다.
) else (
    set "JAVA_HOME=%1"
    echo 인자값이 있습니다: %param%
)

set "JAVA_EXEC=%JAVA_HOME%\bin\java.exe"
echo "JAVA_EXEC=%JAVA_EXEC%"
echo "JAVA_HOME=%JAVA_HOME%"
echo %JAVA_EXEC% -version
%JAVA_EXEC% -version
if %errorlevel% neq 0 (
    echo Java 실행에 실패했습니다. 경로를 확인해주세요.
    exit /b 1
)