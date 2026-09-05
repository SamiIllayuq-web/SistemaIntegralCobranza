-- ============================================================
-- Schema completo - Sistema Integral de Cobranza
-- Todas las tablas sincronizadas con entities Java
-- Ultima actualizacion: based on Operacion entity with 67 columns
-- ============================================================

-- ── empresas ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS empresas (
    id          bigserial PRIMARY KEY,
    nombre      varchar(255) NOT NULL,
    ruc         varchar(50) UNIQUE,
    telefono    varchar(50),
    email       varchar(255),
    direccion   varchar(500),
    activo      boolean NOT NULL DEFAULT true,
    fecha_creacion timestamp
);

-- ── agencias ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS agencias (
    id          bigserial PRIMARY KEY,
    nombre      varchar(255) NOT NULL,
    codigo      varchar(50) UNIQUE,
    telefono    varchar(50),
    direccion   varchar(500),
    empresa_id  bigint NOT NULL,
    activo      boolean NOT NULL DEFAULT true,
    fecha_creacion timestamp,
    CONSTRAINT fk_agencia_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

-- ── clientes ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS clientes (
    id                  bigserial PRIMARY KEY,
    nombre_completo     varchar(255) NOT NULL,
    dni                 varchar(50) UNIQUE,
    telefono            varchar(50),
    telefono2           varchar(50),
    telefono3           varchar(50),
    direccion           varchar(500),
    email               varchar(255),
    activo              boolean NOT NULL DEFAULT true,
    deleted_at          timestamp,
    fecha_creacion      timestamp,
    fecha_actualizacion timestamp
);

-- ── operaciones ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS operaciones (
    id              bigserial PRIMARY KEY,
    cliente_id      bigint NOT NULL,
    empresa_id      bigint NOT NULL,
    agencia_id      bigint,
    abogado_id      bigint,
    cuenta          varchar(255) NOT NULL,
    numero_operacion varchar(255) NOT NULL,
    monto_capital   numeric(15,2),
    monto_total     numeric(15,2),
    dias_mora       integer,
    moneda          varchar(50),
    tipo_credito   varchar(100),
    situacion       varchar(100),
    estado          varchar(100),
    etapa           varchar(100),
    observacion     text,
    rango           varchar(50),
    analista        varchar(255),
    analista_senior varchar(255),

    -- Expediente / judicial
    numero_expediente    varchar(255),
    tipo_proceso         varchar(100),
    tipo_juzgado        varchar(100),
    distrito_judicial    varchar(255),
    numero_juzgado      varchar(50),
    trans               boolean,
    busqueda_bienes     boolean,
    monto_demandado     numeric(15,2),
    escribano_legal     varchar(500),
    codigo_exp_cautelar varchar(500),
    incidente           boolean,

    -- Fechas cuaderno principal
    fecha_presentacion           date,
    fecha_inadmisible_principal  date,
    fecha_admision_principal     date,
    fecha_audiencia_unica        date,
    fecha_auto_final             date,
    fecha_consentimiento         date,
    fecha_ejecutoriada          date,

    -- Fechas etapa ejecucion
    fecha_ingreso_ejecucion       date,
    fecha_tasacion                date,
    fecha_nombramiento_martillero date,
    fecha_remate_1                date,
    fecha_remate_2                date,
    fecha_remate_3                date,

    observacion_actos  text,
    comentario         text,

    -- Ubicacion geografica
    zona          varchar(100),
    departamento  varchar(100),
    provincia     varchar(100),
    distrito      varchar(100),

    -- Contacto deudor
    direccion     text,
    referencia    text,
    telefono      varchar(50),

    -- Montos
    monto_aprobado numeric(15,2),

    -- Estado cartera
    estado_cartera varchar(100),

    -- Desembolso
    fecha_desembolso     date,
    importe_desembolso   numeric(15,2),

    -- Etapa procesal
    etapa_procesal_texto varchar(255),
    acto_pendiente       text,

    -- Fechas estado proceso
    fecha_ultimo_estado_proceso date,
    fecha_aceptacion_demanda    date,
    fecha_envio_judicial        date,
    fecha_asignacion_abogado    date,
    fecha_castigo               date,

    tipo_fondo         varchar(100),

    activo             boolean NOT NULL DEFAULT true,
    fecha_creacion     timestamp,
    fecha_actualizacion timestamp,

    CONSTRAINT fk_operacion_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_operacion_empresa  FOREIGN KEY (empresa_id)  REFERENCES empresas(id),
    CONSTRAINT fk_operacion_agencia  FOREIGN KEY (agencia_id)  REFERENCES agencias(id),
    CONSTRAINT fk_operacion_abogado  FOREIGN KEY (abogado_id)  REFERENCES usuarios(id)
);

-- Unique constraint: no duplicados por empresa+cuenta+numeroOperacion
CREATE UNIQUE INDEX IF NOT EXISTS uk_operacion_empresa_cuenta_operacion
    ON operaciones(empresa_id, cuenta, numero_operacion);

-- ── bienes_embargados ──────────────────────────────────────
CREATE TABLE IF NOT EXISTS bienes_embargados (
    id                        bigserial PRIMARY KEY,
    operacion_id              bigint,
    expediente_id             bigint,  -- legacy, nullable
    tipo_bien                 varchar(100),
    partida_registral         varchar(100),
    detalle_garantia          text,
    direccion                 text,
    distrito                  varchar(100),
    provincia                 varchar(100),
    departamento             varchar(100),
    garantia_inscrita         varchar(100),
    fecha_inscripcion         date,
    fecha_presentacion_rrpp   date,
    asiento_inscripcion       text,
    fecha_presentacion_mc     date,
    fecha_inadmisible         date,
    fecha_admision           date,
    comentario_mc             text,
    detalle_acreedores        text,
    tipo_preferencia          varchar(100),
    titular_predio            text,
    fecha_generacion_mc       date,
    monto_mc                  numeric(15,2),
    moneda_mc                 varchar(10),
    rango                     varchar(50),
    CONSTRAINT fk_bien_operacion FOREIGN KEY (operacion_id) REFERENCES operaciones(id) ON DELETE SET NULL
);

-- ── gestiones ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS gestiones (
    id                  bigserial PRIMARY KEY,
    tipo                varchar(50) NOT NULL,
    fecha_gestion       timestamp NOT NULL,
    observaciones       text,
    monto_compromiso    numeric(15,2),
    fecha_compromiso    timestamp,
    cliente_id          bigint NOT NULL,
    usuario_registra    varchar(255),
    fecha_registro      timestamp,
    CONSTRAINT fk_gestion_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

-- ── importaciones ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS importaciones (
    id                  bigserial PRIMARY KEY,
    nombre_archivo      varchar(255) NOT NULL,
    total_registros     integer,
    registros_exitosos  integer,
    registros_fallidos  integer,
    empresa_id          bigint,
    agencia_id          bigint,
    estado              varchar(50) NOT NULL,
    usuario_importa     varchar(255),
    fecha_importacion   timestamp,
    errores             text
);

-- ── auditoria_eventos ──────────────────────────────────────
CREATE TABLE IF NOT EXISTS auditoria_eventos (
    id              bigserial PRIMARY KEY,
    usuario         varchar(255),
    tipo            varchar(50) NOT NULL,
    objeto_tipo     varchar(100),
    objeto_id       bigint,
    payload         text,
    descripcion     varchar(500),
    fecha_creacion  timestamp
);

-- ============================================================
-- PATCH: agregar columna operacion_id si no existe
-- (bienes_embargados puede tener expediente_id legacy)
-- ============================================================
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'bienes_embargados' AND column_name = 'operacion_id'
    ) THEN
        ALTER TABLE bienes_embargados ADD COLUMN operacion_id bigint;
        ALTER TABLE bienes_embargados ADD CONSTRAINT fk_bien_operacion
            FOREIGN KEY (operacion_id) REFERENCES operaciones(id) ON DELETE SET NULL;
    END IF;
END $$;

-- Patch: hacer expediente_id nullable (legacy)
ALTER TABLE bienes_embargados ALTER COLUMN expediente_id DROP NOT NULL;

-- Patch: eliminar columna empresa_id orphan de clientes si existe
-- (el modelo nuevo es Cliente -> Operacion -> Empresa)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'clientes' AND column_name = 'empresa_id'
    ) THEN
        ALTER TABLE clientes DROP COLUMN empresa_id;
    END IF;
END $$;
