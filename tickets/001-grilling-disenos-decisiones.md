---
id: 1
title: "[GRILLING] Sesión de preguntas sobre el diseño — decisiones validadas"
status: in-progress
type: afk
priority: high
dependencies: []
created: 2026-07-24
---

# Grilling — Decisiones validadas

Sesión de preguntas uno a uno sobre el diseño del sistema. Objetivo: entender cada decisión antes de codear.

## Pregunta 1 — `agencia` como entidad ✓ RESUELTA

**Pregunta:** ¿`agencia` es una entidad independiente o un atributo de `empresa`?

**Respuesta:** `agencia` es un atributo de `empresa`. Son sucursales de Caja Arequipa (Oxapampa, Chanchamayo, Pichanaqui, Tarma, etc.). Podría haber "Oxapampa 2" en el futuro. Las empresas son pocas (actualmente solo Caja Arequipa), pero el modelo debe permitir agregar más sin complejidad extra.

**Impacto en modelo:** La tabla `agencia` como entidad separada (actualmente en PLAN.md) debería convertirse en una columna `agencia` en `empresa`, o una tabla `empresa_sucursal (empresa_id, nombre)` si se quiere mantener histórico.

---

## Pregunta 2 — Datos de contacto: ¿en `cliente` o en `operacion`? ✓ RESUELTA

**Pregunta:** Si el mismo deudor aparece en Banco X y Banco Y, ¿los datos de contacto van al `cliente` o a cada `operacion`?

**Respuesta:** Los datos de contacto van a `cliente`. Si Caja Arequipa reporta un cambio de teléfono para el cliente 123, ese cambio afecta a todas las operaciones del cliente, incluyendo las de otros bancos.

**Impacto:** `cliente` es la fuente única de contacto. `operacion` NO tiene campos de contacto.

---

## Pregunta 3 — Reimportar la misma cartera ✓ RESUELTA

**Pregunta:** Al reimportar el Excel de la Cartera Marzo (mismos registros), ¿se duplican las operaciones o se actualizan?

**Respuesta:** Se actualizan, no se duplican. El par `(cuenta, numero_operacion)` identifica la operación. Reimportar el mismo Excel resulta en las mismas 1000 operaciones actualizadas, no 2000.

**Impacto:** El matching de operaciones es por `(cuenta, numero_operacion)` como clave única. `cartera` es solo un container de importación, sin peso en la lógica de negocio.

---

## Pregunta 4 — Identificador de `operacion`: ¿columna C (C&O) o D+E separados? ⚠️ PENDIENTE

**Pregunta:** En el Excel, la columna C (C&O) es `CUENTA + OPERACIÓN` concatenados. ¿El modelo usa la columna C completa como `cuenta`, o las columnas D y E por separado?

**Contexto del Excel:**
- Columna D: CUENTA (ej. `3565294`)
- Columna E: OPERACIÓN (ej. `17485025`)
- Columna C: C&O = D + E = `356529417485025` (concatenación)

**Hallazgo adicional:** La fila 26 tiene `=D23&E23` como fórmula en la columna C — POI por defecto puede leer la fórmula, no el valor calculado. Requiere manejo especial (`evaluateFormula()`).

**Respuesta:** PENDIENTE. La respuesta define el `UNIQUE INDEX` de `operacion`.

---

## Hallazgos adicionales del Excel real

### Celdas con fórmulas
- Fila 26: columna C (C&O) contiene `=D23&E23`. Se debe invocar `evaluateFormula()` de POI antes de procesar.

### Filas que se derraman
- Filas 14-16 son continuación de la fila 13 (descripción larga de bien embargado que excede el ancho). El parser debe ignorar estas filas extra sin NRO.

### Personas con múltiples operaciones
- Ejemplo: AQUINO ENCISO DIEGO (DNI `42179669`) tiene 2 filas con misma cuenta `2752636` pero operaciones distintas (`16043878` y `13561426`). La clave es el par `(cuenta, operacion)`.

### NRO no es secuencial ni confiable
- NRO salta (1, 2, 3... 25, 26, 27... 111, 112...). No usar NRO como identificador.

---

## Pregunta 4 — Identificador de `operacion`: ¿columna C (C&O) o D+E separados? ✓ RESUELTA

**Respuesta:** Se almacenan D y E por separado. La columna C (C&O concatenada) es solo una ayuda visual del Excel; el modelo usa `cuenta = D` y `operacion = E`, ambos como texto.

**Regla del parser:** La columna C se ignora completamente. El parser lee D y E directamente. C es puramente visual, no tiene peso en la lógica de negocio.

**Impacto:**
- `operacion.cuenta` = valor de columna D (ej. `3565294`)
- `operacion.numero_operacion` = valor de columna E (ej. `17485025`)
- `UNIQUE INDEX` en `operacion(cuenta, numero_operacion)` — el par único
- El Excel parser debe leer D y E directamente, no la columna C

---

## Pregunta 5 — Identificación de cliente por DNI ✓ RESUELTA

**Respuesta: Escenario A — Un cliente se identifica por su DNI.**

- `cliente.dni` es la clave primaria natural de un cliente
- Si el mismo DNI aparece en la cartera de hoy Y en una cartera futura de otro banco, es el mismo cliente
- Los datos de contacto (teléfono, dirección) se actualizan globalmente en el mismo registro `cliente`
- Una persona = un cliente = un DNI
- Las operaciones se relacionan con el cliente vía `cliente_id`

**Impacto en modelo:**
- `cliente` se identifica por `dni` (único)
- `operacion` tiene `cliente_id` (FK a `cliente`)
- Múltiples operaciones pueden apuntar al mismo cliente (mismo DNI, distintas cuentas u operaciones)
- Al importar, se busca cliente por DNI: si existe se reutiliza, si no se crea

---

## Pregunta 6 — ¿Una operación puede existir en múltiples carteras? ✓ RESUELTA

**Respuesta: Sí — Opción B.**

La cartera es solo un container de importación, sin peso en la lógica de negocio. Una operación existe independientemente de la cartera. Si la misma (cuenta, operacion) aparece en dos importaciones distintas, es la misma operación.

**Impacto:**
- La tabla `operacion` NO tiene FK obligatoria a `cartera`
- `cartera` es un registro histórico de la importación, no define pertenencia
- Una operación puede asociarse a múltiples carteras si aparece en varias importaciones (relación muchos-a-muchos vía `operacion_cartera`)
- Opcionalmente: cada vez que una operación aparece en una importación, se registra en `importacion_detalle` sin duplicar la operación

---

## Pregunta 7 — Clasificación de columnas A-G del Excel ✓ RESUELTA

**Clasificación acordada:**

| Columna | Header | Destino |
|---------|--------|---------|
| A | NRO | IGNORADO — solo numeración visual del Excel |
| B | ABOGADO | `operacion.abogado_nombre` (texto) |
| C | C&O | IGNORADO — columna visual, D y E son la fuente de verdad |
| D | CUENTA | `operacion.cuenta` |
| E | OPERACIÓN | `operacion.numero_operacion` |
| F | NOMBRE DEL CLIENTE | `cliente.nombreCompleto` |
| G | DNI | `cliente.dni` |

**Clave de operación:** `(cuenta, numero_operacion)` — sin código de expediente adicional.
**Clave de cliente:** `cliente.dni` — una persona = un cliente.

---

## Pregunta 8 — Clasificación de columnas H-BA del Excel ✓ RESUELTA

**Clasificación acordada (H → BA):**

| Columna | Header | Destino | Tipo |
|---------|--------|---------|------|
| H | TRANS. | `operacion.transferido` | texto (SI/NO) |
| I | OBSERVACION | `operacion.observaciones` | texto |
| J | SITUACION | `operacion.situacion` | texto |
| K | AGENCIA | `operacion.agencia_id` | FK a `agencia` |
| L | MONEDA | `operacion.moneda` | texto |
| M | BUSQUEDA DE BIENES | `operacion.busqueda_bienes` | texto (POSITIVO/NEGATIVO) |
| N | DEUDA CAP | `operacion.deuda_cap` | DECIMAL |
| O | DEUDA TOTAL | `operacion.deuda_total` | DECIMAL |
| P | TIPO DE PROCESO JUDICIAL | `operacion.tipo_proceso` | texto |
| Q | TIPO DE JUZGADO | `operacion.tipo_juzgado` | texto |
| R | DISTRITO JUDICIAL | `operacion.distrito_judicial` | texto |
| S | Nº JUZGADO | `operacion.numero_juzgado` | texto |
| T | N° EXP. | `operacion.numero_expediente` | texto |
| U | INCIDENTE SI - NO | `operacion.tiene_incidente` | BOOLEANO |
| V | MONTO DDO. | `operacion.monto_demandado` | DECIMAL |
| W | ESP. LEGAL (SECRETARIO) | `operacion.secretario_legal` | texto |
| X | CÓDIGO/EXP. CAUTELAR | `operacion.codigo_expediente_cautelar` | texto |
| Y | DETALLE DE BIEN EMBARGADO | `operacion.detalle_bien_embargado` | texto |
| Z | Nº PARTIDA | `operacion.numero_partida` | texto |
| AA | BIEN EMBARGADO (MUEBLE/INMUEBLE) | `operacion.tipo_bien_embargado` | texto |
| AB | RANGO | `operacion.rango` | texto |
| AC | DETALLE DE ACREEDORES... | `operacion.detalle_acreedores` | texto |
| AD | PREFERENTE... | `operacion.tipo_preferente` | texto |
| AE | MONTO DE LA MC | `operacion.monto_medida_cautelar` | DECIMAL |
| AF | MONEDA | `operacion.moneda_mc` | texto |
| AG | MEDIDA CAUTELAR EJECUTADA... | `operacion.medida_cautelar_ejecutada` | texto (SI/NO/NINGUNO) |
| AH | FECHA DE INSCRIP. DEL EMBARGO | `operacion.fecha_inscripcion_embargo` | DATE |
| AI | FECHA DE PRESENTACIÓN DEL TÍTULO... | `operacion.fecha_presentacion_titulo_rrpp` | DATE |
| AJ | ASIENTO DE INSCRIPCIÓN | `operacion.asiento_inscripcion` | texto |
| AK | FECHA DE PRESENTACION DE LA MC | `operacion.fecha_presentacion_mc` | DATE |
| AL | FECHA DE INADMISIBLE | `operacion.fecha_inadmisible` | DATE |
| AM | FECHA DE ADMISION | `operacion.fecha_admision` | DATE |
| AN | COMENTARIO | `operacion.comentario` | texto |
| AO | FECHA DE PRESENTACION | `operacion.fecha_presentacion` | DATE |
| AP | FECHA DE INADMISIBLE | `operacion.fecha_inadmisible_2` | DATE |
| AQ | FECHA DE ADMISION | `operacion.fecha_admision_2` | DATE |
| AR | AUDIENCIA UNICA/SANEMAMIENTO... | `operacion.audiencia_tipo` | texto |
| AS | FECHA DE AUTO FINAL | `operacion.fecha_auto_final` | DATE |
| AT | FECHA DE CONSENTIMIENTO... | `operacion.fecha_ejecutoriada` | DATE |
| AU | INGRESO DE EJECUCION... | `operacion.fecha_nombramiento_peritos` | DATE |
| AV | TASACION/NOMBRAMIENTO MARTILLERO | `operacion.fecha_nombramiento_martillero` | DATE |
| AW | FECHA DE REMATE 1° | `operacion.fecha_remate_1` | DATE |
| AX | FECHA DE REMATE 2° | `operacion.fecha_remate_2` | DATE |
| AY | FECHA DE REMATE 3° | `operacion.fecha_remate_3` | DATE |
| AZ | OBSERVACION/ACTOS PROCESALES... | `operacion.fecha_proximo_acto_procesal` | DATE |
| BA | COMENTARIO | `operacion.comentario_procesal` | texto |

**Total: 47 columnas mapeadas a la tabla `operacion`.**
**Clave única de operación:** `(cuenta, numero_operacion)`.
