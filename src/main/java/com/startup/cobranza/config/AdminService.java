package com.startup.cobranza.config;

import com.startup.cobranza.agencia.repository.AgenciaRepository;
import com.startup.cobranza.cartera.repository.ImportacionRepository;
import com.startup.cobranza.cliente.repository.ClienteRepository;
import com.startup.cobranza.operacion.repository.BienEmbargadoRepository;
import com.startup.cobranza.operacion.repository.OperacionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final OperacionRepository operacionRepository;
    private final BienEmbargadoRepository bienEmbargadoRepository;
    private final ClienteRepository clienteRepository;
    private final ImportacionRepository importacionRepository;
    private final AgenciaRepository agenciaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public ResetResult resetAllData() {
        long ops = operacionRepository.count();
        long bienes = bienEmbargadoRepository.count();
        long clientes = clienteRepository.count();
        long importaciones = importacionRepository.count();
        long agencias = agenciaRepository.count();

        // TRUNCATE resetea los sequences para que los IDs arranquen en 1
        entityManager.createNativeQuery("TRUNCATE TABLE bienes_embargados RESTART IDENTITY CASCADE").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE importaciones RESTART IDENTITY CASCADE").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE operaciones RESTART IDENTITY CASCADE").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE clientes RESTART IDENTITY CASCADE").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE agencias RESTART IDENTITY CASCADE").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE auditoria_eventos RESTART IDENTITY CASCADE").executeUpdate();

        return new ResetResult(ops, bienes, clientes, importaciones, agencias);
    }

    public record ResetResult(long operacionesEliminadas, long bienesEliminados, long clientesEliminados, long importacionesEliminadas, long agenciasEliminadas) {}
}
