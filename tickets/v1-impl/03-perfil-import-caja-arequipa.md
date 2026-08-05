---
id: 3
title: "Perfil de import Caja Arequipa"
status: done
type: afk
priority: high
dependencies: [2]
created: 2026-07-24
resolved: 2026-07-25
---

# Perfil de import Caja Arequipa

**Status: ✅ DONE**

- [x] Archivo JSON en `resources/perfiles-import/caja-arequipa-cartera.json`
- [x] Mapeo de todas las columnas usadas (A-B omitidas, C-Z mapeadas según Excel real)
- [x] Campo `sheetName` apunta a "Hoja2"
- [x] `dateFormat` y `numberFormat` correctos
- [x] El JSON es válido y se puede parsear

## Notas de implementación

- Columna 25 del Excel (VALOR DE TASACION EN SOLES) mapea a `montoMc` en la entidad, no a `valorTasacion`.
- `tipoMc` mapea a `tipoBien` en la entidad (nombres diferentes).
- `numeroFichaRegistral` mapea a `partidaRegistral` en la entidad.
- Las direcciones de inmueble van a `direccion`, `distrito`, `provincia`, `departamento` en la entidad.
- La columna `numeroFichaRegistral` (col 15) determina si se crea un BienEmbargado o no.
