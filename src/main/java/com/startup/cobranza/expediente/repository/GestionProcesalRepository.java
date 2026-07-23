package com.startup.cobranza.expediente.repository;

import com.startup.cobranza.expediente.entity.GestionProcesal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GestionProcesalRepository extends JpaRepository<GestionProcesal, Long> {

    List<GestionProcesal> findByExpedienteIdOrderByFechaDesc(Long expedienteId);
}
