package com.startup.cobranza.cliente.dto;

import java.math.BigDecimal;
import java.util.List;

public class ClienteBandejaDTO {

    private Long id;
    private String dni;
    private String nombreCompleto;
    private List<String> agencias;
    private String estado;
    private String estadoCartera;
    private String etapa;
    private BigDecimal montoTotal;
    private BigDecimal montoCapital;
    private long totalOperaciones;

    public ClienteBandejaDTO() {}

    public ClienteBandejaDTO(Long id, String dni, String nombreCompleto,
                             List<String> agencias,
                             String estado, String etapa,
                             BigDecimal montoTotal, BigDecimal montoCapital,
                             long totalOperaciones) {
        this.id = id;
        this.dni = dni;
        this.nombreCompleto = nombreCompleto;
        this.agencias = agencias;
        this.estado = estado;
        this.etapa = etapa;
        this.montoTotal = montoTotal;
        this.montoCapital = montoCapital;
        this.totalOperaciones = totalOperaciones;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public List<String> getAgencias() { return agencias; }
    public void setAgencias(List<String> agencias) { this.agencias = agencias; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getEstadoCartera() { return estadoCartera; }
    public void setEstadoCartera(String estadoCartera) { this.estadoCartera = estadoCartera; }
    public String getEtapa() { return etapa; }
    public void setEtapa(String etapa) { this.etapa = etapa; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public BigDecimal getMontoCapital() { return montoCapital; }
    public void setMontoCapital(BigDecimal montoCapital) { this.montoCapital = montoCapital; }
    public long getTotalOperaciones() { return totalOperaciones; }
    public void setTotalOperaciones(long totalOperaciones) { this.totalOperaciones = totalOperaciones; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String dni;
        private String nombreCompleto;
            private List<String> agencias;
        private String estado;
        private String estadoCartera;
        private String etapa;
        private BigDecimal montoTotal = BigDecimal.ZERO;
        private BigDecimal montoCapital = BigDecimal.ZERO;
        private long totalOperaciones;

        public Builder id(Long v) { id = v; return this; }
        public Builder dni(String v) { dni = v; return this; }
        public Builder nombreCompleto(String v) { nombreCompleto = v; return this; }
        public Builder agencias(List<String> v) { agencias = v; return this; }
        public Builder estado(String v) { estado = v; return this; }
        public Builder estadoCartera(String v) { estadoCartera = v; return this; }
        public Builder etapa(String v) { etapa = v; return this; }
        public Builder montoTotal(BigDecimal v) { montoTotal = v; return this; }
        public Builder montoCapital(BigDecimal v) { montoCapital = v; return this; }
        public Builder totalOperaciones(long v) { totalOperaciones = v; return this; }

        public ClienteBandejaDTO build() {
            return new ClienteBandejaDTO(id, dni, nombreCompleto, agencias,
                    estado, etapa, montoTotal, montoCapital, totalOperaciones);
        }
    }
}
