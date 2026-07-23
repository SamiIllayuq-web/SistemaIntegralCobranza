package com.startup.cobranza.expediente.entity;

import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.empresa.entity.Empresa;
import com.startup.cobranza.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expedientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agencia_id")
    private Agencia agencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_abogado_id")
    private Usuario abogado;

    @Column(name = "numero_expediente", unique = true, columnDefinition = "TEXT")
    private String numeroExpediente;

    @Column(name = "situacion", columnDefinition = "TEXT")
    private String situacion;

    @Column(name = "tipo_proceso", columnDefinition = "TEXT")
    private String tipoProceso;

    @Column(name = "tipo_juzgado", columnDefinition = "TEXT")
    private String tipoJuzgado;

    @Column(name = "distrito_judicial", columnDefinition = "TEXT")
    private String distritoJudicial;

    @Column(name = "numero_juzgado", columnDefinition = "TEXT")
    private String numeroJuzgado;

    @Column(name = "expediente_cautelar_codigo", columnDefinition = "TEXT")
    private String expedienteCautelarCodigo;

    @Column(name = "incidente", columnDefinition = "TEXT")
    private String incidente;

    @Column(name = "monto_demandado", precision = 15, scale = 2)
    private java.math.BigDecimal montoDemandado;

    @Column(name = "especialista_legal", columnDefinition = "TEXT")
    private String especialistaLegal;

    @Column(name = "etapa_procesal", columnDefinition = "TEXT")
    private String etapaProcesal;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "comentario_general", columnDefinition = "TEXT")
    private String comentarioGeneral;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "expediente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExpedienteCliente> clientes = new ArrayList<>();

    @OneToMany(mappedBy = "expediente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BienEmbargado> bienes = new ArrayList<>();

    @OneToMany(mappedBy = "expediente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GestionProcesal> gestiones = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
