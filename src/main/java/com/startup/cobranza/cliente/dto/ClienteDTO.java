package com.startup.cobranza.cliente.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {
    private Long id;
    private String nombreCompleto;
    private String dni;
    private String numeroCuenta;
    private String numeroOperacion;
    private BigDecimal deudaCapital;
    private BigDecimal deudaTotal;
    private String telefono;
    private String telefono2;
    private String telefono3;
    private String direccion;
    private String estadoGestion;
    private String observaciones;
    private Long empresaId;
    private String empresaNombre;
    private Long agenciaId;
    private String agenciaNombre;
    private String fechaUltimaGestion;
    private String fechaCreacion;
    private Boolean activo;
}
