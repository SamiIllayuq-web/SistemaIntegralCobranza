package com.startup.cobranza.agencia.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Agrupa operaciones por agencia para la vista de agencias.
 */
public class AgenciaAgrupadaDTO {

    private Long agenciaId;
    private String agenciaNombre;
    private String agenciaCodigo;
    private int totalOperaciones;
    private BigDecimal montoTotal;
    private String moneda;
    private List<OperacionResumidaDTO> operaciones;

    public AgenciaAgrupadaDTO() {}

    public AgenciaAgrupadaDTO(Long agenciaId, String agenciaNombre, String agenciaCodigo,
                               int totalOperaciones, BigDecimal montoTotal, String moneda,
                               List<OperacionResumidaDTO> operaciones) {
        this.agenciaId = agenciaId;
        this.agenciaNombre = agenciaNombre;
        this.agenciaCodigo = agenciaCodigo;
        this.totalOperaciones = totalOperaciones;
        this.montoTotal = montoTotal;
        this.moneda = moneda;
        this.operaciones = operaciones;
    }

    // ─── Getters / setters ───────────────────────────────────────
    public Long getAgenciaId() { return agenciaId; }
    public void setAgenciaId(Long agenciaId) { this.agenciaId = agenciaId; }
    public String getAgenciaNombre() { return agenciaNombre; }
    public void setAgenciaNombre(String agenciaNombre) { this.agenciaNombre = agenciaNombre; }
    public String getAgenciaCodigo() { return agenciaCodigo; }
    public void setAgenciaCodigo(String agenciaCodigo) { this.agenciaCodigo = agenciaCodigo; }
    public int getTotalOperaciones() { return totalOperaciones; }
    public void setTotalOperaciones(int totalOperaciones) { this.totalOperaciones = totalOperaciones; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public List<OperacionResumidaDTO> getOperaciones() { return operaciones; }
    public void setOperaciones(List<OperacionResumidaDTO> operaciones) { this.operaciones = operaciones; }
}
