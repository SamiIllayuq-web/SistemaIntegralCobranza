package com.startup.cobranza.gestion.repository;

import com.startup.cobranza.gestion.entity.Gestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GestionRepository extends JpaRepository<Gestion, Long> {

    @Query("SELECT g FROM Gestion g WHERE g.cliente.id = :clienteId ORDER BY g.fechaGestion DESC")
    List<Gestion> findByClienteIdOrderByFechaGestionDesc(@Param("clienteId") Long clienteId);

    @Query("SELECT g FROM Gestion g WHERE g.cliente.id = :clienteId AND g.tipo = :tipo ORDER BY g.fechaGestion DESC")
    List<Gestion> findByClienteIdAndTipo(@Param("clienteId") Long clienteId, @Param("tipo") com.startup.cobranza.gestion.entity.TipoGestion tipo);
}
