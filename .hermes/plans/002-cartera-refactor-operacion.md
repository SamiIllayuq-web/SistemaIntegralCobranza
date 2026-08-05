# Plan: Refactor CarteraService — Modelo Cliente + Operacion

## Problema
El modelo actual de `Cliente` es "plano" — mezcla persona y operación en una sola entidad.
El SPEC.md define un modelo干净的: `Cliente` (persona) → `Operacion` (deuda).

## Modelo objetivo (del SPEC.md)

### Cliente (persona deudora)
- `id`, `dni` (único), `nombreCompleto`, `telefono`, `telefono2`, `telefono3`, `direccion`, `email`
- `activo`, `fechaCreacion`, `fechaActualizacion`
- NO tiene: empresa, agencia, cuenta, operacion, deuda

### Operacion (una deuda)
- `id`, `cliente_id → Cliente`, `empresa_id → Empresa`, `agencia_id → Agencia`
- `cuenta`, `numero_operacion` (clave única)
- `monto_capital`, `monto_total`, `dias_mora`
- `moneda`, `tipo_credito`
- `situacion` (JUDICIAL/EXTRAJUDICIAL/PRESCRITA/PAGADA), `estado` (VIGENTE/VENCIDA), `etapa`
- `observacion`, `rango`, `analista`, `analista_senior`
- `numero_expediente`, `tipo_proceso`, `tipo_juzgado`, `distrito_judicial`, `numero_juzgado`
- `abogado_id → Usuario`
- `activo`, `fechaCreacion`, `fechaActualizacion`

### BienEmbargado (hijo de Operacion)
- Ya existe en `expediente/entity/BienEmbargado.java` — solo agregar `operacion_id`

---

## Pasos de implementación

### Paso 1 — Crear Operacion
- [ ] Crear `operacion/entity/Operacion.java` con todos los campos del SPEC.md
- [ ] Crear `operacion/repository/OperacionRepository.java`
  - `findByEmpresaIdAndCuentaAndNumeroOperacion(empresaId, cuenta, operacion)` → Optional<Operacion>
  - `findByClienteId(clienteId)` → List<Operacion>
- [ ] Crear `operacion/mapper/OperacionMapper.java`
- [ ] Crear `operacion/dto/OperacionDTO.java`
- [ ] Crear `operacion/dto/OperacionFormDTO.java`
- [ ] Crear `operacion/service/OperacionService.java`

### Paso 2 — Modificar Cliente existente
- [x] Quitar de `Cliente.java`: `numeroCuenta`, `numeroOperacion`, `deudaCapital`, `deudaTotal`, `empresa`, `agencia`, `estadoGestion`, `observaciones`, `fechaUltimaGestion`
- [x] Agregar `email`, `deletedAt`
- [ ] Agregar `email` column en la BD (ALTER TABLE) — pendiente migration
- [x] Actualizar `ClienteRepository`: quitar `findByDniAndNumeroCuentaAndNumeroOperacion`
- [x] Agregar `findByDni(dni)` → Optional<Cliente>

### Paso 3 — Actualizar CarteraService
- [x] Reescribir `importarExcel`: upsert Operacion + find-or-create Cliente por DNI
- [x] Fix `GestionService`, `ExpedienteService`, `ClienteController`

### Paso 4 — Actualizar ClienteMapper
- [x] toDTO() simplificado — solo campos persona

### Paso 5 — Limpiar código que referencia campos obsoletos de Cliente
- [ ] Buscar todos los usos de `cliente.getNumeroCuenta()`, `cliente.getDeudaCapital()`, etc.
- [ ] Actualizar donde corresponda

### Paso 6 — Compilar y verificar
- [ ] `mvn clean compile` → 0 errores
- [ ] Verificar que la importacion de Excel funciona end-to-end

### Paso 7 — Ticket 002 cerrado
- [ ] Documentar el cambio de modelo en el ticket

---

## Notas técnicas
- La clave única de Operacion es `(empresa_id, cuenta, numero_operacion)` — no solo (cuenta, operacion)
  porque el mismo número de operación puede repetirse en empresas distintas.
- BienEmbargado necesita связь con Operacion en vez de Expediente.
- Gestión de expediente/procesal sigue siendo hijos de Operacion.

## Archivos a crear (nuevos)
- `src/main/java/com/startup/cobranza/operacion/entity/Operacion.java`
- `src/main/java/com/startup/cobranza/operacion/repository/OperacionRepository.java`
- `src/main/java/com/startup/cobranza/operacion/mapper/OperacionMapper.java`
- `src/main/java/com/startup/cobranza/operacion/dto/OperacionDTO.java`
- `src/main/java/com/startup/cobranza/operacion/dto/OperacionFormDTO.java`
- `src/main/java/com/startup/cobranza/operacion/service/OperacionService.java`
- `src/main/java/com/startup/cobranza/operacion/controller/OperacionController.java`
- `src/main/java/com/startup/cobranza/operacion/exception/OperacionException.java`

## Archivos a modificar
- `Cliente.java` — quitar campos de operación
- `ClienteRepository.java` — cambiar métodos de búsqueda
- `ClienteMapper.java` — adaptar mappings
- `CarteraService.java` — nueva lógica de upsert
- `BienEmbargado.java` — cambiar operacion_id en vez de expediente_id
