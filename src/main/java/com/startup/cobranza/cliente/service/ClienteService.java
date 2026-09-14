package com.startup.cobranza.cliente.service;

import com.startup.cobranza.auditoria.service.AuditoriaService;
import com.startup.cobranza.cliente.dto.ClienteBandejaDTO;
import com.startup.cobranza.cliente.dto.ClienteBusquedaDTO;
import com.startup.cobranza.cliente.dto.ClienteDTO;
import com.startup.cobranza.cliente.dto.ClienteExpedienteDTO;
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
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ClienteService {

    private static final List<String> ESTADO_PRIORIDAD = List.of("VIGENTE", "VENCIDA", "PRESCRITA", "PAGADA");
    private static final List<String> ESTADO_CARTERA_PRIORIDAD = List.of("ACTIVO", "CANCELADA", "DESASIGNADA", "VENDIDA", "DEVUELTA");

    private final ClienteRepository clienteRepository;
    private final OperacionRepository operacionRepository;
    private final ClienteMapper clienteMapper;
    private final AuditoriaService auditoriaService;

    @PersistenceContext
    private EntityManager entityManager;

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
     * Con filtros activos (estado, etapa, mora, monto):
     *   query sobre Operacion para obtener clienteIds únicos, luego lookup de cada cliente.
     */
    public Page<ClienteBandejaDTO> listarBandeja(ClienteBusquedaDTO filtros, Pageable pageable) {
        boolean tieneFiltrosOperacion = filtros.hasFiltrosAdicionales();
        boolean tieneNombreODni = (filtros.getNombre() != null && !filtros.getNombre().isBlank())
                || (filtros.getDni() != null && !filtros.getDni().isBlank());

        if (!tieneFiltrosOperacion && !tieneNombreODni) {
            // Sin filtros: lista simple paginada de clientes activos
            return listarBandejaSimple(pageable);
        }

        if (!tieneFiltrosOperacion) {
            // Solo nombre o DNI: buscar directo sobre Cliente (ignora activo de Operacion)
            return listarBandejaPorNombreODni(filtros, pageable);
        }

        // Con filtros de operación: buscar sobre Operacion
        return listarBandejaConFiltros(filtros, pageable);
    }

    private Page<ClienteBandejaDTO> listarBandejaPorNombreODni(ClienteBusquedaDTO filtros, Pageable pageable) {
        String nombre = filtros.getNombre();
        String dni = filtros.getDni();
        Pageable cleanPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("nombreCompleto").ascending());
        Page<Cliente> clientesPage;
        if (nombre != null && !nombre.isBlank()) {
            clientesPage = clienteRepository.findByNombreCompletoContainingIgnoreCaseAndActivoTrue(nombre, cleanPageable);
        } else {
            // Solo DNI: traer todos los clientes activos y filtrar en memoria
            clientesPage = clienteRepository.findByActivoTrue(cleanPageable);
            List<ClienteBandejaDTO> filtered = clientesPage.getContent().stream()
                    .filter(c -> c.getDni() != null && c.getDni().equalsIgnoreCase(dni))
                    .map(this::toBandejaDTO)
                    .toList();
            return new PageImpl<>(filtered, cleanPageable, filtered.size());
        }
        List<ClienteBandejaDTO> dtos = clientesPage.getContent().stream()
                .map(this::toBandejaDTO)
                .toList();
        return new PageImpl<>(dtos, cleanPageable, clientesPage.getTotalElements());
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
                filtros.getEstado(),
                filtros.getEstadoCartera(),
                filtros.getMinMora(),
                filtros.getMaxMora(),
                filtros.getMinMonto(),
                filtros.getMaxMonto(),
                filtros.getEtapaProcesal(),
                filtros.getNombre(),
                filtros.getDni(),
                unsortedPageable
        );

        List<Long> clienteIds = clienteIdsPage.getContent();
        if (clienteIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
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
                filtros.getEstado(),
                filtros.getEstadoCartera(),
                filtros.getMinMora(),
                filtros.getMaxMora(),
                filtros.getMinMonto(),
                filtros.getMaxMonto(),
                filtros.getEtapaProcesal()
        );
        return buildBandejaDTO(cliente, ops);
    }

    private ClienteBandejaDTO buildBandejaDTO(Cliente cliente, List<Operacion> ops) {
        Set<String> agencias = new LinkedHashSet<>();
        BigDecimal montoTotal = BigDecimal.ZERO;
        BigDecimal montoCapital = BigDecimal.ZERO;
        String peorEstado = null;
        String peorEstadoCartera = null;
        String agenciaNombre = null;
        String numeroOperacion = null;
        String cuenta = null;

        for (Operacion op : ops) {
            if (op.getAgencia() != null && op.getAgencia().getNombre() != null) {
                agencias.add(op.getAgencia().getNombre());
            }
            if (op.getMontoTotal() != null) montoTotal = montoTotal.add(op.getMontoTotal());
            if (op.getMontoCapital() != null) montoCapital = montoCapital.add(op.getMontoCapital());
            if (op.getEstado() != null) peorEstado = priorize(peorEstado, op.getEstado(), ESTADO_PRIORIDAD);
            if (op.getEstadoCartera() != null) peorEstadoCartera = priorize(peorEstadoCartera, op.getEstadoCartera(), ESTADO_CARTERA_PRIORIDAD);
            // Tomar datos de la primera operación para la columna de la bandeja
            if (agenciaNombre == null && op.getAgencia() != null) {
                agenciaNombre = op.getAgencia().getNombre();
            }
            if (numeroOperacion == null) {
                numeroOperacion = op.getNumeroOperacion();
            }
            if (cuenta == null) {
                cuenta = op.getCuenta();
            }
        }

        // Unir nombres de agencias para el badge
        List<String> agenciaList = List.copyOf(agencias);

        return ClienteBandejaDTO.builder()
                .id(cliente.getId())
                .dni(cliente.getDni())
                .nombreCompleto(cliente.getNombreCompleto())
                .agenciaNombre(agenciaNombre)
                .numeroOperacion(numeroOperacion)
                .cuenta(cuenta)
                .estado(peorEstado)
                .estadoCartera(peorEstadoCartera)
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
        // cascade: borrar operaciones del cliente (los bienes se borran por orphanRemoval)
        List<Operacion> ops = operacionRepository.findByClienteIdAndActivoTrue(cliente.getId());
        for (Operacion op : ops) {
            operacionRepository.delete(op);
        }
        clienteRepository.delete(cliente);
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

    @Transactional(readOnly = true)
    public Page<ClienteExpedienteDTO> listarClientesConExpedientes(Long agenciaId, String busqueda, Pageable pageable) {
        StringBuilder where = new StringBuilder();
        where.append("WHERE c.activo = true AND o.activo = true AND o.numero_expediente IS NOT NULL AND o.numero_expediente <> ''");

        if (agenciaId != null) {
            where.append(" AND o.agencia_id = ").append(agenciaId);
        }

        if (busqueda != null && !busqueda.isBlank()) {
            String like = "'%" + busqueda.toLowerCase() + "%'";
            where.append(" AND (LOWER(c.nombre_completo) LIKE ").append(like)
                 .append(" OR c.dni = '").append(busqueda).append("')");
        }

        String sql = """
            SELECT c.id, c.nombre_completo, c.dni, o.agencia_id, a.nombre,
                   SUM(CASE WHEN o.situacion = 'Judicial' THEN 1 ELSE 0 END),
                   SUM(CASE WHEN o.situacion = 'Castigada' THEN 1 ELSE 0 END),
                   COUNT(o.id)
            FROM operaciones o
            JOIN clientes c ON c.id = o.cliente_id
            LEFT JOIN agencias a ON a.id = o.agencia_id
            """
            + where + """
            
            GROUP BY c.id, c.nombre_completo, c.dni, o.agencia_id, a.nombre
            ORDER BY c.nombre_completo ASC
            FETCH FIRST :limit ROWS ONLY
            """;

        jakarta.persistence.Query emQuery = entityManager.createNativeQuery(sql);
        emQuery.setParameter("limit", pageable.getPageSize());
        emQuery.setFirstResult((int) pageable.getOffset());

        @SuppressWarnings("unchecked")
        List<Object[]> rows = emQuery.getResultList();
        List<ClienteExpedienteDTO> dtos = rows.stream().map(row ->
            ClienteExpedienteDTO.builder()
                .clienteId(((Number) row[0]).longValue())
                .nombreCompleto((String) row[1])
                .dni((String) row[2])
                .agenciaId(row[3] != null ? ((Number) row[3]).longValue() : null)
                .agenciaNombre((String) row[4])
                .judiciales(((Number) row[5]).longValue())
                .castigadas(((Number) row[6]).longValue())
                .total(((Number) row[7]).longValue())
                .build()
        ).collect(Collectors.toList());

        String count = """
            SELECT COUNT(DISTINCT c.id)
            FROM operaciones o
            JOIN clientes c ON c.id = o.cliente_id
            """
            + where;

        jakarta.persistence.Query countQuery = entityManager.createNativeQuery(count);
        long total = ((Number) countQuery.getSingleResult()).longValue();

        return new PageImpl<>(dtos, pageable, total);
    }
}
