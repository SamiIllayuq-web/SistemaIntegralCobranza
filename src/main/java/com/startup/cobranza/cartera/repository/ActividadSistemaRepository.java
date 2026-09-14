package com.startup.cobranza.cartera.repository;

import com.startup.cobranza.cartera.entity.ActividadSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadSistemaRepository extends JpaRepository<ActividadSistema, Long> {

    List<ActividadSistema> findAllByOrderByFechaDesc();

    List<ActividadSistema> findByTipoOrderByFechaDesc(String tipo);
}
