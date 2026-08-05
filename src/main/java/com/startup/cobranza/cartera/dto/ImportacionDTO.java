package com.startup.cobranza.cartera.dto;

public class ImportacionDTO {
    private Long id;
    private String nombreArchivo;
    private Integer totalRegistros;
    private Integer registrosExitosos;
    private Integer registrosFallidos;
    private Long empresaId;
    private String empresaNombre;
    private Long agenciaId;
    private String agenciaNombre;
    private String estado;
    private String usuarioImporta;
    private String fechaImportacion;
    private String errores;

    public ImportacionDTO() {}

    public ImportacionDTO(Long id, String nombreArchivo, Integer totalRegistros, Integer registrosExitosos,
                          Integer registrosFallidos, Long empresaId, String empresaNombre, Long agenciaId,
                          String agenciaNombre, String estado, String usuarioImporta,
                          String fechaImportacion, String errores) {
        this.id = id;
        this.nombreArchivo = nombreArchivo;
        this.totalRegistros = totalRegistros;
        this.registrosExitosos = registrosExitosos;
        this.registrosFallidos = registrosFallidos;
        this.empresaId = empresaId;
        this.empresaNombre = empresaNombre;
        this.agenciaId = agenciaId;
        this.agenciaNombre = agenciaNombre;
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
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public String getEmpresaNombre() { return empresaNombre; }
    public void setEmpresaNombre(String empresaNombre) { this.empresaNombre = empresaNombre; }
    public Long getAgenciaId() { return agenciaId; }
    public void setAgenciaId(Long agenciaId) { this.agenciaId = agenciaId; }
    public String getAgenciaNombre() { return agenciaNombre; }
    public void setAgenciaNombre(String agenciaNombre) { this.agenciaNombre = agenciaNombre; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getUsuarioImporta() { return usuarioImporta; }
    public void setUsuarioImporta(String usuarioImporta) { this.usuarioImporta = usuarioImporta; }
    public String getFechaImportacion() { return fechaImportacion; }
    public void setFechaImportacion(String fechaImportacion) { this.fechaImportacion = fechaImportacion; }
    public String getErrores() { return errores; }
    public void setErrores(String errores) { this.errores = errores; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String nombreArchivo;
        private Integer totalRegistros;
        private Integer registrosExitosos;
        private Integer registrosFallidos;
        private Long empresaId;
        private String empresaNombre;
        private Long agenciaId;
        private String agenciaNombre;
        private String estado;
        private String usuarioImporta;
        private String fechaImportacion;
        private String errores;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder nombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; return this; }
        public Builder totalRegistros(Integer totalRegistros) { this.totalRegistros = totalRegistros; return this; }
        public Builder registrosExitosos(Integer registrosExitosos) { this.registrosExitosos = registrosExitosos; return this; }
        public Builder registrosFallidos(Integer registrosFallidos) { this.registrosFallidos = registrosFallidos; return this; }
        public Builder empresaId(Long empresaId) { this.empresaId = empresaId; return this; }
        public Builder empresaNombre(String empresaNombre) { this.empresaNombre = empresaNombre; return this; }
        public Builder agenciaId(Long agenciaId) { this.agenciaId = agenciaId; return this; }
        public Builder agenciaNombre(String agenciaNombre) { this.agenciaNombre = agenciaNombre; return this; }
        public Builder estado(String estado) { this.estado = estado; return this; }
        public Builder usuarioImporta(String usuarioImporta) { this.usuarioImporta = usuarioImporta; return this; }
        public Builder fechaImportacion(String fechaImportacion) { this.fechaImportacion = fechaImportacion; return this; }
        public Builder errores(String errores) { this.errores = errores; return this; }

        public ImportacionDTO build() {
            return new ImportacionDTO(id, nombreArchivo, totalRegistros, registrosExitosos, registrosFallidos,
                    empresaId, empresaNombre, agenciaId, agenciaNombre, estado, usuarioImporta,
                    fechaImportacion, errores);
        }
    }
}
