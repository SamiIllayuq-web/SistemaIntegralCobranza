package com.startup.cobranza.expediente.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gestiones_procesales")
public class GestionProcesal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", nullable = false)
    private Expediente expediente;

    @Column(name = "tipo_gestion", nullable = false)
    private String tipoGestion;

    @Column(name = "etapa")
    private String etapa;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    public GestionProcesal() {}

    public GestionProcesal(Long id, Expediente expediente, String tipoGestion, String etapa, LocalDate fecha,
                           String observacion, LocalDateTime fechaRegistro) {
        this.id = id;
        this.expediente = expediente;
        this.tipoGestion = tipoGestion;
        this.etapa = etapa;
        this.fecha = fecha;
        this.observacion = observacion;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Expediente getExpediente() { return expediente; }
    public void setExpediente(Expediente expediente) { this.expediente = expediente; }
    public String getTipoGestion() { return tipoGestion; }
    public void setTipoGestion(String tipoGestion) { this.tipoGestion = tipoGestion; }
    public String getEtapa() { return etapa; }
    public void setEtapa(String etapa) { this.etapa = etapa; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Expediente expediente;
        private String tipoGestion;
        private String etapa;
        private LocalDate fecha;
        private String observacion;
        private LocalDateTime fechaRegistro;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder expediente(Expediente expediente) { this.expediente = expediente; return this; }
        public Builder tipoGestion(String tipoGestion) { this.tipoGestion = tipoGestion; return this; }
        public Builder etapa(String etapa) { this.etapa = etapa; return this; }
        public Builder fecha(LocalDate fecha) { this.fecha = fecha; return this; }
        public Builder observacion(String observacion) { this.observacion = observacion; return this; }
        public Builder fechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; return this; }

        public GestionProcesal build() {
            return new GestionProcesal(id, expediente, tipoGestion, etapa, fecha, observacion, fechaRegistro);
        }
    }

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
