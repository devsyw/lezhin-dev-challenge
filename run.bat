@echo off

echo === 레진 만화 애플리케이션 실행 스크립트 ===

echo 애플리케이션 빌드 중...
call gradlew.bat clean bootJar

if %ERRORLEVEL% neq 0 (
    echo 빌드 실패!
    exit /b 1
)

echo 빌드 성공!

echo Docker 컨테이너 시작 중...
docker-compose up -d

echo 컨테이너 상태:
docker-compose ps

echo.
echo === 애플리케이션이 시작되었습니다 ===
echo API 서버: http://localhost:8080
echo H2 콘솔: http://localhost:8080/h2-console
echo.