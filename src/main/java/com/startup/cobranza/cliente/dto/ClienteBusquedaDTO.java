package com.startup.cobranza.cliente.dto;

import java.math.BigDecimal;

public class ClienteBusquedaDTO {

    private String nombre;
    private String dni;
    private Long empresaId;
    private String estado;
    private String estadoCartera;
    private String etapa;
    private Integer minMora;
    private Integer maxMora;
    private BigDecimal minMonto;
    private BigDecimal maxMonto;

    public ClienteBusquedaDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getEstadoCartera() { return estadoCartera; }
    public void setEstadoCartera(String estadoCartera) { this.estadoCartera = estadoCartera; }
    public String getEtapa() { return etapa; }
    public void setEtapa(String etapa) { this.etapa = etapa; }
    public Integer getMinMora() { return minMora; }
    public void setMinMora(Integer minMora) { this.minMora = minMora; }
    public Integer getMaxMora() { return maxMora; }
    public void setMaxMora(Integer maxMora) { this.maxMora = maxMora; }
    public BigDecimal getMinMonto() { return minMonto; }
    public void setMinMonto(BigDecimal minMonto) { this.minMonto = minMonto; }
    public BigDecimal getMaxMonto() { return maxMonto; }
    public void setMaxMonto(BigDecimal maxMonto) { this.maxMonto = maxMonto; }

    public boolean hasFiltrosAdicionales() {
        return empresaId != null || estado != null || estadoCartera != null || etapa != null
                || minMora != null || maxMora != null
                || minMonto != null || maxMonto != null;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String nombre;
        private String dni;
        private Long empresaId;
        private String estado;
        private String estadoCartera;
        private String etapa;
        private Integer minMora;
        private Integer maxMora;
        private BigDecimal minMonto;
        private BigDecimal maxMonto;

        public Builder nombre(String v) { nombre = v; return this; }
        public Builder dni(String v) { dni = v; return this; }
        public Builder empresaId(Long v) { empresaId = v; return this; }
        public Builder estado(String v) { estado = v; return this; }
        public Builder estadoCartera(String v) { estadoCartera = v; return this; }
        public Builder etapa(String v) { etapa = v; return this; }
        public Builder minMora(Integer v) { minMora = v; return this; }
        public Builder maxMora(Integer v) { maxMora = v; return this; }
        public Builder minMonto(BigDecimal v) { minMonto = v; return this; }
        public Builder maxMonto(BigDecimal v) { maxMonto = v; return this; }

        public ClienteBusquedaDTO build() {
            ClienteBusquedaDTO dto = new ClienteBusquedaDTO();
            dto.setNombre(nombre);
            dto.setDni(dni);
            dto.setEmpresaId(empresaId);
            dto.setEstado(estado);
            dto.setEstadoCartera(estadoCartera);
            dto.setEtapa(etapa);
            dto.setMinMora(minMora);
            dto.setMaxMora(maxMora);
            dto.setMinMonto(minMonto);
            dto.setMaxMonto(maxMonto);
            return dto;
        }
    }
}
