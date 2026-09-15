#!/bin/bash
# Ocultar Admin (usuarios), Importar e Historial del sidebar
# Uso: ./sidebar-ocultar.sh

LAYOUT="src/main/resources/templates/layout.html"

# Admin: ocultar el div de Admin con style="display:none"
sed -i 's|<div class="nav-section-title">Admin</div>|<div class="nav-section-title" style="display:none">Admin</div>|' "$LAYOUT"

# Importar e Historial: añadir style="display:none" al tag <a> completo
sed -i 's|<a th:href="@{/cartera/importar}" class="nav-item" id="nav-importar">|<a th:href="@{/cartera/importar}" class="nav-item" id="nav-importar" style="display:none">|g' "$LAYOUT"
sed -i 's|<a th:href="@{/cartera/historial}" class="nav-item" id="nav-historial">|<a th:href="@{/cartera/historial}" class="nav-item" id="nav-historial" style="display:none">|g' "$LAYOUT"

echo "Ocultado: Admin, Importar, Historial"
