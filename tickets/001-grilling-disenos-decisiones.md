---
id: 1
title: "[GRILLING] Sesión de preguntas sobre el diseño — decisiones validadas"
status: done
type: afk
priority: high
dependencies: []
created: 2026-07-24
resolved: 2026-07-24
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

## Pregunta 4 — Identificador de `operacion`: ¿columna C (C&O) o D+E separados? ✓ RESUELTA

**Respuesta:** Columnas D y E por separado. La clave única es el par `(cuenta, numero_operacion)`.

**Impacto:**
1. `operacion` tiene dos campos: `cuenta` (String) y `numeroOperacion` (String).
2. `UNIQUE INDEX` en `(cuenta, numero_operacion)` — no en la columna C.
3. En el parsing del Excel se leen las columnas D y E directamente.
4. La columna C (C&O) se ignora para el matching; puede usarse como campo informativo si se requiere.
5. Las fórmulas en columna C (como `=D23&E23`) no afectan el parsing porque no usamos esa columna.

---

## Pregunta 5 — ¿Dos flujos de import o uno solo? ✓ RESUELTA

**Respuesta:** **UNIFICADO** — un solo flujo de importación.

- Por ahora solo Cartera Simple (avance procesal queda para después).
- El usuario elige la empresa/entidad del import (dropdown).
- El sistema usa el perfil de import de esa empresa para parsear las columnas.
- Un solo menú/módulo de importación en la UI.

---

## Pregunta 6 — Pantalla principal (dashboard) ✓ RESUELTA

**Respuesta:** Dashboard con métricas.

- Al entrar muestra totales de cartera, operaciones por estado, alertas, etc.
- Accesos directos a importar / bandeja de clientes / exportar.

---

## Pregunta 7 — estadoGestion: ¿lista cerrada o texto libre? ✓ RESUELTA

**Respuesta:** **Lista sugerida + texto libre** (el usuario puede escribir otro valor).

- Lista cerrada en código (enum o constante) como opciones sugeridas.
- Campo `estadoGestion` en BD sigue siendo TEXT (flexible).
- Interfaz muestra dropdown con opción "Otro".

---

## Pregunta 8 — Exportar: ¿a nivel de qué se filtra? ✓ RESUELTA

**Respuesta:** Filtro por empresa + agencia.

- El usuario elige empresa y agencia.
- Se exportan todas las operaciones de esa agencia.

---

## Pregunta 9 — Detección del tipo de import ✓ RESUELTA

**Respuesta:** El usuario elige empresa + tipo de import (dropdown).

- El perfil de import de la empresa indica las columnas a leer según el tipo elegido.
- Por ahora solo Cartera Simple (avance procesal queda para después, pero el flujo ya está preparado).
- No hay heuristics automáticas.

---

## Pregunta 10 — Estructura del modelo de datos ✓ RESUELTA

**Respuesta:** **3 tablas: Cliente → Operacion → Expediente**.

- Cliente → Operacion → Expediente (Expediente opcional para cartera simple).
- Expediente es opcional (cartera simple = cliente + operacion sin expediente).
- ExpedienteCliente ya existe como link, pero en el nuevo modelo se reemplaza por Operacion como intermediario.

---

## Pregunta 11 — Cartera simple sin expediente ✓ RESUELTA

**Respuesta:** No se crea expediente — se crea solo Cliente + Operacion.

- Expediente queda null (sin relación).
- El expediente se crea solo desde el import de avance procesal.
- La ausencia de expediente es un estado válido en cartera simple.

---

## Pregunta 12 — Clave única de Operacion ✓ RESUELTA

**Respuesta:** `(cuenta, numero_operacion)` es suficiente.

- El caso de misma cuenta + mismo número + productos distintos se maneja con discriminator si aparece en la realidad.
- Por ahora no se agrega complejidad extra.

---

## Pregunta 13 — Auditoría: ¿qué genera eventos? ✓ RESUELTA

**Respuesta:** Import, Export y toda edición manual.

- Create/edit/delete de cliente, operación, expediente generan `auditoria_evento`.
- Cada evento incluye: tipo de operación, usuario, entidad afectada, payload (datos antes/después o delta).
- `updated_at`/`updated_by` de JPA Auditing se sigue usando en las entidades.

---

## Pregunta 14 — Reimport de avance procesal: gestiones ✓ RESUELTA

**Respuesta:** Las gestiones procesales **se acumulan**.

- Las nuevas fechas del Excel crean nuevas filas en `gestion_procesal`.
- Las existentes se preservan (son hechos históricos).
- No hay reemplazo/borrado en reimport.
- Esta pregunta aplica solo para Avance Procesal (cartera simple no crea expediente ni gestiones).

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

## Pregunta 15 — Perfiles de import: ¿dónde se configuran? ✓ RESUELTA

**Respuesta:** Archivo JSON en `resources/` (un archivo por empresa, cargado al startup).

- Más simple que DB para v1, más flexible que código.
- Ej: `resources/perfiles-import/caja-arequipa.json`
- Cargado al iniciar la aplicación.

---

## Pregunta 16 — Módulo de gestión de cobranza (no procesal) ✓ RESUELTA

**Respuesta:** No existe para v1. Solo se maneja el avance procesal (fechas judiciales).

- Las acciones de cobranza (llamadas, visitas, cartas) no se registran en v1.
- Si en el futuro se necesitan, se agrega como extensión.

---

## Pregunta 17 — Bienes embargados: ¿de dónde vienen? ✓ RESUELTA

**Respuesta:** Vienen en el Excel de avance procesal.

- El parser de avance procesal lee los bienes embargados de las columnas dedicadas del Excel.
- No se cargan aparte ni manualmente.

---

## Pregunta 18 — Cambio de abogado en expediente ✓ RESUELTA

**Respuesta:** Se actualiza directamente — el nuevo abogado reemplaza al anterior.

- Sin historial de cambios de abogado en v1.
- `Expediente.abogado` es un `ManyToOne → Usuario`.

---

## Pregunta 19 — Moneda ✓ RESUELTA

**Respuesta:** Solo Soles (S/) en v1.

- No se maneja multimoneda en la primera versión.
- Si otra empresa manda en otra moneda, se define después.

---

## Pregunta 20 — Mismo DNI en distintas empresas ✓ RESUELTA

**Respuesta:** Un solo Cliente — mismo DNI = un solo cliente.

- Aunque la empresa sea distinta, el Cliente es el mismo.
- Todas las operaciones de todas las empresas apuntan al mismo Cliente.
- No se crea `ClienteEmpresa` como entidad separada.
- El matching de import es por `(empresa_id + cuenta + operacion)`, no por DNI solo.

---

## Modelo objetivo (post-refactor, v2)

> Actualizado con P20: un Cliente por empresa (mismo DNI = clientes distintos)

```
Cliente (persona deudora, identificada por DNI)
  id (pk)
  dni (único natural, normalizado)
  nombre_completo
  telefono / telefono2 / telefono3
  direccion
  email
  activo
  created_at / updated_at / deleted_at (soft delete)

ClienteEmpresa (vinculo Cliente × Empresa, necesario porque mismo DNI en empresas distintas = deudores distintos)
  id (pk)
  cliente_id → Cliente
  empresa_id → Empresa
  activo
  -- datos específicos de este par (si los hay en el futuro)

Operacion (una deuda, clave única: empresa+cuenta+numero_operacion)
  id (pk)
  cliente_id → Cliente
  empresa_id → Empresa
  agencia_id → Agencia
  cuenta
  numero_operacion
  monto_capital
  monto_total
  dias_mora
  situacion (JUDICIAL / EXTRAJUDICIAL / PRESCRITA / PAGADA)
  estado (VIGENTE / VENCIDA)
  etapa (EXTRAJUDICIAL / JUDICIAL)
  observacion
  -- Campos procesales (del avance procesal)
  numero_expediente
  tipo_proceso
  tipo_juzgado
  distrito_judicial
  numero_juzgado
  activo
  created_at / updated_at

Expediente (proceso judicial, creado desde avance procesal)
  id (pk)
  numero_expediente (único)
  empresa_id → Empresa
  agencia_id → Agencia
  situacion
  tipo_proceso
  tipo_juzgado
  distrito_judicial
  numero_juzgado
  monto_demandado
  activo
  created_at / updated_at

BienEmbargado (bienes asociados a un expediente)
  id (pk)
  expediente_id → Expediente
  tipo_bien
  partida_registral
  direccion
  monto_mc
  -- ... resto de campos ya definidos en el código existente

GestionProcesal (fechas procesales de un expediente)
  id (pk)
  expediente_id → Expediente
  tipo_gestion (MC / PRINCIPAL / AUDIENCIA / EJECUCION / REMATE)
  etapa
  fecha
  observacion
  created_at

AuditoriaEvento (import / export / eventos de negocio)
  id (pk)
  tipo
  usuario
  payload (JSON)
  created_at
```

---

## Próximas preguntas pendientes

1. **Perfiles de import/export**: ¿cómo se configuran los formatos de columna? ¿JSON en DB o código?
2. **Módulo de gestión (no procesal)**: ¿hay acciones de cobranza (llamada, carta, visita) además de las procesales?
3. **V2 features**: autenticación multiusuario, notificaciones, dashboard BI.

