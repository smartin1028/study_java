@echo off

set "JAVA_HOME=%JAVA_HOME%"

set "param01=%1"

if not defined param01 (
    echo ���ڰ��� �����ϴ�.
) else (
    set "JAVA_HOME=%1"
    echo ���ڰ��� �ֽ��ϴ�: %param%
)

set "JAVA_EXEC=%JAVA_HOME%\bin\java.exe"
echo "JAVA_EXEC=%JAVA_EXEC%"
echo "JAVA_HOME=%JAVA_HOME%"
echo %JAVA_EXEC% -version
%JAVA_EXEC% -version
if %errorlevel% neq 0 (
    echo Java ���࿡ �����߽��ϴ�. ��θ� Ȯ�����ּ���.
    exit /b 1
)