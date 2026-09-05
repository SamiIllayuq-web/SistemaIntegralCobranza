@echo off
:: Cargar variables de .env para modo Online
:: Usar: call load-env.bat
if not exist .env (
    echo [ERROR] .env no encontrado
    exit /b 1
)
for /f "tokens=1,* delims== eol=#" %%a in ('type .env ^| findstr /V "^#" ^| findstr "="') do (
    set %%a=%%b
)
exit /b 0
