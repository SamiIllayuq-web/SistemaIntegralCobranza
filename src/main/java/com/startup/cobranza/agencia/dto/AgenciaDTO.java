package com.startup.cobranza.agencia.dto;

public class AgenciaDTO {
    private Long id;
    private String nombre;
    private String codigo;
    private String telefono;
    private String direccion;
    private Boolean activo;

    public AgenciaDTO() {}

    public AgenciaDTO(Long id, String nombre, String codigo, String telefono, String direccion, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.activo = activo;
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
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String nombre;
        private String codigo;
        private String telefono;
        private String direccion;
        private Boolean activo;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder nombre(String nombre) { this.nombre = nombre; return this; }
        public Builder codigo(String codigo) { this.codigo = codigo; return this; }
        public Builder telefono(String telefono) { this.telefono = telefono; return this; }
        public Builder direccion(String direccion) { this.direccion = direccion; return this; }
        public Builder activo(Boolean activo) { this.activo = activo; return this; }

        public AgenciaDTO build() {
            return new AgenciaDTO(id, nombre, codigo, telefono, direccion, activo);
        }
    }
}
