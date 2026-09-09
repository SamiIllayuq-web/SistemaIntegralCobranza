---
id: 4
title: "Reestructurar frontend operaciones + cartera para reflejar Excel AvanceProcesalFinal"
status: in_progress
type: afk
priority: high
created: 2026-07-27
---

# Reestructurar frontend para reflejar Excel AvanceProcesalFinal

## Contexto

El frontend actual (`operacion/formulario.html`, `cartera/registros.html`, `cartera/expedientes.html`, `operacion/detalle.html`) no refleja los campos reales del Excel `AvanceProcesalFinal.xlsx`. Muchas columnas existen en la entidad `Operacion` y `BienEmbargado` pero no se muestran en la UI. Las secciones están mal organizadas.

## Columnas del Excel (fila 2, datos fila 3+)

| Cols | Sección Excel | Campos |
|------|---------------|--------|
| A-P | DATOS DE LA OPERACIÓN | NRO, ABOGADO, C&O, CUENTA, OPERACIÓN, NOMBRE, CO TITULAR/AVAL, DNI, TRANS, OBSERVACION, SITUACION, AGENCIA, MONEDA, BUSQUEDA DE BIENES, DEUDA CAP, DEUDA TOTAL |
| Q-X | DATOS DEL CUADERNO PRINCIPAL | TIPO PROCESO, TIPO JUZGADO, DISTRITO JUDICIAL, Nº JUZGADO, N° EXP., INCIDENTE SI/NO, MONTO DDO., ESP. LEGAL |
| Y-AP | DATOS DEL EXP. CAUTELAR / HIPOTECA | EXPEDIENTE CAUTELAR, CODIGO CAUTELAR, DETALLE BIEN EMBARGADO, Nº PARTIDA, BIEN EMBARGADO, RANGO, DETALLE ACREEDORES, PREFERENTE, MONTO MC, MONEDA, MEDIDA CAUTELAR EJECUTADA, FECHA INSCRIPCION, FECHA PRESENT. RRPP, ASIENTO INSCRIPCION, FECHA PRESENT. MC, FECHA INADMISIBLE, FECHA ADMISION, COMENTARIO |
| AQ-BC | DATOS DEL EXPEDIENTE PRINCIPAL | FECHA PRESENT., FECHA INADMISIBLE, FECHA ADMISION, AUDIENCIA UNICA, FECHA AUTO FINAL, FECHA CONSENTIMIENTO/EJECUTORIADA, INGRESO EJECUCION, TASACION/NOMBRAMIENTO MARTILLERO, FECHA REMATE 1°, 2°, 3°, OBSERVACIONES, COMENTARIO |

## Problemas detectados

### 1. `operacion/formulario.html` — secciones desalineadas
- "Datos de la Operación" tiene campos que no corresponden a cols A-P
- "Datos del Expediente Judicial" mezcla campos de Q-X con Y-AP
- Fechas procesales están en sección separada, pero en Excel están en AQ-BC
- Faltan campos del Excel que SÍ existen en la entidad: `trans`, `busquedaBienes`, `escribanoLegal`, `codigoExpCautelar`, `incidente`, `coTitularAval`, `numeroPartida`
- `montoCapital` (col O) y `montoTotal` (col P) se muestran como campos genéricos sin label correcto
- `situacion` (col K) se muestra como texto libre

### 2. `BienEmbargado` tiene campos que no se muestran en ningún template
- `tipoBien` (col AC) — en tabla de bienes pero label es "Tipo Bien"
- `numeroPartida` (col AB) — es PARTIDA REGISTRAL, no el bien mismo
- `montoMc` (col AG) — no se muestra
- `monedaMc` (col AH) — no se muestra
- `garantiaInscrita` (col AI) — no se muestra
- `fechaInscripcion` (col AJ) — no se muestra
- `fechaPresentacionRrpp` (col AK) — no se muestra
- `asientoInscripcion` (col AL) — no se muestra
- `fechaPresentacionMc` (col AM) — no se muestra
- `fechaInadmisible` (col AN) — no se muestra
- `fechaAdmision` (col AO) — no se muestra
- `comentarioMc` (col AP) — no se muestra
- `detalleAcreedores` (col AE) — no se muestra
- `tipoPreferencia` (col AF) — no se muestra
- `titularPredio` — no existe en Excel (descartar)
- `fechaGeneracionMc` — no existe en Excel (descartar)

### 3. Campos en Operacion que no están en formulario
- `trans` (col I) — Boolean
- `busquedaBienes` (col N) — Boolean
- `escribanoLegal` (col X) — EPS. LEGAL (SECRETARIO)
- `codigoExpCautelar` (col Z) — CODIGO CAUTELAR (CASILLA)
- `incidente` (col V) — INCIDENTE SI-NO
- `coTitularAval` (col G) — CO TITULAR / AVAL
- `numeroPartida` (col AB) — mapeado en Operacion pero debería estar en BienEmbargado
- `fechaIngresoEjecucion` (col AW) — INGRESO DE EJECUCION
- `fechaNombramientoMartillero` (col AX) — parte de TASACION/NOMBRAMIENTO MARTILLERO
- `fechaTasacion` (col AX) — misma columna que martillero

### 4. Campos obsoletos que no vienen del Excel (permanecen si tienen sentido)
- `estadoCartera` — no viene del Excel pero podría ser útil
- `analista`, `analistaSenior` — no vienen del Excel, mantener si no molestan
- `etapa` — no viene del Excel
- `fechaDesembolso`, `importeDesembolso` — no vienen del Excel
- `montoAprobado`, `fechaAceptacionDemanda`, `fechaEnvioJudicial`, `fechaAsignacionAbogado`, `fechaCastigo` — no vienen del Excel
- `tipoFondo`, `estado`, `zona`, `departamento`, `provincia`, `distrito`, `direccion`, `referencia`, `telefono` — no vienen del Excel

### 5. Campos del Excel sin lugar en la entidad
- `NRO` (col A) — solo número de fila, no guardar
- `ABOGADO` (col B) — buscar en la BD si existe como Usuario
- `C&O` (col C) — sin campo equivalente, guardar en `observacion` o nuevo campo
- `OBSERVACION` (col J) — mapear a `observacion` de Operacion
- `BUSQUEDA DE BIENES` (col N) — Boolean `busquedaBienes`
- `TIPO CREDITO` — no hay equivalente directo, podría ir en `tipoCredito` o `observacion`

## Plan de trabajo

### Fase 1: DTO + Mapper + Entidad (backend)

1. **Agregar campos faltantes a `OperacionFormDTO`**:
   - `trans` (Boolean) — cols I
   - `busquedaBienes` (Boolean) — col N
   - `escribanoLegal` (String) — col X
   - `incidente` (Boolean) — col V
   - `comentarioMc` (String) — col AP (bien embargado)
   - `detalleAcreedores` (String) — col AE (bien embargado)
   - `tipoPreferencia` (String) — col AF (bien embargado)

2. **Verificar `OperacionMapper.toFormDTO()`** — debe mapear todos los campos del DTO

3. **Verificar `OperacionMapper.toEntity()`** — debe mapear de Form → Operacion

4. **Verificar CarteraService** — el import de Excel ya llena todos estos campos?

### Fase 2: Migración schema.sql (si hay nuevas columnas)

Ejecutar ALTER TABLE para agregar columnas faltantes en `operaciones` y `bienes_embargados`.

### Fase 3: HTML formulario — `operacion/formulario.html`

Reorganizar las secciones:

```
SECCIÓN 1: DATOS DE LA OPERACIÓN (cols A-P)
  - Agencia (select) [L]
  - Cuenta [D], Número de Operación [E]
  - Nombre Cliente (solo lectura? viene del Cliente)
  - Co Titular / Aval [G]
  - DNI [H]
  - Transacción [I] — Boolean
  - Observación [J]
  - Situación [K]
  - Moneda [M]
  - Búsqueda de Bienes [N] — Boolean
  - Deuda Capital [O] → montoCapital
  - Deuda Total [P] → montoTotal

SECCIÓN 2: DATOS DEL CUADERNO PRINCIPAL (cols Q-X)
  - Tipo de Proceso Judicial [Q]
  - Tipo de Juzgado [R]
  - Distrito Judicial [S]
  - Nº Juzgado [T]
  - Nº Expediente [U]
  - Incidente [V] — Boolean
  - Monto Demandado [W]
  - Esp. Legal (Secretario) [X]

SECCIÓN 3: DATOS DEL EXPEDIENTE CAUTELAR / HIPOTECA (cols Y-AP)
  - Expediente Cautelar [Y]
  - Código Cautelar (Casilla) [Z]
  — Sub-sección BIEN EMBARGADO (cols AA-AP):
    * Detalle de Bien Embargado / Garantía [AA]
    * Nº Partida [AB]
    * Bien Embargado (Mueble/Inmueble) [AC]
    * Rango [AD]
    * Detalle de Acreedores [AE]
    * Preferente [AF]
    * Monto MC [AG]
    * Moneda [AH]
    * Medida Cautelar Ejecutada / Garantía Inscrita [AI]
    * Fecha de Inscripción del Embargo [AJ]
    * Fecha de Presentación del Título en RRPP [AK]
    * Asiento de Inscripción [AL]
    * Fecha de Presentación de la MC [AM]
    * Fecha de Inadmisible [AN]
    * Fecha de Admisión [AO]
    * Comentario [AP]

SECCIÓN 4: DATOS DEL EXPEDIENTE PRINCIPAL (cols AQ-BC)
  - Fecha de Presentación [AQ]
  - Fecha de Inadmisible [AR]
  - Fecha de Admisión [AS]
  - Audiencia Única [AT]
  - Fecha Auto Final [AU]
  - Fecha Consentimiento / Ejecutoriada [AV]
  - Ingreso de Ejecución y Nombramiento de Peritos [AW]
  - Tasación / Nombramiento de Martillero [AX]
  - Fecha Remate 1° [AY]
  - Fecha Remate 2° [AZ]
  - Fecha Remate 3° [A[]]
  - Observaciones / Actos Procesales Importantes [A\]
  - Comentario [A]]
```

### Fase 4: `operacion/detalle.html`

Actualizar para mostrar las 4 secciones con los campos correctos.

### Fase 5: `cartera/registros.html` y `cartera/expedientes.html`

Actualizar columnas visibles para reflejar los campos relevantes del Excel.

## Dependencias
Ninguna — se trabaja directamente sobre el código existente.

## Verificación
1. `mvn compile` pasa
2. Al abrir una operación importada del Excel, los campos se muestran correctamente
3. Los valores en la UI coinciden con los del Excel original
