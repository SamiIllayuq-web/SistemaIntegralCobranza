#!/bin/bash
# Run Spring Boot con logs persistidos en logs/
set -e

cd "$(dirname "$0")"

mkdir -p logs

export LOG_DIR="$(pwd)/logs"

echo "Logs en: $LOG_DIR"
echo "Ejecutando..."
mvn spring-boot:run 2>&1 | tee -a "$LOG_DIR/run-$(date +%Y%m%d-%H%M%S).log"
