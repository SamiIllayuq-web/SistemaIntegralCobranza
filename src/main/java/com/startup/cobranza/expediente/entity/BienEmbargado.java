package com.startup.cobranza.expediente.entity;

import com.startup.cobranza.operacion.entity.Operacion;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bienes_embargados")
public class BienEmbargado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", nullable = true)
    private Expediente expediente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operacion_id")
    private Operacion operacion;

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

    @Column(name = "fecha_presentacion_mc")
    private LocalDate fechaPresentacionMc;

    @Column(name = "fecha_inadmisible")
    private LocalDate fechaInadmisible;

    @Column(name = "fecha_admision")
    private LocalDate fechaAdmision;

    @Column(name = "comentario_mc", columnDefinition = "TEXT")
    private String comentarioMc;

    @Column(name = "detalle_acreedores", columnDefinition = "TEXT")
    private String detalleAcreedores;

    @Column(name = "tipo_preferencia")
    private String tipoPreferencia;

    @Column(name = "titular_predio", columnDefinition = "TEXT")
    private String titularPredio;

    @Column(name = "fecha_generacion_mc")
    private LocalDate fechaGeneracionMc;

    @Column(name = "monto_mc", precision = 15, scale = 2)
    private BigDecimal montoMc;

    @Column(name = "moneda_mc", length = 10)
    private String monedaMc;

    @Column(name = "rango", length = 50)
    private String rango;

    public BienEmbargado() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Expediente getExpediente() { return expediente; }
    public void setExpediente(Expediente expediente) { this.expediente = expediente; }
    public Operacion getOperacion() { return operacion; }
    public void setOperacion(Operacion operacion) { this.operacion = operacion; }
    public String getTipoBien() { return tipoBien; }
    public void setTipoBien(String tipoBien) { this.tipoBien = tipoBien; }
    public String getPartidaRegistral() { return partidaRegistral; }
    public void setPartidaRegistral(String partidaRegistral) { this.partidaRegistral = partidaRegistral; }
    public String getDetalleGarantia() { return detalleGarantia; }
    public void setDetalleGarantia(String detalleGarantia) { this.detalleGarantia = detalleGarantia; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getGarantiaInscrita() { return garantiaInscrita; }
    public void setGarantiaInscrita(String garantiaInscrita) { this.garantiaInscrita = garantiaInscrita; }
    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDate fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
    public LocalDate getFechaPresentacionRrpp() { return fechaPresentacionRrpp; }
    public void setFechaPresentacionRrpp(LocalDate fechaPresentacionRrpp) { this.fechaPresentacionRrpp = fechaPresentacionRrpp; }
    public String getAsientoInscripcion() { return asientoInscripcion; }
    public void setAsientoInscripcion(String asientoInscripcion) { this.asientoInscripcion = asientoInscripcion; }
    public LocalDate getFechaPresentacionMc() { return fechaPresentacionMc; }
    public void setFechaPresentacionMc(LocalDate fechaPresentacionMc) { this.fechaPresentacionMc = fechaPresentacionMc; }
    public LocalDate getFechaInadmisible() { return fechaInadmisible; }
    public void setFechaInadmisible(LocalDate fechaInadmisible) { this.fechaInadmisible = fechaInadmisible; }
    public LocalDate getFechaAdmision() { return fechaAdmision; }
    public void setFechaAdmision(LocalDate fechaAdmision) { this.fechaAdmision = fechaAdmision; }
    public String getComentarioMc() { return comentarioMc; }
    public void setComentarioMc(String comentarioMc) { this.comentarioMc = comentarioMc; }
    public String getDetalleAcreedores() { return detalleAcreedores; }
    public void setDetalleAcreedores(String detalleAcreedores) { this.detalleAcreedores = detalleAcreedores; }
    public String getTipoPreferencia() { return tipoPreferencia; }
    public void setTipoPreferencia(String tipoPreferencia) { this.tipoPreferencia = tipoPreferencia; }
    public String getTitularPredio() { return titularPredio; }
    public void setTitularPredio(String titularPredio) { this.titularPredio = titularPredio; }
    public LocalDate getFechaGeneracionMc() { return fechaGeneracionMc; }
    public void setFechaGeneracionMc(LocalDate fechaGeneracionMc) { this.fechaGeneracionMc = fechaGeneracionMc; }
    public BigDecimal getMontoMc() { return montoMc; }
    public void setMontoMc(BigDecimal montoMc) { this.montoMc = montoMc; }
    public String getMonedaMc() { return monedaMc; }
    public void setMonedaMc(String monedaMc) { this.monedaMc = monedaMc; }
    public String getRango() { return rango; }
    public void setRango(String rango) { this.rango = rango; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Expediente expediente;
        private Operacion operacion;
        private String tipoBien;
        private String partidaRegistral;
        private String detalleGarantia;
        private String direccion;
        private String distrito;
        private String provincia;
        private String departamento;
        private String garantiaInscrita;
        private LocalDate fechaInscripcion;
        private LocalDate fechaPresentacionRrpp;
        private String asientoInscripcion;
        private LocalDate fechaPresentacionMc;
        private LocalDate fechaInadmisible;
        private LocalDate fechaAdmision;
        private String comentarioMc;
        private String detalleAcreedores;
        private String tipoPreferencia;
        private String titularPredio;
        private LocalDate fechaGeneracionMc;
        private BigDecimal montoMc;
        private String monedaMc;
        private String rango;

        public Builder id(Long v) { id = v; return this; }
        public Builder expediente(Expediente v) { expediente = v; return this; }
        public Builder operacion(Operacion v) { operacion = v; return this; }
        public Builder tipoBien(String v) { tipoBien = v; return this; }
        public Builder partidaRegistral(String v) { partidaRegistral = v; return this; }
        public Builder detalleGarantia(String v) { detalleGarantia = v; return this; }
        public Builder direccion(String v) { direccion = v; return this; }
        public Builder distrito(String v) { distrito = v; return this; }
        public Builder provincia(String v) { provincia = v; return this; }
        public Builder departamento(String v) { departamento = v; return this; }
        public Builder garantiaInscrita(String v) { garantiaInscrita = v; return this; }
        public Builder fechaInscripcion(LocalDate v) { fechaInscripcion = v; return this; }
        public Builder fechaPresentacionRrpp(LocalDate v) { fechaPresentacionRrpp = v; return this; }
        public Builder asientoInscripcion(String v) { asientoInscripcion = v; return this; }
        public Builder fechaPresentacionMc(LocalDate v) { fechaPresentacionMc = v; return this; }
        public Builder fechaInadmisible(LocalDate v) { fechaInadmisible = v; return this; }
        public Builder fechaAdmision(LocalDate v) { fechaAdmision = v; return this; }
        public Builder comentarioMc(String v) { comentarioMc = v; return this; }
        public Builder detalleAcreedores(String v) { detalleAcreedores = v; return this; }
        public Builder tipoPreferencia(String v) { tipoPreferencia = v; return this; }
        public Builder titularPredio(String v) { titularPredio = v; return this; }
        public Builder fechaGeneracionMc(LocalDate v) { fechaGeneracionMc = v; return this; }
        public Builder montoMc(BigDecimal v) { montoMc = v; return this; }
        public Builder monedaMc(String v) { monedaMc = v; return this; }
        public Builder rango(String v) { rango = v; return this; }

        public BienEmbargado build() {
            BienEmbargado e = new BienEmbargado();
            e.setId(id);
            e.setExpediente(expediente);
            e.setOperacion(operacion);
            e.setTipoBien(tipoBien);
            e.setPartidaRegistral(partidaRegistral);
            e.setDetalleGarantia(detalleGarantia);
            e.setDireccion(direccion);
            e.setDistrito(distrito);
            e.setProvincia(provincia);
            e.setDepartamento(departamento);
            e.setGarantiaInscrita(garantiaInscrita);
            e.setFechaInscripcion(fechaInscripcion);
            e.setFechaPresentacionRrpp(fechaPresentacionRrpp);
            e.setAsientoInscripcion(asientoInscripcion);
            e.setFechaPresentacionMc(fechaPresentacionMc);
            e.setFechaInadmisible(fechaInadmisible);
            e.setFechaAdmision(fechaAdmision);
            e.setComentarioMc(comentarioMc);
            e.setDetalleAcreedores(detalleAcreedores);
            e.setTipoPreferencia(tipoPreferencia);
            e.setTitularPredio(titularPredio);
            e.setFechaGeneracionMc(fechaGeneracionMc);
            e.setMontoMc(montoMc);
            e.setMonedaMc(monedaMc);
            e.setRango(rango);
            return e;
        }
    }
}
