package com.startup.cobranza.cliente.repository;

import com.startup.cobranza.cliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {

    List<Cliente> findByActivoTrue();

    List<Cliente> findByEmpresaId(Long empresaId);

    List<Cliente> findByAgenciaId(Long agenciaId);
}
