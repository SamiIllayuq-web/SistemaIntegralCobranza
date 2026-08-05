package com.startup.cobranza.gestion.entity;

import com.startup.cobranza.cliente.entity.Cliente;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gestiones")
public class Gestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoGestion tipo;

    @Column(name = "fecha_gestion", nullable = false)
    private LocalDateTime fechaGestion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "monto_compromiso", precision = 15, scale = 2)
    private BigDecimal montoCompromiso;

    @Column(name = "fecha_compromiso")
    private LocalDateTime fechaCompromiso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "usuario_registra")
    private String usuarioRegistra;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    public Gestion() {}

    public Gestion(Long id, TipoGestion tipo, LocalDateTime fechaGestion, String observaciones,
                   BigDecimal montoCompromiso, LocalDateTime fechaCompromiso, Cliente cliente,
                   String usuarioRegistra, LocalDateTime fechaRegistro) {
        this.id = id;
        this.tipo = tipo;
        this.fechaGestion = fechaGestion;
        this.observaciones = observaciones;
        this.montoCompromiso = montoCompromiso;
        this.fechaCompromiso = fechaCompromiso;
        this.cliente = cliente;
        this.usuarioRegistra = usuarioRegistra;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public String getUsuarioRegistra() { return usuarioRegistra; }
    public void setUsuarioRegistra(String usuarioRegistra) { this.usuarioRegistra = usuarioRegistra; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private TipoGestion tipo;
        private LocalDateTime fechaGestion;
        private String observaciones;
        private BigDecimal montoCompromiso;
        private LocalDateTime fechaCompromiso;
        private Cliente cliente;
        private String usuarioRegistra;
        private LocalDateTime fechaRegistro;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder tipo(TipoGestion tipo) { this.tipo = tipo; return this; }
        public Builder fechaGestion(LocalDateTime fechaGestion) { this.fechaGestion = fechaGestion; return this; }
        public Builder observaciones(String observaciones) { this.observaciones = observaciones; return this; }
        public Builder montoCompromiso(BigDecimal montoCompromiso) { this.montoCompromiso = montoCompromiso; return this; }
        public Builder fechaCompromiso(LocalDateTime fechaCompromiso) { this.fechaCompromiso = fechaCompromiso; return this; }
        public Builder cliente(Cliente cliente) { this.cliente = cliente; return this; }
        public Builder usuarioRegistra(String usuarioRegistra) { this.usuarioRegistra = usuarioRegistra; return this; }
        public Builder fechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; return this; }

        public Gestion build() {
            return new Gestion(id, tipo, fechaGestion, observaciones, montoCompromiso, fechaCompromiso,
                    cliente, usuarioRegistra, fechaRegistro);
        }
    }

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        if (fechaGestion == null) {
            fechaGestion = LocalDateTime.now();
        }
    }
}
