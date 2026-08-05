package com.startup.cobranza.operacion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BienEmbargadoDTO {

    private Long id;
    private Long operacionId;
    private String tipoBien;
    private String partidaRegistral;
    private String detalleGarantia;
    private String direccion;
    private String distrito;
    private String provincia;
    private String departamento;
    private String garantiaInscrita;
    private LocalDate fechaInscripcion;
    private LocalDate fechaPresentacionRrpp;
    private String asientoInscripcion;
    private BigDecimal montoMc;
    private String monedaMc;
    private String rango;
    private String detalleAcreedores;
    private String tipoPreferencia;
    private String titularPredio;
    private LocalDate fechaGeneracionMc;
    private LocalDate fechaPresentacionMc;
    private LocalDate fechaInadmisible;
    private LocalDate fechaAdmision;
    private String comentarioMc;

    public BienEmbargadoDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOperacionId() { return operacionId; }
    public void setOperacionId(Long operacionId) { this.operacionId = operacionId; }
    public String getTipoBien() { return tipoBien; }
    public void setTipoBien(String tipoBien) { this.tipoBien = tipoBien; }
    public String getPartidaRegistral() { return partidaRegistral; }
    public void setPartidaRegistral(String partidaRegistral) { this.partidaRegistral = partidaRegistral; }
    public String getDetalleGarantia() { return detalleGarantia; }
    public void setDetalleGarantia(String detalleGarantia) { this.detalleGarantia = detalleGarantia; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getGarantiaInscrita() { return garantiaInscrita; }
    public void setGarantiaInscrita(String garantiaInscrita) { this.garantiaInscrita = garantiaInscrita; }
    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDate fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
    public LocalDate getFechaPresentacionRrpp() { return fechaPresentacionRrpp; }
    public void setFechaPresentacionRrpp(LocalDate fechaPresentacionRrpp) { this.fechaPresentacionRrpp = fechaPresentacionRrpp; }
    public String getAsientoInscripcion() { return asientoInscripcion; }
    public void setAsientoInscripcion(String asientoInscripcion) { this.asientoInscripcion = asientoInscripcion; }
    public BigDecimal getMontoMc() { return montoMc; }
    public void setMontoMc(BigDecimal montoMc) { this.montoMc = montoMc; }
    public String getMonedaMc() { return monedaMc; }
    public void setMonedaMc(String monedaMc) { this.monedaMc = monedaMc; }
    public String getRango() { return rango; }
    public void setRango(String rango) { this.rango = rango; }
    public String getDetalleAcreedores() { return detalleAcreedores; }
    public void setDetalleAcreedores(String detalleAcreedores) { this.detalleAcreedores = detalleAcreedores; }
    public String getTipoPreferencia() { return tipoPreferencia; }
    public void setTipoPreferencia(String tipoPreferencia) { this.tipoPreferencia = tipoPreferencia; }
    public String getTitularPredio() { return titularPredio; }
    public void setTitularPredio(String titularPredio) { this.titularPredio = titularPredio; }
    public LocalDate getFechaGeneracionMc() { return fechaGeneracionMc; }
    public void setFechaGeneracionMc(LocalDate fechaGeneracionMc) { this.fechaGeneracionMc = fechaGeneracionMc; }
    public LocalDate getFechaPresentacionMc() { return fechaPresentacionMc; }
    public void setFechaPresentacionMc(LocalDate fechaPresentacionMc) { this.fechaPresentacionMc = fechaPresentacionMc; }
    public LocalDate getFechaInadmisible() { return fechaInadmisible; }
    public void setFechaInadmisible(LocalDate fechaInadmisible) { this.fechaInadmisible = fechaInadmisible; }
    public LocalDate getFechaAdmision() { return fechaAdmision; }
    public void setFechaAdmision(LocalDate fechaAdmision) { this.fechaAdmision = fechaAdmision; }
    public String getComentarioMc() { return comentarioMc; }
    public void setComentarioMc(String comentarioMc) { this.comentarioMc = comentarioMc; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long operacionId;
        private String tipoBien;
        private String partidaRegistral;
        private String detalleGarantia;
        private String direccion;
        private String distrito;
        private String provincia;
        private String departamento;
        private String garantiaInscrita;
        private LocalDate fechaInscripcion;
        private LocalDate fechaPresentacionRrpp;
        private String asientoInscripcion;
        private BigDecimal montoMc;
        private String monedaMc;
        private String rango;
        private String detalleAcreedores;
        private String tipoPreferencia;
        private String titularPredio;
        private LocalDate fechaGeneracionMc;
        private LocalDate fechaPresentacionMc;
        private LocalDate fechaInadmisible;
        private LocalDate fechaAdmision;
        private String comentarioMc;

        public Builder id(Long v) { id = v; return this; }
        public Builder operacionId(Long v) { operacionId = v; return this; }
        public Builder tipoBien(String v) { tipoBien = v; return this; }
        public Builder partidaRegistral(String v) { partidaRegistral = v; return this; }
        public Builder detalleGarantia(String v) { detalleGarantia = v; return this; }
        public Builder direccion(String v) { direccion = v; return this; }
        public Builder distrito(String v) { distrito = v; return this; }
        public Builder provincia(String v) { provincia = v; return this; }
        public Builder departamento(String v) { departamento = v; return this; }
        public Builder garantiaInscrita(String v) { garantiaInscrita = v; return this; }
        public Builder fechaInscripcion(LocalDate v) { fechaInscripcion = v; return this; }
        public Builder fechaPresentacionRrpp(LocalDate v) { fechaPresentacionRrpp = v; return this; }
        public Builder asientoInscripcion(String v) { asientoInscripcion = v; return this; }
        public Builder montoMc(BigDecimal v) { montoMc = v; return this; }
        public Builder monedaMc(String v) { monedaMc = v; return this; }
        public Builder rango(String v) { rango = v; return this; }
        public Builder detalleAcreedores(String v) { detalleAcreedores = v; return this; }
        public Builder tipoPreferencia(String v) { tipoPreferencia = v; return this; }
        public Builder titularPredio(String v) { titularPredio = v; return this; }
        public Builder fechaGeneracionMc(LocalDate v) { fechaGeneracionMc = v; return this; }
        public Builder fechaPresentacionMc(LocalDate v) { fechaPresentacionMc = v; return this; }
        public Builder fechaInadmisible(LocalDate v) { fechaInadmisible = v; return this; }
        public Builder fechaAdmision(LocalDate v) { fechaAdmision = v; return this; }
        public Builder comentarioMc(String v) { comentarioMc = v; return this; }

        public BienEmbargadoDTO build() {
            BienEmbargadoDTO dto = new BienEmbargadoDTO();
            dto.setId(id);
            dto.setOperacionId(operacionId);
            dto.setTipoBien(tipoBien);
            dto.setPartidaRegistral(partidaRegistral);
            dto.setDetalleGarantia(detalleGarantia);
            dto.setDireccion(direccion);
            dto.setDistrito(distrito);
            dto.setProvincia(provincia);
            dto.setDepartamento(departamento);
            dto.setGarantiaInscrita(garantiaInscrita);
            dto.setFechaInscripcion(fechaInscripcion);
            dto.setFechaPresentacionRrpp(fechaPresentacionRrpp);
            dto.setAsientoInscripcion(asientoInscripcion);
            dto.setMontoMc(montoMc);
            dto.setMonedaMc(monedaMc);
            dto.setRango(rango);
            dto.setDetalleAcreedores(detalleAcreedores);
            dto.setTipoPreferencia(tipoPreferencia);
            dto.setTitularPredio(titularPredio);
            dto.setFechaGeneracionMc(fechaGeneracionMc);
            dto.setFechaPresentacionMc(fechaPresentacionMc);
            dto.setFechaInadmisible(fechaInadmisible);
            dto.setFechaAdmision(fechaAdmision);
            dto.setComentarioMc(comentarioMc);
            return dto;
        }
    }
}
