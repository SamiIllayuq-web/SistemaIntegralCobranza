package com.startup.cobranza.agencia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgenciaFormDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String codigo;

    private String telefono;

    private String direccion;

    private Long empresaId;
}
