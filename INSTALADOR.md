# Instalador — Sistema Integral de Cobranza

## Estructura de scripts

```
scripts/
├── windows/
│   ├── install.bat     — instala PostgreSQL + configura BD + compila (una vez)
│   ├── launch.bat      — menu online/offline + arranca la app
│   ├── load-env.bat    — helper para cargar variables .env
│   ├── build-exe.bat   — genera .exe con jpackage
│   ├── compile.bat     — solo compila (mvn compile)
│   └── run.bat         — arranca la app directamente
└── linux/
    ├── install.sh      — instala PostgreSQL + configura BD + compila
    ├── launch.sh       — menu online/offline + arranca la app
    ├── load-env.fish   — helper para cargar variables .env (Fish shell)
    └── run.sh          — arranca la app directamente
```

## Instalacion en Windows (nueva maquina)

### Paso 1: Una vez
```cmd
scripts\windows\install.bat
```
Esto:
- Descarga e instala PostgreSQL 16 (si no esta instalado)
- Crea la base de datos `cobranza`
- Configura acceso local con password
- Genera `.env` con configuracion offline
- Compila el proyecto

### Paso 2: Cada vez que quieras usar la app
```cmd
scripts\windows\launch.bat
```
Menu:
- **[1] Online** — usa Supabase (necesita `.env.supabase`)
- **[2] Offline** — usa PostgreSQL local
- **[3] Compilar** — solo compila (mvn compile)
- **[0] Salir**

## Instalacion en Linux

### Paso 1: Una vez
```bash
bash scripts/linux/install.sh
```

### Paso 2: Cada vez
```bash
bash scripts/linux/launch.sh
```

## Configuracion Online (Supabase)

Crea un archivo `.env.supabase` en la raiz del proyecto:

```bash
DB_URL=jdbc:postgresql://aws-0-ca-central-1.pooler.supabase.com:5432/postgres
DB_USER=postgres.PROJECT_REF
DB_PASSWORD=tu_password
SERVER_PORT=8080
SERVER_ADDRESS=0.0.0.0
MAVEN_OPTS=-Djava.net.preferIPv4Stack=true
```

El modo online usa este archivo cuando eliges [1] en el menu.

## Generar .exe con jpackage (Windows)

Requiere JDK 21+ en Windows:

```cmd
scripts\windows\build-exe.bat
```

Genera: `dist/SistemaCobranza/SistemaCobranza.exe`

## Archivos creados por install

```
.env              — configuracion offline (PostgreSQL local)
.env.supabase     — configuracion online (Supabase) — NO sube a git
```

## Requisitos

- Java 21+
- Maven 3.9+
- Windows 10/11 64-bit (para los .bat)
- Linux con PostgreSQL disponible (para los .sh)
