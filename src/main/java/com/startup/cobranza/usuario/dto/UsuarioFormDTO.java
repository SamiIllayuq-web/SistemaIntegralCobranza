package com.startup.cobranza.usuario.dto;

public class UsuarioFormDTO {
    private Long id;
    private String username;
    private String password;
    private String nombreCompleto;
    private String rol;

    public UsuarioFormDTO() {}

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

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String username;
        private String password;
        private String nombreCompleto;
        private String rol;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder nombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; return this; }
        public Builder rol(String rol) { this.rol = rol; return this; }
        public UsuarioFormDTO build() {
            UsuarioFormDTO dto = new UsuarioFormDTO();
            dto.setId(id);
            dto.setUsername(username);
            dto.setPassword(password);
            dto.setNombreCompleto(nombreCompleto);
            dto.setRol(rol);
            return dto;
        }
    }
}
