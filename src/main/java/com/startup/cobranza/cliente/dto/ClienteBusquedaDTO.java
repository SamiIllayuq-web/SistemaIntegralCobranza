package com.startup.cobranza.cliente.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteBusquedaDTO {
    private String nombre;
    private String dni;
    private String numeroCuenta;
    private String numeroOperacion;
    private Long empresaId;
    private Long agenciaId;
}
