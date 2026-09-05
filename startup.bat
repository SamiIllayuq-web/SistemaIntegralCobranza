@echo off
setlocal enabledelayedexpansion

:: =============================================
::  Sistema Integral de Cobranza
::  Script de inicio — modo ONLINE (Supabase)
:: =============================================
echo.
echo =============================================
echo  Sistema Integral de Cobranza
echo =============================================
echo.

:: Cargar variables de .env (Supabase)
if exist .env (
    for /f "tokens=1,* delims==" %%a in ('type .env ^| findstr /V "^#" ^| findstr "="') do (
        set %%a=%%b
    )
    echo [*] Variables cargadas desde .env
) else (
    echo [ERROR] No se encontro archivo .env
    echo Asegurate de que .env este en la misma carpeta.
    pause
    exit /b 1
)

:: Verificar variables esenciales
if not defined DB_URL (
    echo [ERROR] DB_URL no esta definida en .env
    pause
    exit /b 1
)

echo [*] Modo: ONLINE (Supabase)
echo [*] DB_URL: !DB_URL!
echo.
echo [*] Iniciando aplicacion...
echo.

:: Obtener la ruta del directorio donde esta este script
set "SCRIPT_DIR=%~dp0"

:: Ejecutar la aplicacion con las variables cargadas
java -Djava.net.preferIPv4Stack=true -jar "%SCRIPT_DIR%app\cobranza-1.0.0.jar"
