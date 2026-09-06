package com.startup.cobranza.agencia.dto;

import jakarta.validation.constraints.NotBlank;

public class AgenciaFormDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String codigo;
    private String telefono;
    private String direccion;

    public AgenciaFormDTO() {}

    public AgenciaFormDTO(Long id, String nombre, String codigo, String telefono, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.telefono = telefono;
        this.direccion = direccion;
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

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String nombre;
        private String codigo;
        private String telefono;
        private String direccion;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder nombre(String nombre) { this.nombre = nombre; return this; }
        public Builder codigo(String codigo) { this.codigo = codigo; return this; }
        public Builder telefono(String telefono) { this.telefono = telefono; return this; }
        public Builder direccion(String direccion) { this.direccion = direccion; return this; }

        public AgenciaFormDTO build() {
            return new AgenciaFormDTO(id, nombre, codigo, telefono, direccion);
        }
    }
}
