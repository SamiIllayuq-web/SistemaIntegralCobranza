package com.startup.cobranza.auditoria.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_eventos")
public class AuditoriaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario")
    private String usuario;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "objeto_tipo")
    private String objetoTipo;

    @Column(name = "objeto_id")
    private Long objetoId;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    public AuditoriaEvento() {}

    public AuditoriaEvento(String usuario, String tipo, String objetoTipo, Long objetoId,
                           String payload, String descripcion) {
        this.usuario = usuario;
        this.tipo = tipo;
        this.objetoTipo = objetoTipo;
        this.objetoId = objetoId;
        this.payload = payload;
        this.descripcion = descripcion;
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getObjetoTipo() { return objetoTipo; }
    public void setObjetoTipo(String objetoTipo) { this.objetoTipo = objetoTipo; }
    public Long getObjetoId() { return objetoId; }
    public void setObjetoId(Long objetoId) { this.objetoId = objetoId; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String usuario;
        private String tipo;
        private String objetoTipo;
        private Long objetoId;
        private String payload;
        private String descripcion;

        public Builder usuario(String v) { usuario = v; return this; }
        public Builder tipo(String v) { tipo = v; return this; }
        public Builder objetoTipo(String v) { objetoTipo = v; return this; }
        public Builder objetoId(Long v) { objetoId = v; return this; }
        public Builder payload(String v) { payload = v; return this; }
        public Builder descripcion(String v) { descripcion = v; return this; }

        public AuditoriaEvento build() {
            return new AuditoriaEvento(usuario, tipo, objetoTipo, objetoId, payload, descripcion);
        }
    }
}
