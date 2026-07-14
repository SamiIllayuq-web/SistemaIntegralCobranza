package com.startup.cobranza.gestion.entity;

import com.startup.cobranza.cliente.entity.Cliente;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gestiones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoGestion tipo;

    @Column(name = "fecha_gestion", nullable = false)
    private LocalDateTime fechaGestion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "monto_compromiso", precision = 15, scale = 2)
    private BigDecimal montoCompromiso;

    @Column(name = "fecha_compromiso")
    private LocalDateTime fechaCompromiso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "usuario_registra")
    private String usuarioRegistra;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        if (fechaGestion == null) {
            fechaGestion = LocalDateTime.now();
        }
    }
}
