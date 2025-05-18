@echo off
chcp 65001
setlocal

rem 현재 bat 파일 위치를 기준으로 경로 설정
set CURRENT_DIR=%~dp0
rem 한단계 위로
set PARENT_DIR=%~dp0..

set TARGET_DIR=%PARENT_DIR%\target
set TARGET_JAR=%TARGET_DIR%\java11.jar


set JAVA_HOME="C:\Program Files\Java\corretto-11.0.26"

echo JAVA_HOME=%JAVA_HOME%
echo TARGET_DIR=%TARGET_DIR%
echo TARGET_JAR=%TARGET_JAR%

cd %TARGET_DIR%

rem %JAVA_HOME%\bin\java.exe -cp "%TARGET_JAR%;" org.example.Main
%JAVA_HOME%\bin\java.exe -cp "%TARGET_JAR%;%TARGET_DIR%\libs\*" org.example.Main
rem %JAVA_HOME%\bin\java.exe -cp "%TARGET_JAR%;%TARGET_DIR%\libs\*" org.example.mssql.DatabaseConnection


endlocal