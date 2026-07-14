package com.startup.cobranza.empresa.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaDTO {
    private Long id;
    private String nombre;
    private String ruc;
    private String telefono;
    private String email;
    private String direccion;
    private Boolean activo;
}