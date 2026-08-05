package com.startup.cobranza.cliente.dto;

import java.time.LocalDateTime;

public class ClienteDTO {
    private Long id;
    private String nombreCompleto;
    private String dni;
    private String telefono;
    private String telefono2;
    private String telefono3;
    private String direccion;
    private String email;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public ClienteDTO() {}

    public ClienteDTO(Long id, String nombreCompleto, String dni, String telefono, String telefono2,
                       String telefono3, String direccion, String email, Boolean activo,
                       LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.telefono = telefono;
        this.telefono2 = telefono2;
        this.telefono3 = telefono3;
        this.direccion = direccion;
        this.email = email;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getTelefono2() { return telefono2; }
    public void setTelefono2(String telefono2) { this.telefono2 = telefono2; }
    public String getTelefono3() { return telefono3; }
    public void setTelefono3(String telefono3) { this.telefono3 = telefono3; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String nombreCompleto;
        private String dni;
        private String telefono;
        private String telefono2;
        private String telefono3;
        private String direccion;
        private String email;
        private Boolean activo;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;

        public Builder id(Long v) { id = v; return this; }
        public Builder nombreCompleto(String v) { nombreCompleto = v; return this; }
        public Builder dni(String v) { dni = v; return this; }
        public Builder telefono(String v) { telefono = v; return this; }
        public Builder telefono2(String v) { telefono2 = v; return this; }
        public Builder telefono3(String v) { telefono3 = v; return this; }
        public Builder direccion(String v) { direccion = v; return this; }
        public Builder email(String v) { email = v; return this; }
        public Builder activo(Boolean v) { activo = v; return this; }
        public Builder fechaCreacion(LocalDateTime v) { fechaCreacion = v; return this; }
        public Builder fechaActualizacion(LocalDateTime v) { fechaActualizacion = v; return this; }

        public ClienteDTO build() {
            return new ClienteDTO(id, nombreCompleto, dni, telefono, telefono2, telefono3,
                    direccion, email, activo, fechaCreacion, fechaActualizacion);
        }
    }
}
