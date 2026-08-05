package com.startup.cobranza.auditoria.repository;

import com.startup.cobranza.auditoria.entity.AuditoriaEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaEventoRepository extends JpaRepository<AuditoriaEvento, Long> {

    Page<AuditoriaEvento> findByObjetoTipoAndObjetoIdOrderByFechaCreacionDesc(
            String objetoTipo, Long objetoId, Pageable pageable);

    @Query("SELECT a FROM AuditoriaEvento a WHERE a.tipo = :tipo ORDER BY a.fechaCreacion DESC")
    List<AuditoriaEvento> findByTipo(@Param("tipo") String tipo);

    Page<AuditoriaEvento> findAllByOrderByFechaCreacionDesc(Pageable pageable);
}
