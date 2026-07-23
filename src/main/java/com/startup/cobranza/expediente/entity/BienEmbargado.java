package com.startup.cobranza.expediente.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bienes_embargados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BienEmbargado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", nullable = false)
    private Expediente expediente;

    @Column(name = "tipo_bien")
    private String tipoBien;

    @Column(name = "partida_registral")
    private String partidaRegistral;

    @Column(name = "detalle_garantia", columnDefinition = "TEXT")
    private String detalleGarantia;

    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;

    @Column(name = "distrito")
    private String distrito;

    @Column(name = "provincia")
    private String provincia;

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "garantia_inscrita")
    private String garantiaInscrita;

    @Column(name = "fecha_inscripcion")
    private LocalDate fechaInscripcion;

    @Column(name = "fecha_presentacion_rrpp")
    private LocalDate fechaPresentacionRrpp;

    @Column(name = "asiento_inscripcion", columnDefinition = "TEXT")
    private String asientoInscripcion;

    @Column(name = "monto_mc", precision = 15, scale = 2)
    private BigDecimal montoMc;

    @Column(name = "moneda_mc")
    private String monedaMc;

    @Column(name = "rango")
    private String rango;

    @Column(name = "detalle_acreedores", columnDefinition = "TEXT")
    private String detalleAcreedores;

    @Column(name = "tipo_preferencia")
    private String tipoPreferencia;

    @Column(name = "titular_predio", columnDefinition = "TEXT")
    private String titularPredio;

    @Column(name = "fecha_generacion_mc")
    private LocalDate fechaGeneracionMc;
}
