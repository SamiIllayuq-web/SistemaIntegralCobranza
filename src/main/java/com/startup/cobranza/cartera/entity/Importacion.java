package com.startup.cobranza.cartera.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "importaciones")
public class Importacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "total_registros")
    private Integer totalRegistros;

    @Column(name = "registros_exitosos")
    private Integer registrosExitosos;

    @Column(name = "registros_fallidos")
    private Integer registrosFallidos;


    @Column(name = "agencia_id")
    private Long agenciaId;

    @Column(nullable = false)
    private String estado;

    @Column(name = "usuario_importa")
    private String usuarioImporta;

    @Column(name = "fecha_importacion")
    private LocalDateTime fechaImportacion;

    @Column(columnDefinition = "TEXT")
    private String errores;

    public Importacion() {}

    public Importacion(Long id, String nombreArchivo, Integer totalRegistros, Integer registrosExitosos,
                       Integer registrosFallidos, Long agenciaId, String estado,
                       String usuarioImporta, LocalDateTime fechaImportacion, String errores) {
        this.id = id;
        this.nombreArchivo = nombreArchivo;
        this.totalRegistros = totalRegistros;
        this.registrosExitosos = registrosExitosos;
        this.registrosFallidos = registrosFallidos;
        this.agenciaId = agenciaId;
        this.estado = estado;
        this.usuarioImporta = usuarioImporta;
        this.fechaImportacion = fechaImportacion;
        this.errores = errores;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
    public Integer getTotalRegistros() { return totalRegistros; }
    public void setTotalRegistros(Integer totalRegistros) { this.totalRegistros = totalRegistros; }
    public Integer getRegistrosExitosos() { return registrosExitosos; }
    public void setRegistrosExitosos(Integer registrosExitosos) { this.registrosExitosos = registrosExitosos; }
    public Integer getRegistrosFallidos() { return registrosFallidos; }
    public void setRegistrosFallidos(Integer registrosFallidos) { this.registrosFallidos = registrosFallidos; }
    public Long getAgenciaId() { return agenciaId; }
    public void setAgenciaId(Long agenciaId) { this.agenciaId = agenciaId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getUsuarioImporta() { return usuarioImporta; }
    public void setUsuarioImporta(String usuarioImporta) { this.usuarioImporta = usuarioImporta; }
    public LocalDateTime getFechaImportacion() { return fechaImportacion; }
    public void setFechaImportacion(LocalDateTime fechaImportacion) { this.fechaImportacion = fechaImportacion; }
    public String getErrores() { return errores; }
    public void setErrores(String errores) { this.errores = errores; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String nombreArchivo;
        private Integer totalRegistros;
        private Integer registrosExitosos;
        private Integer registrosFallidos;
        private Long agenciaId;
        private String estado;
        private String usuarioImporta;
        private LocalDateTime fechaImportacion;
        private String errores;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder nombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; return this; }
        public Builder totalRegistros(Integer totalRegistros) { this.totalRegistros = totalRegistros; return this; }
        public Builder registrosExitosos(Integer registrosExitosos) { this.registrosExitosos = registrosExitosos; return this; }
        public Builder registrosFallidos(Integer registrosFallidos) { this.registrosFallidos = registrosFallidos; return this; }
        public Builder agenciaId(Long agenciaId) { this.agenciaId = agenciaId; return this; }
        public Builder estado(String estado) { this.estado = estado; return this; }
        public Builder usuarioImporta(String usuarioImporta) { this.usuarioImporta = usuarioImporta; return this; }
        public Builder fechaImportacion(LocalDateTime fechaImportacion) { this.fechaImportacion = fechaImportacion; return this; }
        public Builder errores(String errores) { this.errores = errores; return this; }

        public Importacion build() {
            return new Importacion(id, nombreArchivo, totalRegistros, registrosExitosos, registrosFallidos,
                    agenciaId, estado, usuarioImporta, fechaImportacion, errores);
        }
    }

    @PrePersist
    protected void onCreate() {
        fechaImportacion = LocalDateTime.now();
    }
}
