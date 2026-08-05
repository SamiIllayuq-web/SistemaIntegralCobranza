package com.startup.cobranza.empresa.dto;

import jakarta.validation.constraints.NotBlank;

public class EmpresaFormDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String ruc;

    private String telefono;

    private String email;

    private String direccion;

    public EmpresaFormDTO() {}

    public EmpresaFormDTO(Long id, String nombre, String ruc, String telefono, String email, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.ruc = ruc;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String nombre;
        private String ruc;
        private String telefono;
        private String email;
        private String direccion;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder nombre(String nombre) { this.nombre = nombre; return this; }
        public Builder ruc(String ruc) { this.ruc = ruc; return this; }
        public Builder telefono(String telefono) { this.telefono = telefono; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder direccion(String direccion) { this.direccion = direccion; return this; }

        public EmpresaFormDTO build() {
            return new EmpresaFormDTO(id, nombre, ruc, telefono, email, direccion);
        }
    }
}
