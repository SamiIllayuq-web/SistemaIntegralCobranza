package com.startup.cobranza.cliente.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteFormDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombreCompleto;

    private String dni;

    private String telefono;

    private String telefono2;

    private String telefono3;

    private String direccion;

    @NotNull(message = "La empresa es obligatoria")
    private Long empresaId;

    private Long agenciaId;
}
