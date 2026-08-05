package com.startup.cobranza.expediente.entity;

import com.startup.cobranza.empresa.entity.Empresa;
import com.startup.cobranza.cliente.entity.Cliente;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "expedientes_clientes")
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

    public ExpedienteCliente() {}

    public ExpedienteCliente(Long id, Expediente expediente, Cliente cliente, String tipo, String nombreCompleto,
                             String dni, String cuenta, String operacion, String coTitularAval, String trans,
                             String moneda, BigDecimal deudaCapital, BigDecimal deudaTotal,
                             String busquedaBienes, String observacion) {
        this.id = id;
        this.expediente = expediente;
        this.cliente = cliente;
        this.tipo = tipo;
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.cuenta = cuenta;
        this.operacion = operacion;
        this.coTitularAval = coTitularAval;
        this.trans = trans;
        this.moneda = moneda;
        this.deudaCapital = deudaCapital;
        this.deudaTotal = deudaTotal;
        this.busquedaBienes = busquedaBienes;
        this.observacion = observacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Expediente getExpediente() { return expediente; }
    public void setExpediente(Expediente expediente) { this.expediente = expediente; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getCuenta() { return cuenta; }
    public void setCuenta(String cuenta) { this.cuenta = cuenta; }
    public String getOperacion() { return operacion; }
    public void setOperacion(String operacion) { this.operacion = operacion; }
    public String getCoTitularAval() { return coTitularAval; }
    public void setCoTitularAval(String coTitularAval) { this.coTitularAval = coTitularAval; }
    public String getTrans() { return trans; }
    public void setTrans(String trans) { this.trans = trans; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public BigDecimal getDeudaCapital() { return deudaCapital; }
    public void setDeudaCapital(BigDecimal deudaCapital) { this.deudaCapital = deudaCapital; }
    public BigDecimal getDeudaTotal() { return deudaTotal; }
    public void setDeudaTotal(BigDecimal deudaTotal) { this.deudaTotal = deudaTotal; }
    public String getBusquedaBienes() { return busquedaBienes; }
    public void setBusquedaBienes(String busquedaBienes) { this.busquedaBienes = busquedaBienes; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Expediente expediente;
        private Cliente cliente;
        private String tipo;
        private String nombreCompleto;
        private String dni;
        private String cuenta;
        private String operacion;
        private String coTitularAval;
        private String trans;
        private String moneda;
        private BigDecimal deudaCapital;
        private BigDecimal deudaTotal;
        private String busquedaBienes;
        private String observacion;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder expediente(Expediente expediente) { this.expediente = expediente; return this; }
        public Builder cliente(Cliente cliente) { this.cliente = cliente; return this; }
        public Builder tipo(String tipo) { this.tipo = tipo; return this; }
        public Builder nombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; return this; }
        public Builder dni(String dni) { this.dni = dni; return this; }
        public Builder cuenta(String cuenta) { this.cuenta = cuenta; return this; }
        public Builder operacion(String operacion) { this.operacion = operacion; return this; }
        public Builder coTitularAval(String coTitularAval) { this.coTitularAval = coTitularAval; return this; }
        public Builder trans(String trans) { this.trans = trans; return this; }
        public Builder moneda(String moneda) { this.moneda = moneda; return this; }
        public Builder deudaCapital(BigDecimal deudaCapital) { this.deudaCapital = deudaCapital; return this; }
        public Builder deudaTotal(BigDecimal deudaTotal) { this.deudaTotal = deudaTotal; return this; }
        public Builder busquedaBienes(String busquedaBienes) { this.busquedaBienes = busquedaBienes; return this; }
        public Builder observacion(String observacion) { this.observacion = observacion; return this; }

        public ExpedienteCliente build() {
            return new ExpedienteCliente(id, expediente, cliente, tipo, nombreCompleto, dni, cuenta, operacion,
                    coTitularAval, trans, moneda, deudaCapital, deudaTotal, busquedaBienes, observacion);
        }
    }
}
