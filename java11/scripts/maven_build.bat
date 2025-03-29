@echo off
chcp 65001
setlocal

rem 현재 디렉토리 저장
set ORIGINAL_DIR=%CD%

rem 현재 bat 파일 위치를 기준으로 경로 설정
set CURRENT_DIR=%~dp0
rem 한단계 위로
set PARENT_DIR=%~dp0..
rem 두단계 위로
set PARENT_PARENT_DIR=%~dp0..\..

rem 현재 bat 파일에서 상위 디렉토리로 이동
cd %PARENT_DIR%

rem 현재 디렉토리 저장 > 현재 프로젝트 root위치
set PROJECT_HOME=%CD%

rem Maven 홈 디렉토리 설정 (필요한 경우 경로 수정)
set MAVEN_HOME=C:\Program Files\Apache Maven\apache-maven-3.8.8
rem Java 홈 디렉토리 설정 (필요한 경우 경로 수정)
set JAVA_HOME=C:\Program Files\Java\corretto-11.0.26

rem # 여러 중첩 폴더 한번에 생성
rem PATH에 Maven 추가
set PATH=%MAVEN_HOME%\bin;%PATH%

set TARGET_DIR=D:\Deploy\MyApp

rem 타겟 폴더 확인 및 생성
if not exist "%TARGET_DIR%" (
    echo 타겟 폴더를 생성합니다: %TARGET_DIR%
    mkdir "%TARGET_DIR%"
)

echo PROJECT_HOME %PROJECT_HOME%

echo Maven 빌드를 시작합니다...

rem Maven clean install 실행
call mvn -s "%MAVEN_HOME%\conf\settings.xml" -f "%PROJECT_HOME%\pom.xml" clean install

if %ERRORLEVEL% EQU 0 (
    echo Maven 빌드가 성공적으로 완료되었습니다.
) else (
    echo Maven 빌드 중 오류가 발생했습니다.
)
rem 여러 파일 복사
xcopy /Y /I "%PROJECT_HOME%\target\*.jar" "%TARGET_DIR%"

rem 원래 디렉토리로 복귀
cd /d "%ORIGINAL_DIR%"

echo 10초 후 자동으로 종료됩니다...
timeout /t 10 /nobreak

endlocal