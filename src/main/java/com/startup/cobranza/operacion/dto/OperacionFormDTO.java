package com.startup.cobranza.operacion.dto;

import java.math.BigDecimal;
import java.util.List;

public class OperacionFormDTO {

    private Long id;
    private Long clienteId;
    private String dni;
    private String nombreCliente;
    private String telefono;
    private String email;
    private String direccion;
    private Long agenciaId;
    private String cuenta;
    private String numeroOperacion;
    private BigDecimal montoCapital;
    private BigDecimal montoTotal;
    private Integer diasMora;
    private String moneda;
    private String situacion;
    private String estado;
    private String etapa;
    private String observacion;
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
    private String etapaProcesal;
    private String actoPendiente;
    private String fechaUltimoEstadoProceso;
    private String departamento;
    private String montoAprobado;
    private String fechaAceptacionDemanda;
    private String fechaEnvioJudicial;
    private String fechaAsignacionAbogado;
    private String fechaCastigo;
    private String tipoFondo;
    private String coTitularAval;
    // --- Campos judiciales (desde Excel) ---
    private String trans;
    private String busquedaBienes;
    private String escribanoLegal;
    private String incidente;
    private BigDecimal montoDemandado;
    private String fechaPresentacion;
    private String fechaInadmisiblePrincipal;
    private String fechaAdmisionPrincipal;
    private String fechaAudienciaUnica;
    private String fechaAutoFinal;
    private String fechaConsentimiento;
    private String fechaEjecutoriada;
    private String fechaIngresoEjecucion;
    private String fechaTasacion;
    private String fechaNombramientoMartillero;
    private String fechaRemate1;
    private String fechaRemate2;
    private String fechaRemate3;
    private List<BienEmbargadoDTO> bienesEmbargados;

    public OperacionFormDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
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
    public String getSituacion() { return situacion; }
    public void setSituacion(String situacion) { this.situacion = situacion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getEtapa() { return etapa; }
    public void setEtapa(String etapa) { this.etapa = etapa; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
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
    public String getEtapaProcesal() { return etapaProcesal; }
    public void setEtapaProcesal(String etapaProcesal) { this.etapaProcesal = etapaProcesal; }
    public String getActoPendiente() { return actoPendiente; }
    public void setActoPendiente(String actoPendiente) { this.actoPendiente = actoPendiente; }
    public String getFechaUltimoEstadoProceso() { return fechaUltimoEstadoProceso; }
    public void setFechaUltimoEstadoProceso(String fechaUltimoEstadoProceso) { this.fechaUltimoEstadoProceso = fechaUltimoEstadoProceso; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
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
    public String getCoTitularAval() { return coTitularAval; }
    public void setCoTitularAval(String coTitularAval) { this.coTitularAval = coTitularAval; }
    public String getTrans() { return trans; }
    public void setTrans(String trans) { this.trans = trans; }
    public String getBusquedaBienes() { return busquedaBienes; }
    public void setBusquedaBienes(String busquedaBienes) { this.busquedaBienes = busquedaBienes; }
    public String getEscribanoLegal() { return escribanoLegal; }
    public void setEscribanoLegal(String escribanoLegal) { this.escribanoLegal = escribanoLegal; }
    public String getIncidente() { return incidente; }
    public void setIncidente(String incidente) { this.incidente = incidente; }
    public BigDecimal getMontoDemandado() { return montoDemandado; }
    public void setMontoDemandado(BigDecimal montoDemandado) { this.montoDemandado = montoDemandado; }
    public String getFechaPresentacion() { return fechaPresentacion; }
    public void setFechaPresentacion(String fechaPresentacion) { this.fechaPresentacion = fechaPresentacion; }
    public String getFechaInadmisiblePrincipal() { return fechaInadmisiblePrincipal; }
    public void setFechaInadmisiblePrincipal(String fechaInadmisiblePrincipal) { this.fechaInadmisiblePrincipal = fechaInadmisiblePrincipal; }
    public String getFechaAdmisionPrincipal() { return fechaAdmisionPrincipal; }
    public void setFechaAdmisionPrincipal(String fechaAdmisionPrincipal) { this.fechaAdmisionPrincipal = fechaAdmisionPrincipal; }
    public String getFechaAudienciaUnica() { return fechaAudienciaUnica; }
    public void setFechaAudienciaUnica(String fechaAudienciaUnica) { this.fechaAudienciaUnica = fechaAudienciaUnica; }
    public String getFechaAutoFinal() { return fechaAutoFinal; }
    public void setFechaAutoFinal(String fechaAutoFinal) { this.fechaAutoFinal = fechaAutoFinal; }
    public String getFechaConsentimiento() { return fechaConsentimiento; }
    public void setFechaConsentimiento(String fechaConsentimiento) { this.fechaConsentimiento = fechaConsentimiento; }
    public String getFechaEjecutoriada() { return fechaEjecutoriada; }
    public void setFechaEjecutoriada(String fechaEjecutoriada) { this.fechaEjecutoriada = fechaEjecutoriada; }
    public String getFechaIngresoEjecucion() { return fechaIngresoEjecucion; }
    public void setFechaIngresoEjecucion(String fechaIngresoEjecucion) { this.fechaIngresoEjecucion = fechaIngresoEjecucion; }
    public String getFechaTasacion() { return fechaTasacion; }
    public void setFechaTasacion(String fechaTasacion) { this.fechaTasacion = fechaTasacion; }
    public String getFechaNombramientoMartillero() { return fechaNombramientoMartillero; }
    public void setFechaNombramientoMartillero(String fechaNombramientoMartillero) { this.fechaNombramientoMartillero = fechaNombramientoMartillero; }
    public String getFechaRemate1() { return fechaRemate1; }
    public void setFechaRemate1(String fechaRemate1) { this.fechaRemate1 = fechaRemate1; }
    public String getFechaRemate2() { return fechaRemate2; }
    public void setFechaRemate2(String fechaRemate2) { this.fechaRemate2 = fechaRemate2; }
    public String getFechaRemate3() { return fechaRemate3; }
    public void setFechaRemate3(String fechaRemate3) { this.fechaRemate3 = fechaRemate3; }
    public List<BienEmbargadoDTO> getBienesEmbargados() { return bienesEmbargados; }
    public void setBienesEmbargados(List<BienEmbargadoDTO> bienesEmbargados) { this.bienesEmbargados = bienesEmbargados; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long clienteId;
        private String dni;
        private String nombreCliente;
        private String telefono;
        private String email;
        private String direccion;
        private Long agenciaId;
        private String cuenta;
        private String numeroOperacion;
        private BigDecimal montoCapital;
        private BigDecimal montoTotal;
        private Integer diasMora;
        private String moneda;
        private String situacion;
        private String estado;
        private String etapa;
        private String observacion;
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
        private String etapaProcesal;
        private String actoPendiente;
        private String fechaUltimoEstadoProceso;
        private String departamento;
        private String montoAprobado;
        private String fechaAceptacionDemanda;
        private String fechaEnvioJudicial;
        private String fechaAsignacionAbogado;
        private String fechaCastigo;
        private String tipoFondo;
        private String coTitularAval;
        private String trans;
        private String busquedaBienes;
        private String escribanoLegal;
        private String incidente;
        private BigDecimal montoDemandado;
        private String fechaPresentacion;
        private String fechaInadmisiblePrincipal;
        private String fechaAdmisionPrincipal;
        private String fechaAudienciaUnica;
        private String fechaAutoFinal;
        private String fechaConsentimiento;
        private String fechaEjecutoriada;
        private String fechaIngresoEjecucion;
        private String fechaTasacion;
        private String fechaNombramientoMartillero;
        private String fechaRemate1;
        private String fechaRemate2;
        private String fechaRemate3;
        private List<BienEmbargadoDTO> bienesEmbargados;

        public Builder id(Long v) { id = v; return this; }
        public Builder clienteId(Long v) { clienteId = v; return this; }
        public Builder dni(String v) { dni = v; return this; }
        public Builder nombreCliente(String v) { nombreCliente = v; return this; }
        public Builder telefono(String v) { telefono = v; return this; }
        public Builder email(String v) { email = v; return this; }
        public Builder direccion(String v) { direccion = v; return this; }
        public Builder agenciaId(Long v) { agenciaId = v; return this; }
        public Builder cuenta(String v) { cuenta = v; return this; }
        public Builder numeroOperacion(String v) { numeroOperacion = v; return this; }
        public Builder montoCapital(BigDecimal v) { montoCapital = v; return this; }
        public Builder montoTotal(BigDecimal v) { montoTotal = v; return this; }
        public Builder diasMora(Integer v) { diasMora = v; return this; }
        public Builder moneda(String v) { moneda = v; return this; }
        public Builder situacion(String v) { situacion = v; return this; }
        public Builder estado(String v) { estado = v; return this; }
        public Builder etapa(String v) { etapa = v; return this; }
        public Builder observacion(String v) { observacion = v; return this; }
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
        public Builder etapaProcesal(String v) { etapaProcesal = v; return this; }
        public Builder actoPendiente(String v) { actoPendiente = v; return this; }
        public Builder fechaUltimoEstadoProceso(String v) { fechaUltimoEstadoProceso = v; return this; }
        public Builder departamento(String v) { departamento = v; return this; }
        public Builder montoAprobado(String v) { montoAprobado = v; return this; }
        public Builder fechaAceptacionDemanda(String v) { fechaAceptacionDemanda = v; return this; }
        public Builder fechaEnvioJudicial(String v) { fechaEnvioJudicial = v; return this; }
        public Builder fechaAsignacionAbogado(String v) { fechaAsignacionAbogado = v; return this; }
        public Builder fechaCastigo(String v) { fechaCastigo = v; return this; }
        public Builder tipoFondo(String v) { tipoFondo = v; return this; }
        public Builder coTitularAval(String v) { coTitularAval = v; return this; }
        public Builder trans(String v) { trans = v; return this; }
        public Builder busquedaBienes(String v) { busquedaBienes = v; return this; }
        public Builder escribanoLegal(String v) { escribanoLegal = v; return this; }
        public Builder incidente(String v) { incidente = v; return this; }
        public Builder montoDemandado(BigDecimal v) { montoDemandado = v; return this; }
        public Builder fechaPresentacion(String v) { fechaPresentacion = v; return this; }
        public Builder fechaInadmisiblePrincipal(String v) { fechaInadmisiblePrincipal = v; return this; }
        public Builder fechaAdmisionPrincipal(String v) { fechaAdmisionPrincipal = v; return this; }
        public Builder fechaAudienciaUnica(String v) { fechaAudienciaUnica = v; return this; }
        public Builder fechaAutoFinal(String v) { fechaAutoFinal = v; return this; }
        public Builder fechaConsentimiento(String v) { fechaConsentimiento = v; return this; }
        public Builder fechaEjecutoriada(String v) { fechaEjecutoriada = v; return this; }
        public Builder fechaIngresoEjecucion(String v) { fechaIngresoEjecucion = v; return this; }
        public Builder fechaTasacion(String v) { fechaTasacion = v; return this; }
        public Builder fechaNombramientoMartillero(String v) { fechaNombramientoMartillero = v; return this; }
        public Builder fechaRemate1(String v) { fechaRemate1 = v; return this; }
        public Builder fechaRemate2(String v) { fechaRemate2 = v; return this; }
        public Builder fechaRemate3(String v) { fechaRemate3 = v; return this; }
        public Builder bienesEmbargados(List<BienEmbargadoDTO> v) { bienesEmbargados = v; return this; }

        public OperacionFormDTO build() {
            OperacionFormDTO dto = new OperacionFormDTO();
            dto.setId(id);
            dto.setClienteId(clienteId);
            dto.setDni(dni);
            dto.setNombreCliente(nombreCliente);
            dto.setTelefono(telefono);
            dto.setEmail(email);
            dto.setDireccion(direccion);
            dto.setAgenciaId(agenciaId);
            dto.setCuenta(cuenta);
            dto.setNumeroOperacion(numeroOperacion);
            dto.setMontoCapital(montoCapital);
            dto.setMontoTotal(montoTotal);
            dto.setDiasMora(diasMora);
            dto.setMoneda(moneda);
            dto.setSituacion(situacion);
            dto.setEstado(estado);
            dto.setEtapa(etapa);
            dto.setObservacion(observacion);
            dto.setNumeroExpediente(numeroExpediente);
            dto.setTipoProceso(tipoProceso);
            dto.setTipoJuzgado(tipoJuzgado);
            dto.setDistritoJudicial(distritoJudicial);
            dto.setNumeroJuzgado(numeroJuzgado);
            dto.setAbogadoId(abogadoId);
            dto.setObservacionActos(observacionActos);
            dto.setComentario(comentario);
            dto.setEstadoCartera(estadoCartera);
            dto.setFechaDesembolso(fechaDesembolso);
            dto.setImporteDesembolso(importeDesembolso);
            dto.setEtapaProcesalTexto(etapaProcesalTexto);
            dto.setEtapaProcesal(etapaProcesal);
            dto.setActoPendiente(actoPendiente);
            dto.setFechaUltimoEstadoProceso(fechaUltimoEstadoProceso);
            dto.setDepartamento(departamento);
            dto.setMontoAprobado(montoAprobado);
            dto.setFechaAceptacionDemanda(fechaAceptacionDemanda);
            dto.setFechaEnvioJudicial(fechaEnvioJudicial);
            dto.setFechaAsignacionAbogado(fechaAsignacionAbogado);
            dto.setFechaCastigo(fechaCastigo);
            dto.setTipoFondo(tipoFondo);
            dto.setCoTitularAval(coTitularAval);
            dto.setTrans(trans);
            dto.setBusquedaBienes(busquedaBienes);
            dto.setEscribanoLegal(escribanoLegal);
            dto.setIncidente(incidente);
            dto.setMontoDemandado(montoDemandado);
            dto.setFechaPresentacion(fechaPresentacion);
            dto.setFechaInadmisiblePrincipal(fechaInadmisiblePrincipal);
            dto.setFechaAdmisionPrincipal(fechaAdmisionPrincipal);
            dto.setFechaAudienciaUnica(fechaAudienciaUnica);
            dto.setFechaAutoFinal(fechaAutoFinal);
            dto.setFechaConsentimiento(fechaConsentimiento);
            dto.setFechaEjecutoriada(fechaEjecutoriada);
            dto.setFechaIngresoEjecucion(fechaIngresoEjecucion);
            dto.setFechaTasacion(fechaTasacion);
            dto.setFechaNombramientoMartillero(fechaNombramientoMartillero);
            dto.setFechaRemate1(fechaRemate1);
            dto.setFechaRemate2(fechaRemate2);
            dto.setFechaRemate3(fechaRemate3);
            dto.setBienesEmbargados(bienesEmbargados);
            return dto;
        }
    }
}
