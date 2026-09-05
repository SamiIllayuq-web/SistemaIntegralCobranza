@echo off
setlocal enabledelayedexpansion

echo =============================================
echo  Instalador - Sistema Integral de Cobranza
echo =============================================
echo.

:: =============================================
:: 1) INSTALAR JDK 21
:: =============================================
echo [*] Verificando JDK 21...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr "version"') do set CURRENT_JAVA=%%v
    echo [OK] JDK encontrado: !CURRENT_JAVA!
) else (
    echo [*] JDK no encontrado. Descargando OpenJDK 21...
    set JDK_VERSION=21.0.5
    set JDK_MSI=OpenJDK21U-jdk_x64_windows_hotspot_%JDK_VERSION%.msi
    set JDK_URL=https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.5%%2B11/%JDK_MSI%

    if not exist "%TEMP%\%JDK_MSI%" (
        echo    Descargando OpenJDK 21 (~180 MB)...
        powershell -Command "Invoke-WebRequest -Uri '!JDK_URL!' -OutFile '%TEMP%\%JDK_MSI%'"
    )
    echo    Instalando JDK 21 (puede tardar ~2 minutos)...
    msiexec /i "%TEMP%\%JDK_MSI%" /quiet /norestart ADDLOCAL="FeatureJavaHeadless"
    echo [OK] JDK 21 instalado.
)

:: Detectar JAVA_HOME desde el registro
set "JAVA_HOME="
for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\Eclipse Adoptium\JDK" /v "Path" 2^>nul') do set "JAVA_HOME=%%b"
if "!JAVA_HOME!"=="" (
    for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\JavaSoft\Java Runtime Environment" /v "JavaHome" 2^>nul') do set "JAVA_HOME=%%b"
)
if not exist "!JAVA_HOME!\bin\java.exe" set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot"
if not exist "!JAVA_HOME!\bin\java.exe" set "JAVA_HOME=C:\Program Files\Java\jdk-21"
if exist "!JAVA_HOME!\bin\java.exe" (
    set "PATH=!JAVA_HOME!\bin;!PATH!"
    echo    JAVA_HOME=!JAVA_HOME!
) else (
    echo [WARN] No se pudo detectar JAVA_HOME. Asegurate de que JDK 21 este en el PATH.
)
echo.

:: =============================================
:: 2) INSTALAR POSTGRESQL 16
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
        powershell -Command "Invoke-WebRequest -Uri '!PG_URL!' -OutFile '%TEMP%\%PG_INSTALLER%'"
    )
    echo    Instalando PostgreSQL %PG_VERSION% (puede tardar ~3 minutos)...
    "%TEMP%\%PG_INSTALLER%" ^
        --mode unattended ^
        --unattendedmodeui minimal ^
        --servicename postgresql-x64-16 ^
        --superpassword postgres ^
        --serverport 5432
    echo [OK] PostgreSQL instalado.
)
echo.

:: =============================================
:: 3) VERIFICAR Y ARRANCAR SERVICIO POSTGRESQL
:: =============================================
echo [*] Verificando servicio PostgreSQL...

:: Buscar el nombre real del servicio en el registro
set PG_SERVICE=
for /f "tokens=1 delims=:" %%s in ('sc query type^= service state^= all 2^>nul ^| findstr /i "postgresql"') do (
    sc query %%s 2^>nul | findstr /i "DISPLAY_NAME" >nul 2>&1
    if !errorlevel! equ 0 (
        for /f "tokens=2 delims=:" %%d in ('sc qc %%s 2^>nul ^| findstr BINARY_PATH_NAME') do (
            echo %%d | findstr /i "postgres" >nul 2>&1
            if !errorlevel! equ 0 set "PG_SERVICE=%%s"
        )
    )
)

:: Si no se encontro por el registro, intentar por nombre conocido
if "!PG_SERVICE!"=="" (
    sc query postgresql-x64-16-16 >nul 2>&1
    if !errorlevel! equ 0 set PG_SERVICE=postgresql-x64-16-16
    sc query postgresql-x64-16 >nul 2>&1
    if !errorlevel! equ 0 set PG_SERVICE=postgresql-x64-16
)

if "!PG_SERVICE!"=="" (
    echo [ERROR] No se encontro el servicio de PostgreSQL. Instala PostgreSQL manualmente.
    pause
    exit /b 1
)
echo    Servicio: !PG_SERVICE!

net stop !PG_SERVICE! >nul 2>&1
net start !PG_SERVICE! >nul 2>&1
:: Esperar a que el servicio arranque
timeout /t 3 >nul
echo [OK] Servicio PostgreSQL activo.
echo.

:: =============================================
:: 4) CONFIGURAR pg_hba.conf
:: =============================================
echo [*] Configurando acceso a la base de datos...

set PG_DATA=
for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\PostgreSQL\Installations\postgresql-x64-16" /v "Data Directory" 2^>nul') do set "PG_DATA=%%b"
if "!PG_DATA!"=="" (
    for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\PostgreSQL\Installations\postgresql-x64-16-16" /v "Data Directory" 2^>nul') do set "PG_DATA=%%b"
)

if not exist "!PG_DATA!\pg_hba.conf" (
    echo [ERROR] No se encontro pg_hba.conf en !PG_DATA!
) else (
    :: Agregar lineas si no existen (PostgreSQL ignorara lineas duplicadas)
    findstr /C:"127.0.0.1/32" /C:"::1/128" "!PG_DATA!\pg_hba.conf" | findstr /C:"md5" >nul 2>&1
    if %errorlevel% neq 0 (
        echo host    all     all     127.0.0.1/32    md5 >> "!PG_DATA!\pg_hba.conf"
        echo host    all     all     ::1/128         md5 >> "!PG_DATA!\pg_hba.conf"
        echo [OK] pg_hba.conf actualizado.
        :: Reiniciar para aplicar
        net stop !PG_SERVICE! >nul 2>&1
        net start !PG_SERVICE! >nul 2>&1
        timeout /t 2 >nul
    ) else (
        echo [OK] pg_hba.conf ya tiene configuracion md5.
    )
)
echo.

:: =============================================
:: 5) CREAR BASE DE DATOS
:: =============================================
echo [*] Creando base de datos 'cobranza'...
:: psql esta en el PATH gracias a la instalacion de PostgreSQL
psql -U postgres -c "SELECT 1 FROM pg_database WHERE datname='cobranza'" -t 2>nul | findstr /C:"1" >nul 2>&1
if %errorlevel% neq 0 (
    psql -U postgres -c "CREATE DATABASE cobranza;" 2>nul
    if !errorlevel! equ 0 (
        echo [OK] Base 'cobranza' creada.
    ) else (
        echo [WARN] No se pudo crear la base. Verifica que PostgreSQL este corriendo.
    )
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
echo [*] Compilando la aplicacion...
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
