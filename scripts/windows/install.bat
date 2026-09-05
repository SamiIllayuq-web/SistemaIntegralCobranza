@echo off
setlocal enabledelayedexpansion

echo =============================================
echo  Instalador - Sistema Integral de Cobranza
echo =============================================
echo.

:: =============================================
:: 1) INSTALAR JDK 21 (si no esta)
:: =============================================
echo [*] Verificando JDK 21...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    :: Extraer version
    for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr "version"') do set CURRENT_JAVA=%%v
    echo [OK] JDK encontrado: !CURRENT_JAVA!
) else (
    echo [*] JDK no encontrado. Descargando OpenJDK 21...
    set JDK_VERSION=21.0.5
    set JDK_MSI=OpenJDK21U-jdk_x64_windows_hotspot_%JDK_VERSION%.msi
    set JDK_URL=https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.5%%2B11/%JDK_MSI%

    if not exist "%TEMP%\%JDK_MSI%" (
        echo    Descargando OpenJDK 21 (~180 MB)...
        powershell -Command "Invoke-WebRequest -Uri '%JDK_URL%' -OutFile '%TEMP%\%JDK_MSI%'"
    )
    echo    Instalando JDK 21 (puede tardar ~2 minutos)...
    msiexec /i "%TEMP%\%JDK_MSI%" /quiet /norestart ADDLOCAL="FeatureJavaHeadless"
    echo [OK] JDK 21 instalado.
)

:: Agregar JAVA_HOME al PATH para esta sesion
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot"
if not exist "!JAVA_HOME!" set "JAVA_HOME=C:\Program Files\Java\jdk-21"
if not exist "!JAVA_HOME!" (
    for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\Eclipse Adoptium\JDK" /ve 2^>nul') do set "JAVA_HOME=%%b"
)
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo    JAVA_HOME=!JAVA_HOME!
echo.

:: =============================================
:: 2) INSTALAR POSTGRESQL 16 (si no esta)
:: =============================================
echo [*] Verificando PostgreSQL...
where psql >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] PostgreSQL ya instalado.
) else (
    echo [*] PostgreSQL no encontrado. Descargando...
    set PG_VERSION=16.3
    set PG_INSTALLER=postgresql-%PG_VERSION%-windows-x64.exe
    set PG_URL=https://get.enterprisedb.com/postgresql/postgresql-%PG_VERSION%-windows-x64.exe

    if not exist "%TEMP%\%PG_INSTALLER%" (
        echo    Descargando PostgreSQL %PG_VERSION% (~200 MB)...
        powershell -Command "Invoke-WebRequest -Uri '%PG_URL%' -OutFile '%TEMP%\%PG_INSTALLER%'"
    )
    echo    Instalando PostgreSQL %PG_VERSION% (puede tardar ~3 minutos)...
    "%TEMP%\%PG_INSTALLER%" ^
        --mode unattended ^
        --unattendedmodeui none ^
        --servicename postgresql-x64-16 ^
        --superpassword postgres ^
        --serverport 5432
    echo [OK] PostgreSQL instalado.
)
echo.

:: =============================================
:: 3) VERIFICAR SERVICIO POSTGRESQL
:: =============================================
echo [*] Verificando servicio PostgreSQL...
set PG_SERVICE=postgresql-x64-16
sc query !PG_SERVICE! >nul 2>&1
if %errorlevel% neq 0 (
    set PG_SERVICE=postgresql-x64-16-16
    sc query !PG_SERVICE! >nul 2>&1
    if %errorlevel% neq 0 (
        echo [ERROR] Servicio PostgreSQL no encontrado. Instala PostgreSQL manualmente.
        pause
        exit /b 1
    )
)
echo    Servicio: !PG_SERVICE!

:: Asegurar que el servicio esta corriendo
net start !PG_SERVICE! >nul 2>&1
echo [OK] Servicio PostgreSQL activo.
echo.

:: =============================================
:: 4) CONFIGURAR pg_hba.conf
:: =============================================
echo [*] Configurando acceso a la base de datos...

set PG_DATA=
for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\PostgreSQL\Installations\postgresql-x64-16" /v "Data Directory" 2^>nul') do set PG_DATA=%%a
if "!PG_DATA!"=="" (
    for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\PostgreSQL\Installations\postgresql-x64-16-16" /v "Data Directory" 2^>nul') do set PG_DATA=%%a
)

if not exist "!PG_DATA!\pg_hba.conf" (
    echo [ERROR] No se encontro pg_hba.conf
) else (
    findstr /C:"127.0.0.1/32" "!PG_DATA!\pg_hba.conf" | findstr /C:"md5" >nul 2>&1
    if %errorlevel% neq 0 (
        echo host    all     all     127.0.0.1/32    md5 >> "!PG_DATA!\pg_hba.conf"
        echo host    all     all     ::1/128         md5 >> "!PG_DATA!\pg_hba.conf"
        echo [OK] pg_hba.conf actualizado.
    ) else (
        echo [OK] pg_hba.conf ya configurado.
    )
)

:: Reiniciar para aplicar cambios en pg_hba.conf
net stop !PG_SERVICE! >nul 2>&1
net start !PG_SERVICE! >nul 2>&1
echo    Servicio reiniciado.
echo.

:: =============================================
:: 5) CREAR BASE DE DATOS
:: =============================================
echo [*] Creando base de datos 'cobranza'...

set PGBIN=
for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\PostgreSQL\Installations\postgresql-x64-16" /v "bin directory" 2^>nul') do set PGBIN=%%b
if "!PGBIN!"=="" (
    for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\PostgreSQL\Installations\postgresql-x64-16-16" /v "bin directory" 2^>nul') do set PGBIN=%%b
)

"!PGBIN!\psql.exe" -U postgres -c "SELECT 1 FROM pg_database WHERE datname='cobranza'" -t | findstr /C:"1" >nul 2>&1
if %errorlevel% neq 0 (
    "!PGBIN!\psql.exe" -U postgres -c "CREATE DATABASE cobranza;"
    echo [OK] Base 'cobranza' creada.
) else (
    echo [OK] Base 'cobranza' ya existe.
)
echo.

:: =============================================
:: 6) CREAR ARCHIVO .env
:: =============================================
echo [*] Configurando archivo .env...
echo DB_URL=jdbc:postgresql://localhost:5432/cobranza > .env
echo DB_USER=postgres >> .env
echo DB_PASSWORD=postgres >> .env
echo SERVER_PORT=8080 >> .env
echo SERVER_ADDRESS=0.0.0.0 >> .env
echo [OK] .env configurado.
echo.

:: =============================================
:: 7) COMPILAR APLICACION
:: =============================================
echo [*] Compilando la aplicacion con Maven...
call mvnw.cmd package -DskipTests -q
if %errorlevel% equ 0 (
    echo [OK] Compilacion exitosa.
) else (
    echo [ERROR] Error en compilacion.
    pause
    exit /b 1
)
echo.

:: =============================================
:: FIN
:: =============================================
echo =============================================
echo  Instalacion completada.
echo =============================================
echo.
echo Proximo paso: ejecuta launch.bat para iniciar la aplicacion.
echo.
pause
