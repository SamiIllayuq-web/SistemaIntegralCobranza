#!/bin/bash
# Install script para Linux — Sistema Integral de Cobranza
set -e

echo "============================================="
echo "  Instalador - Sistema Integral de Cobranza"
echo "============================================="
echo ""

# Detectar distribucion
if command -v apt-get &> /dev/null; then
    PKG_MANAGER="apt-get"
elif command -v pacman &> /dev/null; then
    PKG_MANAGER="pacman"
elif command -v dnf &> /dev/null; then
    PKG_MANAGER="dnf"
else
    echo "[ERROR] No se detecto un gestor de paquetes compatible."
    echo "Instala PostgreSQL manualmente."
    exit 1
fi

# Verificar si PostgreSQL esta instalado
if command -v psql &> /dev/null; then
    echo "[OK] PostgreSQL ya esta instalado."
else
    echo "[*] PostgreSQL no encontrado. Instalando..."
    if [ "$PKG_MANAGER" = "apt-get" ]; then
        sudo apt-get update
        sudo apt-get install -y postgresql postgresql-contrib
    elif [ "$PKG_MANAGER" = "pacman" ]; then
        sudo pacman -S --noconfirm postgresql
    elif [ "$PKG_MANAGER" = "dnf" ]; then
        sudo dnf install -y postgresql-server postgresql
    fi
    echo "[OK] PostgreSQL instalado."
fi

# Iniciar servicio
echo ""
echo "Iniciando servicio PostgreSQL..."
if command -v systemctl &> /dev/null; then
    sudo systemctl start postgresql 2>/dev/null || sudo systemctl start postgresql-16 2>/dev/null || true
    sudo systemctl enable postgresql 2>/dev/null || sudo systemctl enable postgresql-16 2>/dev/null || true
else
    echo "[AVISO] systemctl no disponible. Inicia PostgreSQL manualmente."
fi

# Esperar a que este listo
sleep 2

# Crear base de datos
echo ""
echo "Creando base de datos 'cobranza'..."
sudo -u postgres psql -c "SELECT 1 FROM pg_database WHERE datname='cobranza'" -t 2>/dev/null | grep -q 1
if [ $? -ne 0 ]; then
    sudo -u postgres createdb cobranza 2>/dev/null || true
    echo "[OK] Base 'cobranza' creada."
else
    echo "[OK] Base 'cobranza' ya existe."
fi

# Crear usuario si no existe
sudo -u postgres psql -c "SELECT 1 FROM pg_roles WHERE rolname='cobranza'" -t 2>/dev/null | grep -q 1
if [ $? -ne 0 ]; then
    sudo -u postgres psql -c "CREATE USER cobranza WITH PASSWORD 'cobranza123';" 2>/dev/null || true
    sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE cobranza TO cobranza;" 2>/dev/null || true
    echo "[OK] Usuario 'cobranza' creado."
else
    echo "[OK] Usuario 'cobranza' ya existe."
fi

# Configurar pg_hba.conf para login local
PG_HBA=$(sudo -u postgres psql -t -c "SHOW hba_file;" 2>/dev/null | tr -d ' ')
if [ -n "$PG_HBA" ] && [ -f "$PG_HBA" ]; then
    if ! grep -q "127.0.0.1/32.*md5" "$PG_HBA"; then
        echo "host    all     all     127.0.0.1/32    md5" | sudo tee -a "$PG_HBA" > /dev/null
        echo "[OK] pg_hba.conf actualizado."
    else
        echo "[OK] pg_hba.conf ya tiene configuracion de acceso."
    fi
    # Reiniciar para aplicar
    if command -v systemctl &> /dev/null; then
        sudo systemctl restart postgresql 2>/dev/null || sudo systemctl restart postgresql-16 2>/dev/null || true
    fi
fi

# Crear archivo .env para offline
echo ""
echo "Configurando modo offline en .env..."
cat > .env << 'EOF'
DB_URL=jdbc:postgresql://localhost:5432/cobranza
DB_USER=cobranza
DB_PASSWORD=cobranza123
SERVER_PORT=8080
SERVER_ADDRESS=0.0.0.0
MAVEN_OPTS=-Djava.net.preferIPv4Stack=true
EOF
echo "[OK] .env configurado para offline."

# Compilar
echo ""
echo "Compilando la aplicacion..."
mvn package -DskipTests -q
if [ $? -eq 0 ]; then
    echo "[OK] Compilacion exitosa."
else
    echo "[ERROR] Error en compilacion."
    exit 1
fi

echo ""
echo "============================================="
echo "  Instalacion completada."
echo "============================================="
echo ""
echo "Ejecuta ./launch.sh para iniciar la aplicacion."
echo ""
