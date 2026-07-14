package com.startup.cobranza.usuario.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Long id;
    private String username;
    private String password;
    private String nombreCompleto;
    private String rol;
    private Boolean activo;
}