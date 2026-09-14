package com.startup.cobranza.cliente.repository;

import com.startup.cobranza.cliente.entity.Cliente;
import com.startup.cobranza.cliente.dto.ClienteExpedienteDTO;
import com.startup.cobranza.operacion.entity.Operacion;
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
    @Query("""
        SELECT new com.startup.cobranza.cliente.dto.ClienteExpedienteDTO(
            o.cliente.id,
            o.cliente.nombreCompleto,
            o.cliente.dni,
            o.agencia.id,
            o.agencia.nombre,
            SUM(CASE WHEN o.situacion = 'Judicial' THEN 1 ELSE 0 END),
            SUM(CASE WHEN o.situacion = 'Castigada' THEN 1 ELSE 0 END),
            COUNT(o)
        )
        FROM Operacion o
        WHERE o.cliente.activo = true
          AND o.activo = true
          AND o.numeroExpediente IS NOT NULL
          AND o.numeroExpediente <> ''
          AND (:agenciaId IS NULL OR o.agencia.id = :agenciaId)
          AND (:busqueda IS NULL
               OR LOWER(o.cliente.nombreCompleto) LIKE LOWER(CONCAT('%', :busqueda, '%'))
               OR o.cliente.dni = :busqueda)
        GROUP BY o.cliente.id, o.cliente.nombreCompleto, o.cliente.dni, o.agencia.id, o.agencia.nombre
        ORDER BY o.cliente.nombreCompleto ASC
        """)
    Page<ClienteExpedienteDTO> findClientesConExpedientes(
            @Param("agenciaId") Long agenciaId,
            @Param("busqueda") String busqueda,
            Pageable pageable
    );
}
