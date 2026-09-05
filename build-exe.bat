@echo off
:: build-exe.bat — Genera el instalador .exe con jpackage
:: Requiere: JDK 21+ con jpackage en PATH
::            Maven 3.9+
::
:: Uso: run in Windows CMD
::   build-exe.bat

echo =============================================
echo  Build .exe — Sistema Integral de Cobranza
echo =============================================
echo.

:: Verificar Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java no encontrado. Instala JDK 21+.
    exit /b 1
)

:: Verificar jpackage
where jpackage >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] jpackage no encontrado.
    echo Asegurate de tener JDK 21+ con jpackage en PATH.
    exit /b 1
)

:: Verificar Maven
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Maven no encontrado.
    exit /b 1
)

:: Carpeta de salida
set OUTPUT_DIR=dist
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

:: Carpeta de recursos (icono, scripts)
set RESOURCES_DIR=installer-resources
if not exist "%RESOURCES_DIR%" mkdir "%RESOURCES_DIR%"

:: Compilar JAR si no existe
if not exist "target\cobranza-1.0.0.jar" (
    echo.
    echo [*] Compilando proyecto...
    call mvnw.cmd package -DskipTests -q
    if %errorlevel% neq 0 (
        echo [ERROR] Error en compilacion.
        exit /b 1
    )
)

echo.
echo [*] Generando instalador .exe con jpackage...
echo.

:: jpackage — genera un Windows Application Package (.exe installer)
jpackage ^
    --type app-image ^
    --input target ^
    --main-jar cobranza-1.0.0.jar ^
    --name "SistemaCobranza" ^
    --app-version "1.0.0" ^
    --vendor "Sistema Integral Cobranza" ^
    --description "Sistema Integral de Cobranza" ^
    --java-options "-Djava.net.preferIPv4Stack=true" ^
    --dest "%OUTPUT_DIR%" ^
    --win-console

:: --win-console: abre una consola para ver logs de Spring Boot

if %errorlevel% equ 0 (
    echo.
    echo [OK] Build exitoso.
    echo.
    echo Ejecutable en: %OUTPUT_DIR%\SistemaCobranza\SistemaCobranza.exe
    echo.
    echo Para crear instalador MSI:
    echo   jpackage --type msi --win-msi ... (mismos parametros)
) else (
    echo.
    echo [ERROR] jpackage fallo.
)

pause
