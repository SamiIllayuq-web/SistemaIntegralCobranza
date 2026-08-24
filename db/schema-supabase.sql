--
-- PostgreSQL database dump
--

\restrict ldhVzT1vOjoEMbjqTtzAeIoNzcaAH9H2I9IT49wedelNyLFbMbyoCAU2SV2AOQ1

-- Dumped from database version 17.6
-- Dumped by pg_dump version 18.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA public;


--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: agencias; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.agencias (
    id bigint NOT NULL,
    activo boolean NOT NULL,
    codigo character varying(255),
    direccion character varying(255),
    fecha_creacion timestamp(6) without time zone,
    nombre character varying(255) NOT NULL,
    telefono character varying(255),
    empresa_id bigint NOT NULL
);


--
-- Name: agencias_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.agencias_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: agencias_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.agencias_id_seq OWNED BY public.agencias.id;


--
-- Name: auditoria_eventos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.auditoria_eventos (
    id bigint NOT NULL,
    descripcion character varying(255),
    fecha_creacion timestamp(6) without time zone,
    objeto_id bigint,
    objeto_tipo character varying(255),
    payload text,
    tipo character varying(255) NOT NULL,
    usuario character varying(255)
);


--
-- Name: auditoria_eventos_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.auditoria_eventos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: auditoria_eventos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.auditoria_eventos_id_seq OWNED BY public.auditoria_eventos.id;


--
-- Name: bienes_embargados; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.bienes_embargados (
    id bigint NOT NULL,
    asiento_inscripcion text,
    comentario_mc text,
    departamento character varying(255),
    detalle_acreedores text,
    detalle_garantia text,
    direccion text,
    distrito character varying(255),
    fecha_admision date,
    fecha_generacion_mc date,
    fecha_inadmisible date,
    fecha_inscripcion date,
    fecha_presentacion_mc date,
    fecha_presentacion_rrpp date,
    garantia_inscrita character varying(255),
    moneda_mc character varying(10),
    monto_mc numeric(15,2),
    partida_registral character varying(255),
    provincia character varying(255),
    rango character varying(50),
    tipo_bien character varying(255),
    tipo_preferencia character varying(255),
    titular_predio text,
    expediente_id bigint,
    operacion_id bigint
);


--
-- Name: bienes_embargados_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.bienes_embargados_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bienes_embargados_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.bienes_embargados_id_seq OWNED BY public.bienes_embargados.id;


--
-- Name: clientes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.clientes (
    id bigint NOT NULL,
    activo boolean NOT NULL,
    deleted_at timestamp(6) without time zone,
    direccion character varying(255),
    dni character varying(255),
    email character varying(255),
    fecha_actualizacion timestamp(6) without time zone,
    fecha_creacion timestamp(6) without time zone,
    nombre_completo character varying(255) NOT NULL,
    telefono character varying(255),
    telefono2 character varying(255),
    telefono3 character varying(255)
);


--
-- Name: clientes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.clientes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: clientes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.clientes_id_seq OWNED BY public.clientes.id;


--
-- Name: empresas; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.empresas (
    id bigint NOT NULL,
    activo boolean NOT NULL,
    direccion character varying(255),
    email character varying(255),
    fecha_creacion timestamp(6) without time zone,
    nombre character varying(255) NOT NULL,
    ruc character varying(255),
    telefono character varying(255)
);


--
-- Name: empresas_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.empresas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: empresas_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.empresas_id_seq OWNED BY public.empresas.id;


--
-- Name: expedientes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.expedientes (
    id bigint NOT NULL,
    activo boolean,
    codigo_exp_cautelar character varying(500),
    comentario_general text,
    distrito_judicial character varying(200),
    escribano_legal character varying(500),
    especialista_legal character varying(300),
    etapa_procesal character varying(100),
    expediente_cautelar_codigo character varying(500),
    fecha_actualizacion timestamp(6) without time zone,
    fecha_admision_principal date,
    fecha_audiencia_unica date,
    fecha_auto_final date,
    fecha_consentimiento date,
    fecha_creacion timestamp(6) without time zone,
    fecha_ejecutoriada date,
    fecha_inadmisible_principal date,
    fecha_ingreso_ejecucion date,
    fecha_nombramiento_martillero date,
    fecha_presentacion date,
    fecha_remate_1 date,
    fecha_remate_2 date,
    fecha_remate_3 date,
    fecha_tasacion date,
    incidente boolean,
    monto_demandado numeric(15,2),
    numero_expediente character varying(255),
    numero_juzgado character varying(50),
    observacion text,
    observacion_actos text,
    situacion character varying(100),
    tipo_juzgado character varying(100),
    tipo_proceso character varying(100),
    abogado_id bigint,
    agencia_id bigint,
    empresa_id bigint,
    operacion_id bigint
);


--
-- Name: expedientes_clientes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.expedientes_clientes (
    id bigint NOT NULL,
    busqueda_bienes text,
    co_titular_aval character varying(255),
    cuenta character varying(255),
    deuda_capital numeric(15,2),
    deuda_total numeric(15,2),
    dni character varying(255),
    moneda character varying(255),
    nombre_completo text NOT NULL,
    observacion text,
    operacion character varying(255),
    tipo character varying(255) NOT NULL,
    trans character varying(255),
    cliente_id bigint,
    expediente_id bigint NOT NULL
);


--
-- Name: expedientes_clientes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.expedientes_clientes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: expedientes_clientes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.expedientes_clientes_id_seq OWNED BY public.expedientes_clientes.id;


--
-- Name: expedientes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.expedientes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: expedientes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.expedientes_id_seq OWNED BY public.expedientes.id;


--
-- Name: gestiones; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.gestiones (
    id bigint NOT NULL,
    fecha_compromiso timestamp(6) without time zone,
    fecha_gestion timestamp(6) without time zone NOT NULL,
    fecha_registro timestamp(6) without time zone,
    monto_compromiso numeric(15,2),
    observaciones text,
    tipo character varying(255) NOT NULL,
    usuario_registra character varying(255),
    cliente_id bigint NOT NULL,
    CONSTRAINT gestiones_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['LLAMADA'::character varying, 'VISITA'::character varying, 'OBSERVACION'::character varying, 'COMPROMISO_PAGO'::character varying])::text[])))
);


--
-- Name: gestiones_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.gestiones_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: gestiones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.gestiones_id_seq OWNED BY public.gestiones.id;


--
-- Name: gestiones_procesales; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.gestiones_procesales (
    id bigint NOT NULL,
    etapa character varying(255),
    fecha date,
    fecha_registro timestamp(6) without time zone,
    observacion text,
    tipo_gestion character varying(255) NOT NULL,
    expediente_id bigint NOT NULL
);


--
-- Name: gestiones_procesales_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.gestiones_procesales_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: gestiones_procesales_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.gestiones_procesales_id_seq OWNED BY public.gestiones_procesales.id;


--
-- Name: importaciones; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.importaciones (
    id bigint NOT NULL,
    agencia_id bigint,
    empresa_id bigint,
    errores text,
    estado character varying(255) NOT NULL,
    fecha_importacion timestamp(6) without time zone,
    nombre_archivo character varying(255) NOT NULL,
    registros_exitosos integer,
    registros_fallidos integer,
    total_registros integer,
    usuario_importa character varying(255)
);


--
-- Name: importaciones_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.importaciones_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: importaciones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.importaciones_id_seq OWNED BY public.importaciones.id;


--
-- Name: operaciones; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.operaciones (
    id bigint NOT NULL,
    activo boolean NOT NULL,
    acto_pendiente text,
    analista character varying(255),
    analista_senior character varying(255),
    busqueda_bienes boolean,
    codigo_exp_cautelar character varying(500),
    comentario text,
    cuenta character varying(255) NOT NULL,
    departamento character varying(255),
    dias_mora integer,
    direccion text,
    distrito character varying(255),
    distrito_judicial character varying(255),
    escribano_legal character varying(500),
    estado character varying(255),
    estado_cartera character varying(255),
    etapa character varying(255),
    etapa_procesal_texto character varying(255),
    fecha_aceptacion_demanda date,
    fecha_actualizacion timestamp(6) without time zone,
    fecha_admision_principal date,
    fecha_asignacion_abogado date,
    fecha_audiencia_unica date,
    fecha_auto_final date,
    fecha_castigo date,
    fecha_consentimiento date,
    fecha_creacion timestamp(6) without time zone,
    fecha_desembolso date,
    fecha_ejecutoriada date,
    fecha_envio_judicial date,
    fecha_inadmisible_principal date,
    fecha_ingreso_ejecucion date,
    fecha_nombramiento_martillero date,
    fecha_presentacion date,
    fecha_remate_1 date,
    fecha_remate_2 date,
    fecha_remate_3 date,
    fecha_tasacion date,
    fecha_ultimo_estado_proceso date,
    importe_desembolso numeric(15,2),
    incidente boolean,
    moneda character varying(255),
    monto_aprobado numeric(15,2),
    monto_capital numeric(15,2),
    monto_demandado numeric(15,2),
    monto_total numeric(15,2),
    numero_expediente character varying(255),
    numero_juzgado character varying(255),
    numero_operacion character varying(255) NOT NULL,
    observacion text,
    observacion_actos text,
    provincia character varying(255),
    rango character varying(255),
    referencia text,
    situacion character varying(255),
    telefono character varying(255),
    tipo_credito character varying(255),
    tipo_fondo character varying(255),
    tipo_juzgado character varying(255),
    tipo_proceso character varying(255),
    trans boolean,
    zona character varying(255),
    abogado_id bigint,
    agencia_id bigint,
    cliente_id bigint NOT NULL,
    empresa_id bigint NOT NULL
);


--
-- Name: operaciones_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.operaciones_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: operaciones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.operaciones_id_seq OWNED BY public.operaciones.id;


--
-- Name: reportes_mc; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.reportes_mc (
    id bigint NOT NULL,
    anio integer,
    fecha_generacion timestamp(6) without time zone,
    mes text,
    nombre_archivo text,
    empresa_id bigint NOT NULL,
    generado_por bigint
);


--
-- Name: reportes_mc_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.reportes_mc_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: reportes_mc_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.reportes_mc_id_seq OWNED BY public.reportes_mc.id;


--
-- Name: usuarios; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.usuarios (
    id bigint NOT NULL,
    activo boolean NOT NULL,
    fecha_creacion timestamp(6) without time zone,
    nombre_completo character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    rol character varying(255) NOT NULL,
    username character varying(255) NOT NULL
);


--
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.usuarios_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.usuarios_id_seq OWNED BY public.usuarios.id;


--
-- Name: agencias id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agencias ALTER COLUMN id SET DEFAULT nextval('public.agencias_id_seq'::regclass);


--
-- Name: auditoria_eventos id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.auditoria_eventos ALTER COLUMN id SET DEFAULT nextval('public.auditoria_eventos_id_seq'::regclass);


--
-- Name: bienes_embargados id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bienes_embargados ALTER COLUMN id SET DEFAULT nextval('public.bienes_embargados_id_seq'::regclass);


--
-- Name: clientes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clientes ALTER COLUMN id SET DEFAULT nextval('public.clientes_id_seq'::regclass);


--
-- Name: empresas id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.empresas ALTER COLUMN id SET DEFAULT nextval('public.empresas_id_seq'::regclass);


--
-- Name: expedientes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.expedientes ALTER COLUMN id SET DEFAULT nextval('public.expedientes_id_seq'::regclass);


--
-- Name: expedientes_clientes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.expedientes_clientes ALTER COLUMN id SET DEFAULT nextval('public.expedientes_clientes_id_seq'::regclass);


--
-- Name: gestiones id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gestiones ALTER COLUMN id SET DEFAULT nextval('public.gestiones_id_seq'::regclass);


--
-- Name: gestiones_procesales id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gestiones_procesales ALTER COLUMN id SET DEFAULT nextval('public.gestiones_procesales_id_seq'::regclass);


--
-- Name: importaciones id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.importaciones ALTER COLUMN id SET DEFAULT nextval('public.importaciones_id_seq'::regclass);


--
-- Name: operaciones id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.operaciones ALTER COLUMN id SET DEFAULT nextval('public.operaciones_id_seq'::regclass);


--
-- Name: reportes_mc id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reportes_mc ALTER COLUMN id SET DEFAULT nextval('public.reportes_mc_id_seq'::regclass);


--
-- Name: usuarios id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuarios ALTER COLUMN id SET DEFAULT nextval('public.usuarios_id_seq'::regclass);


--
-- Name: agencias agencias_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agencias
    ADD CONSTRAINT agencias_pkey PRIMARY KEY (id);


--
-- Name: auditoria_eventos auditoria_eventos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.auditoria_eventos
    ADD CONSTRAINT auditoria_eventos_pkey PRIMARY KEY (id);


--
-- Name: bienes_embargados bienes_embargados_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bienes_embargados
    ADD CONSTRAINT bienes_embargados_pkey PRIMARY KEY (id);


--
-- Name: clientes clientes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clientes
    ADD CONSTRAINT clientes_pkey PRIMARY KEY (id);


--
-- Name: empresas empresas_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.empresas
    ADD CONSTRAINT empresas_pkey PRIMARY KEY (id);


--
-- Name: expedientes_clientes expedientes_clientes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.expedientes_clientes
    ADD CONSTRAINT expedientes_clientes_pkey PRIMARY KEY (id);


--
-- Name: expedientes expedientes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.expedientes
    ADD CONSTRAINT expedientes_pkey PRIMARY KEY (id);


--
-- Name: gestiones gestiones_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gestiones
    ADD CONSTRAINT gestiones_pkey PRIMARY KEY (id);


--
-- Name: gestiones_procesales gestiones_procesales_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gestiones_procesales
    ADD CONSTRAINT gestiones_procesales_pkey PRIMARY KEY (id);


--
-- Name: importaciones importaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.importaciones
    ADD CONSTRAINT importaciones_pkey PRIMARY KEY (id);


--
-- Name: operaciones operaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.operaciones
    ADD CONSTRAINT operaciones_pkey PRIMARY KEY (id);


--
-- Name: reportes_mc reportes_mc_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reportes_mc
    ADD CONSTRAINT reportes_mc_pkey PRIMARY KEY (id);


--
-- Name: agencias uk_2nlmett9qj966ir7mjsn78su0; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agencias
    ADD CONSTRAINT uk_2nlmett9qj966ir7mjsn78su0 UNIQUE (codigo);


--
-- Name: empresas uk_d6avi1g5t06l7qo67kj8ty2j2; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.empresas
    ADD CONSTRAINT uk_d6avi1g5t06l7qo67kj8ty2j2 UNIQUE (ruc);


--
-- Name: expedientes uk_jmenm14elibkvd22se6o0eh02; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.expedientes
    ADD CONSTRAINT uk_jmenm14elibkvd22se6o0eh02 UNIQUE (operacion_id);


--
-- Name: usuarios uk_m2dvbwfge291euvmk6vkkocao; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT uk_m2dvbwfge291euvmk6vkkocao UNIQUE (username);


--
-- Name: clientes uk_m6ysdwsqke00e5piajbvgn6lg; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clientes
    ADD CONSTRAINT uk_m6ysdwsqke00e5piajbvgn6lg UNIQUE (dni);


--
-- Name: operaciones uk_operacion_empresa_cuenta_operacion; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.operaciones
    ADD CONSTRAINT uk_operacion_empresa_cuenta_operacion UNIQUE (empresa_id, cuenta, numero_operacion);


--
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- Name: expedientes fk1hxge799anqm4oxumwcekt7ph; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.expedientes
    ADD CONSTRAINT fk1hxge799anqm4oxumwcekt7ph FOREIGN KEY (operacion_id) REFERENCES public.operaciones(id);


--
-- Name: gestiones fk46vjmnvkbr5foirik1qa1jj1w; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gestiones
    ADD CONSTRAINT fk46vjmnvkbr5foirik1qa1jj1w FOREIGN KEY (cliente_id) REFERENCES public.clientes(id);


--
-- Name: operaciones fk4irmr1w522hdpna1h0vm51smh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.operaciones
    ADD CONSTRAINT fk4irmr1w522hdpna1h0vm51smh FOREIGN KEY (abogado_id) REFERENCES public.usuarios(id);


--
-- Name: bienes_embargados fk7vrey65gnsrcu5pm9294iq36a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bienes_embargados
    ADD CONSTRAINT fk7vrey65gnsrcu5pm9294iq36a FOREIGN KEY (expediente_id) REFERENCES public.expedientes(id);


--
-- Name: operaciones fkb6gxjyup7vqpyb9remiur52ai; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.operaciones
    ADD CONSTRAINT fkb6gxjyup7vqpyb9remiur52ai FOREIGN KEY (agencia_id) REFERENCES public.agencias(id);


--
-- Name: agencias fkd6d103tcrigjfdbni5hs4i2xr; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.agencias
    ADD CONSTRAINT fkd6d103tcrigjfdbni5hs4i2xr FOREIGN KEY (empresa_id) REFERENCES public.empresas(id);


--
-- Name: expedientes fkdd68iu2pai6lvpmdbg5n64maf; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.expedientes
    ADD CONSTRAINT fkdd68iu2pai6lvpmdbg5n64maf FOREIGN KEY (empresa_id) REFERENCES public.empresas(id);


--
-- Name: gestiones_procesales fkgen5d9ewu9m5cc7p77uutqug0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gestiones_procesales
    ADD CONSTRAINT fkgen5d9ewu9m5cc7p77uutqug0 FOREIGN KEY (expediente_id) REFERENCES public.expedientes(id);


--
-- Name: reportes_mc fkhjmhd10ieo24is4ho1y5191xk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reportes_mc
    ADD CONSTRAINT fkhjmhd10ieo24is4ho1y5191xk FOREIGN KEY (empresa_id) REFERENCES public.empresas(id);


--
-- Name: expedientes_clientes fkhu3pty7biwa6t1e0yh2dqe0fb; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.expedientes_clientes
    ADD CONSTRAINT fkhu3pty7biwa6t1e0yh2dqe0fb FOREIGN KEY (expediente_id) REFERENCES public.expedientes(id);


--
-- Name: operaciones fkjx1kcx7qbms7ymm57uopmm11m; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.operaciones
    ADD CONSTRAINT fkjx1kcx7qbms7ymm57uopmm11m FOREIGN KEY (empresa_id) REFERENCES public.empresas(id);


--
-- Name: expedientes fkkukaxu4ptx5aurgoltv5x7uew; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.expedientes
    ADD CONSTRAINT fkkukaxu4ptx5aurgoltv5x7uew FOREIGN KEY (abogado_id) REFERENCES public.usuarios(id);


--
-- Name: reportes_mc fkledg7ng4cnh3gtdmqa51u8ynj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reportes_mc
    ADD CONSTRAINT fkledg7ng4cnh3gtdmqa51u8ynj FOREIGN KEY (generado_por) REFERENCES public.usuarios(id);


--
-- Name: expedientes_clientes fkmle6ckx5qvgv2bmqj8v2yrlx8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.expedientes_clientes
    ADD CONSTRAINT fkmle6ckx5qvgv2bmqj8v2yrlx8 FOREIGN KEY (cliente_id) REFERENCES public.clientes(id);


--
-- Name: operaciones fkodvr2exlnrn3x0sdevhw4hwd3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.operaciones
    ADD CONSTRAINT fkodvr2exlnrn3x0sdevhw4hwd3 FOREIGN KEY (cliente_id) REFERENCES public.clientes(id);


--
-- Name: expedientes fks39slfum7qwa2xqgkspvi804; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.expedientes
    ADD CONSTRAINT fks39slfum7qwa2xqgkspvi804 FOREIGN KEY (agencia_id) REFERENCES public.agencias(id);


--
-- Name: bienes_embargados fkxahtoninhlig9jqrcqxkyis; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bienes_embargados
    ADD CONSTRAINT fkxahtoninhlig9jqrcqxkyis FOREIGN KEY (operacion_id) REFERENCES public.operaciones(id);


--
-- PostgreSQL database dump complete
--

\unrestrict ldhVzT1vOjoEMbjqTtzAeIoNzcaAH9H2I9IT49wedelNyLFbMbyoCAU2SV2AOQ1

