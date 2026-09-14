package com.startup.cobranza.cliente.dto;

import java.math.BigDecimal;
import java.util.List;

public class ClienteBandejaDTO {

    private Long id;
    private String dni;
    private String nombreCompleto;
    private String agenciaNombre;
    private String numeroOperacion;
    private String cuenta;
    private String estado;
    private String estadoCartera;
    private BigDecimal montoTotal;
    private BigDecimal montoCapital;
    private long totalOperaciones;

    public ClienteBandejaDTO() {}

    public ClienteBandejaDTO(Long id, String dni, String nombreCompleto,
                             String agenciaNombre, String numeroOperacion, String cuenta,
                             String estado, String estadoCartera,
                             BigDecimal montoTotal, BigDecimal montoCapital,
                             long totalOperaciones) {
        this.id = id;
        this.dni = dni;
        this.nombreCompleto = nombreCompleto;
        this.agenciaNombre = agenciaNombre;
        this.numeroOperacion = numeroOperacion;
        this.cuenta = cuenta;
        this.estado = estado;
        this.estadoCartera = estadoCartera;
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
    public String getAgenciaNombre() { return agenciaNombre; }
    public void setAgenciaNombre(String agenciaNombre) { this.agenciaNombre = agenciaNombre; }
    public String getNumeroOperacion() { return numeroOperacion; }
    public void setNumeroOperacion(String numeroOperacion) { this.numeroOperacion = numeroOperacion; }
    public String getCuenta() { return cuenta; }
    public void setCuenta(String cuenta) { this.cuenta = cuenta; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getEstadoCartera() { return estadoCartera; }
    public void setEstadoCartera(String estadoCartera) { this.estadoCartera = estadoCartera; }
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
        private String agenciaNombre;
        private String numeroOperacion;
        private String cuenta;
        private String estado;
        private String estadoCartera;
        private BigDecimal montoTotal = BigDecimal.ZERO;
        private BigDecimal montoCapital = BigDecimal.ZERO;
        private long totalOperaciones;

        public Builder id(Long v) { id = v; return this; }
        public Builder dni(String v) { dni = v; return this; }
        public Builder nombreCompleto(String v) { nombreCompleto = v; return this; }
        public Builder agenciaNombre(String v) { agenciaNombre = v; return this; }
        public Builder numeroOperacion(String v) { numeroOperacion = v; return this; }
        public Builder cuenta(String v) { cuenta = v; return this; }
        public Builder estado(String v) { estado = v; return this; }
        public Builder estadoCartera(String v) { estadoCartera = v; return this; }
        public Builder montoTotal(BigDecimal v) { montoTotal = v; return this; }
        public Builder montoCapital(BigDecimal v) { montoCapital = v; return this; }
        public Builder totalOperaciones(long v) { totalOperaciones = v; return this; }

        public ClienteBandejaDTO build() {
            return new ClienteBandejaDTO(id, dni, nombreCompleto, agenciaNombre, numeroOperacion, cuenta,
                    estado, estadoCartera, montoTotal, montoCapital, totalOperaciones);
        }
    }
}
