package com.startup.cobranza.expediente.entity;

import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.empresa.entity.Empresa;
import com.startup.cobranza.operacion.entity.Operacion;
import com.startup.cobranza.usuario.entity.Usuario;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expedientes")
public class Expediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operacion_id", unique = true)
    private Operacion operacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agencia_id")
    private Agencia agencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "abogado_id")
    private Usuario abogado;

    // ─── Datos generales ────────────────────────────────────────────────

    @Column(name = "numero_expediente")
    private String numeroExpediente;

    @Column(name = "situacion", length = 100)
    private String situacion;

    @Column(name = "tipo_proceso", length = 100)
    private String tipoProceso;

    @Column(name = "etapa_procesal", length = 100)
    private String etapaProcesal;

    @Column(name = "tipo_juzgado", length = 100)
    private String tipoJuzgado;

    @Column(name = "distrito_judicial", length = 200)
    private String distritoJudicial;

    @Column(name = "numero_juzgado", length = 50)
    private String numeroJuzgado;

    @Column(name = "incidente")
    private Boolean incidente;

    @Column(name = "especialista_legal", length = 300)
    private String especialistaLegal;

    @Column(name = "monto_demandado", precision = 15, scale = 2)
    private BigDecimal montoDemandado;

    @Column(name = "escribano_legal", length = 500)
    private String escribanoLegal;

    @Column(name = "codigo_exp_cautelar", length = 500)
    private String codigoExpCautelar;

    @Column(name = "expediente_cautelar_codigo", length = 500)
    private String expedienteCautelarCodigo;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "comentario_general", columnDefinition = "TEXT")
    private String comentarioGeneral;

    @Column(name = "activo")
    private Boolean activo = true;

    // ─── Fechas del cuaderno principal ─────────────────────────────────

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

    // ─── Etapa de ejecución ─────────────────────────────────────────────

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

    // ─── Timestamps ────────────────────────────────────────────────────

    @Column(name = "fecha_creacion")
    private java.time.LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private java.time.LocalDateTime fechaActualizacion;

    public Expediente() {}

    @PrePersist
    protected void onCreate() {
        fechaCreacion = java.time.LocalDateTime.now();
        if (activo == null) activo = true;
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = java.time.LocalDateTime.now();
    }

    // ─── Getters y setters ─────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Operacion getOperacion() { return operacion; }
    public void setOperacion(Operacion operacion) { this.operacion = operacion; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    public Agencia getAgencia() { return agencia; }
    public void setAgencia(Agencia agencia) { this.agencia = agencia; }
    public Usuario getAbogado() { return abogado; }
    public void setAbogado(Usuario abogado) { this.abogado = abogado; }
    public String getNumeroExpediente() { return numeroExpediente; }
    public void setNumeroExpediente(String numeroExpediente) { this.numeroExpediente = numeroExpediente; }
    public String getSituacion() { return situacion; }
    public void setSituacion(String situacion) { this.situacion = situacion; }
    public String getTipoProceso() { return tipoProceso; }
    public void setTipoProceso(String tipoProceso) { this.tipoProceso = tipoProceso; }
    public String getEtapaProcesal() { return etapaProcesal; }
    public void setEtapaProcesal(String etapaProcesal) { this.etapaProcesal = etapaProcesal; }
    public String getTipoJuzgado() { return tipoJuzgado; }
    public void setTipoJuzgado(String tipoJuzgado) { this.tipoJuzgado = tipoJuzgado; }
    public String getDistritoJudicial() { return distritoJudicial; }
    public void setDistritoJudicial(String distritoJudicial) { this.distritoJudicial = distritoJudicial; }
    public String getNumeroJuzgado() { return numeroJuzgado; }
    public void setNumeroJuzgado(String numeroJuzgado) { this.numeroJuzgado = numeroJuzgado; }
    public Boolean getIncidente() { return incidente; }
    public void setIncidente(Boolean incidente) { this.incidente = incidente; }
    public String getEspecialistaLegal() { return especialistaLegal; }
    public void setEspecialistaLegal(String especialistaLegal) { this.especialistaLegal = especialistaLegal; }
    public BigDecimal getMontoDemandado() { return montoDemandado; }
    public void setMontoDemandado(BigDecimal montoDemandado) { this.montoDemandado = montoDemandado; }
    public String getEscribanoLegal() { return escribanoLegal; }
    public void setEscribanoLegal(String escribanoLegal) { this.escribanoLegal = escribanoLegal; }
    public String getCodigoExpCautelar() { return codigoExpCautelar; }
    public void setCodigoExpCautelar(String codigoExpCautelar) { this.codigoExpCautelar = codigoExpCautelar; }
    public String getExpedienteCautelarCodigo() { return expedienteCautelarCodigo; }
    public void setExpedienteCautelarCodigo(String expedienteCautelarCodigo) { this.expedienteCautelarCodigo = expedienteCautelarCodigo; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public String getComentarioGeneral() { return comentarioGeneral; }
    public void setComentarioGeneral(String comentarioGeneral) { this.comentarioGeneral = comentarioGeneral; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
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
    public java.time.LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(java.time.LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public java.time.LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(java.time.LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    // ─── Builder ──────────────────────────────────────────────────────

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Expediente e = new Expediente();
        public Builder id(Long v) { e.setId(v); return this; }
        public Builder operacion(Operacion v) { e.setOperacion(v); return this; }
        public Builder empresa(Empresa v) { e.setEmpresa(v); return this; }
        public Builder agencia(Agencia v) { e.setAgencia(v); return this; }
        public Builder abogado(Usuario v) { e.setAbogado(v); return this; }
        public Builder numeroExpediente(String v) { e.setNumeroExpediente(v); return this; }
        public Builder situacion(String v) { e.setSituacion(v); return this; }
        public Builder tipoProceso(String v) { e.setTipoProceso(v); return this; }
        public Builder etapaProcesal(String v) { e.setEtapaProcesal(v); return this; }
        public Builder tipoJuzgado(String v) { e.setTipoJuzgado(v); return this; }
        public Builder distritoJudicial(String v) { e.setDistritoJudicial(v); return this; }
        public Builder numeroJuzgado(String v) { e.setNumeroJuzgado(v); return this; }
        public Builder incidente(Boolean v) { e.setIncidente(v); return this; }
        public Builder especialistaLegal(String v) { e.setEspecialistaLegal(v); return this; }
        public Builder montoDemandado(BigDecimal v) { e.setMontoDemandado(v); return this; }
        public Builder escribanoLegal(String v) { e.setEscribanoLegal(v); return this; }
        public Builder codigoExpCautelar(String v) { e.setCodigoExpCautelar(v); return this; }
        public Builder expedienteCautelarCodigo(String v) { e.setExpedienteCautelarCodigo(v); return this; }
        public Builder observacion(String v) { e.setObservacion(v); return this; }
        public Builder comentarioGeneral(String v) { e.setComentarioGeneral(v); return this; }
        public Builder activo(Boolean v) { e.setActivo(v); return this; }
        public Builder fechaPresentacion(LocalDate v) { e.setFechaPresentacion(v); return this; }
        public Builder fechaInadmisiblePrincipal(LocalDate v) { e.setFechaInadmisiblePrincipal(v); return this; }
        public Builder fechaAdmisionPrincipal(LocalDate v) { e.setFechaAdmisionPrincipal(v); return this; }
        public Builder fechaAudienciaUnica(LocalDate v) { e.setFechaAudienciaUnica(v); return this; }
        public Builder fechaAutoFinal(LocalDate v) { e.setFechaAutoFinal(v); return this; }
        public Builder fechaConsentimiento(LocalDate v) { e.setFechaConsentimiento(v); return this; }
        public Builder fechaEjecutoriada(LocalDate v) { e.setFechaEjecutoriada(v); return this; }
        public Builder fechaIngresoEjecucion(LocalDate v) { e.setFechaIngresoEjecucion(v); return this; }
        public Builder fechaTasacion(LocalDate v) { e.setFechaTasacion(v); return this; }
        public Builder fechaNombramientoMartillero(LocalDate v) { e.setFechaNombramientoMartillero(v); return this; }
        public Builder fechaRemate1(LocalDate v) { e.setFechaRemate1(v); return this; }
        public Builder fechaRemate2(LocalDate v) { e.setFechaRemate2(v); return this; }
        public Builder fechaRemate3(LocalDate v) { e.setFechaRemate3(v); return this; }
        public Builder observacionActos(String v) { e.setObservacionActos(v); return this; }
        public Expediente build() { return e; }
    }
}
