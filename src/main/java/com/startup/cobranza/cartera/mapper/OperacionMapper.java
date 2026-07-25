package com.startup.cobranza.cartera.mapper;

import com.startup.cobranza.cartera.dto.OperacionDTO;
import com.startup.cobranza.cartera.entity.Operacion;
import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.cliente.entity.Cliente;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class OperacionMapper {

    // Formato del Excel: dd.MM.yyyy
    private static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter FORMATTER_DATETIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public OperacionDTO toDTO(Operacion entity) {
        if (entity == null) return null;
        return OperacionDTO.builder()
                // ID y claves
                .id(entity.getId())
                .cuenta(entity.getCuenta())
                .numeroOperacion(entity.getNumeroOperacion())
                // Relaciones
                .clienteId(entity.getCliente() != null ? entity.getCliente().getId() : null)
                .clienteNombre(entity.getCliente() != null ? entity.getCliente().getNombreCompleto() : null)
                .clienteDni(entity.getCliente() != null ? entity.getCliente().getDni() : null)
                .agenciaId(entity.getAgencia() != null ? entity.getAgencia().getId() : null)
                .agenciaNombre(entity.getAgencia() != null ? entity.getAgencia().getNombre() : null)
                // Abogado
                .abogadoNombre(entity.getAbogadoNombre())
                // Banderas y datos simples
                .transferido(entity.getTransferido())
                .observaciones(entity.getObservaciones())
                .situacion(entity.getSituacion())
                .moneda(entity.getMoneda())
                .busquedaBienes(entity.getBusquedaBienes())
                // Deuda
                .deudaCap(entity.getDeudaCap())
                .deudaTotal(entity.getDeudaTotal())
                // Datos judiciales
                .tipoProceso(entity.getTipoProceso())
                .tipoJuzgado(entity.getTipoJuzgado())
                .distritoJudicial(entity.getDistritoJudicial())
                .numeroJuzgado(entity.getNumeroJuzgado())
                .numeroExpediente(entity.getNumeroExpediente())
                .tieneIncidente(entity.getTieneIncidente())
                .montoDemandado(entity.getMontoDemandado())
                .secretarioLegal(entity.getSecretarioLegal())
                .codigoExpedienteCautelar(entity.getCodigoExpedienteCautelar())
                .detalleBienEmbargado(entity.getDetalleBienEmbargado())
                .numeroPartida(entity.getNumeroPartida())
                .tipoBienEmbargado(entity.getTipoBienEmbargado())
                .rango(entity.getRango())
                // Acreedores y preferencia
                .detalleAcreedores(entity.getDetalleAcreedores())
                .tipoPreferente(entity.getTipoPreferente())
                // Medida cautelar
                .montoMedidaCautelar(entity.getMontoMedidaCautelar())
                .monedaMc(entity.getMonedaMc())
                .medidaCautelarEjecutada(entity.getMedidaCautelarEjecutada())
                .fechaInscripcionEmbargo(fmt(entity.getFechaInscripcionEmbargo()))
                .fechaPresentacionTituloRrpp(fmt(entity.getFechaPresentacionTituloRrpp()))
                .asientoInscripcion(entity.getAsientoInscripcion())
                .fechaPresentacionMc(fmt(entity.getFechaPresentacionMc()))
                // Admisión e inadmissible
                .fechaInadmisible(fmt(entity.getFechaInadmisible()))
                .fechaAdmision(fmt(entity.getFechaAdmision()))
                .comentario(entity.getComentario())
                .fechaPresentacion(fmt(entity.getFechaPresentacion()))
                .fechaInadmisible2(fmt(entity.getFechaInadmisible2()))
                .fechaAdmision2(fmt(entity.getFechaAdmision2()))
                // Audiencias
                .audienciaTipo(entity.getAudienciaTipo())
                // Resoluciones y ejecutoria
                .fechaAutoFinal(fmt(entity.getFechaAutoFinal()))
                .fechaEjecutoriada(fmt(entity.getFechaEjecutoriada()))
                .fechaNombramientoPeritos(fmt(entity.getFechaNombramientoPeritos()))
                .fechaNombramientoMartillero(fmt(entity.getFechaNombramientoMartillero()))
                // Remates
                .fechaRemate1(fmt(entity.getFechaRemate1()))
                .fechaRemate2(fmt(entity.getFechaRemate2()))
                .fechaRemate3(fmt(entity.getFechaRemate3()))
                // Seguimiento
                .fechaProximoActoProcesal(fmt(entity.getFechaProximoActoProcesal()))
                .comentarioProcesal(entity.getComentarioProcesal())
                // Auditoría
                .fechaCreacion(fmtDateTime(entity.getFechaCreacion()))
                .fechaActualizacion(fmtDateTime(entity.getFechaActualizacion()))
                .build();
    }

    public Operacion toEntity(OperacionDTO dto, Cliente cliente, Agencia agencia) {
        if (dto == null) return null;
        return Operacion.builder()
                .id(dto.getId())
                .cuenta(dto.getCuenta())
                .numeroOperacion(dto.getNumeroOperacion())
                .cliente(cliente)
                .agencia(agencia)
                .abogadoNombre(dto.getAbogadoNombre())
                .transferido(dto.getTransferido())
                .observaciones(dto.getObservaciones())
                .situacion(dto.getSituacion())
                .moneda(dto.getMoneda())
                .busquedaBienes(dto.getBusquedaBienes())
                .deudaCap(dto.getDeudaCap())
                .deudaTotal(dto.getDeudaTotal())
                .tipoProceso(dto.getTipoProceso())
                .tipoJuzgado(dto.getTipoJuzgado())
                .distritoJudicial(dto.getDistritoJudicial())
                .numeroJuzgado(dto.getNumeroJuzgado())
                .numeroExpediente(dto.getNumeroExpediente())
                .tieneIncidente(dto.getTieneIncidente())
                .montoDemandado(dto.getMontoDemandado())
                .secretarioLegal(dto.getSecretarioLegal())
                .codigoExpedienteCautelar(dto.getCodigoExpedienteCautelar())
                .detalleBienEmbargado(dto.getDetalleBienEmbargado())
                .numeroPartida(dto.getNumeroPartida())
                .tipoBienEmbargado(dto.getTipoBienEmbargado())
                .rango(dto.getRango())
                .detalleAcreedores(dto.getDetalleAcreedores())
                .tipoPreferente(dto.getTipoPreferente())
                .montoMedidaCautelar(dto.getMontoMedidaCautelar())
                .monedaMc(dto.getMonedaMc())
                .medidaCautelarEjecutada(dto.getMedidaCautelarEjecutada())
                .fechaInscripcionEmbargo(parseDate(dto.getFechaInscripcionEmbargo()))
                .fechaPresentacionTituloRrpp(parseDate(dto.getFechaPresentacionTituloRrpp()))
                .asientoInscripcion(dto.getAsientoInscripcion())
                .fechaPresentacionMc(parseDate(dto.getFechaPresentacionMc()))
                .fechaInadmisible(parseDate(dto.getFechaInadmisible()))
                .fechaAdmision(parseDate(dto.getFechaAdmision()))
                .comentario(dto.getComentario())
                .fechaPresentacion(parseDate(dto.getFechaPresentacion()))
                .fechaInadmisible2(parseDate(dto.getFechaInadmisible2()))
                .fechaAdmision2(parseDate(dto.getFechaAdmision2()))
                .audienciaTipo(dto.getAudienciaTipo())
                .fechaAutoFinal(parseDate(dto.getFechaAutoFinal()))
                .fechaEjecutoriada(parseDate(dto.getFechaEjecutoriada()))
                .fechaNombramientoPeritos(parseDate(dto.getFechaNombramientoPeritos()))
                .fechaNombramientoMartillero(parseDate(dto.getFechaNombramientoMartillero()))
                .fechaRemate1(parseDate(dto.getFechaRemate1()))
                .fechaRemate2(parseDate(dto.getFechaRemate2()))
                .fechaRemate3(parseDate(dto.getFechaRemate3()))
                .fechaProximoActoProcesal(parseDate(dto.getFechaProximoActoProcesal()))
                .comentarioProcesal(dto.getComentarioProcesal())
                .build();
    }

    private String fmt(java.time.LocalDate date) {
        return date != null ? date.format(FORMATTER_DATE) : null;
    }

    private String fmtDateTime(java.time.LocalDateTime dt) {
        return dt != null ? dt.format(FORMATTER_DATETIME) : null;
    }

    private java.time.LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return java.time.LocalDate.parse(s, FORMATTER_DATE);
        } catch (Exception e) {
            return null;
        }
    }
}
