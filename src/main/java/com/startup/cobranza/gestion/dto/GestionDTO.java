package com.startup.cobranza.gestion.dto;

import com.startup.cobranza.gestion.entity.TipoGestion;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GestionDTO {

    private Long id;
    private TipoGestion tipo;
    private String tipoLabel;
    private LocalDateTime fechaGestion;
    private String observaciones;
    private BigDecimal montoCompromiso;
    private LocalDateTime fechaCompromiso;
    private Long clienteId;
    private String clienteNombre;
    private String usuarioRegistra;
    private LocalDateTime fechaRegistro;

    public GestionDTO() {}

    public GestionDTO(Long id, TipoGestion tipo, String tipoLabel, LocalDateTime fechaGestion,
                      String observaciones, BigDecimal montoCompromiso, LocalDateTime fechaCompromiso,
                      Long clienteId, String clienteNombre, String usuarioRegistra, LocalDateTime fechaRegistro) {
        this.id = id;
        this.tipo = tipo;
        this.tipoLabel = tipoLabel;
        this.fechaGestion = fechaGestion;
        this.observaciones = observaciones;
        this.montoCompromiso = montoCompromiso;
        this.fechaCompromiso = fechaCompromiso;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.usuarioRegistra = usuarioRegistra;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TipoGestion getTipo() { return tipo; }
    public void setTipo(TipoGestion tipo) { this.tipo = tipo; }
    public String getTipoLabel() { return tipoLabel; }
    public void setTipoLabel(String tipoLabel) { this.tipoLabel = tipoLabel; }
    public LocalDateTime getFechaGestion() { return fechaGestion; }
    public void setFechaGestion(LocalDateTime fechaGestion) { this.fechaGestion = fechaGestion; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public BigDecimal getMontoCompromiso() { return montoCompromiso; }
    public void setMontoCompromiso(BigDecimal montoCompromiso) { this.montoCompromiso = montoCompromiso; }
    public LocalDateTime getFechaCompromiso() { return fechaCompromiso; }
    public void setFechaCompromiso(LocalDateTime fechaCompromiso) { this.fechaCompromiso = fechaCompromiso; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getUsuarioRegistra() { return usuarioRegistra; }
    public void setUsuarioRegistra(String usuarioRegistra) { this.usuarioRegistra = usuarioRegistra; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private TipoGestion tipo;
        private String tipoLabel;
        private LocalDateTime fechaGestion;
        private String observaciones;
        private BigDecimal montoCompromiso;
        private LocalDateTime fechaCompromiso;
        private Long clienteId;
        private String clienteNombre;
        private String usuarioRegistra;
        private LocalDateTime fechaRegistro;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder tipo(TipoGestion tipo) { this.tipo = tipo; return this; }
        public Builder tipoLabel(String tipoLabel) { this.tipoLabel = tipoLabel; return this; }
        public Builder fechaGestion(LocalDateTime fechaGestion) { this.fechaGestion = fechaGestion; return this; }
        public Builder observaciones(String observaciones) { this.observaciones = observaciones; return this; }
        public Builder montoCompromiso(BigDecimal montoCompromiso) { this.montoCompromiso = montoCompromiso; return this; }
        public Builder fechaCompromiso(LocalDateTime fechaCompromiso) { this.fechaCompromiso = fechaCompromiso; return this; }
        public Builder clienteId(Long clienteId) { this.clienteId = clienteId; return this; }
        public Builder clienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; return this; }
        public Builder usuarioRegistra(String usuarioRegistra) { this.usuarioRegistra = usuarioRegistra; return this; }
        public Builder fechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; return this; }

        public GestionDTO build() {
            return new GestionDTO(id, tipo, tipoLabel, fechaGestion, observaciones, montoCompromiso,
                    fechaCompromiso, clienteId, clienteNombre, usuarioRegistra, fechaRegistro);
        }
    }
}
