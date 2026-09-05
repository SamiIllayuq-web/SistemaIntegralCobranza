@echo off
:: build-exe.bat - Genera el .exe portable con jpackage
:: Requiere: JDK 21+ con jpackage en PATH

echo =============================================
echo  Build .exe - Sistema Integral de Cobranza
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
    echo [ERROR] jpackage no encontrado. Asegurate de tener JDK 21+.
    exit /b 1
)

:: Compilar JAR
echo [*] Compilando proyecto...
call mvnw.cmd package -DskipTests -q
if %errorlevel% neq 0 (
    echo [ERROR] Error en compilacion.
    pause
    exit /b 1
)
echo [OK] Proyecto compilado.

:: Limpiar build anterior
set OUTPUT_DIR=dist
if exist "%OUTPUT_DIR%\SistemaCobranza" rmdir /S /Q "%OUTPUT_DIR%\SistemaCobranza"

echo.
echo [*] Generando .exe con jpackage...

jpackage --type app-image --input target --main-jar cobranza-1.0.0.jar --name SistemaCobranza --app-version 1.0.0 --vendor "Sistema Integral Cobranza" --description "Sistema Integral de Cobranza" --java-options "-Djava.net.preferIPv4Stack=true" --dest "%OUTPUT_DIR%" --win-console

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] jpackage fallo.
    pause
    exit /b 1
)

:: Copiar .env como .env DENTRO de app/ (junto al JAR)
if exist ".env" (
    copy /Y ".env" "%OUTPUT_DIR%\SistemaCobranza\app\.env" >nul
    if %errorlevel% neq 0 (
        echo [ERROR] No se pudo copiar .env
    ) else (
        echo [OK] .env - Supabase - listo en app.
    )
) else (
    echo [AVISO] .env no encontrado.
)

echo.
echo =============================================
echo  Build exitoso!
echo =============================================
echo.
echo Carpeta: %OUTPUT_DIR%\SistemaCobranza\
echo.
echo Contiene:
echo   - SistemaCobranza.exe  (doble click para abrir)
echo   - .env                  (credenciales Supabase)
echo   - JRE incluido          (no necesita Java instalado)
echo.
echo Para crear acceso directo en el escritorio:
echo   1. Abre la carpeta dist\SistemaCobranza
echo   2. Clic derecho en SistemaCobranza.exe
echo   3. Crear acceso directo
echo   4. Mueve el acceso directo al escritorio
echo.
pause
