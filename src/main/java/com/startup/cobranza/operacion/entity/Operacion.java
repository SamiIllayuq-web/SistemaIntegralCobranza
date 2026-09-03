package com.startup.cobranza.operacion.entity;

import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.empresa.entity.Empresa;
import com.startup.cobranza.operacion.entity.BienEmbargado;
import com.startup.cobranza.usuario.entity.Usuario;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "operaciones",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"empresa_id", "cuenta", "numero_operacion"},
           name = "uk_operacion_empresa_cuenta_operacion"))
public class Operacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private com.startup.cobranza.cliente.entity.Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agencia_id")
    private Agencia agencia;

    @Column(name = "cuenta", nullable = false)
    private String cuenta;

    @Column(name = "numero_operacion", nullable = false)
    private String numeroOperacion;

    @Column(name = "monto_capital", precision = 15, scale = 2)
    private BigDecimal montoCapital;

    @Column(name = "monto_total", precision = 15, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "dias_mora")
    private Integer diasMora;

    @Column(name = "moneda")
    private String moneda;

    @Column(name = "tipo_credito")
    private String tipoCredito;

    @Column(name = "situacion")
    private String situacion;

    @Column(name = "estado")
    private String estado;

    @Column(name = "etapa")
    private String etapa;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "rango")
    private String rango;

    @Column(name = "analista")
    private String analista;

    @Column(name = "analista_senior")
    private String analistaSenior;

    @Column(name = "numero_expediente")
    private String numeroExpediente;

    @Column(name = "tipo_proceso")
    private String tipoProceso;

    @Column(name = "tipo_juzgado")
    private String tipoJuzgado;

    @Column(name = "distrito_judicial")
    private String distritoJudicial;

    @Column(name = "numero_juzgado")
    private String numeroJuzgado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "abogado_id")
    private Usuario abogado;

    // ─── Datos del proceso judicial ───────────────────────────────────

    @Column(name = "trans")
    private Boolean trans;

    @Column(name = "busqueda_bienes")
    private Boolean busquedaBienes;

    @Column(name = "monto_demandado", precision = 15, scale = 2)
    private BigDecimal montoDemandado;

    @Column(name = "escribano_legal", length = 500)
    private String escribanoLegal;

    @Column(name = "codigo_exp_cautelar", length = 500)
    private String codigoExpCautelar;

    @Column(name = "incidente")
    private Boolean incidente;

    // ─── Fechas del cuaderno principal ────────────────────────────────

    @Column(name = "fecha_presentacion")
    private LocalDate fechaPresentacion;

    @Column(name = "fecha_inadmisible_principal")
    private LocalDate fechaInadmisiblePrincipal;

    @Column(name = "fecha_admision_principal")
    private LocalDate fechaAdmisionPrincipal;

    @Column(name = "fecha_audiencia_unica")
    private LocalDate fechaAudienciaUnica;

    @Column(name = "fecha_auto_final")
    private LocalDate fechaAutoFinal;

    @Column(name = "fecha_consentimiento")
    private LocalDate fechaConsentimiento;

    @Column(name = "fecha_ejecutoriada")
    private LocalDate fechaEjecutoriada;

    // ─── Etapa de ejecución ───────────────────────────────────────────

    @Column(name = "fecha_ingreso_ejecucion")
    private LocalDate fechaIngresoEjecucion;

    @Column(name = "fecha_tasacion")
    private LocalDate fechaTasacion;

    @Column(name = "fecha_nombramiento_martillero")
    private LocalDate fechaNombramientoMartillero;

    @Column(name = "fecha_remate_1")
    private LocalDate fechaRemate1;

    @Column(name = "fecha_remate_2")
    private LocalDate fechaRemate2;

    @Column(name = "fecha_remate_3")
    private LocalDate fechaRemate3;

    @Column(name = "observacion_actos", columnDefinition = "TEXT")
    private String observacionActos;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    // ─── Ubicación geográfica ─────────────────────────────────────────
    @Column(name = "zona")
    private String zona;

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "provincia")
    private String provincia;

    @Column(name = "distrito")
    private String distrito;

    // ─── Datos de contacto del deudor ─────────────────────────────────
    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;

    @Column(name = "referencia", columnDefinition = "TEXT")
    private String referencia;

    @Column(name = "telefono")
    private String telefono;

    // ─── Monto aprobado ───────────────────────────────────────────────
    @Column(name = "monto_aprobado", precision = 15, scale = 2)
    private BigDecimal montoAprobado;

    // ─── Estado de cartera (5 estados) ─────────────────────────────────
    @Column(name = "estado_cartera")
    private String estadoCartera;

    // ─── Datos del desembolso ──────────────────────────────────────────
    @Column(name = "fecha_desembolso")
    private LocalDate fechaDesembolso;

    @Column(name = "importe_desembolso", precision = 15, scale = 2)
    private BigDecimal importeDesembolso;

    // ─── Etapa procesal (texto libre para nuevos valores) ─────────────
    @Column(name = "etapa_procesal_texto")
    private String etapaProcesalTexto;

    // ─── Acto pendiente ───────────────────────────────────────────────
    @Column(name = "acto_pendiente", columnDefinition = "TEXT")
    private String actoPendiente;

    // ─── Fecha último estado proceso ──────────────────────────────────
    @Column(name = "fecha_ultimo_estado_proceso")
    private LocalDate fechaUltimoEstadoProceso;

    // ─── Fechas judiciales ─────────────────────────────────────────────
    @Column(name = "fecha_aceptacion_demanda")
    private LocalDate fechaAceptacionDemanda;

    @Column(name = "fecha_envio_judicial")
    private LocalDate fechaEnvioJudicial;

    @Column(name = "fecha_asignacion_abogado")
    private LocalDate fechaAsignacionAbogado;

    @Column(name = "fecha_castigo")
    private LocalDate fechaCastigo;

    // ─── Tipo fondo ────────────────────────────────────────────────────
    @Column(name = "tipo_fondo")
    private String tipoFondo;

    @OneToMany(mappedBy = "operacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BienEmbargado> bienesEmbargados = new ArrayList<>();

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Operacion() {}

    public Operacion(Long id, com.startup.cobranza.cliente.entity.Cliente cliente, Empresa empresa,
                     Agencia agencia, String cuenta, String numeroOperacion,
                     BigDecimal montoCapital, BigDecimal montoTotal, Integer diasMora,
                     String moneda, String tipoCredito, String situacion, String estado,
                     String etapa, String observacion, String rango, String analista,
                     String analistaSenior, String numeroExpediente, String tipoProceso,
                     String tipoJuzgado, String distritoJudicial, String numeroJuzgado,
                     Usuario abogado, Boolean trans, Boolean busquedaBienes,
                     BigDecimal montoDemandado, String escribanoLegal,
                     String codigoExpCautelar, Boolean incidente,
                     LocalDate fechaPresentacion, LocalDate fechaInadmisiblePrincipal,
                     LocalDate fechaAdmisionPrincipal, LocalDate fechaAudienciaUnica,
                     LocalDate fechaAutoFinal, LocalDate fechaConsentimiento,
                     LocalDate fechaEjecutoriada, LocalDate fechaIngresoEjecucion,
                     LocalDate fechaTasacion, LocalDate fechaNombramientoMartillero,
                     LocalDate fechaRemate1, LocalDate fechaRemate2, LocalDate fechaRemate3,
                     String observacionActos, String comentario,
                     String estadoCartera, LocalDate fechaDesembolso,
                     BigDecimal importeDesembolso, String etapaProcesalTexto,
                     String actoPendiente, LocalDate fechaUltimoEstadoProceso,
                     List<BienEmbargado> bienesEmbargados, Boolean activo,
                     LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.cliente = cliente;
        this.empresa = empresa;
        this.agencia = agencia;
        this.cuenta = cuenta;
        this.numeroOperacion = numeroOperacion;
        this.montoCapital = montoCapital;
        this.montoTotal = montoTotal;
        this.diasMora = diasMora;
        this.moneda = moneda;
        this.tipoCredito = tipoCredito;
        this.situacion = situacion;
        this.estado = estado;
        this.etapa = etapa;
        this.observacion = observacion;
        this.rango = rango;
        this.analista = analista;
        this.analistaSenior = analistaSenior;
        this.numeroExpediente = numeroExpediente;
        this.tipoProceso = tipoProceso;
        this.tipoJuzgado = tipoJuzgado;
        this.distritoJudicial = distritoJudicial;
        this.numeroJuzgado = numeroJuzgado;
        this.abogado = abogado;
        this.trans = trans;
        this.busquedaBienes = busquedaBienes;
        this.montoDemandado = montoDemandado;
        this.escribanoLegal = escribanoLegal;
        this.codigoExpCautelar = codigoExpCautelar;
        this.incidente = incidente;
        this.fechaPresentacion = fechaPresentacion;
        this.fechaInadmisiblePrincipal = fechaInadmisiblePrincipal;
        this.fechaAdmisionPrincipal = fechaAdmisionPrincipal;
        this.fechaAudienciaUnica = fechaAudienciaUnica;
        this.fechaAutoFinal = fechaAutoFinal;
        this.fechaConsentimiento = fechaConsentimiento;
        this.fechaEjecutoriada = fechaEjecutoriada;
        this.fechaIngresoEjecucion = fechaIngresoEjecucion;
        this.fechaTasacion = fechaTasacion;
        this.fechaNombramientoMartillero = fechaNombramientoMartillero;
        this.fechaRemate1 = fechaRemate1;
        this.fechaRemate2 = fechaRemate2;
        this.fechaRemate3 = fechaRemate3;
        this.observacionActos = observacionActos;
        this.comentario = comentario;
        this.estadoCartera = estadoCartera;
        this.fechaDesembolso = fechaDesembolso;
        this.importeDesembolso = importeDesembolso;
        this.etapaProcesalTexto = etapaProcesalTexto;
        this.actoPendiente = actoPendiente;
        this.fechaUltimoEstadoProceso = fechaUltimoEstadoProceso;
        this.bienesEmbargados = bienesEmbargados;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public com.startup.cobranza.cliente.entity.Cliente getCliente() { return cliente; }
    public void setCliente(com.startup.cobranza.cliente.entity.Cliente cliente) { this.cliente = cliente; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    public Agencia getAgencia() { return agencia; }
    public void setAgencia(Agencia agencia) { this.agencia = agencia; }
    public String getCuenta() { return cuenta; }
    public void setCuenta(String cuenta) { this.cuenta = cuenta; }
    public String getNumeroOperacion() { return numeroOperacion; }
    public void setNumeroOperacion(String numeroOperacion) { this.numeroOperacion = numeroOperacion; }
    public BigDecimal getMontoCapital() { return montoCapital; }
    public void setMontoCapital(BigDecimal montoCapital) { this.montoCapital = montoCapital; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public Integer getDiasMora() { return diasMora; }
    public void setDiasMora(Integer diasMora) { this.diasMora = diasMora; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public String getTipoCredito() { return tipoCredito; }
    public void setTipoCredito(String tipoCredito) { this.tipoCredito = tipoCredito; }
    public String getSituacion() { return situacion; }
    public void setSituacion(String situacion) { this.situacion = situacion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getEtapa() { return etapa; }
    public void setEtapa(String etapa) { this.etapa = etapa; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public String getRango() { return rango; }
    public void setRango(String rango) { this.rango = rango; }
    public String getAnalista() { return analista; }
    public void setAnalista(String analista) { this.analista = analista; }
    public String getAnalistaSenior() { return analistaSenior; }
    public void setAnalistaSenior(String analistaSenior) { this.analistaSenior = analistaSenior; }
    public String getNumeroExpediente() { return numeroExpediente; }
    public void setNumeroExpediente(String numeroExpediente) { this.numeroExpediente = numeroExpediente; }
    public String getTipoProceso() { return tipoProceso; }
    public void setTipoProceso(String tipoProceso) { this.tipoProceso = tipoProceso; }
    public String getTipoJuzgado() { return tipoJuzgado; }
    public void setTipoJuzgado(String tipoJuzgado) { this.tipoJuzgado = tipoJuzgado; }
    public String getDistritoJudicial() { return distritoJudicial; }
    public void setDistritoJudicial(String distritoJudicial) { this.distritoJudicial = distritoJudicial; }
    public String getNumeroJuzgado() { return numeroJuzgado; }
    public void setNumeroJuzgado(String numeroJuzgado) { this.numeroJuzgado = numeroJuzgado; }
    public Usuario getAbogado() { return abogado; }
    public void setAbogado(Usuario abogado) { this.abogado = abogado; }
    public List<BienEmbargado> getBienesEmbargados() { return bienesEmbargados; }
    public void setBienesEmbargados(List<BienEmbargado> bienesEmbargados) { this.bienesEmbargados = bienesEmbargados; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public Boolean getTrans() { return trans; }
    public void setTrans(Boolean trans) { this.trans = trans; }
    public Boolean getBusquedaBienes() { return busquedaBienes; }
    public void setBusquedaBienes(Boolean busquedaBienes) { this.busquedaBienes = busquedaBienes; }
    public BigDecimal getMontoDemandado() { return montoDemandado; }
    public void setMontoDemandado(BigDecimal montoDemandado) { this.montoDemandado = montoDemandado; }
    public String getEscribanoLegal() { return escribanoLegal; }
    public void setEscribanoLegal(String escribanoLegal) { this.escribanoLegal = escribanoLegal; }
    public String getCodigoExpCautelar() { return codigoExpCautelar; }
    public void setCodigoExpCautelar(String codigoExpCautelar) { this.codigoExpCautelar = codigoExpCautelar; }
    public Boolean getIncidente() { return incidente; }
    public void setIncidente(Boolean incidente) { this.incidente = incidente; }
    public LocalDate getFechaPresentacion() { return fechaPresentacion; }
    public void setFechaPresentacion(LocalDate fechaPresentacion) { this.fechaPresentacion = fechaPresentacion; }
    public LocalDate getFechaInadmisiblePrincipal() { return fechaInadmisiblePrincipal; }
    public void setFechaInadmisiblePrincipal(LocalDate fechaInadmisiblePrincipal) { this.fechaInadmisiblePrincipal = fechaInadmisiblePrincipal; }
    public LocalDate getFechaAdmisionPrincipal() { return fechaAdmisionPrincipal; }
    public void setFechaAdmisionPrincipal(LocalDate fechaAdmisionPrincipal) { this.fechaAdmisionPrincipal = fechaAdmisionPrincipal; }
    public LocalDate getFechaAudienciaUnica() { return fechaAudienciaUnica; }
    public void setFechaAudienciaUnica(LocalDate fechaAudienciaUnica) { this.fechaAudienciaUnica = fechaAudienciaUnica; }
    public LocalDate getFechaAutoFinal() { return fechaAutoFinal; }
    public void setFechaAutoFinal(LocalDate fechaAutoFinal) { this.fechaAutoFinal = fechaAutoFinal; }
    public LocalDate getFechaConsentimiento() { return fechaConsentimiento; }
    public void setFechaConsentimiento(LocalDate fechaConsentimiento) { this.fechaConsentimiento = fechaConsentimiento; }
    public LocalDate getFechaEjecutoriada() { return fechaEjecutoriada; }
    public void setFechaEjecutoriada(LocalDate fechaEjecutoriada) { this.fechaEjecutoriada = fechaEjecutoriada; }
    public LocalDate getFechaIngresoEjecucion() { return fechaIngresoEjecucion; }
    public void setFechaIngresoEjecucion(LocalDate fechaIngresoEjecucion) { this.fechaIngresoEjecucion = fechaIngresoEjecucion; }
    public LocalDate getFechaTasacion() { return fechaTasacion; }
    public void setFechaTasacion(LocalDate fechaTasacion) { this.fechaTasacion = fechaTasacion; }
    public LocalDate getFechaNombramientoMartillero() { return fechaNombramientoMartillero; }
    public void setFechaNombramientoMartillero(LocalDate fechaNombramientoMartillero) { this.fechaNombramientoMartillero = fechaNombramientoMartillero; }
    public LocalDate getFechaRemate1() { return fechaRemate1; }
    public void setFechaRemate1(LocalDate fechaRemate1) { this.fechaRemate1 = fechaRemate1; }
    public LocalDate getFechaRemate2() { return fechaRemate2; }
    public void setFechaRemate2(LocalDate fechaRemate2) { this.fechaRemate2 = fechaRemate2; }
    public LocalDate getFechaRemate3() { return fechaRemate3; }
    public void setFechaRemate3(LocalDate fechaRemate3) { this.fechaRemate3 = fechaRemate3; }
    public String getObservacionActos() { return observacionActos; }
    public void setObservacionActos(String observacionActos) { this.observacionActos = observacionActos; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public String getEstadoCartera() { return estadoCartera; }
    public void setEstadoCartera(String estadoCartera) { this.estadoCartera = estadoCartera; }
    public LocalDate getFechaDesembolso() { return fechaDesembolso; }
    public void setFechaDesembolso(LocalDate fechaDesembolso) { this.fechaDesembolso = fechaDesembolso; }
    public BigDecimal getImporteDesembolso() { return importeDesembolso; }
    public void setImporteDesembolso(BigDecimal importeDesembolso) { this.importeDesembolso = importeDesembolso; }
    public String getEtapaProcesalTexto() { return etapaProcesalTexto; }
    public void setEtapaProcesalTexto(String etapaProcesalTexto) { this.etapaProcesalTexto = etapaProcesalTexto; }
    public String getActoPendiente() { return actoPendiente; }
    public void setActoPendiente(String actoPendiente) { this.actoPendiente = actoPendiente; }
    public LocalDate getFechaUltimoEstadoProceso() { return fechaUltimoEstadoProceso; }
    public void setFechaUltimoEstadoProceso(LocalDate fechaUltimoEstadoProceso) { this.fechaUltimoEstadoProceso = fechaUltimoEstadoProceso; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public BigDecimal getMontoAprobado() { return montoAprobado; }
    public void setMontoAprobado(BigDecimal montoAprobado) { this.montoAprobado = montoAprobado; }
    public LocalDate getFechaAceptacionDemanda() { return fechaAceptacionDemanda; }
    public void setFechaAceptacionDemanda(LocalDate fechaAceptacionDemanda) { this.fechaAceptacionDemanda = fechaAceptacionDemanda; }
    public LocalDate getFechaEnvioJudicial() { return fechaEnvioJudicial; }
    public void setFechaEnvioJudicial(LocalDate fechaEnvioJudicial) { this.fechaEnvioJudicial = fechaEnvioJudicial; }
    public LocalDate getFechaAsignacionAbogado() { return fechaAsignacionAbogado; }
    public void setFechaAsignacionAbogado(LocalDate fechaAsignacionAbogado) { this.fechaAsignacionAbogado = fechaAsignacionAbogado; }
    public LocalDate getFechaCastigo() { return fechaCastigo; }
    public void setFechaCastigo(LocalDate fechaCastigo) { this.fechaCastigo = fechaCastigo; }
    public String getTipoFondo() { return tipoFondo; }
    public void setTipoFondo(String tipoFondo) { this.tipoFondo = tipoFondo; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private com.startup.cobranza.cliente.entity.Cliente cliente;
        private Empresa empresa;
        private Agencia agencia;
        private String cuenta;
        private String numeroOperacion;
        private BigDecimal montoCapital;
        private BigDecimal montoTotal;
        private Integer diasMora;
        private String moneda;
        private String tipoCredito;
        private String situacion;
        private String estado;
        private String etapa;
        private String observacion;
        private String rango;
        private String analista;
        private String analistaSenior;
        private String numeroExpediente;
        private String tipoProceso;
        private String tipoJuzgado;
        private String distritoJudicial;
        private String numeroJuzgado;
        private Usuario abogado;
        private List<BienEmbargado> bienesEmbargados;
        private Boolean activo;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;

        public Builder id(Long v) { id = v; return this; }
        public Builder cliente(com.startup.cobranza.cliente.entity.Cliente v) { cliente = v; return this; }
        public Builder empresa(Empresa v) { empresa = v; return this; }
        public Builder agencia(Agencia v) { agencia = v; return this; }
        public Builder cuenta(String v) { cuenta = v; return this; }
        public Builder numeroOperacion(String v) { numeroOperacion = v; return this; }
        public Builder montoCapital(BigDecimal v) { montoCapital = v; return this; }
        public Builder montoTotal(BigDecimal v) { montoTotal = v; return this; }
        public Builder diasMora(Integer v) { diasMora = v; return this; }
        public Builder moneda(String v) { moneda = v; return this; }
        public Builder tipoCredito(String v) { tipoCredito = v; return this; }
        public Builder situacion(String v) { situacion = v; return this; }
        public Builder estado(String v) { estado = v; return this; }
        public Builder etapa(String v) { etapa = v; return this; }
        public Builder observacion(String v) { observacion = v; return this; }
        public Builder rango(String v) { rango = v; return this; }
        public Builder analista(String v) { analista = v; return this; }
        public Builder analistaSenior(String v) { analistaSenior = v; return this; }
        public Builder numeroExpediente(String v) { numeroExpediente = v; return this; }
        public Builder tipoProceso(String v) { tipoProceso = v; return this; }
        public Builder tipoJuzgado(String v) { tipoJuzgado = v; return this; }
        public Builder distritoJudicial(String v) { distritoJudicial = v; return this; }
        public Builder numeroJuzgado(String v) { numeroJuzgado = v; return this; }
        public Builder abogado(Usuario v) { abogado = v; return this; }
        public Builder bienesEmbargados(List<BienEmbargado> v) { bienesEmbargados = v; return this; }
        public Builder activo(Boolean v) { activo = v; return this; }
        public Builder fechaCreacion(LocalDateTime v) { fechaCreacion = v; return this; }
        public Builder fechaActualizacion(LocalDateTime v) { fechaActualizacion = v; return this; }

        private Boolean trans;
        private Boolean busquedaBienes;
        private BigDecimal montoDemandado;
        private String escribanoLegal;
        private String codigoExpCautelar;
        private Boolean incidente;
        private LocalDate fechaPresentacion;
        private LocalDate fechaInadmisiblePrincipal;
        private LocalDate fechaAdmisionPrincipal;
        private LocalDate fechaAudienciaUnica;
        private LocalDate fechaAutoFinal;
        private LocalDate fechaConsentimiento;
        private LocalDate fechaEjecutoriada;
        private LocalDate fechaIngresoEjecucion;
        private LocalDate fechaTasacion;
        private LocalDate fechaNombramientoMartillero;
        private LocalDate fechaRemate1;
        private LocalDate fechaRemate2;
        private LocalDate fechaRemate3;
        private String observacionActos;
        private String comentario;
        private String estadoCartera;
        private LocalDate fechaDesembolso;
        private BigDecimal importeDesembolso;
        private String etapaProcesalTexto;
        private String actoPendiente;
        private LocalDate fechaUltimoEstadoProceso;
        private String zona;
        private String departamento;
        private String provincia;
        private String distrito;
        private String direccion;
        private String referencia;
        private String telefono;
        private BigDecimal montoAprobado;
        private LocalDate fechaAceptacionDemanda;
        private LocalDate fechaEnvioJudicial;
        private LocalDate fechaAsignacionAbogado;
        private LocalDate fechaCastigo;
        private String tipoFondo;

        public Builder trans(Boolean v) { trans = v; return this; }
        public Builder busquedaBienes(Boolean v) { busquedaBienes = v; return this; }
        public Builder montoDemandado(BigDecimal v) { montoDemandado = v; return this; }
        public Builder escribanoLegal(String v) { escribanoLegal = v; return this; }
        public Builder codigoExpCautelar(String v) { codigoExpCautelar = v; return this; }
        public Builder incidente(Boolean v) { incidente = v; return this; }
        public Builder fechaPresentacion(LocalDate v) { fechaPresentacion = v; return this; }
        public Builder fechaInadmisiblePrincipal(LocalDate v) { fechaInadmisiblePrincipal = v; return this; }
        public Builder fechaAdmisionPrincipal(LocalDate v) { fechaAdmisionPrincipal = v; return this; }
        public Builder fechaAudienciaUnica(LocalDate v) { fechaAudienciaUnica = v; return this; }
        public Builder fechaAutoFinal(LocalDate v) { fechaAutoFinal = v; return this; }
        public Builder fechaConsentimiento(LocalDate v) { fechaConsentimiento = v; return this; }
        public Builder fechaEjecutoriada(LocalDate v) { fechaEjecutoriada = v; return this; }
        public Builder fechaIngresoEjecucion(LocalDate v) { fechaIngresoEjecucion = v; return this; }
        public Builder fechaTasacion(LocalDate v) { fechaTasacion = v; return this; }
        public Builder fechaNombramientoMartillero(LocalDate v) { fechaNombramientoMartillero = v; return this; }
        public Builder fechaRemate1(LocalDate v) { fechaRemate1 = v; return this; }
        public Builder fechaRemate2(LocalDate v) { fechaRemate2 = v; return this; }
        public Builder fechaRemate3(LocalDate v) { fechaRemate3 = v; return this; }
        public Builder observacionActos(String v) { observacionActos = v; return this; }
        public Builder comentario(String v) { comentario = v; return this; }
        public Builder estadoCartera(String v) { estadoCartera = v; return this; }
        public Builder fechaDesembolso(LocalDate v) { fechaDesembolso = v; return this; }
        public Builder importeDesembolso(BigDecimal v) { importeDesembolso = v; return this; }
        public Builder etapaProcesalTexto(String v) { etapaProcesalTexto = v; return this; }
        public Builder actoPendiente(String v) { actoPendiente = v; return this; }
        public Builder fechaUltimoEstadoProceso(LocalDate v) { fechaUltimoEstadoProceso = v; return this; }
        public Builder zona(String v) { zona = v; return this; }
        public Builder departamento(String v) { departamento = v; return this; }
        public Builder provincia(String v) { provincia = v; return this; }
        public Builder distrito(String v) { distrito = v; return this; }
        public Builder direccion(String v) { direccion = v; return this; }
        public Builder referencia(String v) { referencia = v; return this; }
        public Builder telefono(String v) { telefono = v; return this; }
        public Builder montoAprobado(BigDecimal v) { montoAprobado = v; return this; }
        public Builder fechaAceptacionDemanda(LocalDate v) { fechaAceptacionDemanda = v; return this; }
        public Builder fechaEnvioJudicial(LocalDate v) { fechaEnvioJudicial = v; return this; }
        public Builder fechaAsignacionAbogado(LocalDate v) { fechaAsignacionAbogado = v; return this; }
        public Builder fechaCastigo(LocalDate v) { fechaCastigo = v; return this; }
        public Builder tipoFondo(String v) { tipoFondo = v; return this; }

        public Operacion build() {
            Operacion o = new Operacion();
            if (id != null) o.setId(id);
            o.setCliente(cliente);
            o.setEmpresa(empresa);
            o.setAgencia(agencia);
            o.setCuenta(cuenta);
            o.setNumeroOperacion(numeroOperacion);
            o.setMontoCapital(montoCapital);
            o.setMontoTotal(montoTotal);
            o.setDiasMora(diasMora);
            o.setMoneda(moneda);
            o.setTipoCredito(tipoCredito);
            o.setSituacion(situacion);
            o.setEstado(estado);
            o.setEtapa(etapa);
            o.setObservacion(observacion);
            o.setRango(rango);
            o.setAnalista(analista);
            o.setAnalistaSenior(analistaSenior);
            o.setNumeroExpediente(numeroExpediente);
            o.setTipoProceso(tipoProceso);
            o.setTipoJuzgado(tipoJuzgado);
            o.setDistritoJudicial(distritoJudicial);
            o.setNumeroJuzgado(numeroJuzgado);
            o.setAbogado(abogado);
            o.setBienesEmbargados(bienesEmbargados);
            o.setActivo(activo != null ? activo : true);
            o.setTrans(trans);
            o.setBusquedaBienes(busquedaBienes);
            o.setMontoDemandado(montoDemandado);
            o.setEscribanoLegal(escribanoLegal);
            o.setCodigoExpCautelar(codigoExpCautelar);
            o.setIncidente(incidente);
            o.setFechaPresentacion(fechaPresentacion);
            o.setFechaInadmisiblePrincipal(fechaInadmisiblePrincipal);
            o.setFechaAdmisionPrincipal(fechaAdmisionPrincipal);
            o.setFechaAudienciaUnica(fechaAudienciaUnica);
            o.setFechaAutoFinal(fechaAutoFinal);
            o.setFechaConsentimiento(fechaConsentimiento);
            o.setFechaEjecutoriada(fechaEjecutoriada);
            o.setFechaIngresoEjecucion(fechaIngresoEjecucion);
            o.setFechaTasacion(fechaTasacion);
            o.setFechaNombramientoMartillero(fechaNombramientoMartillero);
            o.setFechaRemate1(fechaRemate1);
            o.setFechaRemate2(fechaRemate2);
            o.setFechaRemate3(fechaRemate3);
            o.setObservacionActos(observacionActos);
            o.setComentario(comentario);
            o.setEstadoCartera(estadoCartera);
            o.setFechaDesembolso(fechaDesembolso);
            o.setImporteDesembolso(importeDesembolso);
            o.setEtapaProcesalTexto(etapaProcesalTexto);
            o.setActoPendiente(actoPendiente);
            o.setFechaUltimoEstadoProceso(fechaUltimoEstadoProceso);
            o.setZona(zona);
            o.setDepartamento(departamento);
            o.setProvincia(provincia);
            o.setDistrito(distrito);
            o.setDireccion(direccion);
            o.setReferencia(referencia);
            o.setTelefono(telefono);
            o.setMontoAprobado(montoAprobado);
            o.setFechaAceptacionDemanda(fechaAceptacionDemanda);
            o.setFechaEnvioJudicial(fechaEnvioJudicial);
            o.setFechaAsignacionAbogado(fechaAsignacionAbogado);
            o.setFechaCastigo(fechaCastigo);
            o.setTipoFondo(tipoFondo);
            return o;
        }
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
