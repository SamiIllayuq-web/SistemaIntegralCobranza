package com.startup.cobranza.empresa.repository;

import com.startup.cobranza.empresa.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    List<Empresa> findByActivoTrue();

    boolean existsByRuc(String ruc);
}
