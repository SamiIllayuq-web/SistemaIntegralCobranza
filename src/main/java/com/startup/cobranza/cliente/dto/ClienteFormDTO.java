package com.startup.cobranza.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ClienteFormDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombreCompleto;

    private String dni;

    private String telefono;
    private String telefono2;
    private String telefono3;
    private String direccion;

    @Email(message = "Email inválido")
    private String email;

    private Long empresaId;
    private Long agenciaId;

    public ClienteFormDTO() {}

    public ClienteFormDTO(Long id, String nombreCompleto, String dni, String telefono,
                          String telefono2, String telefono3, String direccion, String email,
                          Long empresaId, Long agenciaId) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.telefono = telefono;
        this.telefono2 = telefono2;
        this.telefono3 = telefono3;
        this.direccion = direccion;
        this.email = email;
        this.empresaId = empresaId;
        this.agenciaId = agenciaId;
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
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public Long getAgenciaId() { return agenciaId; }
    public void setAgenciaId(Long agenciaId) { this.agenciaId = agenciaId; }

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
        private Long empresaId;
        private Long agenciaId;

        public Builder id(Long v) { id = v; return this; }
        public Builder nombreCompleto(String v) { nombreCompleto = v; return this; }
        public Builder dni(String v) { dni = v; return this; }
        public Builder telefono(String v) { telefono = v; return this; }
        public Builder telefono2(String v) { telefono2 = v; return this; }
        public Builder telefono3(String v) { telefono3 = v; return this; }
        public Builder direccion(String v) { direccion = v; return this; }
        public Builder email(String v) { email = v; return this; }
        public Builder empresaId(Long v) { empresaId = v; return this; }
        public Builder agenciaId(Long v) { agenciaId = v; return this; }

        public ClienteFormDTO build() {
            return new ClienteFormDTO(id, nombreCompleto, dni, telefono, telefono2, telefono3, direccion, email, empresaId, agenciaId);
        }
    }
}
