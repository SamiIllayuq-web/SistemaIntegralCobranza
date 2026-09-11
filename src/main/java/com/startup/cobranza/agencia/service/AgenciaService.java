package com.startup.cobranza.agencia.service;

import com.startup.cobranza.agencia.dto.AgenciaAgrupadaDTO;
import com.startup.cobranza.agencia.dto.AgenciaDTO;
import com.startup.cobranza.agencia.dto.AgenciaFormDTO;
import com.startup.cobranza.agencia.dto.OperacionResumidaDTO;
import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.agencia.exception.AgenciaException;
import com.startup.cobranza.agencia.mapper.AgenciaMapper;
import com.startup.cobranza.agencia.repository.AgenciaRepository;
import com.startup.cobranza.operacion.entity.Operacion;
import com.startup.cobranza.operacion.repository.OperacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgenciaService {

    private final AgenciaRepository agenciaRepository;
    private final AgenciaMapper agenciaMapper;
    private final OperacionRepository operacionRepository;

    public List<AgenciaDTO> listarTodos() {
        return agenciaRepository.findAll().stream()
                .map(agenciaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<AgenciaDTO> listarActivas() {
        return agenciaRepository.findByActivoTrue().stream()
                .map(agenciaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public AgenciaDTO obtenerPorId(Long id) {
        Agencia agencia = agenciaRepository.findById(id)
                .orElseThrow(() -> new AgenciaException("Agencia no encontrada con id: " + id));
        return agenciaMapper.toDTO(agencia);
    }

    @Transactional
    public AgenciaDTO crear(AgenciaFormDTO form) {
        if (form.getCodigo() != null && !form.getCodigo().isBlank()
                && agenciaRepository.existsByCodigo(form.getCodigo())) {
            throw new AgenciaException("Ya existe una agencia con el codigo: " + form.getCodigo());
        }

        Agencia agencia = agenciaMapper.toEntityFromForm(form);
        return agenciaMapper.toDTO(agenciaRepository.save(agencia));
    }

    @Transactional
    public AgenciaDTO actualizar(Long id, AgenciaFormDTO form) {
        Agencia agencia = agenciaRepository.findById(id)
                .orElseThrow(() -> new AgenciaException("Agencia no encontrada con id: " + id));

        agencia.setNombre(form.getNombre());
        agencia.setCodigo(form.getCodigo());
        agencia.setTelefono(form.getTelefono());
        agencia.setDireccion(form.getDireccion());

        return agenciaMapper.toDTO(agenciaRepository.save(agencia));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!agenciaRepository.existsById(id)) {
            throw new AgenciaException("Agencia no encontrada con id: " + id);
        }
        agenciaRepository.deleteById(id);
    }

    /**
     * Agrupa todas las operaciones activas por agencia.
     * Retorna lista ordenada por nombre de agencia.
     */
    public List<AgenciaAgrupadaDTO> agruparOperaciones() {
        List<Operacion> ops = operacionRepository.findAllActivasAgrupadasPorAgencia();

        Map<Long, AgenciaAgrupadaDTO> mapa = new LinkedHashMap<>();
        Map<Long, BigDecimal> totales = new LinkedHashMap<>();
        Map<Long, String> monedas = new LinkedHashMap<>();

        for (Operacion op : ops) {
            Long agId = op.getAgencia() != null ? op.getAgencia().getId() : null;
            String agNombre = op.getAgencia() != null ? op.getAgencia().getNombre() : "Sin agencia";
            String agCodigo = op.getAgencia() != null ? op.getAgencia().getCodigo() : null;

            mapa.computeIfAbsent(agId, k -> new AgenciaAgrupadaDTO(
                    agId,
                    agNombre,
                    agCodigo,
                    0,
                    BigDecimal.ZERO,
                    null,
                    new ArrayList<>()
            ));

            AgenciaAgrupadaDTO grupo = mapa.get(agId);
            grupo.setTotalOperaciones(grupo.getTotalOperaciones() + 1);

            BigDecimal monto = op.getMontoTotal() != null ? op.getMontoTotal() : BigDecimal.ZERO;
            grupo.setMontoTotal(grupo.getMontoTotal().add(monto));

            if (grupo.getMoneda() == null && op.getMoneda() != null) {
                grupo.setMoneda(op.getMoneda());
            }

            grupo.getOperaciones().add(new OperacionResumidaDTO(
                    op.getId(),
                    op.getCliente().getId(),
                    op.getCliente().getNombreCompleto(),
                    op.getCliente().getDni(),
                    op.getNumeroOperacion(),
                    op.getNumeroExpediente(),
                    op.getSituacion(),
                    op.getEtapa(),
                    op.getMontoTotal(),
                    op.getMoneda()
            ));
        }

        return new ArrayList<>(mapa.values());
    }
}
