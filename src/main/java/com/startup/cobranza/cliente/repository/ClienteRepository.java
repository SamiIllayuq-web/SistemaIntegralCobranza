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
}
