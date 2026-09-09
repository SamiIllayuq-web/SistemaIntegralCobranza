package com.startup.cobranza.config;

import com.startup.cobranza.agencia.repository.AgenciaRepository;
import com.startup.cobranza.cartera.entity.Importacion;
import com.startup.cobranza.cartera.repository.ImportacionRepository;
import com.startup.cobranza.cliente.entity.Cliente;
import com.startup.cobranza.cliente.repository.ClienteRepository;
import com.startup.cobranza.operacion.entity.BienEmbargado;
import com.startup.cobranza.operacion.entity.Operacion;
import com.startup.cobranza.operacion.repository.BienEmbargadoRepository;
import com.startup.cobranza.operacion.repository.OperacionRepository;
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

    @Transactional
    public ResetResult resetAllData() {
        long ops = operacionRepository.count();
        long bienes = bienEmbargadoRepository.count();
        long clientes = clienteRepository.count();
        long importaciones = importacionRepository.count();
        long agencias = agenciaRepository.count();

        bienEmbargadoRepository.deleteAllInBatch();
        operacionRepository.deleteAllInBatch();
        clienteRepository.deleteAllInBatch();
        importacionRepository.deleteAllInBatch();
        agenciaRepository.deleteAllInBatch();

        return new ResetResult(ops, bienes, clientes, importaciones, agencias);
    }

    public record ResetResult(long operacionesEliminadas, long bienesEliminados, long clientesEliminados, long importacionesEliminadas, long agenciasEliminadas) {}
}
