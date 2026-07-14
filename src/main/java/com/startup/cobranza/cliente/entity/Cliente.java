package com.startup.cobranza.cliente.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(name = "dni")
    private String dni;

    @Column(name = "numero_cuenta")
    private String numeroCuenta;

    @Column(name = "numero_operacion")
    private String numeroOperacion;

    @Column(name = "deuda_capital", precision = 15, scale = 2)
    private BigDecimal deudaCapital;

    @Column(name = "deuda_total", precision = 15, scale = 2)
    private BigDecimal deudaTotal;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "telefono2")
    private String telefono2;

    @Column(name = "telefono3")
    private String telefono3;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "estado_gestion")
    private String estadoGestion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private com.startup.cobranza.empresa.entity.Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agencia_id")
    private com.startup.cobranza.agencia.entity.Agencia agencia;

    @Column(name = "fecha_ultima_gestion")
    private LocalDateTime fechaUltimaGestion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(nullable = false)
    private Boolean activo = true;

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
