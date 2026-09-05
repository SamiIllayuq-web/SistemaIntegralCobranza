@echo off
setlocal enabledelayedexpansion

echo =============================================
echo  Instalador - Sistema Integral de Cobranza
echo =============================================
echo.

:: Verificar si PostgreSQL ya esta instalado
where psql >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] PostgreSQL ya esta instalado.
    echo.
) else (
    echo [*] PostgreSQL no encontrado. Descargando...
    echo.

    :: Version de PostgreSQL
    set PG_VERSION=16.3
    set PG_INSTALLER=postgresql-%PG_VERSION%-windows-x64.exe
    set PG_URL=https://get.enterprisedb.com/postgresql/postgresql-%PG_VERSION%-windows-x64.exe

    if not exist "%TEMP%\%PG_INSTALLER%" (
        echo Descargando PostgreSQL %PG_VERSION%...
        powershell -Command "Invoke-WebRequest -Uri '%PG_URL%' -OutFile '%TEMP%\%PG_INSTALLER%'"
    ) else (
        echo Instalador ya existe en %TEMP%\%PG_INSTALLER%
    )

    echo Instalando PostgreSQL en modo silencioso...
    echo (Esto puede tardar unos minutos)
    "%TEMP%\%PG_INSTALLER%" ^
        --mode unattended ^
        --unattendedmodeui none ^
        --servicename postgresql-x64-16 ^
        --superpassword postgres ^
        --serverport 5432

    echo.
    echo [OK] PostgreSQL instalado.
)

:: Verificar que el servicio este corriendo
sc query postgresql-x64-16-16 >nul 2>&1
if %errorlevel% neq 0 (
    :: Intentar con otro nombre de servicio
    sc query postgresql-x64-16 >nul 2>&1
    if %errorlevel% neq 0 (
        echo.
        echo [ADVERTENCIA] El servicio de PostgreSQL no esta registrado.
        echo Por favor inicia el servicio manualmente o reinstala PostgreSQL.
        echo.
    )
) else (
    echo [OK] Servicio PostgreSQL encontrado.
)

:: Configurar pg_hba.conf para permitir login local con password
echo.
echo Configurando acceso a la base de datos...

:: Buscar directorio de datos de PostgreSQL
set PG_DATA=
for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\PostgreSQL\Installations\postgresql-x64-16-16" /v "Data Directory" 2^>nul') do set PG_DATA=%%b
if "!PG_DATA!"=="" (
    for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\PostgreSQL\Installations\postgresql-x64-16" /v "Data Directory" 2^>nul') do set PG_DATA=%%b
)

if not exist "!PG_DATA!\pg_hba.conf" (
    echo [ERROR] No se encontro pg_hba.conf en !PG_DATA!
    echo.
    echo PASO MANUAL: Edita pg_hba.conf y agrega:
    echo   host    all     all     127.0.0.1/32    md5
    echo   host    all     all     ::1/128         md5
    echo.
) else (
    :: Backup
    copy /Y "!PG_DATA!\pg_hba.conf" "!PG_DATA!\pg_hba.conf.bak" >nul 2>&1

    :: Verificar si ya esta configurado
    findstr /C:"127.0.0.1/32" "!PG_DATA!\pg_hba.conf" | findstr /C:"md5" >nul 2>&1
    if %errorlevel% neq 0 (
        echo host    all     all     127.0.0.1/32    md5 >> "!PG_DATA!\pg_hba.conf"
        echo host    all     all     ::1/128         md5 >> "!PG_DATA!\pg_hba.conf"
        echo [OK] pg_hba.conf actualizado.
    ) else (
        echo [OK] pg_hba.conf ya tiene configuracion de acceso.
    )
)

:: Reiniciar servicio para aplicar cambios
echo.
echo Reiniciando servicio PostgreSQL...
net stop postgresql-x64-16-16 >nul 2>&1
net start postgresql-x64-16-16 >nul 2>&1
if %errorlevel% neq 0 (
    net stop postgresql-x64-16 >nul 2>&1
    net start postgresql-x64-16 >nul 2>&1
)
echo [OK] Servicio reiniciado.

:: Crear base de datos y usuario
echo.
echo Creando base de datos 'cobranza'...
"!PG_DATA!\..\bin\psql.exe" -U postgres -c "SELECT 1 FROM pg_database WHERE datname='cobranza'" -t | findstr /C:"1" >nul 2>&1
if %errorlevel% neq 0 (
    "!\PG_DATA!\..\bin\psql.exe" -U postgres -c "CREATE DATABASE cobranza;" >nul 2>&1
    echo [OK] Base 'cobranza' creada.
) else (
    echo [OK] Base 'cobranza' ya existe.
)

:: Configurar aplicacion para offline
echo.
echo Configurando modo offline en .env...
echo DB_URL=jdbc:postgresql://localhost:5432/cobranza > .env
echo DB_USER=postgres >> .env
echo DB_PASSWORD=postgres >> .env
echo SERVER_PORT=8080 >> .env
echo SERVER_ADDRESS=0.0.0.0 >> .env
echo MAVEN_OPTS=-Djava.net.preferIPv4Stack=true >> .env
echo [OK] .env configurado para offline.

:: Compilar la aplicacion
echo.
echo Compilando la aplicacion...
call mvn package -DskipTests -q
if %errorlevel% equ 0 (
    echo [OK] Compilacion exitosa.
) else (
    echo [ERROR] Error en compilacion.
    echo Asegurate de tener Maven instalado y en PATH.
    pause
    exit /b 1
)

echo.
echo =============================================
echo  Instalacion completada.
echo =============================================
echo.
echo Ejecuta launch.bat para iniciar la aplicacion.
echo.
pause
