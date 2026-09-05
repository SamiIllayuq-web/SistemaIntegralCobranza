#!/bin/bash
# Launch script — Sistema Integral de Cobranza
# Uso: ./launch.sh

# Cargar .env si existe (modo offline por defecto)
load_env() {
    if [ -f .env ]; then
        set -a
        source .env
        set +a
    fi
}

menu() {
    clear
    echo "============================================="
    echo "  Sistema Integral de Cobranza"
    echo "  Menu de lanzamiento"
    echo "============================================="
    echo ""
    echo "  [1] Online  — Conectar a Supabase (requiere internet)"
    echo "  [2] Offline — Usar base de datos local (PostgreSQL)"
    echo "  [3] Solo compilar (mvn compile)"
    echo ""
    echo "  [0] Salir"
    echo ""
}

load_env

while true; do
    menu
    read -p "Elige una opcion: " choice
    echo ""

    case "$choice" in
        1)  # Online
            echo "[*] Cargando variables de Supabase..."
            if [ -f load-env.fish ]; then
                # fish script — source directamente no funciona en bash
                # Extraer variables del .env para supabase
                if [ -f .env.supabase ]; then
                    set -a
                    source .env.supabase
                    set +a
                    echo "[OK] Variables de Supabase cargadas."
                else
                    echo "[ERROR] .env.supabase no encontrado."
                    echo "Crea un archivo .env.supabase con las variables de Supabase:"
                    echo "  DB_URL=jdbc:postgresql://..."
                    echo "  DB_USER=postgres.projectref"
                    echo "  DB_PASSWORD=..."
                    read -p "Presiona Enter para continuar..."
                    continue
                fi
            elif [ -f .env ]; then
                set -a
                source .env
                set +a
                echo "[OK] Variables .env cargadas."
            fi
            echo ""
            echo "[*] Iniciando aplicacion en modo ONLINE..."
            echo ""
            mvn spring-boot:run
            ;;
        2)  # Offline
            echo "[*] Iniciando en modo OFFLINE..."
            # Configurar variables para offline
            export DB_URL=jdbc:postgresql://localhost:5432/cobranza
            export DB_USER=cobranza
            export DB_PASSWORD=cobranza123
            export SERVER_PORT=8080
            export SERVER_ADDRESS=0.0.0.0
            echo "DB_URL: $DB_URL"
            echo "DB_USER: $DB_USER"
            echo ""
            echo "[*] Iniciando aplicacion en modo OFFLINE..."
            echo ""
            mvn spring-boot:run
            ;;
        3)  # Compilar
            echo "Compilando..."
            mvn compile -q
            if [ $? -eq 0 ]; then
                echo "[OK] Compilacion exitosa."
            else
                echo "[ERROR] Error en compilacion."
            fi
            read -p "Presiona Enter para continuar..."
            ;;
        0)
            echo "Hasta luego."
            exit 0
            ;;
        *)
            echo "Opcion invalida."
            sleep 2
            ;;
    esac
done
