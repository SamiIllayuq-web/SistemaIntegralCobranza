package com.startup.cobranza.expediente.repository;

import com.startup.cobranza.expediente.entity.Expediente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpedienteRepository extends JpaRepository<Expediente, Long> {

    Optional<Expediente> findByOperacionId(Long operacionId);

    Optional<Expediente> findByNumeroExpediente(String numeroExpediente);

    @Query("SELECT e FROM Expediente e WHERE e.activo = true AND e.empresa.id = :empresaId")
    List<Expediente> findActivosPorEmpresa(@Param("empresaId") Long empresaId);

    Page<Expediente> findByEmpresaId(Long empresaId, Pageable pageable);

    Page<Expediente> findBySituacion(String situacion, Pageable pageable);

    Page<Expediente> findByNumeroExpedienteContaining(String numeroExpediente, Pageable pageable);
}
