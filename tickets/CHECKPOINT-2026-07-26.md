# CHECKPOINT 2026-07-26 — SistemaIntegralCobranza

## Proyecto
- Java 21 + Spring Boot 3 + Thymeleaf + PostgreSQL
- Ubicación: `/mnt/d/dev/gato/SistemaIntegralCobranza/`
- Skill local: `.agents/skills/cobranza-continue`
- Workspace dev: `/mnt/c/Users/Giordan23`

---

## Última actividad (2026-07-26 noite)
Bienes Embargados editables en formulario de operación + quitado filtro/columna Agencia de cartera.

---

## Mapa de columnas del Excel
**Sheet:** "CARTERA SELVA CENTRAL", header en fila 2

Cols Operacion: 1-24 (A-X)
Cols BienEmbargado: 25-53 (Y-BA)
Cols Expediente: no tiene en este Excel

| # | Letra | Header Excel | Mapeo BD |
|---|-------|-------------|----------|
| 7 | G | DNI | cliente.dni |
| 8 | H | TRANS | operacion.trans (bool) |
| 13| M | BUSQUEDA DE BIENES | operacion.busquedaBienes (bool) |
| 20| T | N° EXP. | operacion.numeroExpediente |
| 21| U | INCIDENTE SI - NO | operacion.incidente (bool) |
| 22| V | MONTO DDO. | operacion.montoDemandado |
| 23| W | ESP. LEGAL (SECRETARIO) | operacion.escribanoLegal |
| 24| X | CÓDIGO/EXP. CAUTELAR | operacion.codigoExpCautelar |
| 25| Y | DETALLE DE BIEN EMBARGADO Y/O GARANTIA | bien.detalleGarantia |
| 26| Z | Nº PARTIDA | bien.partidaRegistral |
| 27| AA | BIEN EMBARGADO (MUEBLE/INMUEBLE) | bien.tipoBien |
| 28| AB | RANGO | bien.rango |
| 29| AC | DETALLE DE ACRREEDORES... | bien.detalleAcreedores |
| 30| AD | PREFERENTE | bien.tipoPreferencia |
| 31| AE | MONTO DE LA MC | bien.montoMc |
| 32| AF | MONEDA | bien.monedaMc |
| 33| AG | MEDIDA CAUTELAR... (INSCRITA SI/NO) | bien.garantiaInscrita |
| 34| AH | FECHA DE INSCRIP. DEL EMBARGO | bien.fechaInscripcion |
| 35| AI | FECHA DE PRESENTACIÓN DEL TITULO EN RRPP | bien.fechaPresentacionRrpp |
| 36| AJ | ASIENTO DE INSCRIPCION | bien.asientoInscripcion |
| 37| AK | FECHA DE PRESENTACION DE LA MC | bien.fechaPresentacionMc |
| 38| AL | FECHA DE INADMISIBLE | bien.fechaInadmisible |
| 39| AM | FECHA DE ADMISION | bien.fechaAdmision |
| 40| AN | COMENTARIO | bien.comentarioMc |
| 46| AT | FECHA DE CONSENTIMIENTO.../EJECUTORIADA | operacion.fechaConsentimiento |
| 52| AZ | OBSERVACION/ACTOS PROCESALES IMPORTANTES | operacion.observacionActos |
| 53| BA | COMENTARIO | operacion.comentario |

---

## Cambios realizados (sesión noite 2026-07-26)

### 1. Bienes Embargados editables en formulario.html

**OperacionFormDTO.java**
- Campo `List<BienEmbargadoDTO> bienesEmbargados`
- Getter/setter
- Builder: field + method

**OperacionMapper.java**
- `toFormDTO`: mapea `entity.bienesEmbargados` → `form.bienesEmbargados`
  - Usa `bienEmbargadoMapper.toDto()` por cada bien

**OperacionService.java**
- Inyectado `BienEmbargadoRepository`
- `actualizar()`: syncea bienes con `existing.getBienesEmbargados().clear()` + `add()` (orphanRemoval en cascada borra los eliminados)

**formulario.html (operacion)**
- Nueva sección "Bienes Embargados" después del form principal (solo visible en edición)
- Tabla editable con 7 columnas: Detalle Bien/Garantía, N° Partida, Tipo Bien, Dirección, Distrito, Garantía Inscrita, Monto MC
- Fila `<tr>` con `th:each` indexado: `bienesEmbargados[__${stat.index}__]`
- `<template id="bienRowTemplate">` para JS dinámico
- JS: `agregarBien()` / `eliminarBien()` con template HTML5

### 2. Quitado filtro/columna Agencia — cartera/registros.html
- Filtro `<select id="filtroAgencia">` removido del form de búsqueda
- Columna "Agencia" removida de la tabla (colspan 20→19)
- Links de paginación: removido `agenciaId=` de todos los `@{/cartera/registros(...)}`
- JS `fetch('/api/agencias?...')` removido
- Etiqueta `<select>` empresa: ahora cierra en col-md-2 sin Select Agencies hijo

### 3. Fix columnas Excel (sesiones anteriores)
- `observacionActos`: col 52 (AZ), NO col 46 (AT)
  - AT era "FECHA DE CONSENTIMIENTO DE LA RESOLUCIÓN"
  - AZ era "OBSERVACION/ACTOS PROCESALES IMPORTANTES"
- Perfil JSON `excel-avance-procesal-arequipa.json`: `observacionActos: 52`

---

## Estado de archivos clave

### Modificados esta sesión
- `src/main/java/com/startup/cobranza/operacion/dto/OperacionFormDTO.java`
- `src/main/java/com/startup/cobranza/operacion/mapper/OperacionMapper.java`
- `src/main/java/com/startup/cobranza/operacion/service/OperacionService.java`
- `src/main/resources/templates/operacion/formulario.html`
- `src/main/resources/templates/cartera/registros.html`

### Pendiente de probar
- _(vacío — todo probado y funcionando)_

---

## Errores фиjados sesiones anteriores
1. OperacionRepository typo maxMora→maxMonto (count query)
2. OperacionController: falta endpoint /editar/{id} + /guardar
3. OperacionService: falta obtenerEntityPorId
4. OperacionMapper: falta toFormDTO
5. OperacionFormDTO: falta observacionActos + comentario
6. operacion/formulario.html: template no existía
7. cliente/formulario.html: campos mezclados con operacion
8. OperacionFormDTO.Builder: faltaba `private String comentario` (error compilación)
9. Columnas Excel incorrectas: observacionActos era 46 → corregido a 52

## Logs
- `logs/run-20260726-163614.log` — compilación fallida (Builder error, fixeado)
- `logs/run-20260726-163906.log` — startup OK post-fix
