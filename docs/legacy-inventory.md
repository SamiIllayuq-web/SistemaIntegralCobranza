# Legacy Inventory — SistemaIntegralCobranza

Fecha: 2026-07-28
Objetivo: Mapear código muerto para limpiar sin romper nada.

---

## 1. Entities — Estado

| Entity | Tabla BD | ¿Se usa? | Notes |
|--------|----------|----------|-------|
| `Cliente` | `clientes` | ✅ ACTIVO | Cliente principal, 1:1 con operaciones |
| `Operacion` | `operaciones` | ✅ ACTIVO | Core del sistema |
| `Agencia` | `agencias` | ✅ ACTIVO | Agrupación operativa |
| `Gestion` | `gestiones` | ✅ ACTIVO | Historial por cliente (REST llamado desde detalle.html) |
| `Importacion` | `importaciones` | ✅ ACTIVO | Tracking de importaciones |
| `AuditoriaEvento` | `auditoria_eventos` | ✅ ACTIVO | Auditoria |
| `Usuario` | `usuarios` | ✅ ACTIVO | Auth |
| `BienEmbargado` | `bienes_embargados` | ✅ ACTIVO | Editable en formulario.html |

**No se encontraron entities legacy** (Expediente, GestionProcesal, Cliente old) — ya fueron removidos del código.

---

## 2. Controllers — ¿Están todos vivos?

### ✅ ACTIVO (necesarios)

| Controller | Tipo | Ruta | ¿Template usa? |
|-----------|------|------|---------------|
| `ClienteController` | @Controller | `/clientes` | ✅ todos los cliente/*.html |
| `OperacionController` | @Controller | `/operaciones` | ✅ operaciones/*.html |
| `CarteraController` | @Controller | `/cartera` | ✅ cartera/*.html |
| `AgenciaController` | @Controller | `/agencias` | ✅ agencia/*.html |
| `UsuarioController` | @Controller | `/usuarios` | ✅ usuario/*.html |
| `DashboardController` | @Controller | `/dashboard` | ✅ dashboard/index.html |
| `LoginController` | @Controller | `/login` | ✅ login/index.html |
| `GlobalControllerAdvice` | @ControllerAdvice | — | ✅ global |
| `GestionController` | @RestController | `/clientes/{id}/gestiones` | ✅ llamado con fetch() desde cliente/detalle.html |
| `AdminController` | @RestController | `/api/admin` | ✅ llamado con fetch() desde cartera/importar.html |

### ⚠️ DUPLICADO / SIN USO DETECTADO

| Controller | Ruta | Problema |
|-----------|------|----------|
| `AgenciaRestController` | `/api/agencias` | DUPLICADO de `AgenciaApiController`. Mismo endpoint, mismo método `activas()`. Ningún template usa `fetch('/api/agencias')`. |
| `AgenciaApiController` | `/api/agencias/activas` | Igual que arriba — mismo contenido. Ningún template lo llama. |
| `PlantillaExcelController` | `/cartera/descargar-plantilla` | Genera plantilla con 9 columnas que no coincide con la importación real (56 columnas). No encontrado en ningún template. |

---

## 3. Templates — ¿Todos vivos?

### ✅ EN USO (vía `layout :: sidebar`)

```
agencia/formulario.html    agencia/lista.html
cartera/estado-cartera.html  cartera/expedientes.html  cartera/historial.html  cartera/importar.html
cliente/detalle.html       cliente/formulario.html    cliente/lista.html
dashboard/index.html
login/index.html
operacion/detalle.html     operacion/formulario.html
usuario/formulario.html    usuario/lista.html
layout.html
```

### ❌ SIN USO

| Template | Problema |
|----------|----------|
| `_sidebar.html` | Fragmento `sidebar` definido pero **no se usa**. Todas las páginas usan `layout :: sidebar` (fragment en layout.html). `_sidebar.html` es un archivo separado y nunca es referenciado. |

---

## 4. Campos legacy en entities activas

| Campo | Entity | Tabla | ¿Se usa en código? | Recomendación |
|-------|--------|-------|-------------------|---------------|
| `etapaProcesalTexto` | `Operacion` | `operaciones.etapa_procesal_texto` | NO — solo existe como columna/field, no se lee ni escribe | BORRAR (verificar BD primero) |

---

## 5. BD — Tablas existentes (detectadas por entidades)

```
agencias              — ✅ activa
auditoria_eventos     — ✅ activa
bienes_embargados     — ✅ activa
clientes              — ✅ activa
gestiones             — ✅ activa
importaciones         — ✅ activa (2 columnas nuevas registries_creados/actualizados)
operaciones           — ✅ activa (etapa_procesal nueva columna)
usuarios              — ✅ activa
```

**No hay tablas `expedientes` ni `expediente_clientes`** — ya fueron removidas.

---

## 6. SQL pendiente de ejecutar

```sql
-- Solo si etapa_procesal_texto está vacío en todos los registros:
ALTER TABLE operaciones DROP COLUMN etapa_procesal_texto;
```

**Verificar antes:**
```sql
SELECT COUNT(*) FROM operaciones WHERE etapa_procesal_texto IS NOT NULL;
-- Si > 0: no borrar, dejar como está
-- Si = 0: seguro borrar
```

---

## 7. Resumen de acciones de limpieza

### Limpieza segura (sin riesgo, bajo esfuerzo)

| # | Acción | Riesgo |
|---|--------|--------|
| L1 | Borrar `_sidebar.html` (nunca se usa) | Muy bajo — archivo standalone, ningún código lo referencia |
| L2 | Borrar `AgenciaRestController.java` (duplicado de AgenciaApiController) | Muy bajo — `/api/agencias` nunca se llama desde ningún lado |
| L3 | Borrar `PlantillaExcelController.java` (plantilla no coincide con import real) | Muy bajo — endpoint `/cartera/descargar-plantilla` no existe en ningún template |
| L4 | Verificar `etapa_procesal_texto` en BD → borrar columna si está vacía | Bajo — verificar primero con COUNT |

### Limpieza con riesgo medio

| # | Acción | Riesgo |
|---|--------|--------|
| L5 | Si `AgenciaApiController` tampoco se usa, borrar ambos y dejar solo `AgenciaController` | Medio — hay que verificar que no haya otro consumer REST de `/api/agencias` |

### No tocar

| Item | Razón |
|------|-------|
| `GestionController` | Usado activamente desde `cliente/detalle.html` con fetch() |
| `AdminController` | Usado desde `cartera/importar.html` con fetch() |
| Todas las entities | Todas están activas |

---

## 8. Pasos recomendados

1. **L1–L4** → ejecutar ahora, son seguros
2. **L5** → verificar con Giordan si `/api/agencias` se usa desde algún cliente externo (Postman, mobile app, etc.)
3. **L4** → ejecutar el COUNT primero, si hay datos en `etapa_procesal_texto` → decisión de Giordan si migra o conserva

---

## 9. Archivo this doc

`docs/legacy-inventory.md` — mantener actualizado si se agrega código nuevo que después queda sin usar.
