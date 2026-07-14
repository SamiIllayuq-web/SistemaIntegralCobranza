package com.startup.cobranza.empresa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaFormDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String ruc;

    private String telefono;

    private String email;

    private String direccion;
}
