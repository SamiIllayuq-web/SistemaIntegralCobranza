# Plan: Sistema de Gestión de Carteras de Cobranza

> Versión revisada del plan original. Donde había ambigüedad, se tomó una
> decisión concreta para destrabar. Todas las decisiones se listan en la
> sección 13 con notas para revertir si no te cierran.

## 1. Contexto y problema

Un estudio jurídico recibe carteras de clientes morosos de entidades financieras
(bancos, financieras, retail). Cada cartera viene en un Excel con formato propio
de la entidad. Hoy el estudio:

- Recibe el Excel por email/WeTransfer.
- Lo abre en Excel, lo limpia a mano.
- Lo carga a un sistema manual (probablemente otra planilla o un CRM genérico).
- Cuando reporta gestión a la entidad, completa otro Excel "a mano" con el
  formato que la entidad exige.

El sistema automatiza los pasos manuales: **importar → revisar/clasificar →
exportar**, con trazabilidad completa de cada operación.

## 2. Usuarios y escala

### Usuarios

- **Equipo del estudio jurídico** (3-5 personas): abogados y administrativos
  que gestionan las carteras día a día. Todos internos, todos de confianza.
- **No hay acceso externo en v1** (ni morosos ni entidades).

### Escala esperada

- Carteras activas simultáneas: 3-8.
- Clientes por cartera: 1k-50k (depende de la entidad).
- Volumen total en el sistema: < 200k clientes en v1.
- **Usuarios concurrentes: 1 por vez** (no simultáneo). El sistema debe
  tolerar que dos personas vean datos distintos al mismo tiempo, pero no
  asumimos edición concurrente.

## 3. Objetivos de v1

### Funcionalidades incluidas

- **Importar Excel** de una cartera → poblar la base de datos.
- **Matchear clientes** por DNI normalizado.
  - Si existe: actualizar campos mutables, preservar historial.
  - Si no: crear.
- **Listar clientes** con búsqueda (DNI, nombre) y filtros (empresa, estado,
  etapa, días de mora, cartera).
- **Ver y editar** un cliente (campos de contacto, estado, etapa, notas).
- **Exportar Excel** con el formato que pide la entidad (perfil configurable).
- **Auditoría** de operaciones: cada import / export / edición queda
  registrada con usuario, fecha, y diff.

### Criterios de éxito (cómo sabemos que v1 está lista)

1. Importamos un Excel real de la entidad A end-to-end sin tocar Excel a mano.
2. Reimportamos el mismo Excel → 0 duplicados.
3. Editamos un cliente → aparece en el historial con el valor anterior y el nuevo.
4. Exportamos un Excel que la entidad A acepta sin pedir correcciones de formato.
5. Si el Excel tiene 3 filas con DNI inválido, las otras 997 se importan y las
   3 quedan en un reporte de errores descargable.
6. Un abogado nuevo puede usar el sistema leyendo solo el README.

## 4. Fuera de scope de v1 (y por qué)

- **Autenticación de usuarios**: 3-5 personas de confianza, ambiente interno.
  Sumar auth es trabajo serio y no resuelve un problema real todavía. (v2 sí.)
- **Notificaciones a morosos**: requiere templates, opt-out, y probablemente
  integración con Twilio/SendGrid. No es lo que la entidad nos pide hoy.
- **Generación de PDFs**: el entregable a la entidad es Excel. PDFs son para
 control interno del estudio y los manejan con sus herramientas actuales.
- **Integración con APIs externas**: la entidad manda Excel, no expone API.
  Si en el futuro lo hace, lo tomamos como un caso de uso de import nuevo.
- **Multi-tenant**: el estudio es un solo cliente del sistema. No necesitamos
  aislar datos entre estudios.
- **App móvil**: la operación es de oficina, no de campo.
- **Dashboard / métricas**: reportes ad-hoc se hacen con SQL sobre la DB. Un
  dashboard BI es v2.

## 5. Stack técnico

> El stack está **fijado por `PROJECT_CONTEXT.md`**. Este plan no lo modifica:
> solo documenta qué pieza se usa para qué.

### Backend

- **Java 21**
- **Spring Boot 3**
- **Spring Security** + **JWT** (login del MVP)
- **Spring Data JPA** + **Hibernate**
- **RestClient** (HTTP client moderno de Spring) — para Kmente
- **Maven**

### Frontend

- **Thymeleaf** (server-rendered, sin build step)
- **Bootstrap 5** (UI)
- **HTML5 + CSS3 + JavaScript vanilla** (nada de frameworks JS)

### Datos

- **PostgreSQL** hosteado en **Supabase Cloud**
- **Apache POI** para Excel (XSSF para `.xlsx`, HSSF para `.xls`)

### Repo y operación

- **Git + GitHub** — ramas `feature/<nombre>`, nada directo sobre `main`
- Despliegue dev: Spring Boot local + Supabase Cloud
- Despliegue prod: **Railway** + Supabase Cloud
- **Variables de entorno** para todo dato sensible (usuarios, passwords, JWT
  secret, API keys). Nunca en el repo, nunca en código.

### Stack explícitamente NO aprobado (PROJECT_CONTEXT.md)

- React / Angular / Vue
- Docker
- Redis
- Microservicios
- WebFlux / programación reactiva
- Librerías innecesarias (Lombok, Hibernate Envers, Flyway, H2, etc. — si
  aparecen, justificar antes de sumar)

### Piezas que no usamos y por qué

- **Lombok**: Java 21 tiene records; el boilerplate que ahorra no compensa la
  dependencia extra.
- **Hibernate Envers**: auditoría fina se cubre con `auditoria_evento` + JPA
  Auditing (`@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`,
  `@LastModifiedBy`). Si en una iteración futura necesitamos diff por campo,
  se evalúa.
- **Flyway**: para el MVP alcanza con `spring.jpa.hibernate.ddl-auto=update`
  contra Supabase. Cuando necesitemos versionado de schema en serio, se suma.
- **Caché (Caffeine/Redis)**: no hace falta con el volumen esperado del MVP.
- **Cola de mensajes**: el import es batch chico, no justifica Kafka/Rabbit.

## 6. Modelo de datos (esqueleto)

> Auditoría: `created_at` / `updated_at` / `created_by` / `updated_by` se
> manejan con JPA Auditing. Los eventos de negocio (import, export, edición
> relevante) van a la tabla `auditoria_evento`. No usamos Hibernate Envers.

```sql
-- Una empresa (banco, financiera, retail) que encarga la cobranza
empresa (
  id            BIGSERIAL PRIMARY KEY,
  nombre        TEXT NOT NULL,
  tipo          TEXT NOT NULL,  -- banco | financiera | retail | otro
  ruc           TEXT,           -- RUC de la empresa
  created_at    TIMESTAMP NOT NULL,
  updated_at    TIMESTAMP NOT NULL
)

-- Una sucursal del estudio jurídico que gestiona los casos
agencia (
  id            BIGSERIAL PRIMARY KEY,
  nombre        TEXT NOT NULL,           -- "Chanchamayo", "Oxapampa", etc.
  region        TEXT,
  created_at    TIMESTAMP NOT NULL,
  updated_at    TIMESTAMP NOT NULL
)

-- Un lote de casos recibidos en una fecha
cartera (
  id            BIGSERIAL PRIMARY KEY,
  empresa_id    BIGINT NOT NULL REFERENCES empresa(id),
  agencia_id    BIGINT NOT NULL REFERENCES agencia(id),
  nombre        TEXT NOT NULL,           -- "Banco X — Cartera Marzo 2026"
  archivo_origen TEXT,                   -- nombre del Excel original
  fecha_recepcion DATE NOT NULL,
  created_at    TIMESTAMP NOT NULL
)

-- Persona morosa. Una persona puede tener N operaciones (casos).
cliente (
  id            BIGSERIAL PRIMARY KEY,
  dni           TEXT,                    -- normalizado, 8 dígitos
  nombre        TEXT NOT NULL,
  direccion     TEXT,
  comuna        TEXT,
  ciudad        TEXT,
  telefono      TEXT,
  email         TEXT,
  estado        TEXT NOT NULL DEFAULT 'VIGENTE',
                                      -- VIGENTE | CON_ACUERDO | INCOBRABLE
  requiere_revision BOOLEAN NOT NULL DEFAULT false,
  notas         TEXT,
  created_at    TIMESTAMP NOT NULL,
  updated_at    TIMESTAMP NOT NULL,
  deleted_at    TIMESTAMP,               -- soft delete
  version       BIGINT NOT NULL DEFAULT 0   -- optimistic lock (JPA @Version)
)
CREATE UNIQUE INDEX uq_cliente_dni ON cliente (dni) WHERE deleted_at IS NULL;
CREATE INDEX idx_cliente_nombre ON cliente USING gin (nombre gin_trgm_ops);
CREATE INDEX idx_cliente_requiere_revision ON cliente (requiere_revision)
  WHERE requiere_revision = true;

-- Una operación = un caso de cobranza. Un cliente puede tener N operaciones.
-- empresa_id y agencia_id están denormalizados respecto a cartera para queries directas.
operacion (
  id            BIGSERIAL PRIMARY KEY,
  cliente_id    BIGINT NOT NULL REFERENCES cliente(id),
  cartera_id    BIGINT NOT NULL REFERENCES cartera(id),
  empresa_id    BIGINT NOT NULL REFERENCES empresa(id),
  agencia_id    BIGINT NOT NULL REFERENCES agencia(id),
  cuenta        TEXT,                    -- número de cuenta (del Excel)
  numero_operacion TEXT,                -- número de operación (del Excel)
  abogado       TEXT,                    -- nombre del abogado asignado
  secretario    TEXT,                    -- especialista legal / secretario
  situacion     TEXT,                    -- JUDICIAL | CASTIGADA | REF_JUDICIAL | EXTRAJUDICIAL
  observacion   TEXT,                    -- notas libres del Excel
  busqueda_bienes TEXT,                 -- POSITIVO | NEGATIVO
  moneda        TEXT,                    -- "S/" en el ejemplo
  monto_capital NUMERIC(18,2),
  monto_total   NUMERIC(18,2),
  monto_ddo     NUMERIC(18,2),
  fecha_origen  DATE,
  dias_mora     INT,                     -- derivado, recalculable
  estado        TEXT NOT NULL,           -- VIGENTE | VENCIDA | PAGADA | PRESCRITA
  etapa         TEXT,                    -- EXTRAJUDICIAL | JUDICIAL | PRESCRITA
  tipo_proceso  TEXT,                    -- ODSD | MC/ODSD | etc.
  tipo_juzgado  TEXT,
  distrito_judicial TEXT,
  nro_juzgado   TEXT,
  nro_expediente TEXT,
  contrato_ref  TEXT,                    -- contrato en la entidad
  -- Medida cautelar (subset mínimo; resto se omite en MVP)
  monto_mc      NUMERIC(18,2),
  fecha_inscripcion_mc DATE,
  -- Eventos procesales clave (subset mínimo; resto se omite en MVP)
  fecha_presentacion DATE,
  fecha_admision      DATE,
  fecha_auto_final    DATE,
  fecha_remate        DATE,
  created_at    TIMESTAMP NOT NULL,
  updated_at    TIMESTAMP NOT NULL,
  version       BIGINT NOT NULL DEFAULT 0
)
CREATE INDEX idx_operacion_cliente ON operacion (cliente_id);
CREATE INDEX idx_operacion_cartera ON operacion (cartera_id);
CREATE INDEX idx_operacion_empresa ON operacion (empresa_id);
CREATE INDEX idx_operacion_agencia ON operacion (agencia_id);
CREATE INDEX idx_operacion_situacion ON operacion (situacion);

-- Perfil de import por empresa: cómo leer sus Excel
import_perfil (
  id            BIGSERIAL PRIMARY KEY,
  empresa_id    BIGINT NOT NULL REFERENCES empresa(id),
  nombre        TEXT NOT NULL,           -- "Banco X — Perfil estándar"
  version       INT NOT NULL,
  config_json   JSONB NOT NULL,          -- header row, mappings, date format
  activo        BOOLEAN NOT NULL DEFAULT true,
  created_at    TIMESTAMP NOT NULL,
  UNIQUE (empresa_id, version)
)

-- Perfil de export: cómo escribir el Excel que la empresa espera
export_perfil (
  id            BIGSERIAL PRIMARY KEY,
  empresa_id    BIGINT NOT NULL REFERENCES empresa(id),
  nombre        TEXT NOT NULL,
  version       INT NOT NULL,
  config_json   JSONB NOT NULL,
  activo        BOOLEAN NOT NULL DEFAULT true,
  created_at    TIMESTAMP NOT NULL,
  UNIQUE (empresa_id, version)
)

-- Eventos de auditoría a nivel aplicación
auditoria_evento (
  id            BIGSERIAL PRIMARY KEY,
  usuario       TEXT NOT NULL,
  tipo          TEXT NOT NULL,           -- IMPORT_OK | IMPORT_ERROR | EXPORT_OK
                                        -- | CLIENTE_EDIT | CLIENTE_DELETE
  objeto_tipo   TEXT,                    -- "cliente" | "cartera" | "operacion"
  objeto_id     BIGINT,
  payload       JSONB,                   -- diff o resumen
  created_at    TIMESTAMP NOT NULL
)
CREATE INDEX idx_auditoria_created ON auditoria_evento (created_at);
CREATE INDEX idx_auditoria_tipo ON auditoria_evento (tipo);

-- Resultado de una importación
importacion (
  id            BIGSERIAL PRIMARY KEY,
  cartera_id    BIGINT REFERENCES cartera(id),
  import_perfil_id BIGINT REFERENCES import_perfil(id),
  archivo_nombre TEXT NOT NULL,
  total_filas   INT NOT NULL,
  filas_ok      INT NOT NULL,
  filas_error   INT NOT NULL,
  errores       JSONB,                   -- [{ fila, columna, valor, mensaje }]
  created_at    TIMESTAMP NOT NULL
)

-- Resultado de una exportación
exportacion (
  id            BIGSERIAL PRIMARY KEY,
  cartera_id    BIGINT REFERENCES cartera(id),
  export_perfil_id BIGINT REFERENCES export_perfil(id),
  archivo_nombre TEXT NOT NULL,
  total_filas   INT NOT NULL,
  created_at    TIMESTAMP NOT NULL
)
```

## 7. Workflow de uso

1. **Entidad envía Excel** de cartera → estudio.
2. **Estudio sube el Excel** al sistema (formulario de carga).
3. **Sistema parsea, valida y matchea** clientes.
4. **Sistema reporta**: X importados / Y actualizados / Z con error.
5. **Estudio descarga reporte de errores** (si hay) y revisa casos puntuales.
6. **Estudio edita / clasifica** clientes (estado, etapa, notas).
7. **Estudio exporta Excel de gestión** con el formato de la entidad.
8. **Estudio envía Excel** → entidad.

**Cada paso 2, 3, 4, 6, 7, 8 genera un evento en `auditoria_evento`.**

## 8. Detalle de features v1

### 8.1 Importar Excel

**Input:** archivo `.xlsx`, empresa asociada, perfil de import (auto-sugerido).

**Proceso:**

1. Detectar encoding y leer header según `import_perfil.config_json`.
2. Mapear columnas según el perfil.
3. Por cada fila:
   - Normalizar DNI (validar 8 dígitos numéricos; trim de espacios).
   - Validar campos obligatorios (DNI, nombre, monto).
   - Si pasa → encolar para upsert.
   - Si no → agregar a `errores[]` con fila + columna + razón.
4. Al final, en una transacción:
   - Hacer upsert por DNI.
   - Persistir `importacion` con totales y errores.
   - Persistir `auditoria_evento`.

**Output al usuario:**

- Total: X / OK: Y / Errores: Z.
- Botón "Descargar reporte de errores" (CSV).
- Botón "Ver clientes importados" (link al listado filtrado por cartera).

**Decisiones:**

- **Importamos lo válido, fallamos lo inválido por fila.** No rompemos todo el
  import por 1 fila mala. La entidad manda Exceles con basura; eso no puede
  bloquear el proceso.
- **Tope de advertencia al 50% de error**: si más de la mitad de las filas
  fallan, advertimos al usuario. Es señal de que el perfil está mal configurado.
- **Reimportar no modifica deudas existentes.** Solo crea deudas nuevas. La
  deuda existente se da por "terminada" cuando la entidad lo indique en otro
  Excel (manejo de deuda_pagada es feature v2, ver §16).

### 8.2 Matchear clientes

**Reglas (en orden):**

1. **Match por DNI normalizado** (único entre clientes no soft-deleted).
   - Si `cliente.dni = X` existe → actualizar campos mutables.
   - Si no existe → crear.
2. **Fallback: si no hay DNI**, matchear por `(empresa_id, contrato_ref)`. Si
   existe, vincular la deuda a ese cliente. Si no, crear cliente con `rut = NULL`
   y marcar con `requiere_revision = true` para revisión manual.
3. **Nunca** matchear solo por nombre — los morosos tienen muchos homónimos.

**Campos actualizables en match:**

- `direccion`, `comuna`, `ciudad`, `telefono`, `email`, `notas`.
- `updated_at` se actualiza.

**Campos NO actualizables en match** (solo edición manual):

- `etapa`, `estado` — el estudio clasifica, el Excel no manda eso.
- `created_at`.

### 8.3 Ver lista y buscar clientes

**Vista principal:** `/clientes`

- Tabla con: DNI, nombre, empresa(s), estado, etapa, monto total adeudado,
  cartera más reciente.
- Paginación: 50 por página.
- Búsqueda: input que matchea DNI (exacto o prefijo) o nombre (trigram).
- Filtros (sidebar o dropdowns):
  - Entidad
  - Cartera
  - Estado (vigente, vencido, prescrito, con acuerdo)
  - Etapa (extrajudicial, judicial)
  - Rango de días de mora
  - Rango de monto
- Export del listado filtrado a CSV (uso interno, no es el "export" de gestión).

### 8.4 Ver y editar cliente

**Vista:** `/clientes/{id}`

- **Header**: DNI, nombre, empresa(s), estado, etapa.
- **Sección "Deudas"**: tabla (monto, días de mora, estado, cartera, fecha origen).
- **Sección "Contacto"**: dirección, comuna, ciudad, teléfono, email.
- **Sección "Clasificación"**: estado, etapa, notas (editables).
- **Sección "Historial"**: últimas 20 entradas de auditoría.

**Edición:**

- Solo se editan los campos de clasificación y contacto (no DNI, no deudas).
- Cada cambio genera entrada en `auditoria_evento` con diff.
- Botón "Eliminar" → soft delete (confirma con typing del DNI).

### 8.5 Exportar Excel

**Input:** cartera, perfil de export (auto-sugerido por empresa), filtros opcionales.

**Proceso:**

1. Cargar `export_perfil.config_json` (versión activa).
2. Consultar deudas que matchean los filtros.
3. Generar Excel con la estructura del perfil:
   - Header en la fila configurada.
   - Mapeo de columnas a campos del modelo.
   - Formatos de fecha y número según el perfil.
4. Guardar el archivo en disco y servirlo al usuario.
5. Persistir `exportacion` + `auditoria_evento`.

**v1:** soportamos 1 perfil por empresa, cargado como JSON en la DB. Si la
empresa cambia el formato, bumpeamos `version` y agregamos el nuevo perfil.

### 8.6 Historial y auditoría

**Vista:** `/auditoria` (todos la ven en v1, no hay roles todavía).

- Tabla paginada: fecha, usuario, tipo, descripción corta, link al detalle.
- Filtros: tipo, fecha, usuario, empresa.
- Detalle del evento: JSON completo con payload.

## 9. Configuración de formatos (import/export)

Los perfiles viven en la DB (no hardcoded en código). Estructura del JSON para
import:

```json
{
  "headerRow": 1,
  "sheetName": null,
  "encoding": "UTF-8",
  "dateFormat": "dd/MM/yyyy",
  "numberFormat": "#,##0.00",
  "columns": {
    "dni": "A",
    "nombre": "C",
    "abogado": "B",
    "agencia": "K",
    "cuenta": "D",
    "numeroOperacion": "E",
    "situacion": "J",
    "moneda": "L",
    "busquedaBienes": "M",
    "montoCapital": "N",
    "montoTotal": "O",
    "montoDdo": "V",
    "tipoProceso": "P",
    "tipoJuzgado": "Q",
    "distritoJudicial": "R",
    "nroJuzgado": "S",
    "nroExpediente": "T"
  },
  "validations": {
    "rut": { "type": "rut-cl", "required": true },
    "montoOriginal": { "type": "decimal", "min": 0 }
  }
}
```

Para export, estructura similar pero con `outputColumns` y transformaciones.

**Decisión:** mantener el formato simple en v1 (letras de columna, sin
expresiones). Si una empresa tiene un caso raro, lo manejamos con código Java
custom registrado por reflexión en una segunda iteración.

## 10. Datos sensibles y compliance

- **No loggear PII**: Logback con pattern que enmascare DNI, RUC y email.
- **No commitear sample data real**: `.gitignore` excluye `data/`, `*.xls`,
  `*.xlsx`, `samples/`. Solo sample data sintético en repo.
- **Credenciales fuera del repo**: variables de entorno, nunca en `yml` ni
  en código. `.gitignore` excluye `.env`, `.env.*`, `application-local.yml`.
- **HTTPS obligatorio en prod**: provisto por Railway.
- **Passwords en la DB**: BCrypt (lo gestiona Spring Security).
- **Acceso a Supabase**: restringido al equipo del estudio vía Supabase
  dashboard + RLS si se configura. No exponer la service-role key.
- **Backups**: provistos por Supabase (point-in-time recovery). Documentar la
  política de retención en README.
- **Soft delete + auditoría**: datos de morosos tienen implicancias legales;
  nunca hard delete sin querer.

## 11. Riesgos

| ID  | Riesgo                                              | Probabilidad | Impacto | Mitigación |
|-----|-----------------------------------------------------|--------------|---------|------------|
| R1  | Exceles con formatos distintos por empresa          | Alta         | Alto    | Perfiles de import configurables en DB; primer import manual asistido |
| R2  | DNI con formato inconsistente (int vs str, espacios) | Alta        | Medio   | Normalización + validación de 8 dígitos; reporte de filas inválidas |
| R3  | Duplicados: mismo cliente en varias carteras        | Alta         | Medio   | Match por DNI único (no soft-deleted); el cliente es deudor, no de la cartera |
| R4  | Formato de export cambia sin aviso                  | Media        | Alto    | Versionado de perfiles; export anterior queda en `exportacion.archivo_nombre` |
| R5  | Datos personales en logs / commits                  | Media        | Alto    | Logback masking + .gitignore + code review checklist |
| R6  | Volumen > esperado (cartera gigante)                | Media        | Medio   | Índices + paginación; medir con cartera real antes de optimizar |
| R7  | Encoding raro en Excel (Latin1, Win-1252)           | Alta         | Bajo    | POI permite charset; perfiles tienen campo `encoding` |
| R8  | Excel con celdas combinadas, fórmulas, varias hojas | Alta         | Bajo    | Documentar en README qué NO soportamos; primer import detecta perfil roto |
| R9  | Edición concurrente (dos personas a la vez)         | Baja         | Medio   | `@Version` JPA → optimistic lock; UI muestra "editado por otro, recargá" |
| R10 | Data loss por bug en import                         | Baja         | Alto    | Import en transacción; rollback total si falla mid-flight; Excel original queda intacto |
| R11 | Deudas prescritas que se siguen cobrando            | Baja         | Crítico | Deuda `PRESCRITA` se bloquea para export; alerta visual al editar |

## 12. Plan de despliegue

> Definido por `PROJECT_CONTEXT.md`. Este plan lo respeta: no se modifica
> código para desplegar; solo se cambian variables de entorno.

### Ambientes

- **Desarrollo**: Spring Boot local + Supabase Cloud.
- **Producción**: Railway + Supabase Cloud.

### Variables de entorno (nunca en el repo)

- `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` (Supabase)
- `JWT_SECRET`
- `KMENTE_API_URL`, `KMENTE_API_KEY`
- Cualquier credencial o endpoint externo

### `.gitignore` obligatorio

- `.env`, `.env.*`
- `application-local.yml`
- `target/`, `*.log`, credenciales, archivos temporales

### Operación

- Deploy: commit a `main` (vía PR + review) → Railway redespliega.
- Logs:集中在 Railway.
- Monitoreo: `/actuator/health` desde Railway.
- Backups: provistos por Supabase (point-in-time recovery incluido en el plan
  cloud). Documentar la política de retención en README.

## 13. Decisiones tomadas (donde el plan original era ambiguo)

| Pregunta original                                | Decisión                                | Para revertir |
|--------------------------------------------------|-----------------------------------------|---------------|
| 1. Campo identificador                           | DNI normalizado como primary key        | Si la empresa no manda DNI, fallback a `(empresa, contrato_ref)` + flag `requiere_revision` |
| 2. Cliente con varias operaciones                | Modelo `cliente 1—N operacion`          | Un cliente puede tener operaciones con varias entidades |
| 3. Soft vs hard delete                           | **Soft delete**                         | Legal: nunca perder el rastro de un moroso |
| 4. Excel con errores                             | Procesar lo válido, aislar errores      | Reporte descargable; si > 50% falla, advertencia |
| 5. Versionar perfiles                            | Sí, con `version` en `import_perfil` / `export_perfil` | El activo se elige por `(entidad, activo=true)` |
| 6. Nivel de auditoría                            | JPA Auditing + tabla `auditoria_evento` | `created_at/updated_at/created_by/updated_by` por tabla; eventos de negocio en `auditoria_evento` |
| 7. Dónde corre                                   | Railway + Supabase Cloud                | Definido por `PROJECT_CONTEXT.md` |
| 8. Thymeleaf vs React                            | Thymeleaf                               | Reevaluar si la UI crece a 20+ pantallas |
| 9. H2 vs Postgres                                | Solo Postgres (Supabase)                | H2 no estaba en el stack aprobado |
| 10. Editar operaciones desde la UI               | No en MVP                               | Operaciones se crean en import; clasificación a nivel cliente (estado) |
| 11. Tipo de identificador deudor                 | DNI peruano de 8 dígitos                | Si vienen cédulas de otros países, extender validador |
| 12. Lombok / Envers / Flyway / H2 / Docker        | **No se usan**                          | No están en el stack aprobado ni son necesarios en MVP |

## 14. Definition of Done (DoD) para v1

Una feature se considera done cuando:

- [ ] Hay unit tests para la lógica de negocio (matching, normalización, validación).
- [ ] Hay al menos un integration test que cubre el flujo end-to-end
      (import → ver → editar → export).
- [ ] La UI funciona en el flujo principal y muestra mensajes de error claros.
- [ ] Los logs no contienen PII (verificado con grep).
- [ ] No hay secretos en el repo.
- [ ] El README tiene instrucciones de setup + uso.
- [ ] Hay sample data sintético para que el equipo del estudio pueda probar.
- [ ] Code review aprobado.

**v1 está "lista" cuando:**

- [ ] Las 6 features tienen DoD cumplida.
- [ ] Importamos 1 Excel real de la entidad A, editamos 5 clientes, exportamos
      y la entidad lo acepta sin pedir correcciones de formato.
- [ ] El sistema corre en la máquina del estudio con backup configurado.
- [ ] El equipo del estudio lo usa de forma autónoma sin pedir soporte crítico.

## 15. Próximos pasos

1. **Spike técnico**: agarrar un Excel real, parsearlo, validar identificadores,
   matchear contra una DB de prueba. Valida o invalida varias suposiciones del
   parser antes de comprometerse con código de producción.
2. **Spike de UX**: sentarse con alguien del estudio y ver cómo trabaja hoy.
   Confirmar el workflow asumido en §7.
3. **Setup del repo** con skeleton Spring Boot, CI básico, y conexión a
   Postgres/Supabase.
4. **Iteración 1**: import + match + listado básico. Primera versión usable;
   probablemente ya reemplaza parte del trabajo manual.
5. **Iteración 2**: edición + export (si entra en MVP).
6. **Iteración 3**: auditoría + pulido + deploy.

### Backlog tentativo v2 (no empezar antes de cerrar v1)

- Manejo de deuda PAGADA (cuando la entidad lo reporta).
- Cálculo automático de días de mora al abrir la vista.
- Acciones de cobranza (llamadas, cartas) y su historial por cliente.
- Acuerdos de pago (cuotas, seguimiento).
- Reportes / dashboard (BI simple con Metabase o similar).
- Multi-cartera simultánea en un mismo Excel.
