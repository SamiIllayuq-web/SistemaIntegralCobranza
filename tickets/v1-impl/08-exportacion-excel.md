# 08 — Exportación a Excel

**What to build:** El usuario puede exportar un Excel filtrado por empresa + agencia. Genera el archivo según el perfil de export de la empresa.

**Blocked by:** 07

**Status:** ready-for-agent

- [ ] Filtro: empresa + agencia (todas las operaciones de esa agencia)
- [ ] Perfil de export `resources/perfiles-export/caja-arequipa.json` (mapeo inverso de columnas)
- [ ] Generación del archivo Excel con Apache POI
- [ ] AuditoriaEvento para EXPORT_OK
- [ ] Descarga del archivo desde el navegador
- [ ] Validación: si no hay operaciones para el filtro, mostrar mensaje

---

## Notes

Ver SPEC.md sección 4.6.
El perfil de export define qué columnas se incluyen y en qué orden.
Por ahora es un solo perfil para Caja Arequipa — extensibilidad para más empresas queda para después.
