# Plan — Sesión próxima

## 1. Limpieza de restos de "Estado Cartera"

**Contexto:** Se eliminó la sección `/cartera/estado-cartera` completa (controller, template, sidebar).
Quedan restos en el código que referencian `estadoCartera` pero ya no están conectados
al filtro de `/clientes`. Hay que limpiarlos antes de reconstruir el filtro.

### Restos identificados (103 matches de `estadoCartera`)

| Archivo | Qué tiene | Acción |
|---------|-----------|--------|
| `OperacionRepository.java` | Parámetro `estadoCartera` en 3 métodos JPQL (`findClienteIdsConFiltros`, `countClienteIdsConFiltros`, `findOperacionesPorClienteConFiltros`) | ** Limpiar** — el parámetro ya no se usa desde que se sacó del controller |
| `operacion/formulario.html` | Select `estadoCartera` en el formulario de operación | **Conservar** — se usa para editar operaciones |
| `operacion/detalle.html` | Muestra `estadoCartera` de la operación | **Conservar** — muestra el campo |
| `OperacionService.java` | `operacion.setEstadoCartera(...)` en guardar/actualizar | **Conservar** — lógica de persistencia |
| `OperacionMapper.java` | Mapeos `estadoCartera` entity↔dto↔form | **Conservar** — necesarios |
| `Operacion.java` (entity) | Campo `estadoCartera` + columna `estado_cartera` | **Conservar** — columna existe en DB |
| `schema.sql` | Columna `estado_cartera` en tabla `operaciones` | **Conservar** — DB schema |

### Tarea 1.1 — Limpiar `OperacionRepository.java`

Los métodos `findClienteIdsConFiltros`, `countClienteIdsConFiltros`, y
`findOperacionesPorClienteConFiltros` tienen parámetro `@Param("estadoCartera") String estadoCartera`
y la condición `AND (:estadoCartera IS NULL OR o.estadoCartera = :estadoCartera)` en el WHERE.

Estos métodos se usan en `ClienteService.buscarConFiltros()` para contar operaciones
por cliente. El parámetro `estadoCartera` ya no viene del controller (se sacó),
así que actualmente se pasa `null` siempre — la condición es un NOOP.

**Decisión:** Quitar el parámetro `estadoCartera` y la condición del WHERE
de estos 3 métodos en `OperacionRepository`. También quitar `@Param` y tipo correspondiente.

**Archivos a tocar:**
- `OperacionRepository.java` — 3 métodos, quitar parámetro y condición

---

## 2. Entender `estado_cartera` en la BD

### Tarea 2.1 — Inspeccionar la columna

```sql
-- Qué valores existen (y cuántos)
SELECT estado_cartera, COUNT(*) FROM operaciones WHERE activo = true GROUP BY estado_cartera;

-- Qué relación tiene con otras columnas (ej: etapa, situacion, estado)
SELECT estado_cartera, etapa, situacion, estado, COUNT(*) 
FROM operaciones WHERE activo = true 
GROUP BY estado_cartera, etapa, situacion, estado;

-- Relación con mora: hay estado_cartera = ACTIVO con días de mora altos?
SELECT estado_cartera, MIN(dias_mora), MAX(dias_mora), AVG(dias_mora) 
FROM operaciones WHERE activo = true GROUP BY estado_cartera;
```

### Tarea 2.2 — Entender语义

Según lo que resulte de 2.1, entender:
- `ACTIVO` vs `DESASIGNADA`: son estados de la operación dentro de la cartera, no estados procesales
- ¿`estadoCartera` es algo que viene del Excel y se setea en import, o es una clasificación que asigna el estudio?
- ¿Cómo se relaciona con `estado` (VIGENTE/VENCIDA/PAGADA/PRESCRITA) y con `etapa` (JUDICIAL/EXTRAJUDICIAL)?

### Tarea 2.3 — Documentar en glossary

Agregar entrada en `docs/domain/glossary.md`:
- Qué es `estado_cartera`
- Valores posibles
- Quién lo setea (import vs manual)
- Relación con `estado` y `etapa`

---

## 3. Reconstruir filtro `estadoCartera` en `/clientes`

### Tarea 3.1 — Agregar campo a DTOs

**`ClienteBusquedaDTO.java`:**
- Agregar campo `estadoCartera` (String, nullable)
- Agregar getter
- Agregar setter en builder

**`ClienteBandejaDTO.java`:**
- Agregar campo `estadoCartera` (String, nullable)
- Agregar getter
- Agregar al constructor
- Agregar a `equals`/`hashCode`/`toString` si ya los tiene

### Tarea 3.2 — Agregar parámetro al Controller

**`ClienteController.java`** — `GET /clientes`:
- Agregar `@RequestParam(required = false) String estadoCartera`
- Pasarlo al builder de `ClienteBusquedaDTO`

### Tarea 3.3 — Agregar lógica de filtrado en Service

**`ClienteService.buscarConFiltros()`:**
- Tomar `estadoCartera` del DTO de búsqueda
- Ya hay parámetro en `operacionRepository.findClienteIdsConFiltros()` — verificar que se pase
- Si el repository ya filtra por `estadoCartera` (en los métodos que NO limpiamos en 1.1),
  no hay que agregar lógica extra en el service

### Tarea 3.4 — Agregar select en template

**`cliente/lista.html`:**
- Agregar `<select>` con options: `ACTIVO`, `DESASIGNADA` (valores que devuelve la DB)
- Agregar al filter form
- Mantener nombre del campo: `estadoCartera`

### Tarea 3.5 — Rebuild pagination links

- Agregar `estadoCartera=${filtros.estadoCartera}` a todos los links de paginación
- Verificar que el filter form preserve el valor al hacer submit

### Tarea 3.6 — Probar

```bash
# Sin filtro
curl "http://localhost:8080/clientes?size=5"

# Con filtro ACTIVO
curl "http://localhost:8080/clientes?estadoCartera=ACTIVO&size=5"

# Con filtro DESASIGNADA
curl "http://localhost:8080/clientes?estadoCartera=DESASIGNADA&size=5"

# Verificar counts en UI vs SQL directo
SELECT COUNT(DISTINCT cliente_id) FROM operaciones WHERE activo = true AND estado_cartera = 'ACTIVO';
```

---

## Orden de ejecución

```
1.1 → Limpiar OperacionRepository (quitar parámetro muerto)
2.1 → Inspectar columna estado_cartera en DB
2.2 → Entender semantic relationships
2.3 → Documentar en glossary
3.1 → Agregar campo a DTOs
3.2 → Agregar parámetro al Controller
3.3 → Agregar lógica en Service (verificar que repo ya filtra)
3.4 → Agregar select en template
3.5 → Rebuild pagination
3.6 → Probar end-to-end
```

## Criteria de done

- `/clientes?estadoCartera=ACTIVO` devuelve solo operaciones con `estado_cartera = 'ACTIVO'`
- `/clientes?estadoCartera=DESASIGNADA` devuelve solo operaciones con `estado_cartera = 'DESASIGNADA'`
- `/clientes` (sin filtro) devuelve todas las operaciones (comportamiento anterior)
- Filtro se preserva al cambiar de página
- No hay `null pointer` ni errores 500
- Compila y arranca sin warnings
