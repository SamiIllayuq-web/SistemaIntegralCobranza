package com.startup.cobranza.expediente.entity;

import com.startup.cobranza.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes_mc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteMc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private com.startup.cobranza.empresa.entity.Empresa empresa;

    @Column(name = "nombre_archivo", columnDefinition = "TEXT")
    private String nombreArchivo;

    @Column(name = "mes", columnDefinition = "TEXT")
    private String mes;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generado_por")
    private Usuario generadoPor;

    @PrePersist
    protected void onCreate() {
        fechaGeneracion = LocalDateTime.now();
    }
}
