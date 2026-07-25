package com.startup.cobranza.cartera.service;

import com.startup.cobranza.cartera.dto.OperacionDTO;
import com.startup.cobranza.cartera.mapper.OperacionMapper;
import com.startup.cobranza.cartera.repository.OperacionRepository;
import com.startup.cobranza.empresa.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperacionService {

    private final OperacionRepository operacionRepository;
    private final OperacionMapper operacionMapper;
    private final EmpresaRepository empresaRepository;

    public Page<OperacionDTO> listarPaginado(Pageable pageable) {
        return operacionRepository.findAll(pageable)
                .map(operacionMapper::toDTO);
    }

    public List<OperacionDTO> listarPorCliente(Long clienteId) {
        return operacionRepository.findByClienteId(clienteId).stream()
                .map(operacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OperacionDTO> listarPorAgencia(Long agenciaId) {
        return operacionRepository.findByAgenciaId(agenciaId).stream()
                .map(operacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OperacionDTO> listarPorExpediente(String numeroExpediente) {
        return operacionRepository.findByNumeroExpediente(numeroExpediente).stream()
                .map(operacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OperacionDTO> listarPorSituacion(String situacion) {
        return operacionRepository.findBySituacion(situacion).stream()
                .map(operacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OperacionDTO> listarFiltradas(String agenciaNombre, String situacion, String numeroExpediente, String dni, String cuenta, Pageable pageable) {
        return operacionRepository.findAll(pageable).stream()
                .map(operacionMapper::toDTO)
                .filter(op -> agenciaNombre == null || agenciaNombre.isBlank() ||
                        (op.getAgenciaNombre() != null && op.getAgenciaNombre().toLowerCase().contains(agenciaNombre.toLowerCase())))
                .filter(op -> situacion == null || situacion.isBlank() ||
                        (op.getSituacion() != null && op.getSituacion().equalsIgnoreCase(situacion)))
                .filter(op -> numeroExpediente == null || numeroExpediente.isBlank() ||
                        (op.getNumeroExpediente() != null && op.getNumeroExpediente().contains(numeroExpediente)))
                .filter(op -> dni == null || dni.isBlank() ||
                        (op.getClienteDni() != null && op.getClienteDni().contains(dni)))
                .filter(op -> cuenta == null || cuenta.isBlank() ||
                        (op.getCuenta() != null && op.getCuenta().contains(cuenta)))
                .collect(Collectors.toList());
    }

    public long count() {
        return operacionRepository.count();
    }
}
