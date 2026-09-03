package com.startup.cobranza.operacion.repository;

import com.startup.cobranza.operacion.entity.BienEmbargado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BienEmbargadoRepository extends JpaRepository<BienEmbargado, Long> {

    List<BienEmbargado> findByOperacionId(Long operacionId);
}
