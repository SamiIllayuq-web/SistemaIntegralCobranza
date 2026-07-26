package com.startup.cobranza.cartera.repository;

import com.startup.cobranza.cartera.entity.Operacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperacionRepository extends JpaRepository<Operacion, Long> {

    Optional<Operacion> findByCuentaAndNumeroOperacion(String cuenta, String numeroOperacion);

    List<Operacion> findByClienteId(Long clienteId);

    @Query("SELECT o FROM Operacion o WHERE o.agencia.id = :agenciaId")
    List<Operacion> findByAgenciaId(@Param("agenciaId") Long agenciaId);

    @Query("SELECT o FROM Operacion o WHERE o.numeroExpediente = :numeroExpediente")
    List<Operacion> findByNumeroExpediente(@Param("numeroExpediente") String numeroExpediente);

    @Query("SELECT o FROM Operacion o WHERE o.situacion = :situacion")
    List<Operacion> findBySituacion(@Param("situacion") String situacion);

    boolean existsByCuentaAndNumeroOperacion(String cuenta, String numeroOperacion);

    List<Operacion> findByNumeroCuentaContaining(String numeroCuenta);

    List<Operacion> findByNumeroOperacionContaining(String numeroOperacion);
}
