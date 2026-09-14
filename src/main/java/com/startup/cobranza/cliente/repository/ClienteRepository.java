package com.startup.cobranza.cliente.repository;

import com.startup.cobranza.cliente.entity.Cliente;
import com.startup.cobranza.cliente.dto.ClienteExpedienteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {

    List<Cliente> findByNombreCompletoContainingIgnoreCaseAndActivoTrue(String nombre);

    List<Cliente> findByActivoTrue();

    Page<Cliente> findByNombreCompletoContainingIgnoreCaseAndActivoTrue(String nombre, Pageable pageable);

    Page<Cliente> findByActivoTrue(Pageable pageable);

    Optional<Cliente> findByDni(String dni);

    List<Cliente> findByDniContainingIgnoreCase(String dni);

    /**
     * Clientes que tienen al menos una operación con número de expediente.
     * Retorna DTOs con conteos Judicial / Castigada por cliente.
     * Filtra por agencia (del expediente) y búsqueda (nombre, DNI del cliente).
     */
    @Query(value = """
        SELECT c.id AS clienteId,
               c.nombre_completo AS nombreCompleto,
               c.dni AS dni,
               o.agencia_id AS agenciaId,
               a.nombre AS agenciaNombre,
               COUNT(CASE WHEN o.situacion = 'Judicial' THEN 1 END) AS judiciales,
               COUNT(CASE WHEN o.situacion = 'Castigada' THEN 1 END) AS castigadas,
               COUNT(o.id) AS total
        FROM clientes c
        JOIN operaciones o ON o.cliente_id = c.id AND o.activo = true
               AND o.numero_expediente IS NOT NULL AND o.numero_expediente <> ''
        LEFT JOIN agencias a ON a.id = o.agencia_id
        WHERE c.activo = true
          AND (:agenciaId IS NULL OR o.agencia_id = :agenciaId)
          AND (:busqueda IS NULL
               OR LOWER(c.nombre_completo) LIKE LOWER(CONCAT('%', :busqueda, '%'))
               OR c.dni = :busqueda)
        GROUP BY c.id, c.nombre_completo, c.dni, o.agencia_id, a.nombre
        ORDER BY c.nombre_completo ASC
        """,
        countQuery = """
        SELECT COUNT(DISTINCT c.id)
        FROM clientes c
        JOIN operaciones o ON o.cliente_id = c.id AND o.activo = true
               AND o.numero_expediente IS NOT NULL AND o.numero_expediente <> ''
        WHERE c.activo = true
          AND (:agenciaId IS NULL OR o.agencia_id = :agenciaId)
          AND (:busqueda IS NULL
               OR LOWER(c.nombre_completo) LIKE LOWER(CONCAT('%', :busqueda, '%'))
               OR c.dni = :busqueda)
        """,
        nativeQuery = true)
    Page<ClienteExpedienteDTO> findClientesConExpedientes(
            @Param("agenciaId") Long agenciaId,
            @Param("busqueda") String busqueda,
            Pageable pageable
    );
}
