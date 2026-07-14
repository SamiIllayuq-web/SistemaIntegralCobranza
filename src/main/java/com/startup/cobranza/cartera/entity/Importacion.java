package com.startup.cobranza.cartera.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "importaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Importacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "total_registros")
    private Integer totalRegistros;

    @Column(name = "registros_exitosos")
    private Integer registrosExitosos;

    @Column(name = "registros_fallidos")
    private Integer registrosFallidos;

    @Column(name = "empresa_id")
    private Long empresaId;

    @Column(name = "agencia_id")
    private Long agenciaId;

    @Column(nullable = false)
    private String estado;

    @Column(name = "usuario_importa")
    private String usuarioImporta;

    @Column(name = "fecha_importacion")
    private LocalDateTime fechaImportacion;

    @Column(columnDefinition = "TEXT")
    private String errores;

    @PrePersist
    protected void onCreate() {
        fechaImportacion = LocalDateTime.now();
    }
}
