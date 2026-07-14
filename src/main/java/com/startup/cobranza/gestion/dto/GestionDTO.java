package com.startup.cobranza.gestion.dto;

import com.startup.cobranza.gestion.entity.TipoGestion;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GestionDTO {

    private Long id;
    private TipoGestion tipo;
    private String tipoLabel;
    private LocalDateTime fechaGestion;
    private String observaciones;
    private BigDecimal montoCompromiso;
    private LocalDateTime fechaCompromiso;
    private Long clienteId;
    private String clienteNombre;
    private String usuarioRegistra;
    private LocalDateTime fechaRegistro;
}
