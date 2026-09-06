package com.startup.cobranza.operacion.repository;

import com.startup.cobranza.operacion.entity.Operacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OperacionRepository extends JpaRepository<Operacion, Long> {

    Optional<Operacion> findByCuentaAndNumeroOperacion(
            String cuenta, String numeroOperacion);

    List<Operacion> findByClienteId(Long clienteId);

    List<Operacion> findByActivoTrue();

    List<Operacion> findByClienteIdAndActivoTrue(Long clienteId);

    @Query("""
        SELECT o FROM Operacion o
        LEFT JOIN FETCH o.bienesEmbargados
        WHERE o.cliente.id = :clienteId AND o.activo = true
        """)
    List<Operacion> findByClienteIdAndActivoTrueWithBienes(Long clienteId);

    @Query("SELECT o FROM Operacion o LEFT JOIN FETCH o.bienesEmbargados WHERE o.id = :id")
    Optional<Operacion> findByIdWithBienes(Long id);

    /**
     * Busca operaciones activas con filtros. Se usa para la bandeja de clientes
     * cuando hay filtros activos (estado, etapa, mora, monto).
     * Retorna paginado los clienteIds únicos.
     */
    @Query("""
        SELECT DISTINCT o.cliente.id
        FROM Operacion o
        WHERE o.activo = true
          AND (:estado IS NULL OR o.estado = :estado)
          AND (:estadoCartera IS NULL OR o.estadoCartera = :estadoCartera)
          AND (:etapa IS NULL OR o.etapa = :etapa)
          AND (:minMora IS NULL OR o.diasMora >= :minMora)
          AND (:maxMora IS NULL OR o.diasMora <= :maxMora)
          AND (:minMonto IS NULL OR o.montoTotal >= :minMonto)
          AND (:maxMonto IS NULL OR o.montoTotal <= :maxMonto)
        """)
    Page<Long> findClienteIdsConFiltros(
            @Param("estado") String estado,
            @Param("estadoCartera") String estadoCartera,
            @Param("etapa") String etapa,
            @Param("minMora") Integer minMora,
            @Param("maxMora") Integer maxMora,
            @Param("minMonto") BigDecimal minMonto,
            @Param("maxMonto") BigDecimal maxMonto,
            Pageable pageable
    );

    /**
     * Cuenta total de clientes únicos con filtros (para paginación).
     */
    @Query("""
        SELECT COUNT(DISTINCT o.cliente.id)
        FROM Operacion o
        WHERE o.activo = true
          AND (:estado IS NULL OR o.estado = :estado)
          AND (:estadoCartera IS NULL OR o.estadoCartera = :estadoCartera)
          AND (:etapa IS NULL OR o.etapa = :etapa)
          AND (:minMora IS NULL OR o.diasMora >= :minMora)
          AND (:maxMora IS NULL OR o.diasMora <= :maxMora)
          AND (:minMonto IS NULL OR o.montoTotal >= :minMonto)
          AND (:maxMonto IS NULL OR o.montoTotal <= :maxMonto)
        """)
    long countClienteIdsConFiltros(
            @Param("estado") String estado,
            @Param("estadoCartera") String estadoCartera,
            @Param("etapa") String etapa,
            @Param("minMora") Integer minMora,
            @Param("maxMora") Integer maxMora,
            @Param("minMonto") BigDecimal minMonto,
            @Param("maxMonto") BigDecimal maxMonto
    );

    /**
     * Todas las operaciones activas de un cliente, con filtros de bandeja aplicados.
     */
    @Query("""
        SELECT o FROM Operacion o
        LEFT JOIN FETCH o.agencia
        WHERE o.cliente.id = :clienteId
          AND o.activo = true
          AND (:estado IS NULL OR o.estado = :estado)
          AND (:estadoCartera IS NULL OR o.estadoCartera = :estadoCartera)
          AND (:etapa IS NULL OR o.etapa = :etapa)
          AND (:minMora IS NULL OR o.diasMora >= :minMora)
          AND (:maxMora IS NULL OR o.diasMora <= :maxMora)
          AND (:minMonto IS NULL OR o.montoTotal >= :minMonto)
          AND (:maxMonto IS NULL OR o.montoTotal <= :maxMonto)
        """)
    List<Operacion> findByClienteIdConFiltros(
            @Param("clienteId") Long clienteId,
            @Param("estado") String estado,
            @Param("estadoCartera") String estadoCartera,
            @Param("etapa") String etapa,
            @Param("minMora") Integer minMora,
            @Param("maxMora") Integer maxMora,
            @Param("minMonto") BigDecimal minMonto,
            @Param("maxMonto") BigDecimal maxMonto
    );

    /**
     * Cuenta operaciones de un cliente con filtros (para totalOperaciones en el DTO).
     */
    @Query("""
        SELECT COUNT(o) FROM Operacion o
        WHERE o.cliente.id = :clienteId
          AND o.activo = true
          AND (:estado IS NULL OR o.estado = :estado)
          AND (:estadoCartera IS NULL OR o.estadoCartera = :estadoCartera)
          AND (:etapa IS NULL OR o.etapa = :etapa)
          AND (:minMora IS NULL OR o.diasMora >= :minMora)
          AND (:maxMora IS NULL OR o.diasMora <= :maxMora)
          AND (:minMonto IS NULL OR o.montoTotal >= :minMonto)
          AND (:maxMonto IS NULL OR o.montoTotal <= :maxMonto)
        """)
    long countByClienteIdConFiltros(
            @Param("clienteId") Long clienteId,
            @Param("estado") String estado,
            @Param("estadoCartera") String estadoCartera,
            @Param("etapa") String etapa,
            @Param("minMora") Integer minMora,
            @Param("maxMora") Integer maxMora,
            @Param("minMonto") BigDecimal minMonto,
            @Param("maxMonto") BigDecimal maxMonto
    );

    /**
     * Listado paginado de operaciones con filtros (para vista de cartera importada).
     */
    @Query("""
        SELECT o FROM Operacion o
        JOIN FETCH o.cliente
        LEFT JOIN FETCH o.agencia
        WHERE o.activo = true
          AND (:agenciaId IS NULL OR o.agencia.id = :agenciaId)
          AND (:estado IS NULL OR o.estado = :estado)
          AND (:etapa IS NULL OR o.etapa = :etapa)
          AND (:busqueda IS NULL OR (
              LOWER(o.cliente.nombreCompleto) LIKE LOWER(CONCAT('%', :busqueda, '%'))
              OR o.cliente.dni = :busqueda
              OR o.numeroOperacion = :busqueda
              OR o.numeroExpediente = :busqueda
          ))
        ORDER BY o.cliente.nombreCompleto ASC, o.id ASC
        """)
    Page<Operacion> findCarteraConFiltros(
            @Param("agenciaId") Long agenciaId,
            @Param("estado") String estado,
            @Param("etapa") String etapa,
            @Param("busqueda") String busqueda,
            Pageable pageable
    );


    /**
     * Lista de operaciones CON numeroExpediente informado — usado para la vista "Expedientes"
     * que muestra operaciones judiciales agrupadas por expediente.
     */
    @Query("""
        SELECT o FROM Operacion o
        JOIN FETCH o.cliente
        LEFT JOIN FETCH o.agencia
        WHERE o.activo = true
          AND o.numeroExpediente IS NOT NULL
          AND o.numeroExpediente <> ''
          AND (:situacion IS NULL OR o.situacion = :situacion)
          AND (:busqueda IS NULL OR
              LOWER(o.numeroExpediente) LIKE LOWER(CONCAT('%', :busqueda, '%'))
              OR LOWER(o.cliente.nombreCompleto) LIKE LOWER(CONCAT('%', :busqueda, '%'))
              OR o.cliente.dni = :busqueda
          )
        ORDER BY o.numeroExpediente ASC, o.cliente.nombreCompleto ASC
        """)
    Page<Operacion> findExpedientes(
            @Param("situacion") String situacion,
            @Param("busqueda") String busqueda,
            Pageable pageable
    );
}
