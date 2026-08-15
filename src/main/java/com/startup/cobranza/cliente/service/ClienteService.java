package com.startup.cobranza.cliente.service;

import com.startup.cobranza.auditoria.service.AuditoriaService;
import com.startup.cobranza.cliente.dto.ClienteBandejaDTO;
import com.startup.cobranza.cliente.dto.ClienteBusquedaDTO;
import com.startup.cobranza.cliente.dto.ClienteDTO;
import com.startup.cobranza.cliente.dto.ClienteFormDTO;
import com.startup.cobranza.cliente.entity.Cliente;
import com.startup.cobranza.cliente.exception.ClienteException;
import com.startup.cobranza.cliente.mapper.ClienteMapper;
import com.startup.cobranza.cliente.repository.ClienteRepository;
import com.startup.cobranza.operacion.entity.Operacion;
import com.startup.cobranza.operacion.repository.OperacionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private static final List<String> ESTADO_PRIORIDAD = List.of("VIGENTE", "VENCIDA", "PRESCRITA", "PAGADA");
    private static final List<String> ESTADO_CARTERA_PRIORIDAD = List.of("ACTIVO", "CANCELADA", "DESASIGNADA", "VENDIDA", "DEVUELTA");
    private static final List<String> ETAPA_PRIORIDAD = List.of("JUDICIAL", "EXTRAJUDICIAL");

    private final ClienteRepository clienteRepository;
    private final OperacionRepository operacionRepository;
    private final ClienteMapper clienteMapper;
    private final AuditoriaService auditoriaService;

    public ClienteService(ClienteRepository clienteRepository,
                          OperacionRepository operacionRepository,
                          ClienteMapper clienteMapper,
                          AuditoriaService auditoriaService) {
        this.clienteRepository = clienteRepository;
        this.operacionRepository = operacionRepository;
        this.clienteMapper = clienteMapper;
        this.auditoriaService = auditoriaService;
    }

    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(clienteMapper::toDTO)
                .toList();
    }

    public List<ClienteDTO> listarActivos() {
        return clienteRepository.findByActivoTrue().stream()
                .map(clienteMapper::toDTO)
                .toList();
    }

    public ClienteDTO obtenerPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteException("Cliente no encontrado con id: " + id));
        return clienteMapper.toDTO(cliente);
    }

    public List<ClienteDTO> buscar(ClienteBusquedaDTO busqueda) {
        List<Cliente> resultados;
        if (busqueda.getDni() != null && !busqueda.getDni().isBlank()) {
            resultados = clienteRepository.findByDniContainingIgnoreCase(busqueda.getDni());
        } else if (busqueda.getNombre() != null && !busqueda.getNombre().isBlank()) {
            resultados = clienteRepository.findByNombreCompletoContainingIgnoreCaseAndActivoTrue(busqueda.getNombre());
        } else {
            resultados = clienteRepository.findByActivoTrue();
        }
        return resultados.stream().map(clienteMapper::toDTO).toList();
    }

    /**
     * Bandeja paginada con datos aggregate derivados de las operaciones.
     * Sin filtros activos: usa búsqueda simple por nombre/DNI sobre Cliente.
     * Con filtros activos (empresa, estado, etapa, mora, monto):
     *   query sobre Operacion para obtener clienteIds únicos, luego lookup de cada cliente.
     */
    public Page<ClienteBandejaDTO> listarBandeja(ClienteBusquedaDTO filtros, Pageable pageable) {
        boolean conFiltros = filtros.hasFiltrosAdicionales()
                || (filtros.getNombre() != null && !filtros.getNombre().isBlank())
                || (filtros.getDni() != null && !filtros.getDni().isBlank());

        if (!conFiltros) {
            // Sin filtros: lista simple paginada de clientes activos
            return listarBandejaSimple(pageable);
        }

        // Con filtros: buscar sobre operaciones primero
        return listarBandejaConFiltros(filtros, pageable);
    }

    private Page<ClienteBandejaDTO> listarBandejaSimple(Pageable pageable) {
        // El sort del pageable puede venir con "cliente.nombreCompleto" (del controller)
        // pero ClienteRepository.findByActivoTrue opera sobre Cliente, que tiene nombreCompleto directo.
        // Creamos un pageable limpio con sort correcto.
        Pageable cleanPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("nombreCompleto").ascending());
        Page<Cliente> clientesPage = clienteRepository.findByActivoTrue(cleanPageable);
        List<ClienteBandejaDTO> dtos = clientesPage.getContent().stream()
                .map(this::toBandejaDTO)
                .toList();
        return new PageImpl<>(dtos, cleanPageable, clientesPage.getTotalElements());
    }

    private Page<ClienteBandejaDTO> listarBandejaConFiltros(ClienteBusquedaDTO filtros, Pageable pageable) {
        // Sin sort en el pageable porque el ORDER BY de DISTINCT debe estar en el SELECT (PostgreSQL)
        Pageable unsortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<Long> clienteIdsPage = operacionRepository.findClienteIdsConFiltros(
                filtros.getEmpresaId(),
                filtros.getEstado(),
                filtros.getEstadoCartera(),
                filtros.getEtapa(),
                filtros.getMinMora(),
                filtros.getMaxMora(),
                filtros.getMinMonto(),
                filtros.getMaxMonto(),
                unsortedPageable
        );

        // Si hay búsqueda por nombre o DNI, filtrar adicionalmente
        List<Long> clienteIds = clienteIdsPage.getContent();
        if (clienteIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        if ((filtros.getNombre() != null && !filtros.getNombre().isBlank())
                || (filtros.getDni() != null && !filtros.getDni().isBlank())) {
            clienteIds = filtrarClienteIdsPorNombreODni(clienteIds, filtros);
        }

        if (clienteIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, clienteIdsPage.getTotalElements());
        }

        // 2) Lookup de los clientes
        List<Cliente> clientes = clienteRepository.findAllById(clienteIds);

        // 3) Convertir a DTOs con datos aggregate de operaciones
        List<ClienteBandejaDTO> dtos = clientes.stream()
                .map(c -> toBandejaDTOConFiltros(c, filtros))
                .toList();

        return new PageImpl<>(dtos, pageable, clienteIdsPage.getTotalElements());
    }

    private List<Long> filtrarClienteIdsPorNombreODni(List<Long> clienteIds, ClienteBusquedaDTO filtros) {
        List<Cliente> candidatos = clienteRepository.findAllById(clienteIds);
        return candidatos.stream()
                .filter(c -> {
                    if (filtros.getDni() != null && !filtros.getDni().isBlank()) {
                        return c.getDni() != null
                                && c.getDni().toLowerCase().contains(filtros.getDni().toLowerCase());
                    }
                    if (filtros.getNombre() != null && !filtros.getNombre().isBlank()) {
                        return c.getNombreCompleto() != null
                                && c.getNombreCompleto().toLowerCase()
                                        .contains(filtros.getNombre().toLowerCase());
                    }
                    return true;
                })
                .map(Cliente::getId)
                .toList();
    }

    /**
     * Convierte un Cliente a ClienteBandejaDTO usando TODAS sus operaciones activas.
     */
    private ClienteBandejaDTO toBandejaDTO(Cliente cliente) {
        List<Operacion> ops = operacionRepository.findByClienteIdAndActivoTrue(cliente.getId());
        return buildBandejaDTO(cliente, ops);
    }

    /**
     * Convierte un Cliente a ClienteBandejaDTO usando operaciones filtradas.
     */
    private ClienteBandejaDTO toBandejaDTOConFiltros(Cliente cliente, ClienteBusquedaDTO filtros) {
        List<Operacion> ops = operacionRepository.findByClienteIdConFiltros(
                cliente.getId(),
                filtros.getEmpresaId(),
                filtros.getEstado(),
                filtros.getEstadoCartera(),
                filtros.getEtapa(),
                filtros.getMinMora(),
                filtros.getMaxMora(),
                filtros.getMinMonto(),
                filtros.getMaxMonto()
        );
        return buildBandejaDTO(cliente, ops);
    }

    private ClienteBandejaDTO buildBandejaDTO(Cliente cliente, List<Operacion> ops) {
        Set<String> empresas = new HashSet<>();
        Set<String> agencias = new HashSet<>();
        BigDecimal montoTotal = BigDecimal.ZERO;
        BigDecimal montoCapital = BigDecimal.ZERO;
        String peorEstado = null;
        String peorEstadoCartera = null;
        String peorEtapa = null;

        for (Operacion op : ops) {
            if (op.getEmpresa() != null) empresas.add(op.getEmpresa().getNombre());
            if (op.getAgencia() != null && op.getAgencia().getNombre() != null) {
                agencias.add(op.getAgencia().getNombre());
            }
            if (op.getMontoTotal() != null) montoTotal = montoTotal.add(op.getMontoTotal());
            if (op.getMontoCapital() != null) montoCapital = montoCapital.add(op.getMontoCapital());
            if (op.getEstado() != null) peorEstado = priorize(peorEstado, op.getEstado(), ESTADO_PRIORIDAD);
            if (op.getEstadoCartera() != null) peorEstadoCartera = priorize(peorEstadoCartera, op.getEstadoCartera(), ESTADO_CARTERA_PRIORIDAD);
            if (op.getEtapa() != null) peorEtapa = priorize(peorEtapa, op.getEtapa(), ETAPA_PRIORIDAD);
        }

        return ClienteBandejaDTO.builder()
                .id(cliente.getId())
                .dni(cliente.getDni())
                .nombreCompleto(cliente.getNombreCompleto())
                .empresas(List.copyOf(empresas))
                .agencias(List.copyOf(agencias))
                .estado(peorEstado)
                .estadoCartera(peorEstadoCartera)
                .etapa(peorEtapa)
                .montoTotal(montoTotal)
                .montoCapital(montoCapital)
                .totalOperaciones(ops.size())
                .build();
    }

    /**
     * Retorna el valor con mayor prioridad de la lista.
     * Si current es null, retorna candidate.
     */
    private String priorize(String current, String candidate, List<String> prioridad) {
        if (candidate == null) return current;
        if (current == null) return candidate;
        int idxCurrent = prioridad.indexOf(current.toUpperCase());
        int idxCandidate = prioridad.indexOf(candidate.toUpperCase());
        if (idxCandidate >= 0 && (idxCurrent < 0 || idxCandidate < idxCurrent)) {
            return candidate.toUpperCase();
        }
        return current;
    }

    @Transactional
    public ClienteDTO crear(ClienteFormDTO form) {
        Cliente cliente = clienteMapper.toEntityFromForm(form);
        return clienteMapper.toDTO(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteDTO actualizar(Long id, ClienteFormDTO form, String usuario) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteException("Cliente no encontrado con id: " + id));

        // Capturar estado anterior para auditoría
        Map<String, Object> antes = Map.of(
                "nombreCompleto", cliente.getNombreCompleto() != null ? cliente.getNombreCompleto() : "",
                "telefono", cliente.getTelefono() != null ? cliente.getTelefono() : "",
                "telefono2", cliente.getTelefono2() != null ? cliente.getTelefono2() : "",
                "telefono3", cliente.getTelefono3() != null ? cliente.getTelefono3() : "",
                "direccion", cliente.getDireccion() != null ? cliente.getDireccion() : "",
                "email", cliente.getEmail() != null ? cliente.getEmail() : ""
        );

        cliente.setNombreCompleto(form.getNombreCompleto());
        cliente.setDni(form.getDni());
        cliente.setTelefono(form.getTelefono());
        cliente.setTelefono2(form.getTelefono2());
        cliente.setTelefono3(form.getTelefono3());
        cliente.setDireccion(form.getDireccion());
        cliente.setEmail(form.getEmail());

        ClienteDTO resultado = clienteMapper.toDTO(clienteRepository.save(cliente));

        // Registrar auditoría
        Map<String, Object> despues = Map.of(
                "nombreCompleto", form.getNombreCompleto() != null ? form.getNombreCompleto() : "",
                "telefono", form.getTelefono() != null ? form.getTelefono() : "",
                "telefono2", form.getTelefono2() != null ? form.getTelefono2() : "",
                "telefono3", form.getTelefono3() != null ? form.getTelefono3() : "",
                "direccion", form.getDireccion() != null ? form.getDireccion() : "",
                "email", form.getEmail() != null ? form.getEmail() : ""
        );
        auditoriaService.registrar(
                AuditoriaService.TIPO_CLIENTE_UPDATE,
                "Cliente",
                id,
                usuario,
                Map.of("antes", antes, "despues", despues)
        );

        return resultado;
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteException("Cliente no encontrado con id: " + id));
        cliente.setActivo(false);
        clienteRepository.save(cliente);
    }

    @Transactional
    public ClienteDTO findOrCreateByDni(String dni, String nombreCompleto) {
        return clienteRepository.findByDni(dni)
                .map(clienteMapper::toDTO)
                .orElseGet(() -> {
                    Cliente nuevo = Cliente.builder()
                            .dni(dni)
                            .nombreCompleto(nombreCompleto)
                            .activo(true)
                            .build();
                    return clienteMapper.toDTO(clienteRepository.save(nuevo));
                });
    }
}
