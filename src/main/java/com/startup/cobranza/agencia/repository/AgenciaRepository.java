package com.startup.cobranza.agencia.repository;

import com.startup.cobranza.agencia.entity.Agencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgenciaRepository extends JpaRepository<Agencia, Long> {

    List<Agencia> findByActivoTrue();

    Optional<Agencia> findByNombreIgnoreCaseAndActivoTrue(String nombre);

    boolean existsByCodigo(String codigo);
}
