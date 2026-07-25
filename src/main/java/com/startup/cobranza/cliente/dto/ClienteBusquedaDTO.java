package com.startup.cobranza.cliente.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteBusquedaDTO {
    private String nombre;
    private String dni;
    private Long empresaId;
    private Long agenciaId;
}
