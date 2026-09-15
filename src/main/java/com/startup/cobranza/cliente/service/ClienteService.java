package com.startup.cobranza.cliente.service;

import com.startup.cobranza.auditoria.service.AuditoriaService;
import com.startup.cobranza.cartera.entity.ActividadSistema;
import com.startup.cobranza.cartera.repository.ActividadSistemaRepository;
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
    private final ActividadSistemaRepository actividadSistemaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ClienteService(ClienteRepository clienteRepository,
                          OperacionRepository operacionRepository,
                          ClienteMapper clienteMapper,
                          AuditoriaService auditoriaService,
                          ActividadSistemaRepository actividadSistemaRepository) {
        this.clienteRepository = clienteRepository;
        this.operacionRepository = operacionRepository;
        this.clienteMapper = clienteMapper;
        this.auditoriaService = auditoriaService;
        this.actividadSistemaRepository = actividadSistemaRepository;
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
        StringBuilder where = new StringBuilder("WHERE o.activo = true");
        String estado = filtros.getEstado();
        String estadoCartera = filtros.getEstadoCartera();
        Integer minMora = filtros.getMinMora();
        Integer maxMora = filtros.getMaxMora();
        BigDecimal minMonto = filtros.getMinMonto();
        BigDecimal maxMonto = filtros.getMaxMonto();
        String etapaProcesal = filtros.getEtapaProcesal();
        String nombre = filtros.getNombre();
        String dni = filtros.getDni();

        if (estado != null && !estado.isBlank()) {
            where.append(" AND o.estado = :estado");
        }
        if (estadoCartera != null && !estadoCartera.isBlank()) {
            where.append(" AND o.\"estado_cartera\" = :estadoCartera");
        }
        if (minMora != null) {
            where.append(" AND o.\"dias_mora\" >= :minMora");
        }
        if (maxMora != null) {
            where.append(" AND o.\"dias_mora\" <= :maxMora");
        }
        if (minMonto != null) {
            where.append(" AND o.\"monto_total\" >= :minMonto");
        }
        if (maxMonto != null) {
            where.append(" AND o.\"monto_total\" <= :maxMonto");
        }
        if (etapaProcesal != null && !etapaProcesal.isBlank()) {
            where.append(" AND o.\"etapa_procesal\" = :etapaProcesal");
        }
        if (nombre != null && !nombre.isBlank()) {
            where.append(" AND UPPER(c.\"nombre_completo\") LIKE UPPER(CONCAT('%', :nombre, '%'))");
        }
        if (dni != null && !dni.isBlank()) {
            where.append(" AND c.dni = :dni");
        }

        String whereSql = where.toString();

        String sql = """
            SELECT c.id, c.nombre_completo, c.dni,
                   a.id, a.nombre,
                   o.numero_operacion, o.cuenta,
                   o.estado, o."estado_cartera",
                   o."monto_total", o."monto_capital",
                   COUNT(o.id) OVER (PARTITION BY c.id) as total_ops
            FROM operaciones o
            JOIN clientes c ON c.id = o.cliente_id
            LEFT JOIN agencias a ON a.id = o.agencia_id
            """
            + whereSql + """
            ORDER BY c.nombre_completo ASC
            LIMIT :limit OFFSET :offset
            """;

        int limit = pageable.getPageSize();
        int offset = (int) pageable.getOffset();

        jakarta.persistence.Query emQuery = entityManager.createNativeQuery(sql);
        if (estado != null && !estado.isBlank()) emQuery.setParameter("estado", estado);
        if (estadoCartera != null && !estadoCartera.isBlank()) emQuery.setParameter("estadoCartera", estadoCartera);
        if (minMora != null) emQuery.setParameter("minMora", minMora);
        if (maxMora != null) emQuery.setParameter("maxMora", maxMora);
        if (minMonto != null) emQuery.setParameter("minMonto", minMonto);
        if (maxMonto != null) emQuery.setParameter("maxMonto", maxMonto);
        if (etapaProcesal != null && !etapaProcesal.isBlank()) emQuery.setParameter("etapaProcesal", etapaProcesal);
        if (nombre != null && !nombre.isBlank()) emQuery.setParameter("nombre", nombre);
        if (dni != null && !dni.isBlank()) emQuery.setParameter("dni", dni);
        emQuery.setParameter("limit", limit);
        emQuery.setParameter("offset", offset);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = emQuery.getResultList();

        if (rows.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        String countSql = """
            SELECT COUNT(DISTINCT c.id)
            FROM operaciones o
            JOIN clientes c ON c.id = o.cliente_id
            """
            + whereSql;

        jakarta.persistence.Query countQuery = entityManager.createNativeQuery(countSql);
        if (estado != null && !estado.isBlank()) countQuery.setParameter("estado", estado);
        if (estadoCartera != null && !estadoCartera.isBlank()) countQuery.setParameter("estadoCartera", estadoCartera);
        if (minMora != null) countQuery.setParameter("minMora", minMora);
        if (maxMora != null) countQuery.setParameter("maxMora", maxMora);
        if (minMonto != null) countQuery.setParameter("minMonto", minMonto);
        if (maxMonto != null) countQuery.setParameter("maxMonto", maxMonto);
        if (etapaProcesal != null && !etapaProcesal.isBlank()) countQuery.setParameter("etapaProcesal", etapaProcesal);
        if (nombre != null && !nombre.isBlank()) countQuery.setParameter("nombre", nombre);
        if (dni != null && !dni.isBlank()) countQuery.setParameter("dni", dni);
        Number total = (Number) countQuery.getSingleResult();

        // Build DTOs — agrupar por cliente (primera fila de cada cliente)
        Map<Long, ClienteBandejaDTO> seen = new java.util.LinkedHashMap<>();
        for (Object[] row : rows) {
            Long cid = ((Number) row[0]).longValue();
            if (seen.containsKey(cid)) continue;

            ClienteBandejaDTO dto = ClienteBandejaDTO.builder()
                    .id(cid)
                    .dni((String) row[2])
                    .nombreCompleto((String) row[1])
                    .agenciaNombre((String) row[4])
                    .numeroOperacion((String) row[5])
                    .cuenta((String) row[6])
                    .estado((String) row[7])
                    .estadoCartera((String) row[8])
                    .montoTotal(row[9] != null ? new BigDecimal(row[9].toString()) : BigDecimal.ZERO)
                    .montoCapital(row[10] != null ? new BigDecimal(row[10].toString()) : BigDecimal.ZERO)
                    .totalOperaciones(row[11] != null ? ((Number) row[11]).intValue() : 1)
                    .build();
            seen.put(cid, dto);
        }

        return new PageImpl<>(new java.util.ArrayList<>(seen.values()), pageable, total.longValue());
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
        Cliente saved = clienteRepository.save(cliente);
        return clienteMapper.toDTO(saved);
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
        List<Operacion> ops = operacionRepository.findByClienteIdAndActivoTrue(cliente.getId());
        StringBuilder sb = new StringBuilder("{\"operaciones_eliminadas\": [");
        for (int i = 0; i < ops.size(); i++) {
            Operacion op = ops.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"id\": ").append(op.getId())
              .append(", \"numero_operacion\": \"").append(op.getNumeroOperacion() != null ? op.getNumeroOperacion() : "").append("\"")
              .append(", \"cuenta\": \"").append(op.getCuenta() != null ? op.getCuenta() : "").append("\"")
              .append(", \"situacion\": \"").append(op.getSituacion() != null ? op.getSituacion() : "").append("\"}}");
            operacionRepository.delete(op);
        }
        sb.append("]}");
        actividadSistemaRepository.save(new ActividadSistema(
                "CLIENTE_ELIMINADO",
                cliente.getId(),
                cliente.getNombreCompleto(),
                sb.toString(),
                null
        ));
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
    public Page<ClienteExpedienteDTO> listarClientesConExpedientes(Long agenciaId, String situacion, String busqueda, Pageable pageable) {
        StringBuilder where = new StringBuilder();
        where.append("WHERE c.activo = true AND o.activo = true AND o.numero_expediente IS NOT NULL AND o.numero_expediente <> ''");

        if (agenciaId != null) {
            where.append(" AND o.agencia_id = ").append(agenciaId);
        }

        if (situacion != null && !situacion.isBlank()) {
            where.append(" AND UPPER(o.situacion) = '").append(situacion.toUpperCase()).append("'");
        } else {
            // Sin filtro de situacion: solo Judicial o Castigada
            where.append(" AND UPPER(o.situacion) IN ('JUDICIAL', 'CASTIGADA')");
        }

        if (busqueda != null && !busqueda.isBlank()) {
            String like = "'%" + busqueda.toLowerCase() + "%'";
            where.append(" AND (LOWER(c.nombre_completo) LIKE ").append(like)
                 .append(" OR c.dni = '").append(busqueda).append("')");
        }

        String sql = """
            SELECT c.id, c.nombre_completo, c.dni, o.agencia_id, a.nombre,
                   SUM(CASE WHEN UPPER(o.situacion) = 'JUDICIAL' THEN 1 ELSE 0 END),
                   SUM(CASE WHEN UPPER(o.situacion) = 'CASTIGADA' THEN 1 ELSE 0 END),
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
