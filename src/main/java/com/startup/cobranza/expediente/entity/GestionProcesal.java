package com.startup.cobranza.expediente.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gestiones_procesales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GestionProcesal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", nullable = false)
    private Expediente expediente;

    @Column(name = "tipo_gestion", nullable = false)
    private String tipoGestion;

    @Column(name = "etapa")
    private String etapa;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
