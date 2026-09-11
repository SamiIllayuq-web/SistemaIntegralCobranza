# Plan — Sesión próxima

## 1. Revisar filtros de Expedientes y Agencias ✅

**Expedientes** (`/cartera/expedientes`):
- Revisar qué filtros existen actualmente ✅
- Verificar que correspondan a columnas reales del Excel (`AvanceProcesalFinal.xlsx`) ✅
- Eliminar filtros para campos que no existen en los datos ✅
- Asegurar que los filtros activos/devueltos tengan sentido con los 90 registros importados ✅

**Resultado:** Filtro `agenciaId` estaba desconectado (bug). Se agregó al repository query y se propagó al controller y template. Los filtros `situacion` y `busqueda` ya funcionaban correctamente.

**Pendiente para Agencias (`/agencias`):**
- Revisar cómo funciona el accordion agrupado por agencia
- Verificar que los datos agrupados sean correctos
- Verificar que filtros/búsqueda (si existen) usen campos reales del Excel

## 2. Lógica de importación: update vs insert por DNI ✅

**Situación anterior:** Import crea operaciones nuevas sin verificar si el cliente ya existe.

**Resultado:** La lógica find-or-update YA EXISTÍA en CarteraService (líneas 304-319, 336-470). Solo faltaba persistir el desglose:
- `Importacion` entity: campos `registrosCreados` + `registrosActualizados` ✅
- `ImportacionDTO`: mismo ✅
- `schema-supabase.sql`: 2 columnas nuevas ✅
- `registrarImportacion()`: persiste ambos valores separados ✅
- Template historial: columna "Exitosos" → "Creados" + "Actualizados" ✅

**Pendiente:**
- Ejecutar en Supabase: `ALTER TABLE importaciones ADD COLUMN IF NOT EXISTS registros_creados integer; ALTER TABLE importaciones ADD COLUMN IF NOT EXISTS registros_actualizados integer;`
