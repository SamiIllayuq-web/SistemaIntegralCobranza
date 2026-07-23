package com.startup.cobranza.expediente.repository;

import com.startup.cobranza.expediente.entity.ExpedienteCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExpedienteClienteRepository extends JpaRepository<ExpedienteCliente, Long> {

    List<ExpedienteCliente> findByExpedienteId(Long expedienteId);

    List<ExpedienteCliente> findByExpedienteIdIn(List<Long> expedienteIds);

    List<ExpedienteCliente> findByDni(String dni);
}
