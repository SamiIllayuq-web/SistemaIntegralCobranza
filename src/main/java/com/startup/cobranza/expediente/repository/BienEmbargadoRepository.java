package com.startup.cobranza.expediente.repository;

import com.startup.cobranza.expediente.entity.BienEmbargado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BienEmbargadoRepository extends JpaRepository<BienEmbargado, Long> {

    List<BienEmbargado> findByExpedienteId(Long expedienteId);

    List<BienEmbargado> findByExpedienteIdIn(List<Long> expedienteIds);

    List<BienEmbargado> findByOperacionId(Long operacionId);
}
