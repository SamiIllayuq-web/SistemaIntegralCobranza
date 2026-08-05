---
id: 2
title: "Corregir CarteraService: match por DNI + UPSERT"
status: blocked
type: afk
priority: high
dependencies: [1]
created: 2026-07-24
blocked_reason: "El proyecto no compila por errores preexistentes (Lombok annotation processor). Una vez resueltos los errores de compilación, este fix está listo para aplicar."
note: "La lógica de upsert YA está escrita en CarteraService.java pero pendiente de verificación. Requiere que los errores preexistentes de Lombok en UsuarioMapper, ClienteMapper, etc. estén resueltos."
---

# Corregir CarteraService: match por DNI + UPSERT

## Problema

`CarteraService.importarExcel()` hace `clienteRepository.save()` sin buscar si el cliente ya existe. Cada import crea duplicados.

## Decisión de diseño validada (ticket 001)

- Matching por **DNI normalizado** (8 dígitos).
- Si existe: actualizar campos mutables (nombre, telefono, direccion, deudaCapital, deudaTotal, estadoGestion, agencia).
- Si no existe: crear nuevo.
- Campos inmutables: `dni` (identificador), `empresa` (viene del parámetro, no del Excel).

## Cambios necesarios

1. **`ClienteRepository`**: agregar `findByDni(String dni)` — búsqueda por DNI normalizado.
2. **`CarteraService.importarExcel()`**:
   - Por cada fila, buscar `Cliente` por DNI antes de guardar.
   - Si existe → actualizar campos mutables.
   - Si no existe → crear.
3. Agregar log.info de debug para saber qué se creó vs actualizó.

##验收标准

1. Importar el mismo Excel dos veces → 0 duplicados, las filas se actualizan.
2. Importar un Excel con cliente nuevo → se crea correctamente.
3. `mvn compile` pasa sin errores.
