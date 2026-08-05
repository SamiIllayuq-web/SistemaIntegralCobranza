package com.startup.cobranza.operacion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OperacionDTO {

    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String clienteDni;
    private Long empresaId;
    private String empresaNombre;
    private Long agenciaId;
    private String agenciaNombre;
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
    private Long abogadoId;
    private String abogadoNombre;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private List<BienEmbargadoDTO> bienEmbargados;

    // --- Campos judiciales ---
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

    public OperacionDTO() {}

    public OperacionDTO(Long id, Long clienteId, String clienteNombre, String clienteDni,
                        Long empresaId, String empresaNombre, Long agenciaId, String agenciaNombre,
                        String cuenta, String numeroOperacion, BigDecimal montoCapital,
                        BigDecimal montoTotal, Integer diasMora, String moneda, String tipoCredito,
                        String situacion, String estado, String etapa, String observacion,
                        String rango, String analista, String analistaSenior,
                        String numeroExpediente, String tipoProceso, String tipoJuzgado,
                        String distritoJudicial, String numeroJuzgado, Long abogadoId,
                        String abogadoNombre, Boolean activo,
                        LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.clienteDni = clienteDni;
        this.empresaId = empresaId;
        this.empresaNombre = empresaNombre;
        this.agenciaId = agenciaId;
        this.agenciaNombre = agenciaNombre;
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
        this.abogadoId = abogadoId;
        this.abogadoNombre = abogadoNombre;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getClienteDni() { return clienteDni; }
    public void setClienteDni(String clienteDni) { this.clienteDni = clienteDni; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public String getEmpresaNombre() { return empresaNombre; }
    public void setEmpresaNombre(String empresaNombre) { this.empresaNombre = empresaNombre; }
    public Long getAgenciaId() { return agenciaId; }
    public void setAgenciaId(Long agenciaId) { this.agenciaId = agenciaId; }
    public String getAgenciaNombre() { return agenciaNombre; }
    public void setAgenciaNombre(String agenciaNombre) { this.agenciaNombre = agenciaNombre; }
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
    public Long getAbogadoId() { return abogadoId; }
    public void setAbogadoId(Long abogadoId) { this.abogadoId = abogadoId; }
    public String getAbogadoNombre() { return abogadoNombre; }
    public void setAbogadoNombre(String abogadoNombre) { this.abogadoNombre = abogadoNombre; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public List<BienEmbargadoDTO> getBienEmbargados() { return bienEmbargados; }
    public void setBienEmbargados(List<BienEmbargadoDTO> bienEmbargados) { this.bienEmbargados = bienEmbargados; }
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

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long clienteId;
        private String clienteNombre;
        private String clienteDni;
        private Long empresaId;
        private String empresaNombre;
        private Long agenciaId;
        private String agenciaNombre;
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
        private Long abogadoId;
        private String abogadoNombre;
        private Boolean activo;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;
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

        public Builder id(Long v) { id = v; return this; }
        public Builder clienteId(Long v) { clienteId = v; return this; }
        public Builder clienteNombre(String v) { clienteNombre = v; return this; }
        public Builder clienteDni(String v) { clienteDni = v; return this; }
        public Builder empresaId(Long v) { empresaId = v; return this; }
        public Builder empresaNombre(String v) { empresaNombre = v; return this; }
        public Builder agenciaId(Long v) { agenciaId = v; return this; }
        public Builder agenciaNombre(String v) { agenciaNombre = v; return this; }
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
        public Builder abogadoId(Long v) { abogadoId = v; return this; }
        public Builder abogadoNombre(String v) { abogadoNombre = v; return this; }
        public Builder activo(Boolean v) { activo = v; return this; }
        public Builder fechaCreacion(LocalDateTime v) { fechaCreacion = v; return this; }
        public Builder fechaActualizacion(LocalDateTime v) { fechaActualizacion = v; return this; }
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

        public OperacionDTO build() {
            OperacionDTO dto = new OperacionDTO(id, clienteId, clienteNombre, clienteDni, empresaId, empresaNombre,
                    agenciaId, agenciaNombre, cuenta, numeroOperacion, montoCapital, montoTotal,
                    diasMora, moneda, tipoCredito, situacion, estado, etapa, observacion, rango,
                    analista, analistaSenior, numeroExpediente, tipoProceso, tipoJuzgado,
                    distritoJudicial, numeroJuzgado, abogadoId, abogadoNombre, activo,
                    fechaCreacion, fechaActualizacion);
            dto.setTrans(trans);
            dto.setBusquedaBienes(busquedaBienes);
            dto.setMontoDemandado(montoDemandado);
            dto.setEscribanoLegal(escribanoLegal);
            dto.setCodigoExpCautelar(codigoExpCautelar);
            dto.setIncidente(incidente);
            dto.setFechaPresentacion(fechaPresentacion);
            dto.setFechaInadmisiblePrincipal(fechaInadmisiblePrincipal);
            dto.setFechaAdmisionPrincipal(fechaAdmisionPrincipal);
            dto.setFechaAudienciaUnica(fechaAudienciaUnica);
            dto.setFechaAutoFinal(fechaAutoFinal);
            dto.setFechaConsentimiento(fechaConsentimiento);
            dto.setFechaEjecutoriada(fechaEjecutoriada);
            dto.setFechaIngresoEjecucion(fechaIngresoEjecucion);
            dto.setFechaTasacion(fechaTasacion);
            dto.setFechaNombramientoMartillero(fechaNombramientoMartillero);
            dto.setFechaRemate1(fechaRemate1);
            dto.setFechaRemate2(fechaRemate2);
            dto.setFechaRemate3(fechaRemate3);
            dto.setObservacionActos(observacionActos);
            dto.setComentario(comentario);
            return dto;
        }
    }
}
