package com.startup.cobranza.agencia.dto;

import java.math.BigDecimal;

/**
 * Versión resumida de una operación para la vista agrupada por agencia.
 */
public class OperacionResumidaDTO {

    private Long operacionId;
    private Long clienteId;
    private String clienteNombre;
    private String clienteDni;
    private String numeroOperacion;
    private String numeroExpediente;
    private String situacion;
    private String etapaProcesal;
    private BigDecimal montoTotal;
    private String moneda;

    public OperacionResumidaDTO() {}

    public OperacionResumidaDTO(Long operacionId, Long clienteId, String clienteNombre,
                                 String clienteDni, String numeroOperacion, String numeroExpediente,
                                 String situacion, String etapaProcesal,
                                 BigDecimal montoTotal, String moneda) {
        this.operacionId = operacionId;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.clienteDni = clienteDni;
        this.numeroOperacion = numeroOperacion;
        this.numeroExpediente = numeroExpediente;
        this.situacion = situacion;
        this.etapaProcesal = etapaProcesal;
        this.montoTotal = montoTotal;
        this.moneda = moneda;
    }

    // ─── Getters / setters ───────────────────────────────────────
    public Long getOperacionId() { return operacionId; }
    public void setOperacionId(Long operacionId) { this.operacionId = operacionId; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getClienteDni() { return clienteDni; }
    public void setClienteDni(String clienteDni) { this.clienteDni = clienteDni; }
    public String getNumeroOperacion() { return numeroOperacion; }
    public void setNumeroOperacion(String numeroOperacion) { this.numeroOperacion = numeroOperacion; }
    public String getNumeroExpediente() { return numeroExpediente; }
    public void setNumeroExpediente(String numeroExpediente) { this.numeroExpediente = numeroExpediente; }
    public String getSituacion() { return situacion; }
    public void setSituacion(String situacion) { this.situacion = situacion; }
    public String getEtapaProcesal() { return etapaProcesal; }
    public void setEtapaProcesal(String etapaProcesal) { this.etapaProcesal = etapaProcesal; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
}
