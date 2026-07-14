package com.startup.cobranza.gestion.dto;

import com.startup.cobranza.gestion.entity.TipoGestion;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GestionFormDTO {

    @NotNull(message = "El tipo de gestión es obligatorio")
    private TipoGestion tipo;

    private LocalDateTime fechaGestion;

    private String observaciones;

    private BigDecimal montoCompromiso;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime fechaCompromiso;

    private Long clienteId;
}
