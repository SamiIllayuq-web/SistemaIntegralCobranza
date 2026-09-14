package com.startup.cobranza.cartera.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "actividad_sistema")
public class ActividadSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo; // CLIENTE_CREADO | CLIENTE_ELIMINADO | OPERACION_ELIMINADA

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column
    private Long entidadId; // id del cliente o operación afectada

    @Column
    private String entidadNombre; // nombre del cliente o nro de operación

    @Column
    private String detalle; // JSON con datos adicionales

    @Column
    private String usuario;

    public ActividadSistema() {
        this.fecha = LocalDateTime.now();
    }

    public ActividadSistema(String tipo, Long entidadId, String entidadNombre, String detalle, String usuario) {
        this.tipo = tipo;
        this.fecha = LocalDateTime.now();
        this.entidadId = entidadId;
        this.entidadNombre = entidadNombre;
        this.detalle = detalle;
        this.usuario = usuario;
    }

    // ─── Getters & Setters ──────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public Long getEntidadId() { return entidadId; }
    public void setEntidadId(Long entidadId) { this.entidadId = entidadId; }
    public String getEntidadNombre() { return entidadNombre; }
    public void setEntidadNombre(String entidadNombre) { this.entidadNombre = entidadNombre; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}
