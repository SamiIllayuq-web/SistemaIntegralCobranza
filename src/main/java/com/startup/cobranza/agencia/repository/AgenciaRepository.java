package com.startup.cobranza.agencia.repository;

import com.startup.cobranza.agencia.entity.Agencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgenciaRepository extends JpaRepository<Agencia, Long> {

    List<Agencia> findByActivoTrue();

    List<Agencia> findByEmpresaId(Long empresaId);

    List<Agencia> findByEmpresaIdAndActivoTrue(Long empresaId);

    boolean existsByCodigo(String codigo);
}
