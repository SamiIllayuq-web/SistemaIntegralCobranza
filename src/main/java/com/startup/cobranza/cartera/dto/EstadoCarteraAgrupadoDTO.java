package com.startup.cobranza.cartera.dto;

import com.startup.cobranza.agencia.dto.OperacionResumidaDTO;
import java.math.BigDecimal;
import java.util.List;

/**
 * Agrupa operaciones por estadoCartera para la vista de Estado de Cartera.
 */
public class EstadoCarteraAgrupadoDTO {

    private String estadoCartera;
    private int totalOperaciones;
    private BigDecimal montoTotal;
    private List<OperacionResumidaDTO> operaciones;

    public EstadoCarteraAgrupadoDTO() {}

    public EstadoCarteraAgrupadoDTO(String estadoCartera, int totalOperaciones,
                                     BigDecimal montoTotal, List<OperacionResumidaDTO> operaciones) {
        this.estadoCartera = estadoCartera;
        this.totalOperaciones = totalOperaciones;
        this.montoTotal = montoTotal;
        this.operaciones = operaciones;
    }

    // ─── Getters / setters ───────────────────────────────────────
    public String getEstadoCartera() { return estadoCartera; }
    public void setEstadoCartera(String estadoCartera) { this.estadoCartera = estadoCartera; }
    public int getTotalOperaciones() { return totalOperaciones; }
    public void setTotalOperaciones(int totalOperaciones) { this.totalOperaciones = totalOperaciones; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public List<OperacionResumidaDTO> getOperaciones() { return operaciones; }
    public void setOperaciones(List<OperacionResumidaDTO> operaciones) { this.operaciones = operaciones; }
}
