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

## Próxima pregunta a responder

**Cuando se responda P4:** definir si `operacion.cuenta` almacena el valor de la columna C (string largo concatenado) o si se guarda D y E por separado. Impacta:
1. El `UNIQUE INDEX` de `operacion`
2. El parsing del Excel (cómo se extrae el valor de la columna C si es fórmula)
