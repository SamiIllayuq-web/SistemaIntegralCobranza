---
id: 4
title: "Importación cartera simple (nuevo modelo)"
status: done
type: afk
priority: high
dependencies: [2, 3]
created: 2026-07-24
resolved: 2026-07-25
---

# Importación cartera simple (nuevo modelo)

**Status: ✅ DONE**

- [x] Servicio que lee el perfil JSON y parsea el Excel con POI
- [x] Upsert por (cuenta, numero_operacion) — si existe actualiza, si no crea
- [x] Creación de Cliente si el DNI no existe
- [x] Creación de Agencia si no existe (buscar por nombre + empresa)
- [x] Creación de BienEmbargado si hay partida registral (columna 15)
- [x] Validación de campos obligatorios (DNI, cuenta, operacion)
- [x] Reporte de resultado: creados / actualizados / errores
- [x] Endpoint existente (CarteraController) — funciona sin cambios

## Implementación

`CarteraService.java` reescrito con:
- `@PostConstruct init()`: carga el perfil JSON de `resources/perfiles-import/caja-arequipa-cartera.json`
- `parseRow()`: lee celdas según mapeo del perfil JSON
- Find-or-create de Cliente por DNI normalizado
- Find-or-create de Agencia por nombre + empresa
- Upsert de Operacion por `(empresa_id, cuenta, numero_operacion)`
- BienEmbargado creado directamente si `numeroFichaRegistral` tiene valor
- Errores collected con número de fila para reporte

## Notas

- `bienEmbargadoRepository.save(bien)` persiste directamente (FK `operacion_id` seteada en builder)
- La colección `operacion.bienesEmbargados` no se modifica en el proceso (orphanRemoval no se activa)
- El `sheetName` se lee del perfil JSON; fallback a `SheetAt(0)` si no existe
- `dateFormat` y `numberFormat` del perfil se aplican en los parsers
