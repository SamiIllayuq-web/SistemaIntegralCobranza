package com.startup.cobranza.gestion.dto;

import com.startup.cobranza.gestion.entity.TipoGestion;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GestionFormDTO {

    @NotNull(message = "El tipo de gestión es obligatorio")
    private TipoGestion tipo;

    private LocalDateTime fechaGestion;

    private String observaciones;

    private BigDecimal montoCompromiso;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime fechaCompromiso;

    private Long clienteId;

    public GestionFormDTO() {}

    public GestionFormDTO(TipoGestion tipo, LocalDateTime fechaGestion, String observaciones,
                          BigDecimal montoCompromiso, LocalDateTime fechaCompromiso, Long clienteId) {
        this.tipo = tipo;
        this.fechaGestion = fechaGestion;
        this.observaciones = observaciones;
        this.montoCompromiso = montoCompromiso;
        this.fechaCompromiso = fechaCompromiso;
        this.clienteId = clienteId;
    }

    public TipoGestion getTipo() { return tipo; }
    public void setTipo(TipoGestion tipo) { this.tipo = tipo; }
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

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private TipoGestion tipo;
        private LocalDateTime fechaGestion;
        private String observaciones;
        private BigDecimal montoCompromiso;
        private LocalDateTime fechaCompromiso;
        private Long clienteId;

        public Builder tipo(TipoGestion tipo) { this.tipo = tipo; return this; }
        public Builder fechaGestion(LocalDateTime fechaGestion) { this.fechaGestion = fechaGestion; return this; }
        public Builder observaciones(String observaciones) { this.observaciones = observaciones; return this; }
        public Builder montoCompromiso(BigDecimal montoCompromiso) { this.montoCompromiso = montoCompromiso; return this; }
        public Builder fechaCompromiso(LocalDateTime fechaCompromiso) { this.fechaCompromiso = fechaCompromiso; return this; }
        public Builder clienteId(Long clienteId) { this.clienteId = clienteId; return this; }

        public GestionFormDTO build() {
            return new GestionFormDTO(tipo, fechaGestion, observaciones, montoCompromiso, fechaCompromiso, clienteId);
        }
    }
}
