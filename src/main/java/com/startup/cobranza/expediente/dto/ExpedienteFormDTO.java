package com.startup.cobranza.expediente.dto;

import java.math.BigDecimal;

public class ExpedienteFormDTO {
    private String numeroExpediente;
    private String situacion;
    private String tipoProceso;
    private String tipoJuzgado;
    private String distritoJudicial;
    private String numeroJuzgado;
    private String incidente;
    private BigDecimal montoDemandado;
    private String especialistaLegal;
    private String etapaProcesal;
    private String observacion;
    private String comentarioGeneral;
    private Long empresaId;
    private Long agenciaId;
    private Long abogadoId;

    public ExpedienteFormDTO() {}

    public ExpedienteFormDTO(String numeroExpediente, String situacion, String tipoProceso, String tipoJuzgado,
                             String distritoJudicial, String numeroJuzgado, String incidente,
                             BigDecimal montoDemandado, String especialistaLegal, String etapaProcesal,
                             String observacion, String comentarioGeneral, Long empresaId, Long agenciaId, Long abogadoId) {
        this.numeroExpediente = numeroExpediente;
        this.situacion = situacion;
        this.tipoProceso = tipoProceso;
        this.tipoJuzgado = tipoJuzgado;
        this.distritoJudicial = distritoJudicial;
        this.numeroJuzgado = numeroJuzgado;
        this.incidente = incidente;
        this.montoDemandado = montoDemandado;
        this.especialistaLegal = especialistaLegal;
        this.etapaProcesal = etapaProcesal;
        this.observacion = observacion;
        this.comentarioGeneral = comentarioGeneral;
        this.empresaId = empresaId;
        this.agenciaId = agenciaId;
        this.abogadoId = abogadoId;
    }

    public String getNumeroExpediente() { return numeroExpediente; }
    public void setNumeroExpediente(String numeroExpediente) { this.numeroExpediente = numeroExpediente; }
    public String getSituacion() { return situacion; }
    public void setSituacion(String situacion) { this.situacion = situacion; }
    public String getTipoProceso() { return tipoProceso; }
    public void setTipoProceso(String tipoProceso) { this.tipoProceso = tipoProceso; }
    public String getTipoJuzgado() { return tipoJuzgado; }
    public void setTipoJuzgado(String tipoJuzgado) { this.tipoJuzgado = tipoJuzgado; }
    public String getDistritoJudicial() { return distritoJudicial; }
    public void setDistritoJudicial(String distritoJudicial) { this.distritoJudicial = distritoJudicial; }
    public String getNumeroJuzgado() { return numeroJuzgado; }
    public void setNumeroJuzgado(String numeroJuzgado) { this.numeroJuzgado = numeroJuzgado; }
    public String getIncidente() { return incidente; }
    public void setIncidente(String incidente) { this.incidente = incidente; }
    public BigDecimal getMontoDemandado() { return montoDemandado; }
    public void setMontoDemandado(BigDecimal montoDemandado) { this.montoDemandado = montoDemandado; }
    public String getEspecialistaLegal() { return especialistaLegal; }
    public void setEspecialistaLegal(String especialistaLegal) { this.especialistaLegal = especialistaLegal; }
    public String getEtapaProcesal() { return etapaProcesal; }
    public void setEtapaProcesal(String etapaProcesal) { this.etapaProcesal = etapaProcesal; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public String getComentarioGeneral() { return comentarioGeneral; }
    public void setComentarioGeneral(String comentarioGeneral) { this.comentarioGeneral = comentarioGeneral; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public Long getAgenciaId() { return agenciaId; }
    public void setAgenciaId(Long agenciaId) { this.agenciaId = agenciaId; }
    public Long getAbogadoId() { return abogadoId; }
    public void setAbogadoId(Long abogadoId) { this.abogadoId = abogadoId; }
}
