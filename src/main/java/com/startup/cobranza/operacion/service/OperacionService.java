package com.startup.cobranza.operacion.service;

import com.startup.cobranza.operacion.dto.OperacionDTO;
import com.startup.cobranza.operacion.dto.OperacionFormDTO;
import com.startup.cobranza.operacion.entity.Operacion;
import com.startup.cobranza.operacion.exception.OperacionException;
import com.startup.cobranza.operacion.mapper.OperacionMapper;
import com.startup.cobranza.operacion.repository.OperacionRepository;
import com.startup.cobranza.cliente.entity.Cliente;
import com.startup.cobranza.cliente.repository.ClienteRepository;
import com.startup.cobranza.empresa.entity.Empresa;
import com.startup.cobranza.empresa.repository.EmpresaRepository;
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
    private final EmpresaRepository empresaRepository;
    private final AgenciaRepository agenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final BienEmbargadoRepository bienEmbargadoRepository;

    public OperacionService(OperacionRepository operacionRepository,
                            OperacionMapper operacionMapper,
                            ClienteRepository clienteRepository,
                            EmpresaRepository empresaRepository,
                            AgenciaRepository agenciaRepository,
                            UsuarioRepository usuarioRepository,
                            BienEmbargadoRepository bienEmbargadoRepository) {
        this.operacionRepository = operacionRepository;
        this.operacionMapper = operacionMapper;
        this.clienteRepository = clienteRepository;
        this.empresaRepository = empresaRepository;
        this.agenciaRepository = agenciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.bienEmbargadoRepository = bienEmbargadoRepository;
    }

    public List<OperacionDTO> listarPorEmpresa(Long empresaId) {
        return operacionRepository.findByEmpresaIdAndActivoTrue(empresaId).stream()
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
                .orElseThrow(() -> new OperacionException("Operación no encontrada"));
        return operacionMapper.toDTO(op);
    }

    public Operacion obtenerEntityPorId(Long id) {
        return operacionRepository.findByIdWithBienes(id)
                .orElseThrow(() -> new OperacionException("Operación no encontrada"));
    }

    @Transactional
    public OperacionDTO crear(OperacionFormDTO form) {
        Cliente cliente = clienteRepository.findById(form.getClienteId())
                .orElseThrow(() -> new OperacionException("Cliente no encontrado"));
        Empresa empresa = empresaRepository.findById(form.getEmpresaId())
                .orElseThrow(() -> new OperacionException("Empresa no encontrada"));
        Agencia agencia = form.getAgenciaId() != null
                ? agenciaRepository.findById(form.getAgenciaId()).orElse(null)
                : null;
        Usuario abogado = form.getAbogadoId() != null
                ? usuarioRepository.findById(form.getAbogadoId()).orElse(null)
                : null;

        Operacion operacion = operacionMapper.toEntityFromForm(form, cliente, empresa, agencia, abogado);
        Operacion saved = operacionRepository.save(operacion);
        return operacionMapper.toDTO(saved);
    }

    @Transactional
    public OperacionDTO actualizar(Long id, OperacionFormDTO form) {
        Operacion existing = operacionRepository.findById(id)
                .orElseThrow(() -> new OperacionException("Operación no encontrada"));

        Cliente cliente = clienteRepository.findById(form.getClienteId())
                .orElseThrow(() -> new OperacionException("Cliente no encontrado"));
        Empresa empresa = empresaRepository.findById(form.getEmpresaId())
                .orElseThrow(() -> new OperacionException("Empresa no encontrada"));
        Agencia agencia = form.getAgenciaId() != null
                ? agenciaRepository.findById(form.getAgenciaId()).orElse(null)
                : null;
        Usuario abogado = form.getAbogadoId() != null
                ? usuarioRepository.findById(form.getAbogadoId()).orElse(null)
                : null;

        existing.setCliente(cliente);
        existing.setEmpresa(empresa);
        existing.setAgencia(agencia);
        existing.setCuenta(form.getCuenta());
        existing.setNumeroOperacion(form.getNumeroOperacion());
        existing.setMontoCapital(form.getMontoCapital());
        existing.setMontoTotal(form.getMontoTotal());
        existing.setDiasMora(form.getDiasMora());
        existing.setMoneda(form.getMoneda());
        existing.setTipoCredito(form.getTipoCredito());
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
        existing.setObservacionActos(form.getObservacionActos());
        existing.setComentario(form.getComentario());

        // Sync bienes embargados
        if (form.getBienesEmbargados() != null) {
            existing.getBienesEmbargados().clear();
            for (BienEmbargadoDTO bDto : form.getBienesEmbargados()) {
                BienEmbargado bien = new BienEmbargado();
                bien.setId(bDto.getId());
                bien.setOperacion(existing);
                bien.setDetalleGarantia(bDto.getDetalleGarantia());
                bien.setPartidaRegistral(bDto.getPartidaRegistral());
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
                .orElseThrow(() -> new OperacionException("Operación no encontrada"));
        op.setActivo(false);
        operacionRepository.save(op);
    }

    public Page<OperacionDTO> listarCarteraConFiltros(
            Long empresaId, Long agenciaId, String estado, String etapa,
            String busqueda, Pageable pageable) {
        return operacionRepository.findCarteraConFiltros(
                empresaId, agenciaId, estado, etapa, busqueda, pageable)
                .map(operacionMapper::toDTO);
    }

    /**
     * Lista de operaciones con numeroExpediente — la vista "Expedientes"
     * lee de la misma entidad Operacion, solo filtra y ordena diferente.
     */
    public Page<OperacionDTO> listarExpedientes(Long empresaId, String situacion,
                                                 String busqueda, Pageable pageable) {
        return operacionRepository.findExpedientes(empresaId, situacion, busqueda, pageable)
                .map(operacionMapper::toDTO);
    }
}
