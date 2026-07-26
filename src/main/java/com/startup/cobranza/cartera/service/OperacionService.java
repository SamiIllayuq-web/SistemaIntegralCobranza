package com.startup.cobranza.cartera.service;

import com.startup.cobranza.cartera.dto.OperacionDTO;
import com.startup.cobranza.cartera.entity.Operacion;
import com.startup.cobranza.cartera.mapper.OperacionMapper;
import com.startup.cobranza.cartera.repository.OperacionRepository;
import com.startup.cobranza.empresa.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperacionService {

    private final OperacionRepository operacionRepository;
    private final OperacionMapper operacionMapper;
    private final EmpresaRepository empresaRepository;

    public Page<OperacionDTO> listarPaginado(Pageable pageable) {
        return operacionRepository.findAll(pageable)
                .map(operacionMapper::toDTO);
    }

    public List<OperacionDTO> listarPorCliente(Long clienteId) {
        return operacionRepository.findByClienteId(clienteId).stream()
                .map(operacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OperacionDTO> listarPorAgencia(Long agenciaId) {
        return operacionRepository.findByAgenciaId(agenciaId).stream()
                .map(operacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OperacionDTO> listarPorExpediente(String numeroExpediente) {
        return operacionRepository.findByNumeroExpediente(numeroExpediente).stream()
                .map(operacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OperacionDTO> listarPorSituacion(String situacion) {
        return operacionRepository.findBySituacion(situacion).stream()
                .map(operacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OperacionDTO> listarFiltradas(String agenciaNombre, String situacion, String numeroExpediente, String dni, String cuenta, Pageable pageable) {
        return operacionRepository.findAll(pageable).stream()
                .map(operacionMapper::toDTO)
                .filter(op -> agenciaNombre == null || agenciaNombre.isBlank() ||
                        (op.getAgenciaNombre() != null && op.getAgenciaNombre().toLowerCase().contains(agenciaNombre.toLowerCase())))
                .filter(op -> situacion == null || situacion.isBlank() ||
                        (op.getSituacion() != null && op.getSituacion().equalsIgnoreCase(situacion)))
                .filter(op -> numeroExpediente == null || numeroExpediente.isBlank() ||
                        (op.getNumeroExpediente() != null && op.getNumeroExpediente().contains(numeroExpediente)))
                .filter(op -> dni == null || dni.isBlank() ||
                        (op.getClienteDni() != null && op.getClienteDni().contains(dni)))
                .filter(op -> cuenta == null || cuenta.isBlank() ||
                        (op.getCuenta() != null && op.getCuenta().contains(cuenta)))
                .collect(Collectors.toList());
    }

    public long count() {
        return operacionRepository.count();
    }

    public OperacionDTO buscarPorId(Long id) {
        return operacionRepository.findById(id)
                .map(operacionMapper::toDTO)
                .orElse(null);
    }

    public OperacionDTO buscarPorId(Long id, boolean lazy) {
        return buscarPorId(id);
    }

    public void actualizar(Long id, OperacionDTO dto) {
        Operacion op = operacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Operación no encontrada: " + id));

        op.setAbogadoNombre(dto.getAbogadoNombre());
        op.setTransferido(dto.getTransferido());
        op.setObservaciones(dto.getObservaciones());
        op.setSituacion(dto.getSituacion());
        op.setMoneda(dto.getMoneda());
        op.setBusquedaBienes(dto.getBusquedaBienes());
        op.setDeudaCap(dto.getDeudaCap());
        op.setDeudaTotal(dto.getDeudaTotal());
        op.setTipoProceso(dto.getTipoProceso());
        op.setTipoJuzgado(dto.getTipoJuzgado());
        op.setDistritoJudicial(dto.getDistritoJudicial());
        op.setNumeroJuzgado(dto.getNumeroJuzgado());
        op.setNumeroExpediente(dto.getNumeroExpediente());
        op.setTieneIncidente(dto.getTieneIncidente());
        op.setMontoDemandado(dto.getMontoDemandado());
        op.setSecretarioLegal(dto.getSecretarioLegal());
        op.setCodigoExpedienteCautelar(dto.getCodigoExpedienteCautelar());
        op.setDetalleBienEmbargado(dto.getDetalleBienEmbargado());
        op.setNumeroPartida(dto.getNumeroPartida());
        op.setTipoBienEmbargado(dto.getTipoBienEmbargado());
        op.setRango(dto.getRango());
        op.setDetalleAcreedores(dto.getDetalleAcreedores());
        op.setTipoPreferente(dto.getTipoPreferente());
        op.setMontoMedidaCautelar(dto.getMontoMedidaCautelar());
        op.setMonedaMc(dto.getMonedaMc());
        op.setMedidaCautelarEjecutada(dto.getMedidaCautelarEjecutada());
        op.setFechaInscripcionEmbargo(parseDate(dto.getFechaInscripcionEmbargo()));
        op.setFechaPresentacionTituloRrpp(parseDate(dto.getFechaPresentacionTituloRrpp()));
        op.setAsientoInscripcion(dto.getAsientoInscripcion());
        op.setFechaPresentacionMc(parseDate(dto.getFechaPresentacionMc()));
        op.setFechaInadmisible(parseDate(dto.getFechaInadmisible()));
        op.setFechaAdmision(parseDate(dto.getFechaAdmision()));
        op.setComentario(dto.getComentario());
        op.setFechaPresentacion(parseDate(dto.getFechaPresentacion()));
        op.setFechaInadmisible2(parseDate(dto.getFechaInadmisible2()));
        op.setFechaAdmision2(parseDate(dto.getFechaAdmision2()));
        op.setAudienciaTipo(dto.getAudienciaTipo());
        op.setFechaAutoFinal(parseDate(dto.getFechaAutoFinal()));
        op.setFechaEjecutoriada(parseDate(dto.getFechaEjecutoriada()));
        op.setFechaNombramientoPeritos(parseDate(dto.getFechaNombramientoPeritos()));
        op.setFechaNombramientoMartillero(parseDate(dto.getFechaNombramientoMartillero()));
        op.setFechaRemate1(parseDate(dto.getFechaRemate1()));
        op.setFechaRemate2(parseDate(dto.getFechaRemate2()));
        op.setFechaRemate3(parseDate(dto.getFechaRemate3()));
        op.setFechaProximoActoProcesal(parseDate(dto.getFechaProximoActoProcesal()));
        op.setComentarioProcesal(dto.getComentarioProcesal());

        operacionRepository.save(op);
    }

    private java.time.LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return java.time.LocalDate.parse(s);
        } catch (Exception e) {
            return null;
        }
    }
}
