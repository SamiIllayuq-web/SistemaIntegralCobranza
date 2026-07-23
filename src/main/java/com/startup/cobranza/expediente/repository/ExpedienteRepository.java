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

    Optional<Expediente> findByNumeroExpediente(String numeroExpediente);

    List<Expediente> findByEmpresaId(Long empresaId);

    List<Expediente> findByAgenciaId(Long agenciaId);

    @Query("SELECT e FROM Expediente e WHERE e.empresa.id = :empresaId AND e.activo = true")
    List<Expediente> findActivosPorEmpresa(@Param("empresaId") Long empresaId);

    @Query("SELECT e FROM Expediente e JOIN e.clientes c WHERE e.empresa.id = :empresaId AND c.cuenta = :cuenta AND c.operacion = :operacion")
    List<Expediente> findByEmpresaAndClienteCuentaOperacion(
            @Param("empresaId") Long empresaId,
            @Param("cuenta") String cuenta,
            @Param("operacion") String operacion);

    Page<Expediente> findByEmpresaId(Long empresaId, Pageable pageable);

    Page<Expediente> findBySituacion(String situacion, Pageable pageable);

    @Query("SELECT e FROM Expediente e WHERE LOWER(e.numeroExpediente) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Expediente> findByNumeroExpedienteContaining(@Param("texto") String texto, Pageable pageable);
}
