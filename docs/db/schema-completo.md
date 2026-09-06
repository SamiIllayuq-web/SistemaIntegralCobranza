# Schema de Base de Datos — Sistema Integral de Cobranza

> **Estado:** En construcción — Fase 0
> **Decisión tomada:** La tabla `expedientes` se elimina. Los datos judiciales viven en `operaciones`. Expediente se presenta como vista/sección dentro de operaciones.

---

## Mapa de Entidades y Relaciones

```
EMPRESA (1)──(N) AGENCIA (1)──(N) OPERACION (N)──(1) CLIENTE
                                              │
                         ┌─────────────────────┼─────────────────────┐
                         │                     │                     │
                   PARTICIPANTES          BIENES_EMBARGADOS    GESTIONES_JUDICIALES
                   OPERACION               (N:1)                (N:1)
                                                 │
                                          GESTIONES ──(N)──(1) CLIENTE

AUDITORIA_EVENTOS    ← log genérico de cambios
IMPORTACIONES        ← historial de cargas Excel
REPORTES_MC          ← registro de reportes generados
```

---

## Las 13 Tablas

### 1. `empresas`
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGSERIAL | PK |
| nombre | VARCHAR | no nulo |
| ruc | VARCHAR | único |
| telefono | VARCHAR | |
| email | VARCHAR | |
| direccion | VARCHAR | |
| activo | BOOLEAN | default true |
| fecha_creacion | TIMESTAMP | |

**Qué es:** Entidad financiera (Caja Arequipa, banco, etc.). Es el "cliente" del software.
**Para qué existe:** Cada operación pertenece a una empresa. Define el origen de la cartera.

---

### 2. `agencias`
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGSERIAL | PK |
| nombre | VARCHAR | no nulo |
| codigo | VARCHAR | único |
| telefono | VARCHAR | |
| direccion | VARCHAR | |
| empresa_id | BIGINT | FK → empresas |
| activo | BOOLEAN | default true |
| fecha_creacion | TIMESTAMP | |

**Qué es:** Sucursal o zona de una empresa — "Caja Arequipa agencia Puno".
**Para qué existe:** Las carteras se asignan por agencia. Filtro principal en la bandeja de operaciones.

---

### 3. `usuarios`
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGSERIAL | PK |
| username | VARCHAR | único, no nulo |
| password | VARCHAR | BCrypt |
| nombre | VARCHAR | |
| rol | VARCHAR | ADMIN / SECRETARIO |
| activo | BOOLEAN | default true |
| fecha_creacion | TIMESTAMP | |

**Qué es:** Abogados, secretarios y administradores que usan el sistema.
**Para qué existe:** Autenticación + registro de quién hizo cada gestión.

---

### 4. `clientes`
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGSERIAL | PK |
| nombre_completo | VARCHAR | no nulo |
| dni | VARCHAR | único |
| telefono | VARCHAR | |
| telefono2 | VARCHAR | |
| telefono3 | VARCHAR | |
| direccion | VARCHAR | |
| email | VARCHAR | |
| activo | BOOLEAN | default true |
| deleted_at | TIMESTAMP | soft delete |
| fecha_creacion | TIMESTAMP | |
| fecha_actualizacion | TIMESTAMP | |

**Qué es:** Persona deudora identificada por DNI. Una persona = un cliente.
**Para qué existe:** Contacto con el deudor. Un cliente puede tener varias operaciones (créditos distintos).

---

### 5. `operaciones` ← TABLA PRINCIPAL
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGSERIAL | PK |
| cliente_id | BIGINT | FK → clientes, no nulo |
| agencia_id | BIGINT | FK → agencias |
| cuenta | VARCHAR | no nulo |
| numero_operacion | VARCHAR | no nulo |
| monto_capital | DECIMAL(15,2) | |
| monto_total | DECIMAL(15,2) | |
| dias_mora | INTEGER | |
| moneda | VARCHAR | |
| tipo_credito | VARCHAR | |
| situacion | VARCHAR | texto libre del Excel |
| estado | VARCHAR | |
| etapa | VARCHAR | |
| observacion | TEXT | |
| rango | VARCHAR | |
| analista | VARCHAR | |
| analista_senior | VARCHAR | |
| numero_expediente | VARCHAR | **clave para vista expedientes** |
| tipo_proceso | VARCHAR | |
| tipo_juzgado | VARCHAR | |
| distrito_judicial | VARCHAR | |
| numero_juzgado | VARCHAR | |
| abogado_id | BIGINT | FK → usuarios |
| trans | BOOLEAN | transferido |
| busqueda_bienes | BOOLEAN | |
| monto_demandado | DECIMAL(15,2) | |
| escribano_legal | VARCHAR(500) | |
| codigo_exp_cautelar | VARCHAR(500) | |
| incidente | BOOLEAN | |
| fecha_presentacion | DATE | |
| fecha_inadmisible_principal | DATE | |
| fecha_admision_principal | DATE | |
| fecha_audiencia_unica | DATE | |
| fecha_auto_final | DATE | |
| fecha_consentimiento | DATE | |
| fecha_ejecutoriada | DATE | |
| fecha_ingreso_ejecucion | DATE | |
| fecha_tasacion | DATE | |
| fecha_nombramiento_martillero | DATE | |
| fecha_remate_1 | DATE | |
| fecha_remate_2 | DATE | |
| fecha_remate_3 | DATE | |
| observacion_actos | TEXT | |
| comentario | TEXT | comentario general |
| zona | VARCHAR | |
| departamento | VARCHAR | |
| provincia | VARCHAR | |
| distrito | VARCHAR | |
| direccion | TEXT | dirección del deudor |
| referencia | TEXT | |
| telefono | VARCHAR | teléfono del deudor |
| monto_aprobado | DECIMAL(15,2) | |
| estado_cartera | VARCHAR | ACTIVO/CANCELADA/DESASIGNADA/VENDIDA/DEVUELTA |
| fecha_desembolso | DATE | |
| importe_desembolso | DECIMAL(15,2) | |
| etapa_procesal_texto | VARCHAR | texto libre |
| acto_pendiente | TEXT | |
| fecha_ultimo_estado_proceso | DATE | |
| fecha_aceptacion_demanda | DATE | |
| fecha_envio_judicial | DATE | |
| fecha_asignacion_abogado | DATE | |
| fecha_castigo | DATE | |
| tipo_fondo | VARCHAR | |
| activo | BOOLEAN | default true |
| fecha_creacion | TIMESTAMP | |
| fecha_actualizacion | TIMESTAMP | |

**Unique constraint:** `(cuenta, numero_operacion)`

**Qué es:** Cada fila del Excel importado = una operación. Es la tabla más importante del sistema.
**Para qué existe:** Registra la deuda, los datos del cliente, y TODOS los datos judiciales. La vista "Expedientes" filtra operaciones que tienen `numero_expediente` informado.

---

### 6. `participantes_operacion` (expedientes_clientes)
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGSERIAL | PK |
| expediente_id | BIGINT | FK → expedientes (NULO tras fusión) |
| operacion_id | BIGINT | FK → operaciones |
| cliente_id | BIGINT | FK → clientes |
| tipo | VARCHAR | TITULAR / CO-TITULAR / AVAL / GARANTE |
| nombre_completo | TEXT | no nulo |
| dni | VARCHAR | |
| cuenta | VARCHAR | |
| operacion | VARCHAR | número de operación |
| co_titular_aval | VARCHAR | |
| trans | VARCHAR | |
| moneda | VARCHAR | |
| deuda_capital | DECIMAL(15,2) | |
| deuda_total | DECIMAL(15,2) | |
| busqueda_bienes | TEXT | |
| observacion | TEXT | |

**Qué es:** Participantes de un expediente judicial — deudor principal, co-titulares, avales, garantes.
**Para qué existe:** Un expediente puede tener varias personas obligadas. Cada una tiene su propio DNI y monto de deuda.
**Post-fusión:** Se relacional directo con `operacion_id` (ya no con `expediente_id`).

---

### 7. `gestiones`
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGSERIAL | PK |
| tipo | VARCHAR | LLAMADA / VISITA / OBSERVACION / COMPROMISO_PAGO |
| fecha_gestion | TIMESTAMP | no nulo |
| observaciones | TEXT | |
| monto_compromiso | DECIMAL(15,2) | |
| fecha_compromiso | TIMESTAMP | |
| cliente_id | BIGINT | FK → clientes, no nulo |
| usuario_registra | VARCHAR | username |
| fecha_registro | TIMESTAMP | |

**Qué es:** Llamadas, visitas y observaciones de cobranza extrajudicial.
**Para qué existe:** Registro de contacto con el cliente — qué le dije, qué prometió, cuándo paga.

---

### 8. `gestiones_judiciales` (ex-gestiones_procesales)
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGSERIAL | PK |
| operacion_id | BIGINT | FK → operaciones |
| tipo_gestion | VARCHAR | MC / PRINCIPAL / AUDIENCIA / EJECUCION / REMATE |
| etapa | VARCHAR | PRESENTACION / INADMISIBLE / ADMISION / UNICA / AUTO_FINAL / etc. |
| fecha | DATE | |
| observacion | TEXT | |
| fecha_registro | TIMESTAMP | |

**Qué es:** Fechas y actos procesales dentro del expediente judicial — presentación de MC, admisión, audiencia, remate, etc.
**Para qué existe:** Registrar las fechas oficiales del proceso judicial. Se diferencia de `gestiones` en que estas son del sistema judicial, no llamadas al cliente.
**Post-fusión:** Se relaaciona con `operacion_id` en vez de `expediente_id`.

---

### 9. `bienes_embargados`
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGSERIAL | PK |
| expediente_id | BIGINT | FK → expedientes (NULO tras fusión) |
| operacion_id | BIGINT | FK → operaciones |
| tipo_bien | VARCHAR | INMUEBLE / MUEBLE |
| partida_registral | VARCHAR | |
| detalle_garantia | TEXT | |
| direccion | TEXT | |
| distrito | VARCHAR | |
| provincia | VARCHAR | |
| departamento | VARCHAR | |
| garantia_inscrita | VARCHAR | |
| fecha_inscripcion | DATE | |
| fecha_presentacion_rrpp | DATE | |
| asiento_inscripcion | TEXT | |
| fecha_presentacion_mc | DATE | |
| fecha_inadmisible | DATE | |
| fecha_admision | DATE | |
| comentario_mc | TEXT | |
| detalle_acreedores | TEXT | |
| tipo_preferencia | VARCHAR | |
| titular_predio | TEXT | |
| fecha_generacion_mc | DATE | |
| monto_mc | DECIMAL(15,2) | |
| moneda_mc | VARCHAR(10) | |
| rango | VARCHAR(50) | |

**Qué es:** Bienes (inmuebles, muebles) sobre los cuales el juzgado ordenó embargo o medida cautelar.
**Para qué existe:** Identificar qué bien está embargado, partida registral, dirección, monto de la MC, estado de inscripción en registros públicos.
**Post-fusión:** Se relaaciona con `operacion_id` (el FK a `expediente` queda nullable o se migra).

---

### 10. `reportes_mc`
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGSERIAL | PK |
| empresa_id | BIGINT | FK → empresas, no nulo |
| nombre_archivo | TEXT | |
| mes | TEXT | |
| anio | INTEGER | |
| fecha_generacion | TIMESTAMP | |
| generado_por | BIGINT | FK → usuarios |

**Qué es:** Registro de cada vez que se generó un reporte de medida cautelar.
**Para qué existe:** Historial de reportes generados, por empresa y período.

---

### 11. `auditoria_eventos`
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGSERIAL | PK |
| usuario | VARCHAR | username |
| tipo | VARCHAR | CREATE / UPDATE / DELETE |
| objeto_tipo | VARCHAR | nombre de la entidad |
| objeto_id | BIGINT | ID del objeto afectado |
| payload | TEXT | JSON con el detalle |
| descripcion | VARCHAR | texto legible |
| fecha_creacion | TIMESTAMP | |

**Qué es:** Log de auditoría — cada cambio en el sistema se graba aquí.
**Para qué existe:** Trazabilidad legal. Si un expediente desaparece, quién lo tocó.

---

### 12. `importaciones`
| Campo | Tipo | Notas |
|---|---|---|
| id | BIGSERIAL | PK |
| nombre_archivo | VARCHAR | no nulo |
| total_registros | INTEGER | |
| registros_exitosos | INTEGER | |
| registros_fallidos | INTEGER | |
| empresa_id | BIGINT | |
| agencia_id | BIGINT | |
| estado | VARCHAR | no nulo |
| usuario_importa | VARCHAR | username |
| fecha_importacion | TIMESTAMP | |
| errores | TEXT | |

**Qué es:** Historial de cada carga de Excel al sistema.
**Para qué existe:** Saber qué se importó, cuántos registros vinieron, cuántos se grabaron bien.

---

## Vistas del Sistema

### Vista "Operaciones"
`GET /operaciones`
- Bandeja general con todos los filtros: agencia, situación, estado cartera, DNI, cuenta, número expediente
- Paginación 50/page
- Acciones: ver detalle, editar

### Vista "Expedientes"
`GET /operaciones/expedientes`
- Misma tabla de operaciones
- Filtro implícito: solo operaciones con `numero_expediente` informado
- Columnas visibles: número expediente, juzgado, distrito judicial, tipo proceso, etapa procesal, monto demandado, abogado, próximas fechas
- Acciones: ver detalle completo de la operación (sección judicial expandida)
- **Usa la MISMA entidad Operacion** — no hay tabla separada

---

## Decisiones de Diseño

### Por qué `operaciones` es la tabla principal
- Cada fila del Excel = una operación
- Los datos judiciales viven en la misma tabla que los datos financieros
- La vista expedientes es solo un filtro + reorganización de columnas de `operaciones`

### Por qué se eliminó `expedientes` como tabla separada
- Los datos judiciales siempre vienen del Excel
- Nunca se crea un expediente "a mano" — siempre se sobrescribe desde el Excel
- Mantener `expedientes` duplicaba los mismos campos en ambas tablas
- La relación 1:1 entre Expediente y Operacion generaba confusión

### Nombres post-fusión
- `expedientes_clientes` → `participantes_operacion`
- `gestiones_procesales` → `gestiones_judiciales`
- La tabla `expedientes` se elimina

---

## Flujo de Importación (post-fusión)

```
Excel Avance Procesal → CarteraService
  ├── Upsert Cliente por DNI
  ├── Upsert Operacion por (empresa_id, cuenta, numero_operacion)
  │    └── Actualiza campos judiciales
  ├── Upsert ParticipantesOperacion (expedientes_clientes)
  ├── Upsert BienesEmbargados (link a operacion)
  └── Upsert GestionesJudiciales (ex-gestiones_procesales)
```

---

## Campos de Expediente vs Operacion (comparación)

| Campo en Expediente | En Operacion? | Notas |
|---|---|---|
| numero_expediente | ✅ `numero_expediente` | |
| situacion | ✅ `situacion` | |
| tipo_proceso | ✅ `tipo_proceso` | |
| tipo_juzgado | ✅ `tipo_juzgado` | |
| distrito_judicial | ✅ `distrito_judicial` | |
| numero_juzgado | ✅ `numero_juzgado` | |
| monto_demandado | ✅ `monto_demandado` | |
| incidente | ✅ `incidente` | |
| especialista_legal | ❌ → agregar a Operacion | |
| escribano_legal | ✅ `escribano_legal` | |
| codigo_exp_cautelar | ✅ `codigo_exp_cautelar` | |
| expediente_cautelar_codigo | ❌ duplicado de codigo_exp_cautelar? | |
| observacion | ✅ `observacion` | |
| comentario_general | ✅ `comentario` | mismo |
| activo | ✅ `activo` | |
| fecha_presentacion | ✅ `fecha_presentacion` | |
| fecha_inadmisible_principal | ✅ `fecha_inadmisible_principal` | |
| fecha_admision_principal | ✅ `fecha_admision_principal` | |
| fecha_audiencia_unica | ✅ `fecha_audiencia_unica` | |
| fecha_auto_final | ✅ `fecha_auto_final` | |
| fecha_consentimiento | ✅ `fecha_consentimiento` | |
| fecha_ejecutoriada | ✅ `fecha_ejecutoriada` | |
| fecha_ingreso_ejecucion | ✅ `fecha_ingreso_ejecucion` | |
| fecha_tasacion | ✅ `fecha_tasacion` | |
| fecha_nombramiento_martillero | ✅ `fecha_nombramiento_martillero` | |
| fecha_remate_1/2/3 | ✅ `fecha_remate_1/2/3` | |
| observacion_actos | ✅ `observacion_actos` | |

**Resumen:** Solo `especialista_legal` y `expediente_cautelar_codigo` (si es distinto de `codigo_exp_cautelar`) no existen en Operacion. Se incorporarán durante la migración.
