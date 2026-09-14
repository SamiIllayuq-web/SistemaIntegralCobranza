package com.startup.cobranza.operacion.service;

import com.startup.cobranza.operacion.dto.OperacionDTO;
import com.startup.cobranza.operacion.dto.OperacionFormDTO;
import com.startup.cobranza.operacion.entity.Operacion;
import com.startup.cobranza.operacion.exception.OperacionException;
import com.startup.cobranza.operacion.mapper.OperacionMapper;
import com.startup.cobranza.operacion.repository.OperacionRepository;
import com.startup.cobranza.cartera.entity.ActividadSistema;
import com.startup.cobranza.cartera.repository.ActividadSistemaRepository;
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
    private final ActividadSistemaRepository actividadSistemaRepository;

    public OperacionService(OperacionRepository operacionRepository,
                            OperacionMapper operacionMapper,
                            ClienteRepository clienteRepository,
                            AgenciaRepository agenciaRepository,
                            UsuarioRepository usuarioRepository,
                            BienEmbargadoRepository bienEmbargadoRepository,
                            ActividadSistemaRepository actividadSistemaRepository) {
        this.operacionRepository = operacionRepository;
        this.operacionMapper = operacionMapper;
        this.clienteRepository = clienteRepository;
        this.agenciaRepository = agenciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.bienEmbargadoRepository = bienEmbargadoRepository;
        this.actividadSistemaRepository = actividadSistemaRepository;
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
        // Find-or-create Cliente por DNI
        String dni = form.getDni();
        Cliente cliente;
        if (dni != null && !dni.isBlank()) {
            cliente = clienteRepository.findByDni(dni).orElse(null);
            if (cliente == null) {
                cliente = Cliente.builder()
                        .dni(dni)
                        .nombreCompleto(form.getNombreCliente())
                        .telefono(form.getTelefono())
                        .email(form.getEmail())
                        .direccion(form.getDireccion())
                        .activo(true)
                        .build();
                cliente = clienteRepository.save(cliente);
            } else {
                boolean updated = false;
                if (form.getNombreCliente() != null && !form.getNombreCliente().isBlank()) {
                    cliente.setNombreCompleto(form.getNombreCliente());
                    updated = true;
                }
                if (form.getTelefono() != null && !form.getTelefono().isBlank()) {
                    cliente.setTelefono(form.getTelefono());
                    updated = true;
                }
                if (form.getEmail() != null && !form.getEmail().isBlank()) {
                    cliente.setEmail(form.getEmail());
                    updated = true;
                }
                if (form.getDireccion() != null && !form.getDireccion().isBlank()) {
                    cliente.setDireccion(form.getDireccion());
                    updated = true;
                }
                if (updated) {
                    cliente = clienteRepository.save(cliente);
                }
            }
        } else {
            throw new OperacionException("DNI es obligatorio para crear una operación");
        }

        Agencia agencia = form.getAgenciaId() != null
                ? agenciaRepository.findById(form.getAgenciaId()).orElse(null)
                : null;
        Usuario abogado = form.getAbogadoId() != null
                ? usuarioRepository.findById(form.getAbogadoId()).orElse(null)
                : null;

        Operacion operacion = operacionMapper.toEntityFromForm(form, cliente, agencia, abogado);

        // Sync todos los campos — igual que actualizar() pero sin el findById previo
        operacion.setTrans(form.getTrans());
        operacion.setBusquedaBienes(form.getBusquedaBienes());
        operacion.setMontoDemandado(form.getMontoDemandado());
        operacion.setEscribanoLegal(form.getEscribanoLegal());
        operacion.setCodigoExpCautelar(form.getCodigoExpCautelar());
        operacion.setIncidente(form.getIncidente());
        operacion.setFechaPresentacion(parseFecha(form.getFechaPresentacion()));
        operacion.setFechaInadmisiblePrincipal(parseFecha(form.getFechaInadmisiblePrincipal()));
        operacion.setFechaAdmisionPrincipal(parseFecha(form.getFechaAdmisionPrincipal()));
        operacion.setFechaAudienciaUnica(parseFecha(form.getFechaAudienciaUnica()));
        operacion.setFechaAutoFinal(parseFecha(form.getFechaAutoFinal()));
        operacion.setFechaConsentimiento(parseFecha(form.getFechaConsentimiento()));
        operacion.setFechaEjecutoriada(parseFecha(form.getFechaEjecutoriada()));
        operacion.setFechaIngresoEjecucion(parseFecha(form.getFechaIngresoEjecucion()));
        operacion.setFechaTasacion(parseFecha(form.getFechaTasacion()));
        operacion.setFechaNombramientoMartillero(parseFecha(form.getFechaNombramientoMartillero()));
        operacion.setFechaRemate1(parseFecha(form.getFechaRemate1()));
        operacion.setFechaRemate2(parseFecha(form.getFechaRemate2()));
        operacion.setFechaRemate3(parseFecha(form.getFechaRemate3()));
        operacion.setObservacionActos(form.getObservacionActos());
        operacion.setComentario(form.getComentario());
        operacion.setEstadoCartera(form.getEstadoCartera());
        operacion.setFechaDesembolso(parseFecha(form.getFechaDesembolso()));
        operacion.setImporteDesembolso(parseImporte(form.getImporteDesembolso()));
        operacion.setEtapaProcesalTexto(form.getEtapaProcesalTexto());
        operacion.setActoPendiente(form.getActoPendiente());
        operacion.setFechaUltimoEstadoProceso(parseFecha(form.getFechaUltimoEstadoProceso()));
        operacion.setZona(form.getZona());
        operacion.setDepartamento(form.getDepartamento());
        operacion.setProvincia(form.getProvincia());
        operacion.setDistrito(form.getDistrito());
        operacion.setReferencia(form.getReferencia());
        operacion.setMontoAprobado(parseImporte(form.getMontoAprobado()));
        operacion.setFechaAceptacionDemanda(parseFecha(form.getFechaAceptacionDemanda()));
        operacion.setFechaEnvioJudicial(parseFecha(form.getFechaEnvioJudicial()));
        operacion.setFechaAsignacionAbogado(parseFecha(form.getFechaAsignacionAbogado()));
        operacion.setFechaCastigo(parseFecha(form.getFechaCastigo()));
        operacion.setTipoFondo(form.getTipoFondo());
        operacion.setCoTitularAval(form.getCoTitularAval());
        operacion.setNumeroPartida(form.getNumeroPartida());
        operacion.setNumeroFichaRegistral(form.getNumeroFichaRegistral());

        // Bienes embargados
        if (form.getBienesEmbargados() != null && !form.getBienesEmbargados().isEmpty()) {
            for (BienEmbargadoDTO bDto : form.getBienesEmbargados()) {
                BienEmbargado bien = new BienEmbargado();
                bien.setOperacion(operacion);
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
                operacion.getBienesEmbargados().add(bien);
            }
        }

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
        existing.setEtapaProcesal(form.getEtapaProcesal());
        existing.setActoPendiente(form.getActoPendiente());
        existing.setFechaUltimoEstadoProceso(parseFecha(form.getFechaUltimoEstadoProceso()));
        existing.setZona(form.getZona());
        existing.setDepartamento(form.getDepartamento());
        existing.setProvincia(form.getProvincia());
        existing.setDistrito(form.getDistrito());
        existing.setReferencia(form.getReferencia());
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
        Operacion op = operacionRepository.findByIdWithBienes(id)
                .orElseThrow(() -> new OperacionException("Operacion no encontrada"));
        String detalle = "{\"numero_operacion\": \"" + (op.getNumeroOperacion() != null ? op.getNumeroOperacion() : "")
                      + "\", \"cuenta\": \"" + (op.getCuenta() != null ? op.getCuenta() : "")
                      + "\", \"situacion\": \"" + (op.getSituacion() != null ? op.getSituacion() : "")
                      + "\", \"cliente_id\": " + (op.getCliente() != null ? op.getCliente().getId() : "null") + "}";
        actividadSistemaRepository.save(new ActividadSistema(
                "OPERACION_ELIMINADA",
                op.getId(),
                op.getNumeroOperacion(),
                detalle,
                null
        ));
        operacionRepository.delete(op);
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
            Long agenciaId, String estado,
            String busqueda, Pageable pageable) {
        return operacionRepository.findCarteraConFiltros(
                agenciaId, estado, busqueda, pageable)
                .map(operacionMapper::toDTO);
    }

    /**
     * Lista operaciones filtradas por estadoCartera y etapaProcesal — /cartera/estado-cartera.
     */
    public Page<OperacionDTO> listarPorEstadoCartera(
            String estadoCartera, String etapaProcesal, Long agenciaId, Pageable pageable) {
        return operacionRepository.findByEstadoCarteraConFiltros(
                estadoCartera, etapaProcesal, agenciaId, pageable)
                .map(operacionMapper::toDTO);
    }

    /**
     * Lista de operaciones con numeroExpediente — la vista "Expedientes"
     * lee de la misma entidad Operacion, solo filtra y ordena diferente.
     */
    public Page<OperacionDTO> listarExpedientes(Long agenciaId, String situacion,
                                                 String busqueda, Pageable pageable) {
        return operacionRepository.findExpedientes(agenciaId, situacion, busqueda, pageable)
                .map(operacionMapper::toDTO);
    }
}
