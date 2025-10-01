@echo off
echo ========================================
echo   Starting Hotel Management System
echo   WITH JVM OPTIMIZATIONS
echo ========================================
echo.

REM Stop any existing Gradle daemon
call gradlew --stop

REM Start with optimized JVM flags
call gradlew bootRun ^
  -Dorg.gradle.jvmargs="-XX:TieredStopAtLevel=1 -XX:+UseParallelGC -Xms512m -Xmx1g -Dspring.jmx.enabled=false -Dspring.main.lazy-initialization=true"

pause
