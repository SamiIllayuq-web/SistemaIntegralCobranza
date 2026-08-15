package com.startup.cobranza.operacion.mapper;

import com.startup.cobranza.cliente.entity.Cliente;
import com.startup.cobranza.empresa.entity.Empresa;
import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.expediente.entity.BienEmbargado;
import com.startup.cobranza.operacion.dto.BienEmbargadoDTO;
import com.startup.cobranza.operacion.dto.OperacionDTO;
import com.startup.cobranza.operacion.dto.OperacionFormDTO;
import com.startup.cobranza.operacion.entity.Operacion;
import com.startup.cobranza.usuario.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperacionMapper {

    public OperacionDTO toDTO(Operacion entity) {
        if (entity == null) return null;
        OperacionDTO dto = OperacionDTO.builder()
                .id(entity.getId())
                .cuenta(entity.getCuenta())
                .numeroOperacion(entity.getNumeroOperacion())
                .montoCapital(entity.getMontoCapital())
                .montoTotal(entity.getMontoTotal())
                .diasMora(entity.getDiasMora())
                .moneda(entity.getMoneda())
                .tipoCredito(entity.getTipoCredito())
                .situacion(entity.getSituacion())
                .estado(entity.getEstado())
                .etapa(entity.getEtapa())
                .observacion(entity.getObservacion())
                .rango(entity.getRango())
                .analista(entity.getAnalista())
                .analistaSenior(entity.getAnalistaSenior())
                .numeroExpediente(entity.getNumeroExpediente())
                .tipoProceso(entity.getTipoProceso())
                .tipoJuzgado(entity.getTipoJuzgado())
                .distritoJudicial(entity.getDistritoJudicial())
                .numeroJuzgado(entity.getNumeroJuzgado())
                .activo(entity.getActivo())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .trans(entity.getTrans())
                .busquedaBienes(entity.getBusquedaBienes())
                .montoDemandado(entity.getMontoDemandado())
                .escribanoLegal(entity.getEscribanoLegal())
                .codigoExpCautelar(entity.getCodigoExpCautelar())
                .incidente(entity.getIncidente())
                .fechaPresentacion(entity.getFechaPresentacion())
                .fechaInadmisiblePrincipal(entity.getFechaInadmisiblePrincipal())
                .fechaAdmisionPrincipal(entity.getFechaAdmisionPrincipal())
                .fechaAudienciaUnica(entity.getFechaAudienciaUnica())
                .fechaAutoFinal(entity.getFechaAutoFinal())
                .fechaConsentimiento(entity.getFechaConsentimiento())
                .fechaEjecutoriada(entity.getFechaEjecutoriada())
                .fechaIngresoEjecucion(entity.getFechaIngresoEjecucion())
                .fechaTasacion(entity.getFechaTasacion())
                .fechaNombramientoMartillero(entity.getFechaNombramientoMartillero())
                .fechaRemate1(entity.getFechaRemate1())
                .fechaRemate2(entity.getFechaRemate2())
                .fechaRemate3(entity.getFechaRemate3())
                .observacionActos(entity.getObservacionActos())
                .comentario(entity.getComentario())
                .estadoCartera(entity.getEstadoCartera())
                .fechaDesembolso(entity.getFechaDesembolso())
                .importeDesembolso(entity.getImporteDesembolso())
                .etapaProcesalTexto(entity.getEtapaProcesalTexto())
                .actoPendiente(entity.getActoPendiente())
                .fechaUltimoEstadoProceso(entity.getFechaUltimoEstadoProceso())
                .build();

        if (entity.getCliente() != null) {
            dto.setClienteId(entity.getCliente().getId());
            dto.setClienteNombre(entity.getCliente().getNombreCompleto());
            dto.setClienteDni(entity.getCliente().getDni());
        }
        if (entity.getEmpresa() != null) {
            dto.setEmpresaId(entity.getEmpresa().getId());
            dto.setEmpresaNombre(entity.getEmpresa().getNombre());
        }
        if (entity.getAgencia() != null) {
            dto.setAgenciaId(entity.getAgencia().getId());
            dto.setAgenciaNombre(entity.getAgencia().getNombre());
        }
        if (entity.getAbogado() != null) {
            dto.setAbogadoId(entity.getAbogado().getId());
            dto.setAbogadoNombre(entity.getAbogado().getNombreCompleto());
        }
        if (entity.getBienesEmbargados() != null) {
            dto.setBienEmbargados(
                entity.getBienesEmbargados().stream()
                    .map(this::toBienEmbargadoDTO)
                    .toList()
            );
        }
        return dto;
    }

    private BienEmbargadoDTO toBienEmbargadoDTO(BienEmbargado entity) {
        return BienEmbargadoDTO.builder()
                .id(entity.getId())
                .operacionId(entity.getOperacion() != null ? entity.getOperacion().getId() : null)
                .tipoBien(entity.getTipoBien())
                .partidaRegistral(entity.getPartidaRegistral())
                .detalleGarantia(entity.getDetalleGarantia())
                .direccion(entity.getDireccion())
                .distrito(entity.getDistrito())
                .provincia(entity.getProvincia())
                .departamento(entity.getDepartamento())
                .garantiaInscrita(entity.getGarantiaInscrita())
                .fechaInscripcion(entity.getFechaInscripcion())
                .fechaPresentacionRrpp(entity.getFechaPresentacionRrpp())
                .asientoInscripcion(entity.getAsientoInscripcion())
                .montoMc(entity.getMontoMc())
                .monedaMc(entity.getMonedaMc())
                .rango(entity.getRango())
                .detalleAcreedores(entity.getDetalleAcreedores())
                .tipoPreferencia(entity.getTipoPreferencia())
                .titularPredio(entity.getTitularPredio())
                .fechaGeneracionMc(entity.getFechaGeneracionMc())
                .build();
    }

    public Operacion toEntity(OperacionDTO dto, Cliente cliente, Empresa empresa, Agencia agencia, Usuario abogado) {
        if (dto == null) return null;
        return Operacion.builder()
                .id(dto.getId())
                .cliente(cliente)
                .empresa(empresa)
                .agencia(agencia)
                .cuenta(dto.getCuenta())
                .numeroOperacion(dto.getNumeroOperacion())
                .montoCapital(dto.getMontoCapital())
                .montoTotal(dto.getMontoTotal())
                .diasMora(dto.getDiasMora())
                .moneda(dto.getMoneda())
                .tipoCredito(dto.getTipoCredito())
                .situacion(dto.getSituacion())
                .estado(dto.getEstado())
                .etapa(dto.getEtapa())
                .observacion(dto.getObservacion())
                .rango(dto.getRango())
                .analista(dto.getAnalista())
                .analistaSenior(dto.getAnalistaSenior())
                .numeroExpediente(dto.getNumeroExpediente())
                .tipoProceso(dto.getTipoProceso())
                .tipoJuzgado(dto.getTipoJuzgado())
                .distritoJudicial(dto.getDistritoJudicial())
                .numeroJuzgado(dto.getNumeroJuzgado())
                .abogado(abogado)
                .observacionActos(dto.getObservacionActos())
                .comentario(dto.getComentario())
                .estadoCartera(dto.getEstadoCartera())
                .fechaDesembolso(dto.getFechaDesembolso())
                .importeDesembolso(dto.getImporteDesembolso())
                .etapaProcesalTexto(dto.getEtapaProcesalTexto())
                .actoPendiente(dto.getActoPendiente())
                .fechaUltimoEstadoProceso(dto.getFechaUltimoEstadoProceso())
                .build();
    }

    public OperacionFormDTO toFormDTO(Operacion entity) {
        if (entity == null) return null;
        return OperacionFormDTO.builder()
                .id(entity.getId())
                .clienteId(entity.getCliente() != null ? entity.getCliente().getId() : null)
                .empresaId(entity.getEmpresa() != null ? entity.getEmpresa().getId() : null)
                .agenciaId(entity.getAgencia() != null ? entity.getAgencia().getId() : null)
                .cuenta(entity.getCuenta())
                .numeroOperacion(entity.getNumeroOperacion())
                .montoCapital(entity.getMontoCapital())
                .montoTotal(entity.getMontoTotal())
                .diasMora(entity.getDiasMora())
                .moneda(entity.getMoneda())
                .tipoCredito(entity.getTipoCredito())
                .situacion(entity.getSituacion())
                .estado(entity.getEstado())
                .etapa(entity.getEtapa())
                .observacion(entity.getObservacion())
                .rango(entity.getRango())
                .analista(entity.getAnalista())
                .analistaSenior(entity.getAnalistaSenior())
                .numeroExpediente(entity.getNumeroExpediente())
                .tipoProceso(entity.getTipoProceso())
                .tipoJuzgado(entity.getTipoJuzgado())
                .distritoJudicial(entity.getDistritoJudicial())
                .numeroJuzgado(entity.getNumeroJuzgado())
                .abogadoId(entity.getAbogado() != null ? entity.getAbogado().getId() : null)
                .observacionActos(entity.getObservacionActos())
                .comentario(entity.getComentario())
                .estadoCartera(entity.getEstadoCartera())
                .fechaDesembolso(entity.getFechaDesembolso() != null ? entity.getFechaDesembolso().toString() : null)
                .importeDesembolso(entity.getImporteDesembolso() != null ? entity.getImporteDesembolso().toString() : null)
                .etapaProcesalTexto(entity.getEtapaProcesalTexto())
                .actoPendiente(entity.getActoPendiente())
                .fechaUltimoEstadoProceso(entity.getFechaUltimoEstadoProceso() != null ? entity.getFechaUltimoEstadoProceso().toString() : null)
                .bienesEmbargados(entity.getBienesEmbargados() != null
                        ? entity.getBienesEmbargados().stream().map(this::toBienEmbargadoDTO).toList()
                        : null)
                .build();
    }

    public Operacion toEntityFromForm(OperacionFormDTO form, Cliente cliente, Empresa empresa, Agencia agencia, Usuario abogado) {
        if (form == null) return null;
        return Operacion.builder()
                .id(form.getId())
                .cliente(cliente)
                .empresa(empresa)
                .agencia(agencia)
                .cuenta(form.getCuenta())
                .numeroOperacion(form.getNumeroOperacion())
                .montoCapital(form.getMontoCapital())
                .montoTotal(form.getMontoTotal())
                .diasMora(form.getDiasMora())
                .moneda(form.getMoneda())
                .tipoCredito(form.getTipoCredito())
                .situacion(form.getSituacion())
                .estado(form.getEstado())
                .etapa(form.getEtapa())
                .observacion(form.getObservacion())
                .rango(form.getRango())
                .analista(form.getAnalista())
                .analistaSenior(form.getAnalistaSenior())
                .numeroExpediente(form.getNumeroExpediente())
                .tipoProceso(form.getTipoProceso())
                .tipoJuzgado(form.getTipoJuzgado())
                .distritoJudicial(form.getDistritoJudicial())
                .numeroJuzgado(form.getNumeroJuzgado())
                .abogado(abogado)
                .observacionActos(form.getObservacionActos())
                .comentario(form.getComentario())
                .estadoCartera(form.getEstadoCartera())
                .fechaDesembolso(form.getFechaDesembolso() != null && !form.getFechaDesembolso().isEmpty()
                        ? java.time.LocalDate.parse(form.getFechaDesembolso()) : null)
                .importeDesembolso(form.getImporteDesembolso() != null && !form.getImporteDesembolso().isEmpty()
                        ? new java.math.BigDecimal(form.getImporteDesembolso()) : null)
                .etapaProcesalTexto(form.getEtapaProcesalTexto())
                .actoPendiente(form.getActoPendiente())
                .fechaUltimoEstadoProceso(form.getFechaUltimoEstadoProceso() != null && !form.getFechaUltimoEstadoProceso().isEmpty()
                        ? java.time.LocalDate.parse(form.getFechaUltimoEstadoProceso()) : null)
                .build();
    }
}
