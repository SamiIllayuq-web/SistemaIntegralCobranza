package com.startup.cobranza.expediente.repository;

import com.startup.cobranza.expediente.entity.ReporteMc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReporteMcRepository extends JpaRepository<ReporteMc, Long> {

    List<ReporteMc> findByEmpresaIdOrderByFechaGeneracionDesc(Long empresaId);
}
