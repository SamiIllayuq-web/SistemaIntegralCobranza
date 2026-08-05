package com.startup.cobranza.expediente.entity;

import com.startup.cobranza.usuario.entity.Usuario;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes_mc")
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

    public ReporteMc() {}

    public ReporteMc(Long id, com.startup.cobranza.empresa.entity.Empresa empresa, String nombreArchivo,
                      String mes, Integer anio, LocalDateTime fechaGeneracion, Usuario generadoPor) {
        this.id = id;
        this.empresa = empresa;
        this.nombreArchivo = nombreArchivo;
        this.mes = mes;
        this.anio = anio;
        this.fechaGeneracion = fechaGeneracion;
        this.generadoPor = generadoPor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public com.startup.cobranza.empresa.entity.Empresa getEmpresa() { return empresa; }
    public void setEmpresa(com.startup.cobranza.empresa.entity.Empresa empresa) { this.empresa = empresa; }
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
    public String getMes() { return mes; }
    public void setMes(String mes) { this.mes = mes; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public Usuario getGeneradoPor() { return generadoPor; }
    public void setGeneradoPor(Usuario generadoPor) { this.generadoPor = generadoPor; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private com.startup.cobranza.empresa.entity.Empresa empresa;
        private String nombreArchivo;
        private String mes;
        private Integer anio;
        private LocalDateTime fechaGeneracion;
        private Usuario generadoPor;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder empresa(com.startup.cobranza.empresa.entity.Empresa empresa) { this.empresa = empresa; return this; }
        public Builder nombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; return this; }
        public Builder mes(String mes) { this.mes = mes; return this; }
        public Builder anio(Integer anio) { this.anio = anio; return this; }
        public Builder fechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; return this; }
        public Builder generadoPor(Usuario generadoPor) { this.generadoPor = generadoPor; return this; }

        public ReporteMc build() {
            return new ReporteMc(id, empresa, nombreArchivo, mes, anio, fechaGeneracion, generadoPor);
        }
    }

    @PrePersist
    protected void onCreate() {
        fechaGeneracion = LocalDateTime.now();
    }
}
