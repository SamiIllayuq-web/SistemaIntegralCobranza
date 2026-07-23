package com.startup.cobranza.expediente.entity;

import com.startup.cobranza.empresa.entity.Empresa;
import com.startup.cobranza.cliente.entity.Cliente;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "expedientes_clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpedienteCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", nullable = false)
    private Expediente expediente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "nombre_completo", nullable = false, columnDefinition = "TEXT")
    private String nombreCompleto;

    @Column(name = "dni")
    private String dni;

    @Column(name = "cuenta")
    private String cuenta;

    @Column(name = "operacion")
    private String operacion;

    @Column(name = "co_titular_aval")
    private String coTitularAval;

    @Column(name = "trans")
    private String trans;

    @Column(name = "moneda")
    private String moneda;

    @Column(name = "deuda_capital", precision = 15, scale = 2)
    private BigDecimal deudaCapital;

    @Column(name = "deuda_total", precision = 15, scale = 2)
    private BigDecimal deudaTotal;

    @Column(name = "busqueda_bienes", columnDefinition = "TEXT")
    private String busquedaBienes;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;
}
