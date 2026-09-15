#!/bin/bash
# Restaurar Admin, Importar e Historial en el sidebar
# Uso: ./sidebar-mostrar.sh

LAYOUT="src/main/resources/templates/layout.html"

# Admin: quitar style="display:none" del div de Admin
sed -i 's|<div class="nav-section-title" style="display:none">Admin</div>|<div class="nav-section-title">Admin</div>|' "$LAYOUT"

# Importar e Historial: quitar style="display:none"
sed -i 's|<a th:href="@{/cartera/importar}" class="nav-item" id="nav-importar" style="display:none">|<a th:href="@{/cartera/importar}" class="nav-item" id="nav-importar">|g' "$LAYOUT"
sed -i 's|<a th:href="@{/cartera/historial}" class="nav-item" id="nav-historial" style="display:none">|<a th:href="@{/cartera/historial}" class="nav-item" id="nav-historial">|g' "$LAYOUT"

echo "Restaurado: Admin, Importar, Historial"
