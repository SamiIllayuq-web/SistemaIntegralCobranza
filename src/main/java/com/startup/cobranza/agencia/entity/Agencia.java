package com.startup.cobranza.agencia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agencias")
public class Agencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "codigo", unique = true)
    private String codigo;

    private String telefono;

    private String direccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private com.startup.cobranza.empresa.entity.Empresa empresa;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    public Agencia() {}

    public Agencia(Long id, String nombre, String codigo, String telefono, String direccion,
                   com.startup.cobranza.empresa.entity.Empresa empresa, Boolean activo, LocalDateTime fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.empresa = empresa;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public com.startup.cobranza.empresa.entity.Empresa getEmpresa() { return empresa; }
    public void setEmpresa(com.startup.cobranza.empresa.entity.Empresa empresa) { this.empresa = empresa; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String nombre;
        private String codigo;
        private String telefono;
        private String direccion;
        private com.startup.cobranza.empresa.entity.Empresa empresa;
        private Boolean activo;
        private LocalDateTime fechaCreacion;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder nombre(String nombre) { this.nombre = nombre; return this; }
        public Builder codigo(String codigo) { this.codigo = codigo; return this; }
        public Builder telefono(String telefono) { this.telefono = telefono; return this; }
        public Builder direccion(String direccion) { this.direccion = direccion; return this; }
        public Builder empresa(com.startup.cobranza.empresa.entity.Empresa empresa) { this.empresa = empresa; return this; }
        public Builder activo(Boolean activo) { this.activo = activo; return this; }
        public Builder fechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; return this; }

        public Agencia build() {
            return new Agencia(id, nombre, codigo, telefono, direccion, empresa, activo, fechaCreacion);
        }
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
