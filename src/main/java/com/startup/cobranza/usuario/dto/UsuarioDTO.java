package com.startup.cobranza.usuario.dto;

public class UsuarioDTO {
    private Long id;
    private String username;
    private String password;
    private String nombreCompleto;
    private String rol;
    private Boolean activo;

    public UsuarioDTO() {}

    public UsuarioDTO(Long id, String username, String password, String nombreCompleto, String rol, Boolean activo) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.activo = activo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String username;
        private String password;
        private String nombreCompleto;
        private String rol;
        private Boolean activo;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder nombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; return this; }
        public Builder rol(String rol) { this.rol = rol; return this; }
        public Builder activo(Boolean activo) { this.activo = activo; return this; }
        public UsuarioDTO build() { return new UsuarioDTO(id, username, password, nombreCompleto, rol, activo); }
    }
}
