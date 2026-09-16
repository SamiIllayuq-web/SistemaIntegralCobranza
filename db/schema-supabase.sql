-- ============================================================
-- Schema completo - Sistema Integral de Cobranza
-- Dump from Supabase (2026-09-15)
-- Tablas: agencias, usuarios, clientes, operaciones,
--         bienes_embargados, gestiones, importaciones,
--         actividad_sistema
-- ELIMINADAS: empresas, auditoria_eventos, reportes_mc
-- COLUMNAS ELIMINADAS de operaciones: zona, provincia,
--         analista, analista_senior, referencia
-- ============================================================

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- ── agencias ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS agencias (
    id              bigserial PRIMARY KEY,
    nombre          varchar(255) NOT NULL,
    codigo          varchar(50) UNIQUE,
    telefono        varchar(50),
    direccion       varchar(500),
    activo          boolean NOT NULL DEFAULT true,
    fecha_creacion  timestamp
);

-- ── usuarios ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS usuarios (
    id              bigserial PRIMARY KEY,
    username        varchar(100) NOT NULL UNIQUE,
    password        varchar(255) NOT NULL,
    nombre          varchar(255) NOT NULL,
    rol             varchar(50) NOT NULL,
    activo          boolean NOT NULL DEFAULT true,
    fecha_creacion  timestamp
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
    id                              bigserial PRIMARY KEY,
    cliente_id                      bigint NOT NULL,
    agencia_id                      bigint,
    abogado_id                      bigint,
    cuenta                          varchar(255) NOT NULL,
    numero_operacion               varchar(255) NOT NULL,
    monto_capital                   numeric(15,2),
    monto_total                     numeric(15,2),
    dias_mora                       integer,
    moneda                          varchar(50),
    tipo_credito                   varchar(100),
    situacion                       varchar(100),
    estado                          varchar(100),
    etapa                           varchar(100),
    observacion                     text,

    -- Expediente / judicial
    numero_expediente              varchar(255),
    tipo_proceso                   varchar(100),
    tipo_juzgado                   varchar(100),
    distrito_judicial               varchar(255),
    numero_juzgado                 varchar(50),
    trans                          varchar(10),
    busqueda_bienes                varchar(10),
    monto_demandado                numeric(15,2),
    escribano_legal                varchar(500),
    incidente                      boolean,

    -- Fechas cuaderno principal
    fecha_presentacion             date,
    fecha_inadmisible_principal    date,
    fecha_admision_principal       date,
    fecha_audiencia_unica          date,
    fecha_auto_final               date,
    fecha_consentimiento           date,
    fecha_ejecutoriada             date,

    -- Fechas etapa ejecucion
    fecha_ingreso_ejecucion        date,
    fecha_tasacion                  date,
    fecha_nombramiento_martillero  date,
    fecha_remate_1                  date,
    fecha_remate_2                  date,
    fecha_remate_3                  date,

    observacion_actos               text,
    comentario                     text,

    -- Ubicacion (solo departamento, NO distrito)
    departamento                   varchar(100),

    -- Monto aprobado
    monto_aprobado                 numeric(15,2),

    -- Estado cartera
    estado_cartera                 varchar(100),

    -- Co-titular / Aval
    co_titular_aval                text,

    -- Numero ficha registral
    numero_ficha_registral         text,

    -- Desembolso
    fecha_desembolso                date,
    importe_desembolso              numeric(15,2),

    -- Etapa procesal
    etapa_procesal                 varchar(100),

    -- Acto pendiente
    acto_pendiente                  text,

    -- Fechas estado proceso
    fecha_ultimo_estado_proceso    date,
    fecha_aceptacion_demanda       date,
    fecha_envio_judicial            date,
    fecha_asignacion_abogado        date,
    fecha_castigo                   date,

    tipo_fondo                      varchar(100),

    activo                         boolean NOT NULL DEFAULT true,
    fecha_creacion                 timestamp,
    fecha_actualizacion            timestamp,

    CONSTRAINT fk_operacion_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_operacion_agencia FOREIGN KEY (agencia_id)  REFERENCES agencias(id),
    CONSTRAINT fk_operacion_abogado FOREIGN KEY (abogado_id)  REFERENCES usuarios(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_operacion_cuenta_operacion
    ON operaciones(cuenta, numero_operacion);

-- ── bienes_embargados ──────────────────────────────────────
CREATE TABLE IF NOT EXISTS bienes_embargados (
    id                        bigserial PRIMARY KEY,
    operacion_id              bigint,
    expediente_id             bigint,
    codigo_exp_cautelar       varchar(500),
    tipo_bien                 varchar(100),
    numero_partida            varchar(100),
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
    fecha_presentacion_mc     text,
    fecha_inadmisible_mc      date,
    fecha_admision_mc         date,
    comentario_mc             text,
    detalle_acreedores        text,
    tipo_preferencia          varchar(100),
    titular_predio            text,
    fecha_generacion_mc       date,
    monto_mc                  numeric(15,2),
    moneda_mc                 varchar(10),
    rango                     varchar(50),
    CONSTRAINT fk_bien_operacion FOREIGN KEY (operacion_id)
        REFERENCES operaciones(id) ON DELETE SET NULL
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
    id                      bigserial PRIMARY KEY,
    nombre_archivo          varchar(255) NOT NULL,
    total_registros         integer,
    registros_exitosos      integer,
    registros_fallidos      integer,
    registros_creados       integer,
    registros_actualizados  integer,
    agencia_id               bigint,
    estado                  varchar(50) NOT NULL,
    usuario_importa         varchar(255),
    fecha_importacion       timestamp,
    errores                 text
);

-- ── actividad_sistema ───────────────────────────────────────
-- Reemplaza a auditoria_eventos
CREATE TABLE IF NOT EXISTS actividad_sistema (
    id              bigserial PRIMARY KEY,
    tipo            varchar(50) NOT NULL,
    fecha           timestamp NOT NULL DEFAULT now(),
    entidad_id      bigint,
    entidad_nombre  varchar(500),
    detalle         text,
    usuario         varchar(255)
);
