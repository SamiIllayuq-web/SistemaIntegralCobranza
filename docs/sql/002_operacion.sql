-- ================================================================
-- TABLA: operaciones
-- Fuente: grilling columnas Excel (A-BA)
-- Clave única: (cuenta, numero_operacion)
-- ================================================================

CREATE TABLE IF NOT EXISTS operaciones (
    id                          BIGSERIAL PRIMARY KEY,

    -- Datos de operación (columnas D, E del Excel)
    cuenta                      VARCHAR(100)    NOT NULL,
    numero_operacion            VARCHAR(100)    NOT NULL,

    -- Datos del cliente (columnas F, G — se relaciona con tabla clientes)
    cliente_id                  BIGINT         NOT NULL,
    CONSTRAINT fk_operacion_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),

    -- Agencia (columna K — FK a tabla agencias)
    agencia_id                  BIGINT,
    CONSTRAINT fk_operacion_agencia FOREIGN KEY (agencia_id) REFERENCES agencias(id),

    -- === DATOS DEL ABOGADO ===
    abogado_nombre              VARCHAR(255),

    -- === BANDERAS Y DATOS SIMPLES (columnas H, I, J, L, M) ===
    transferido                 VARCHAR(10),    -- SI / NO
    observaciones                TEXT,
    situacion                    VARCHAR(100),
    moneda                       VARCHAR(20),    -- S/. u otra

    -- === DEUDA (columnas N, O) ===
    deuda_cap                   DECIMAL(18,2),
    deuda_total                 DECIMAL(18,2),

    -- === DATOS JUDICIALES (columnas P-AB) ===
    tipo_proceso                VARCHAR(100),   -- ACCION PAULIANA, ODSD, MC/ODSD, etc.
    tipo_juzgado                VARCHAR(100),
    distrito_judicial           VARCHAR(100),
    numero_juzgado              VARCHAR(100),
    numero_expediente           VARCHAR(100),
    tiene_incidente             BOOLEAN,        -- SI -> true, NO -> false
    monto_demandado              DECIMAL(18,2),
    secretario_legal            VARCHAR(255),
    codigo_expediente_cautelar  VARCHAR(100),
    detalle_bien_embargado       TEXT,
    numero_partida              VARCHAR(100),
    tipo_bien_embargado         VARCHAR(50),    -- INMUEBLE, VEHICULO, NINGUNO
    rango                       VARCHAR(50),    -- PRIMER, SEGUNDO, TERCERO, NINGUNO

    -- === ACREEDORES Y PREFERENCIA (columnas AC, AD) ===
    detalle_acreedores          TEXT,
    tipo_preferente             VARCHAR(100),

    -- === MEDIDA CAUTELAR (columnas AE-AJ) ===
    monto_medida_cautelar       DECIMAL(18,2),
    moneda_mc                   VARCHAR(20),
    medida_cautelar_ejecutada   VARCHAR(50),    -- SI, NO, NINGUNO
    fecha_inscripcion_embargo    DATE,
    fecha_presentacion_titulo_rrpp DATE,
    asiento_inscripcion          VARCHAR(100),
    fecha_presentacion_mc       DATE,

    -- === ADMISIÓN E INADMISIÓN (columnas AK-AQ) ===
    fecha_inadmisible           DATE,
    fecha_admision              DATE,
    comentario                  TEXT,
    fecha_presentacion          DATE,
    fecha_inadmisible_2         DATE,
    fecha_admision_2            DATE,

    -- === AUDIENCIAS ===
    audiencia_tipo              VARCHAR(100),

    -- === RESOLUCIONES Y EJECUTORIA (columnas AS-AU) ===
    fecha_auto_final            DATE,
    fecha_ejecutoriada          DATE,
    fecha_nombramiento_peritos  DATE,
    fecha_nombramiento_martillero DATE,

    -- === REMATES (columnas AW-AY) ===
    fecha_remate_1              DATE,
    fecha_remate_2              DATE,
    fecha_remate_3              DATE,

    -- === SEGUIMIENTO (columnas AZ, BA) ===
    fecha_proximo_acto_procesal DATE,
    comentario_procesal         TEXT,

    -- === AUDITORÍA ===
    fecha_creacion              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- === RESTRICCIÓN DE UNICIDAD ===
    CONSTRAINT uk_operacion_cuenta_numero UNIQUE (cuenta, numero_operacion)
);

-- Índice para buscar operaciones por cliente
CREATE INDEX IF NOT EXISTS idx_operacion_cliente_id ON operaciones(cliente_id);

-- Índice para buscar por agencia
CREATE INDEX IF NOT EXISTS idx_operacion_agencia_id ON operaciones(agencia_id);

-- Índice para buscar por expediente judicial
CREATE INDEX IF NOT EXISTS idx_operacion_numero_expediente ON operaciones(numero_expediente);

-- Índice para buscar por situación
CREATE INDEX IF NOT EXISTS idx_operacion_situacion ON operaciones(situacion);

-- Trigger para actualizar fecha_actualizacion
CREATE OR REPLACE FUNCTION actualizar_fecha_actualizacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_operacion_actualizacion
    BEFORE UPDATE ON operaciones
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_actualizacion();
