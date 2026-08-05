@echo off
setlocal

cd /d "%~dp0"

if not exist logs mkdir logs

set LOGFILE=logs\run-%date:~0,4%%date:~5,2%%date:~8,2%-%time:~0,2%%time:~3,2%%time:~6,2%.log
set LOGFILE=%LOGFILE: =0%

echo Logging to: %LOGFILE%
echo Running mvn spring-boot:run...

cmd /c "set JAVA_HOME=C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot&& set PATH=%JAVA_HOME%\bin;%PATH%&& mvn spring-boot:run 2>&1" | tee logs\current-run.log
