package com.startup.cobranza.cartera.entity;

import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.cliente.entity.Cliente;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "operaciones",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_operacion_cuenta_numero",
                        columnNames = {"cuenta", "numero_operacion"})
        },
        indexes = {
                @Index(name = "idx_operacion_cliente_id", columnList = "cliente_id"),
                @Index(name = "idx_operacion_agencia_id", columnList = "agencia_id"),
                @Index(name = "idx_operacion_numero_expediente", columnList = "numero_expediente"),
                @Index(name = "idx_operacion_situacion", columnList = "situacion")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Operacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === DATOS DE OPERACIÓN (columnas D, E del Excel) ===
    @Column(name = "cuenta", nullable = false, length = 100)
    private String cuenta;

    @Column(name = "numero_operacion", nullable = false, length = 100)
    private String numeroOperacion;

    // === RELACIONES ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agencia_id")
    private Agencia agencia;

    // === DATOS DEL ABOGADO (columna B) ===
    @Column(name = "abogado_nombre", length = 255)
    private String abogadoNombre;

    // === BANDERAS Y DATOS SIMPLES (columnas H, I, J, L, M) ===
    @Column(name = "transferido", length = 10)
    private String transferido;  // SI / NO

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "situacion", length = 100)
    private String situacion;

    @Column(name = "moneda", length = 20)
    private String moneda;

    @Column(name = "busqueda_bienes", length = 50)
    private String busquedaBienes;  // POSITIVO / NEGATIVO

    // === DEUDA (columnas N, O) ===
    @Column(name = "deuda_cap", precision = 18, scale = 2)
    private BigDecimal deudaCap;

    @Column(name = "deuda_total", precision = 18, scale = 2)
    private BigDecimal deudaTotal;

    // === DATOS JUDICIALES (columnas P-AB) ===
    @Column(name = "tipo_proceso", length = 100)
    private String tipoProceso;

    @Column(name = "tipo_juzgado", length = 100)
    private String tipoJuzgado;

    @Column(name = "distrito_judicial", length = 100)
    private String distritoJudicial;

    @Column(name = "numero_juzgado", length = 100)
    private String numeroJuzgado;

    @Column(name = "numero_expediente", length = 100)
    private String numeroExpediente;

    @Column(name = "tiene_incidente")
    private Boolean tieneIncidente;

    @Column(name = "monto_demandado", precision = 18, scale = 2)
    private BigDecimal montoDemandado;

    @Column(name = "secretario_legal", length = 255)
    private String secretarioLegal;

    @Column(name = "codigo_expediente_cautelar", length = 100)
    private String codigoExpedienteCautelar;

    @Column(name = "detalle_bien_embargado", columnDefinition = "TEXT")
    private String detalleBienEmbargado;

    @Column(name = "numero_partida", length = 100)
    private String numeroPartida;

    @Column(name = "tipo_bien_embargado", length = 50)
    private String tipoBienEmbargado;  // INMUEBLE, VEHICULO, NINGUNO

    @Column(name = "rango", length = 50)
    private String rango;  // PRIMER, SEGUNDO, TERCERO, NINGUNO

    // === ACREEDORES Y PREFERENCIA (columnas AC, AD) ===
    @Column(name = "detalle_acreedores", columnDefinition = "TEXT")
    private String detalleAcreedores;

    @Column(name = "tipo_preferente", length = 100)
    private String tipoPreferente;

    // === MEDIDA CAUTELAR (columnas AE-AJ) ===
    @Column(name = "monto_medida_cautelar", precision = 18, scale = 2)
    private BigDecimal montoMedidaCautelar;

    @Column(name = "moneda_mc", length = 20)
    private String monedaMc;

    @Column(name = "medida_cautelar_ejecutada", length = 50)
    private String medidaCautelarEjecutada;  // SI, NO, NINGUNO

    @Column(name = "fecha_inscripcion_embargo")
    private LocalDate fechaInscripcionEmbargo;

    @Column(name = "fecha_presentacion_titulo_rrpp")
    private LocalDate fechaPresentacionTituloRrpp;

    @Column(name = "asiento_inscripcion", length = 100)
    private String asientoInscripcion;

    @Column(name = "fecha_presentacion_mc")
    private LocalDate fechaPresentacionMc;

    // === ADMISIÓN E INADMISIÓN (columnas AK-AQ) ===
    @Column(name = "fecha_inadmisible")
    private LocalDate fechaInadmisible;

    @Column(name = "fecha_admision")
    private LocalDate fechaAdmision;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_presentacion")
    private LocalDate fechaPresentacion;

    @Column(name = "fecha_inadmisible_2")
    private LocalDate fechaInadmisible2;

    @Column(name = "fecha_admision_2")
    private LocalDate fechaAdmision2;

    // === AUDIENCIAS (columna AR) ===
    @Column(name = "audiencia_tipo", length = 100)
    private String audienciaTipo;

    // === RESOLUCIONES Y EJECUTORIA (columnas AS-AU) ===
    @Column(name = "fecha_auto_final")
    private LocalDate fechaAutoFinal;

    @Column(name = "fecha_ejecutoriada")
    private LocalDate fechaEjecutoriada;

    @Column(name = "fecha_nombramiento_peritos")
    private LocalDate fechaNombramientoPeritos;

    @Column(name = "fecha_nombramiento_martillero")
    private LocalDate fechaNombramientoMartillero;

    // === REMATES (columnas AW-AY) ===
    @Column(name = "fecha_remate_1")
    private LocalDate fechaRemate1;

    @Column(name = "fecha_remate_2")
    private LocalDate fechaRemate2;

    @Column(name = "fecha_remate_3")
    private LocalDate fechaRemate3;

    // === SEGUIMIENTO (columnas AZ, BA) ===
    @Column(name = "fecha_proximo_acto_procesal")
    private LocalDate fechaProximoActoProcesal;

    @Column(name = "comentario_procesal", columnDefinition = "TEXT")
    private String comentarioProcesal;

    // === AUDITORÍA ===
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
