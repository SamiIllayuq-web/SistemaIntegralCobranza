package com.startup.cobranza.operacion.service;

import com.startup.cobranza.operacion.dto.OperacionDTO;
import com.startup.cobranza.operacion.dto.OperacionFormDTO;
import com.startup.cobranza.operacion.entity.Operacion;
import com.startup.cobranza.operacion.exception.OperacionException;
import com.startup.cobranza.operacion.mapper.OperacionMapper;
import com.startup.cobranza.operacion.repository.OperacionRepository;
import com.startup.cobranza.cliente.entity.Cliente;
import com.startup.cobranza.cliente.repository.ClienteRepository;
import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.agencia.repository.AgenciaRepository;
import com.startup.cobranza.usuario.entity.Usuario;
import com.startup.cobranza.usuario.repository.UsuarioRepository;
import com.startup.cobranza.operacion.entity.BienEmbargado;
import com.startup.cobranza.operacion.repository.BienEmbargadoRepository;
import com.startup.cobranza.operacion.dto.BienEmbargadoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OperacionService {

    private final OperacionRepository operacionRepository;
    private final OperacionMapper operacionMapper;
    private final ClienteRepository clienteRepository;
    private final AgenciaRepository agenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final BienEmbargadoRepository bienEmbargadoRepository;

    public OperacionService(OperacionRepository operacionRepository,
                            OperacionMapper operacionMapper,
                            ClienteRepository clienteRepository,
                            AgenciaRepository agenciaRepository,
                            UsuarioRepository usuarioRepository,
                            BienEmbargadoRepository bienEmbargadoRepository) {
        this.operacionRepository = operacionRepository;
        this.operacionMapper = operacionMapper;
        this.clienteRepository = clienteRepository;
        this.agenciaRepository = agenciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.bienEmbargadoRepository = bienEmbargadoRepository;
    }

    public List<OperacionDTO> listarActivas() {
        return operacionRepository.findByActivoTrue().stream()
                .map(operacionMapper::toDTO)
                .toList();
    }

    public List<OperacionDTO> listarPorCliente(Long clienteId) {
        return operacionRepository.findByClienteIdAndActivoTrueWithBienes(clienteId).stream()
                .map(operacionMapper::toDTO)
                .toList();
    }

    public OperacionDTO obtenerPorId(Long id) {
        Operacion op = operacionRepository.findByIdWithBienes(id)
                .orElseThrow(() -> new OperacionException("Operacion no encontrada"));
        return operacionMapper.toDTO(op);
    }

    public Operacion obtenerEntityPorId(Long id) {
        return operacionRepository.findByIdWithBienes(id)
                .orElseThrow(() -> new OperacionException("Operacion no encontrada"));
    }

    @Transactional
    public OperacionDTO crear(OperacionFormDTO form) {
        Cliente cliente = clienteRepository.findById(form.getClienteId())
                .orElseThrow(() -> new OperacionException("Cliente no encontrado"));
        Agencia agencia = form.getAgenciaId() != null
                ? agenciaRepository.findById(form.getAgenciaId()).orElse(null)
                : null;
        Usuario abogado = form.getAbogadoId() != null
                ? usuarioRepository.findById(form.getAbogadoId()).orElse(null)
                : null;

        Operacion operacion = operacionMapper.toEntityFromForm(form, cliente, agencia, abogado);
        Operacion saved = operacionRepository.save(operacion);
        return operacionMapper.toDTO(saved);
    }

    @Transactional
    public OperacionDTO actualizar(Long id, OperacionFormDTO form) {
        Operacion existing = operacionRepository.findById(id)
                .orElseThrow(() -> new OperacionException("Operacion no encontrada"));

        Cliente cliente = clienteRepository.findById(form.getClienteId())
                .orElseThrow(() -> new OperacionException("Cliente no encontrado"));
        Agencia agencia = form.getAgenciaId() != null
                ? agenciaRepository.findById(form.getAgenciaId()).orElse(null)
                : null;
        Usuario abogado = form.getAbogadoId() != null
                ? usuarioRepository.findById(form.getAbogadoId()).orElse(null)
                : null;

        existing.setCliente(cliente);
        existing.setAgencia(agencia);
        existing.setCuenta(form.getCuenta());
        existing.setNumeroOperacion(form.getNumeroOperacion());
        existing.setMontoCapital(form.getMontoCapital());
        existing.setMontoTotal(form.getMontoTotal());
        existing.setDiasMora(form.getDiasMora());
        existing.setMoneda(form.getMoneda());
        existing.setSituacion(form.getSituacion());
        existing.setEstado(form.getEstado());
        existing.setEtapa(form.getEtapa());
        existing.setObservacion(form.getObservacion());
        existing.setRango(form.getRango());
        existing.setAnalista(form.getAnalista());
        existing.setAnalistaSenior(form.getAnalistaSenior());
        existing.setNumeroExpediente(form.getNumeroExpediente());
        existing.setTipoProceso(form.getTipoProceso());
        existing.setTipoJuzgado(form.getTipoJuzgado());
        existing.setDistritoJudicial(form.getDistritoJudicial());
        existing.setNumeroJuzgado(form.getNumeroJuzgado());
        existing.setAbogado(abogado);
        existing.setTrans(form.getTrans());
        existing.setBusquedaBienes(form.getBusquedaBienes());
        existing.setMontoDemandado(form.getMontoDemandado());
        existing.setEscribanoLegal(form.getEscribanoLegal());
        existing.setCodigoExpCautelar(form.getCodigoExpCautelar());
        existing.setIncidente(form.getIncidente());
        existing.setFechaPresentacion(parseFecha(form.getFechaPresentacion()));
        existing.setFechaInadmisiblePrincipal(parseFecha(form.getFechaInadmisiblePrincipal()));
        existing.setFechaAdmisionPrincipal(parseFecha(form.getFechaAdmisionPrincipal()));
        existing.setFechaAudienciaUnica(parseFecha(form.getFechaAudienciaUnica()));
        existing.setFechaAutoFinal(parseFecha(form.getFechaAutoFinal()));
        existing.setFechaConsentimiento(parseFecha(form.getFechaConsentimiento()));
        existing.setFechaEjecutoriada(parseFecha(form.getFechaEjecutoriada()));
        existing.setFechaIngresoEjecucion(parseFecha(form.getFechaIngresoEjecucion()));
        existing.setFechaTasacion(parseFecha(form.getFechaTasacion()));
        existing.setFechaNombramientoMartillero(parseFecha(form.getFechaNombramientoMartillero()));
        existing.setFechaRemate1(parseFecha(form.getFechaRemate1()));
        existing.setFechaRemate2(parseFecha(form.getFechaRemate2()));
        existing.setFechaRemate3(parseFecha(form.getFechaRemate3()));
        existing.setObservacionActos(form.getObservacionActos());
        existing.setComentario(form.getComentario());
        existing.setEstadoCartera(form.getEstadoCartera());
        existing.setFechaDesembolso(parseFecha(form.getFechaDesembolso()));
        existing.setImporteDesembolso(parseImporte(form.getImporteDesembolso()));
        existing.setEtapaProcesalTexto(form.getEtapaProcesalTexto());
        existing.setActoPendiente(form.getActoPendiente());
        existing.setFechaUltimoEstadoProceso(parseFecha(form.getFechaUltimoEstadoProceso()));
        existing.setZona(form.getZona());
        existing.setDepartamento(form.getDepartamento());
        existing.setProvincia(form.getProvincia());
        existing.setDistrito(form.getDistrito());
        existing.setDireccion(form.getDireccion());
        existing.setReferencia(form.getReferencia());
        existing.setTelefono(form.getTelefono());
        existing.setMontoAprobado(parseImporte(form.getMontoAprobado()));
        existing.setFechaAceptacionDemanda(parseFecha(form.getFechaAceptacionDemanda()));
        existing.setFechaEnvioJudicial(parseFecha(form.getFechaEnvioJudicial()));
        existing.setFechaAsignacionAbogado(parseFecha(form.getFechaAsignacionAbogado()));
        existing.setFechaCastigo(parseFecha(form.getFechaCastigo()));
        existing.setTipoFondo(form.getTipoFondo());
        existing.setCoTitularAval(form.getCoTitularAval());
        existing.setNumeroPartida(form.getNumeroPartida());
        existing.setNumeroFichaRegistral(form.getNumeroFichaRegistral());

        // Sync bienes embargados
        if (form.getBienesEmbargados() != null) {
            existing.getBienesEmbargados().clear();
            for (BienEmbargadoDTO bDto : form.getBienesEmbargados()) {
                BienEmbargado bien = new BienEmbargado();
                bien.setId(bDto.getId());
                bien.setOperacion(existing);
                bien.setDetalleGarantia(bDto.getDetalleGarantia());
                bien.setPartidaRegistral(bDto.getNumeroPartida());
                bien.setTipoBien(bDto.getTipoBien());
                bien.setDireccion(bDto.getDireccion());
                bien.setDistrito(bDto.getDistrito());
                bien.setProvincia(bDto.getProvincia());
                bien.setDepartamento(bDto.getDepartamento());
                bien.setGarantiaInscrita(bDto.getGarantiaInscrita());
                bien.setFechaInscripcion(bDto.getFechaInscripcion());
                bien.setFechaPresentacionRrpp(bDto.getFechaPresentacionRrpp());
                bien.setAsientoInscripcion(bDto.getAsientoInscripcion());
                bien.setFechaPresentacionMc(bDto.getFechaPresentacionMc());
                bien.setFechaInadmisible(bDto.getFechaInadmisible());
                bien.setFechaAdmision(bDto.getFechaAdmision());
                bien.setComentarioMc(bDto.getComentarioMc());
                bien.setDetalleAcreedores(bDto.getDetalleAcreedores());
                bien.setTipoPreferencia(bDto.getTipoPreferencia());
                bien.setTitularPredio(bDto.getTitularPredio());
                bien.setMontoMc(bDto.getMontoMc());
                bien.setMonedaMc(bDto.getMonedaMc());
                bien.setRango(bDto.getRango());
                existing.getBienesEmbargados().add(bien);
            }
        } else {
            existing.getBienesEmbargados().clear();
        }

        Operacion saved = operacionRepository.save(existing);
        return operacionMapper.toDTO(saved);
    }

    @Transactional
    public void eliminar(Long id) {
        Operacion op = operacionRepository.findById(id)
                .orElseThrow(() -> new OperacionException("Operacion no encontrada"));
        op.setActivo(false);
        operacionRepository.save(op);
    }

    private java.time.LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) return null;
        try { return java.time.LocalDate.parse(fecha); }
        catch (Exception e) { return null; }
    }

    private java.math.BigDecimal parseImporte(String valor) {
        if (valor == null || valor.isBlank()) return null;
        try { return new java.math.BigDecimal(valor); }
        catch (Exception e) { return null; }
    }

    public Page<OperacionDTO> listarCarteraConFiltros(
            Long agenciaId, String estado, String etapa,
            String busqueda, Pageable pageable) {
        return operacionRepository.findCarteraConFiltros(
                agenciaId, estado, etapa, busqueda, pageable)
                .map(operacionMapper::toDTO);
    }

    /**
     * Lista de operaciones con numeroExpediente — la vista "Expedientes"
     * lee de la misma entidad Operacion, solo filtra y ordena diferente.
     */
    public Page<OperacionDTO> listarExpedientes(String situacion,
                                                 String busqueda, Pageable pageable) {
        return operacionRepository.findExpedientes(situacion, busqueda, pageable)
                .map(operacionMapper::toDTO);
    }
}
