# Plan de implementación v1 — Tickets

> Generado a partir de `SPEC.md` y la inspección del código actual.
> Proposal — necesita aprobación antes de publicar.

---

## Tickets propuestos

### 01 — Fix de compilación
**Qué entrega:** El proyecto compila sin errores (`mvn compile` pasa).

**Blocked by:** Ninguno — puede empezar inmediatamente.

**Estado actual:** El proyecto no compila por errores de Lombok (annotation processor no genera los métodos). El ticket `003-compilacion-lombok.md` ya existe con el diagnóstico.

---

### 02 — Nuevas entidades del modelo
**Qué entrega:** Nuevas entidades JPA para Cliente, Operacion, BienEmbargado, Agencia, Empresa, AuditoriaEvento — compilando y con repositorios básicos.

**Blocked by:** 01 (compilación necesaria para verificar)

**Qué incluye:**
- `Cliente`: id, dni (único), nombre_completo, telefono, telefono2, telefono3, direccion, email, activo, created_at, updated_at, deleted_at
- `Operacion`: id, cliente_id, empresa_id, agencia_id, cuenta, numero_operacion (unique index juntos), monto_capital, monto_total, dias_mora, moneda, tipo_credito, situacion, estado, etapa, observacion, rango, analista, analista_senior, numero_expediente, tipo_proceso, tipo_juzgado, distrito_judicial, numero_juzgado, activo, created_at, updated_at
- `BienEmbargado`: id, operacion_id, tipo_mc, fecha_inscripcion_rrpp, numero_ficha_registral, direccion_inmueble, distrito, provincia, departamento, valor_tasacion, titular_predio
- `Agencia`: id, empresa_id, nombre, region, created_at, updated_at
- `Empresa`: id, nombre, tipo, ruc, created_at, updated_at
- `AuditoriaEvento`: id, usuario, tipo, objeto_tipo, objeto_id, payload (JSON), created_at
- Repositorios con las queries que van a necesitarse (buscar por DNI, upsert por cuenta+operacion, etc.)

**Notas:**
- Las entidades nuevas viven en nuevos paquetes bajo `model/` o `dominio/` para separarlas del código viejo.
- El código viejo (Cliente, Expediente, etc.) sigue existiendo — esto es expand, no replace.

---

### 03 — Perfil de import Caja Arequipa
**Qué entrega:** Archivo `resources/perfiles-import/caja-arequipa-cartera.json` con el mapeo completo de las 26 columnas del Excel real.

**Blocked by:** 02 (necesita las entidades para saber los nombres de campo)

---

### 04 — Importación cartera simple (nuevo modelo)
**Qué entrega:** El usuario puede importar un Excel de cartera simple y las operaciones aparecen en la BD asociadas al Cliente correcto.

**Blocked by:** 02 + 03

**Qué incluye:**
- Servicio que lee el perfil JSON y parsea el Excel con POI
- Upsert por (cuenta, numero_operacion) — si existe actualiza, si no crea
- Creación de Cliente si el DNI no existe
- Creación de Agencia si no existe (buscar por nombre)
- Creación de BienEmbargado si hay partida registral (columna O)
- Validación de campos obligatorios
- Reporte de errores (X ok, Y actualizados, Z errores)
- AuditoriaEvento para IMPORT_OK / IMPORT_ERROR

**Verificable:** Importar `05 - MAYO MC.xlsx` end-to-end sin errores.

---

### 05 — Bandeja de clientes (nuevo modelo)
**Qué entrega:** Página web con la lista de clientes del nuevo modelo, búsqueda y filtros.

**Blocked by:** 04 (primero el import debe funcionar para tener datos)

**Qué incluye:**
- Endpoint `/clientes` con lista paginada
- Búsqueda por DNI (exacto) y nombre (parcial)
- Filtros: empresa, agencia, estado, etapa, rango de mora, rango de monto
- Vinculación a la vista de detalle del cliente

---

### 06 — Ver / editar cliente y operación
**Qué entrega:** Vista de detalle de un cliente con sus operaciones y bienes embargados. Edición funcional.

**Blocked by:** 05

**Qué incluye:**
- Página `/clientes/{id}` con datos del cliente + operaciones + bienes embargados
- Edición de contacto (teléfono, email, dirección), estado, etapa, notas
- Cada edición genera AuditoriaEvento
- Link a la operación individual

---

### 07 — Ver operación
**Qué entrega:** Vista独立性 de una operación con todos sus datos y bien embargado.

**Blocked by:** 06

**Qué incluye:**
- Página `/operaciones/{id}` con todos los campos de la operación
- Ver bien embargado si existe
- Edición de estado, etapa, analista, abogado, notas

---

### 08 — Exportación a Excel
**Qué entrega:** El usuario puede exportar un Excel filtrado por empresa + agencia.

**Blocked by:** 07

**Qué incluye:**
- Filtro empresa + agencia → genera Excel
- Perfil de export `resources/perfiles-export/caja-arequipa.json`
- Generación del archivo Excel con POI
- AuditoriaEvento para EXPORT_OK

---

### 09 — Dashboard con métricas
**Qué entrega:** Pantalla inicial con totales de cartera, operaciones por estado/etapa, alertas.

**Blocked by:** 04 (necesita datos para las métricas)

**Qué incluye:**
- Totales: clientes únicos, operaciones totales, monto total
- Desglose por estado (VIGENTE/VENCIDA) y etapa (EXTRAJUDICIAL/JUDICIAL)
- Alertas: operaciones sin bien embargado, operaciones sin expediente
- Accesos directos a importar / bandeja / exportar

---

### 10 — Auditoría (historial)
**Qué entrega:** Vista de historial de auditoría con filtros y detalle de cada evento.

**Blocked by:** 04

**Qué incluye:**
- Página `/auditoria` con tabla paginada
- Filtros: tipo, fecha, usuario
- Detalle del payload JSON

---

### 11 — Limpieza del código viejo
**Qué entrega:** Código viejo (Cliente con campos mezclados, Expediente como entidad separada) removido o marcado como deprecated.

**Blocked by:** 05 + 06 + 07 + 08 + 09 + 10 (todo el código nuevo debe estar funcionando antes de borrar el viejo)

**Qué incluye:**
- Evaluar si las tablas/entities viejas se pueden eliminar o necesitan migración de datos
- Eliminar código Thymeleaf viejo que referencia las entities viejas
- Eliminar endpoints REST/Controller viejos que ya fueron reemplazados
- Este ticket es el contract final del expand-contract

---

## Estructura de dependencias

```
01 (fix compilación)
  └─ 02 (nuevas entidades)
        ├─ 03 (perfil JSON)
        │     └─ 04 (import)
        │           ├─ 05 (bandeja) → 06 (ver/editar) → 07 (ver operación)
        │           │                                     └─ 08 (export)
        │           ├─ 09 (dashboard)
        │           └─ 10 (auditoría)
        │
        └─ 11 (limpieza código viejo — depende de 05+06+07+08+09+10)
```

## Preguntas para vos

1. ¿La granularidad está bien? ¿Algún ticket es muy grande o muy pequeño?
2. ¿Los blockers están correctos?
3. ¿Algún ticket debería partirse o合并erse?
