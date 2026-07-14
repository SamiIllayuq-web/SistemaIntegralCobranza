package com.startup.cobranza.cartera.repository;

import com.startup.cobranza.cartera.entity.Importacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportacionRepository extends JpaRepository<Importacion, Long> {

    List<Importacion> findAllByOrderByFechaImportacionDesc();
}
