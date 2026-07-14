package com.startup.cobranza.agencia.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgenciaDTO {
    private Long id;
    private String nombre;
    private String codigo;
    private String telefono;
    private String direccion;
    private Long empresaId;
    private String empresaNombre;
    private Boolean activo;
}
