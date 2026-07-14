package com.startup.cobranza.cartera.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportacionDTO {
    private Long id;
    private String nombreArchivo;
    private Integer totalRegistros;
    private Integer registrosExitosos;
    private Integer registrosFallidos;
    private Long empresaId;
    private String empresaNombre;
    private Long agenciaId;
    private String agenciaNombre;
    private String estado;
    private String usuarioImporta;
    private String fechaImportacion;
    private String errores;
}
