@echo off
setlocal enabledelayedexpansion

:: Cargar .env si existe
if exist .env (
    for /f "tokens=1,* delims==" %%a in ('type .env ^| findstr /V "^#" ^| findstr "="') do (
        set %%a=%%b
    )
)

:menu
cls
echo =============================================
echo  Sistema Integral de Cobranza
echo  Menu de lanzamiento
echo =============================================
echo.
echo  [1] Online  — Conectar a Supabase (requiere internet)
echo  [2] Offline — Usar base de datos local
echo  [3] Solo compilar (mvn compile)
echo.
echo  [0] Salir
echo.
set /p choice="Elige una opcion: "

if "!choice!"=="1" goto :online
if "!choice!"=="2" goto :offline
if "!choice!"=="3" goto :compile
if "!choice!"=="0" goto :exit

echo Opcion invalida.
timeout /t 2 >nul
goto :menu

:online
cls
echo [*] Cargando variables de Supabase...
echo.
if not exist load-env.bat (
    echo [ERROR] load-env.bat no encontrado.
    echo Crea el archivo con las variables de Supabase:
    echo   DB_URL=...
    echo   DB_USER=...
    echo   DB_PASSWORD=...
    echo   SERVER_PORT=8080
    echo   SERVER_ADDRESS=0.0.0.0
    pause
    goto :menu
)
call load-env.bat
if errorlevel 1 (
    echo [ERROR] Error al cargar variables de Supabase.
    pause
    goto :menu
)
echo [OK] Variables cargadas.
echo.
echo Iniciando aplicacion en modo ONLINE...
echo.
call mvnw.cmd spring-boot:run
goto :menu

:offline
cls
echo [*] Iniciando en modo OFFLINE...
echo.
:: Verificar que PostgreSQL este corriendo
sc query postgresql-x64-16-16 >nul 2>&1
if %errorlevel% neq 0 (
    sc query postgresql-x64-16 >nul 2>&1
    if %errorlevel% neq 0 (
        echo [ERROR] El servicio PostgreSQL no esta corriendo.
        echo Ejecuta install.bat primero.
        pause
        goto :menu
    )
)

:: Configurar variables para offline
set DB_URL=jdbc:postgresql://localhost:5432/cobranza
set DB_USER=postgres
set DB_PASSWORD=postgres
set SERVER_PORT=8080
set SERVER_ADDRESS=0.0.0.0

echo DB_URL: !DB_URL!
echo DB_USER: !DB_USER!
echo.
echo Iniciando aplicacion en modo OFFLINE...
echo.
call mvnw.cmd spring-boot:run
goto :menu

:compile
cls
echo Compilando...
call mvnw.cmd compile -q
if errorlevel 1 (
    echo [ERROR] Error en compilacion.
) else (
    echo [OK] Compilacion exitosa.
)
pause
goto :menu

:exit
echo Hasta luego.
exit /b 0
