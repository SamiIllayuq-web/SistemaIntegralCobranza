package com.startup.cobranza.cliente.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {
    private Long id;
    private String nombreCompleto;
    private String dni;
    private String telefono;
    private String telefono2;
    private String telefono3;
    private String direccion;
    private Long empresaId;
    private String empresaNombre;
    private String fechaCreacion;
    private Boolean activo;
}
