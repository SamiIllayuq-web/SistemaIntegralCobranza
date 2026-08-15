---
id: 12
title: "[GRILLING] Mapeo hojas 2 y 3 + 5 estados de cartera + 7 campos nuevos"
status: ready-for-agent
type: afk
priority: high
dependencies: []
created: 2026-08-14
---

# Grilling — Mapeo hojas 2 y 3 + estados de cartera

## Decisiones validadas

### Estructura de hojas

| Hoja | Nombre | Contenido |
|------|--------|-----------|
| 1 | CARTERA SELVA CENTRAL | Casos activos (ya mapeada) |
| 2 | CARTERA C. CREDITO CANCELADO | Cancelada + Desasignada + Vendida |
| 3 | CARPETAS DEVUELTAS. | Mismo esquema que hoja 2, pero de otra agencia/region |

Hojas 2 y 3 tienen las mismas 55 columnas. Hoja 1 tiene 53 columnas con headers en fila 2 (merged cells en fila 1).

### Detección de estado (sheet 2 y 3)

Los estados se detectan por **section headers en columna B** (col 2):

| Header en col B | `estadoCartera` |
|-----------------|-----------------|
| (ninguno, antes del primer header) | CANCELADA |
| `CARTERA DESASIGNADA` | DESASIGNADA |
| `CARTERA VENDIDA` | VENDIDA |

Para **sheet 3** (CARPETAS DEVUELTAS): el estado por defecto es DEVUELTA si no hay section header explícito.

### 5 estados de cartera

| `estadoCartera` | Fuente | ¿Final o transicional? |
|-----------------|--------|------------------------|
| ACTIVO | Hoja 1 | Transicional |
| CANCELADA | Hoja 2/3 | Puede ser final o transicional |
| DESASIGNADA | Hoja 2/3 | Puede ser final o transicional |
| VENDIDA | Hoja 2/3 | Puede ser final o transicional |
| DEVUELTA | Hoja 3 | Puede ser final o transicional |

Cualquier estado puede ir a cualquier otro — no hay flujo fijo. El usuario hace UPDATE manual de `estadoCartera` cuando mueve la operación.

**Observaciones en OBSERVACION** (como "CARTERA VENDIDA", "CARTERA CASTIGADA Y DEVUELTA") son solo texto — no van a campo propio.

### Campos nuevos (no existían en Operacion.java)

| Campo Excel | Tipo | Entidad | Notas |
|-------------|------|---------|-------|
| `FEC. DESEM.` | LocalDate | Operacion | `fechaDesembolso` |
| `IMP DESEM.` | BigDecimal | Operacion | `importeDesembolso` |
| `ETAPA PROCESAL ...` | String | Operacion | `etapaProcesalTexto` — texto libre, no enum |
| `ACTO PENDIENTE/ESCRITO A PRESENTAR` | String | Operacion | `actoPendiente` |
| `FECHA DE ULTIMO ESTADO PROCESO Y/O ESCRITO` | LocalDate | Operacion | `fechaUltimoEstadoProceso` |
| `ASIENTO DE INSCRIPCION` | String | BienEmbargado | Ya existe como `asientoInscripcion` |
| `FECHA DE PRESENTACIÓN DEL TITULO EN RRPP` | LocalDate | BienEmbargado | Ya existe como `fechaPresentacionRrpp` |

Los dos últimos ya existen en BienEmbargado — verificar que el parser los mapea correctamente para la hoja 1 (que es donde aparecen estos campos).

### Notas adicionales

- `distritoJudicial` funciona como filtro de región (no necesita campo propio de región).
- La hoja 3 comparte estructura completa con hoja 2 — el parser puede ser el mismo con detection de estado diferente.
- Nuevos valores de etapa son texto libre — no requieren enum cerrado.

## Implementación

1. Agregar 5 campos nuevos a `Operacion.java`:
   - `fechaDesembolso` (LocalDate)
   - `importeDesembolso` (BigDecimal)
   - `etapaProcesalTexto` (String)
   - `actoPendiente` (String, TEXT)
   - `fechaUltimoEstadoProceso` (LocalDate)
   - `estadoCartera` (String) — nuevo campo para los 5 estados

2. Agregar `estadoCartera` como campo en `OperacionFormDTO`, `OperacionDTO`, `OperacionMapper`.

3. Actualizar `CarteraService` — parser para hojas 2 y 3:
   - Detectar section headers en col B para asignar `estadoCartera`
   - Para sheet 2: CANCELADA por defecto, cambia en DESASIGNADA/VENDIDA
   - Para sheet 3: DEVUELTA por defecto
   - Mapear los 5 campos nuevos de Operacion
   - Verificar mapeo correcto de `asientoInscripcion` y `fechaPresentacionRrpp` en BienEmbargado para hoja 1

4. Actualizar `CarteraServiceTest` con casos de las nuevas hojas.

5. `mvn compile` — verificar compilación.

6. Commit.
