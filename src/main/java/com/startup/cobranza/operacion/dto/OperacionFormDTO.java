package com.startup.cobranza.operacion.dto;

import java.math.BigDecimal;
import java.util.List;

public class OperacionFormDTO {

    private Long id;
    private Long clienteId;
    private Long empresaId;
    private Long agenciaId;
    private String cuenta;
    private String numeroOperacion;
    private BigDecimal montoCapital;
    private BigDecimal montoTotal;
    private Integer diasMora;
    private String moneda;
    private String tipoCredito;
    private String situacion;
    private String estado;
    private String etapa;
    private String observacion;
    private String rango;
    private String analista;
    private String analistaSenior;
    private String numeroExpediente;
    private String tipoProceso;
    private String tipoJuzgado;
    private String distritoJudicial;
    private String numeroJuzgado;
    private Long abogadoId;
    private String observacionActos;
    private String comentario;
    private String estadoCartera;
    private String fechaDesembolso;
    private String importeDesembolso;
    private String etapaProcesalTexto;
    private String actoPendiente;
    private String fechaUltimoEstadoProceso;
    private String zona;
    private String departamento;
    private String provincia;
    private String distrito;
    private String direccion;
    private String referencia;
    private String telefono;
    private String montoAprobado;
    private String fechaAceptacionDemanda;
    private String fechaEnvioJudicial;
    private String fechaAsignacionAbogado;
    private String fechaCastigo;
    private String tipoFondo;
    private List<BienEmbargadoDTO> bienesEmbargados;

    public OperacionFormDTO() {}

    public OperacionFormDTO(Long id, Long clienteId, Long empresaId, Long agenciaId,
                            String cuenta, String numeroOperacion, BigDecimal montoCapital,
                            BigDecimal montoTotal, Integer diasMora, String moneda,
                            String tipoCredito, String situacion, String estado, String etapa,
                            String observacion, String rango, String analista, String analistaSenior,
                            String numeroExpediente, String tipoProceso, String tipoJuzgado,
                            String distritoJudicial, String numeroJuzgado, Long abogadoId,
                            String observacionActos, String comentario) {
        this.id = id;
        this.clienteId = clienteId;
        this.empresaId = empresaId;
        this.agenciaId = agenciaId;
        this.cuenta = cuenta;
        this.numeroOperacion = numeroOperacion;
        this.montoCapital = montoCapital;
        this.montoTotal = montoTotal;
        this.diasMora = diasMora;
        this.moneda = moneda;
        this.tipoCredito = tipoCredito;
        this.situacion = situacion;
        this.estado = estado;
        this.etapa = etapa;
        this.observacion = observacion;
        this.rango = rango;
        this.analista = analista;
        this.analistaSenior = analistaSenior;
        this.numeroExpediente = numeroExpediente;
        this.tipoProceso = tipoProceso;
        this.tipoJuzgado = tipoJuzgado;
        this.distritoJudicial = distritoJudicial;
        this.numeroJuzgado = numeroJuzgado;
        this.abogadoId = abogadoId;
        this.observacionActos = observacionActos;
        this.comentario = comentario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public Long getAgenciaId() { return agenciaId; }
    public void setAgenciaId(Long agenciaId) { this.agenciaId = agenciaId; }
    public String getCuenta() { return cuenta; }
    public void setCuenta(String cuenta) { this.cuenta = cuenta; }
    public String getNumeroOperacion() { return numeroOperacion; }
    public void setNumeroOperacion(String numeroOperacion) { this.numeroOperacion = numeroOperacion; }
    public BigDecimal getMontoCapital() { return montoCapital; }
    public void setMontoCapital(BigDecimal montoCapital) { this.montoCapital = montoCapital; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public Integer getDiasMora() { return diasMora; }
    public void setDiasMora(Integer diasMora) { this.diasMora = diasMora; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public String getTipoCredito() { return tipoCredito; }
    public void setTipoCredito(String tipoCredito) { this.tipoCredito = tipoCredito; }
    public String getSituacion() { return situacion; }
    public void setSituacion(String situacion) { this.situacion = situacion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getEtapa() { return etapa; }
    public void setEtapa(String etapa) { this.etapa = etapa; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public String getRango() { return rango; }
    public void setRango(String rango) { this.rango = rango; }
    public String getAnalista() { return analista; }
    public void setAnalista(String analista) { this.analista = analista; }
    public String getAnalistaSenior() { return analistaSenior; }
    public void setAnalistaSenior(String analistaSenior) { this.analistaSenior = analistaSenior; }
    public String getNumeroExpediente() { return numeroExpediente; }
    public void setNumeroExpediente(String numeroExpediente) { this.numeroExpediente = numeroExpediente; }
    public String getTipoProceso() { return tipoProceso; }
    public void setTipoProceso(String tipoProceso) { this.tipoProceso = tipoProceso; }
    public String getTipoJuzgado() { return tipoJuzgado; }
    public void setTipoJuzgado(String tipoJuzgado) { this.tipoJuzgado = tipoJuzgado; }
    public String getDistritoJudicial() { return distritoJudicial; }
    public void setDistritoJudicial(String distritoJudicial) { this.distritoJudicial = distritoJudicial; }
    public String getNumeroJuzgado() { return numeroJuzgado; }
    public void setNumeroJuzgado(String numeroJuzgado) { this.numeroJuzgado = numeroJuzgado; }
    public Long getAbogadoId() { return abogadoId; }
    public void setAbogadoId(Long abogadoId) { this.abogadoId = abogadoId; }
    public String getObservacionActos() { return observacionActos; }
    public void setObservacionActos(String observacionActos) { this.observacionActos = observacionActos; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public String getEstadoCartera() { return estadoCartera; }
    public void setEstadoCartera(String estadoCartera) { this.estadoCartera = estadoCartera; }
    public String getFechaDesembolso() { return fechaDesembolso; }
    public void setFechaDesembolso(String fechaDesembolso) { this.fechaDesembolso = fechaDesembolso; }
    public String getImporteDesembolso() { return importeDesembolso; }
    public void setImporteDesembolso(String importeDesembolso) { this.importeDesembolso = importeDesembolso; }
    public String getEtapaProcesalTexto() { return etapaProcesalTexto; }
    public void setEtapaProcesalTexto(String etapaProcesalTexto) { this.etapaProcesalTexto = etapaProcesalTexto; }
    public String getActoPendiente() { return actoPendiente; }
    public void setActoPendiente(String actoPendiente) { this.actoPendiente = actoPendiente; }
    public String getFechaUltimoEstadoProceso() { return fechaUltimoEstadoProceso; }
    public void setFechaUltimoEstadoProceso(String fechaUltimoEstadoProceso) { this.fechaUltimoEstadoProceso = fechaUltimoEstadoProceso; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getMontoAprobado() { return montoAprobado; }
    public void setMontoAprobado(String montoAprobado) { this.montoAprobado = montoAprobado; }
    public String getFechaAceptacionDemanda() { return fechaAceptacionDemanda; }
    public void setFechaAceptacionDemanda(String fechaAceptacionDemanda) { this.fechaAceptacionDemanda = fechaAceptacionDemanda; }
    public String getFechaEnvioJudicial() { return fechaEnvioJudicial; }
    public void setFechaEnvioJudicial(String fechaEnvioJudicial) { this.fechaEnvioJudicial = fechaEnvioJudicial; }
    public String getFechaAsignacionAbogado() { return fechaAsignacionAbogado; }
    public void setFechaAsignacionAbogado(String fechaAsignacionAbogado) { this.fechaAsignacionAbogado = fechaAsignacionAbogado; }
    public String getFechaCastigo() { return fechaCastigo; }
    public void setFechaCastigo(String fechaCastigo) { this.fechaCastigo = fechaCastigo; }
    public String getTipoFondo() { return tipoFondo; }
    public void setTipoFondo(String tipoFondo) { this.tipoFondo = tipoFondo; }
    public List<BienEmbargadoDTO> getBienesEmbargados() { return bienesEmbargados; }
    public void setBienesEmbargados(List<BienEmbargadoDTO> bienesEmbargados) { this.bienesEmbargados = bienesEmbargados; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long clienteId;
        private Long empresaId;
        private Long agenciaId;
        private String cuenta;
        private String numeroOperacion;
        private BigDecimal montoCapital;
        private BigDecimal montoTotal;
        private Integer diasMora;
        private String moneda;
        private String tipoCredito;
        private String situacion;
        private String estado;
        private String etapa;
        private String observacion;
        private String rango;
        private String analista;
        private String analistaSenior;
        private String numeroExpediente;
        private String tipoProceso;
        private String tipoJuzgado;
        private String distritoJudicial;
        private String numeroJuzgado;
        private Long abogadoId;
        private String observacionActos;
        private String comentario;
        private String estadoCartera;
        private String fechaDesembolso;
        private String importeDesembolso;
        private String etapaProcesalTexto;
        private String actoPendiente;
        private String fechaUltimoEstadoProceso;
        private String zona;
        private String departamento;
        private String provincia;
        private String distrito;
        private String direccion;
        private String referencia;
        private String telefono;
        private String montoAprobado;
        private String fechaAceptacionDemanda;
        private String fechaEnvioJudicial;
        private String fechaAsignacionAbogado;
        private String fechaCastigo;
        private String tipoFondo;
        private List<BienEmbargadoDTO> bienesEmbargados;

        public Builder id(Long v) { id = v; return this; }
        public Builder clienteId(Long v) { clienteId = v; return this; }
        public Builder empresaId(Long v) { empresaId = v; return this; }
        public Builder agenciaId(Long v) { agenciaId = v; return this; }
        public Builder cuenta(String v) { cuenta = v; return this; }
        public Builder numeroOperacion(String v) { numeroOperacion = v; return this; }
        public Builder montoCapital(BigDecimal v) { montoCapital = v; return this; }
        public Builder montoTotal(BigDecimal v) { montoTotal = v; return this; }
        public Builder diasMora(Integer v) { diasMora = v; return this; }
        public Builder moneda(String v) { moneda = v; return this; }
        public Builder tipoCredito(String v) { tipoCredito = v; return this; }
        public Builder situacion(String v) { situacion = v; return this; }
        public Builder estado(String v) { estado = v; return this; }
        public Builder etapa(String v) { etapa = v; return this; }
        public Builder observacion(String v) { observacion = v; return this; }
        public Builder rango(String v) { rango = v; return this; }
        public Builder analista(String v) { analista = v; return this; }
        public Builder analistaSenior(String v) { analistaSenior = v; return this; }
        public Builder numeroExpediente(String v) { numeroExpediente = v; return this; }
        public Builder tipoProceso(String v) { tipoProceso = v; return this; }
        public Builder tipoJuzgado(String v) { tipoJuzgado = v; return this; }
        public Builder distritoJudicial(String v) { distritoJudicial = v; return this; }
        public Builder numeroJuzgado(String v) { numeroJuzgado = v; return this; }
        public Builder abogadoId(Long v) { abogadoId = v; return this; }
        public Builder observacionActos(String v) { observacionActos = v; return this; }
        public Builder comentario(String v) { comentario = v; return this; }
        public Builder estadoCartera(String v) { estadoCartera = v; return this; }
        public Builder fechaDesembolso(String v) { fechaDesembolso = v; return this; }
        public Builder importeDesembolso(String v) { importeDesembolso = v; return this; }
        public Builder etapaProcesalTexto(String v) { etapaProcesalTexto = v; return this; }
        public Builder actoPendiente(String v) { actoPendiente = v; return this; }
        public Builder fechaUltimoEstadoProceso(String v) { fechaUltimoEstadoProceso = v; return this; }
        public Builder zona(String v) { zona = v; return this; }
        public Builder departamento(String v) { departamento = v; return this; }
        public Builder provincia(String v) { provincia = v; return this; }
        public Builder distrito(String v) { distrito = v; return this; }
        public Builder direccion(String v) { direccion = v; return this; }
        public Builder referencia(String v) { referencia = v; return this; }
        public Builder telefono(String v) { telefono = v; return this; }
        public Builder montoAprobado(String v) { montoAprobado = v; return this; }
        public Builder fechaAceptacionDemanda(String v) { fechaAceptacionDemanda = v; return this; }
        public Builder fechaEnvioJudicial(String v) { fechaEnvioJudicial = v; return this; }
        public Builder fechaAsignacionAbogado(String v) { fechaAsignacionAbogado = v; return this; }
        public Builder fechaCastigo(String v) { fechaCastigo = v; return this; }
        public Builder tipoFondo(String v) { tipoFondo = v; return this; }
        public Builder bienesEmbargados(List<BienEmbargadoDTO> v) { bienesEmbargados = v; return this; }

        public OperacionFormDTO build() {
            OperacionFormDTO dto = new OperacionFormDTO(id, clienteId, empresaId, agenciaId, cuenta, numeroOperacion,
                    montoCapital, montoTotal, diasMora, moneda, tipoCredito, situacion, estado, etapa,
                    observacion, rango, analista, analistaSenior, numeroExpediente, tipoProceso,
                    tipoJuzgado, distritoJudicial, numeroJuzgado, abogadoId, observacionActos, comentario);
            dto.setEstadoCartera(estadoCartera);
            dto.setFechaDesembolso(fechaDesembolso);
            dto.setImporteDesembolso(importeDesembolso);
            dto.setEtapaProcesalTexto(etapaProcesalTexto);
            dto.setActoPendiente(actoPendiente);
            dto.setFechaUltimoEstadoProceso(fechaUltimoEstadoProceso);
            dto.setZona(zona);
            dto.setDepartamento(departamento);
            dto.setProvincia(provincia);
            dto.setDistrito(distrito);
            dto.setDireccion(direccion);
            dto.setReferencia(referencia);
            dto.setTelefono(telefono);
            dto.setMontoAprobado(montoAprobado);
            dto.setFechaAceptacionDemanda(fechaAceptacionDemanda);
            dto.setFechaEnvioJudicial(fechaEnvioJudicial);
            dto.setFechaAsignacionAbogado(fechaAsignacionAbogado);
            dto.setFechaCastigo(fechaCastigo);
            dto.setTipoFondo(tipoFondo);
            dto.setBienesEmbargados(bienesEmbargados);
            return dto;
        }
    }
}
