package com.startup.cobranza.agencia.service;

import com.startup.cobranza.agencia.dto.AgenciaDTO;
import com.startup.cobranza.agencia.dto.AgenciaFormDTO;
import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.agencia.exception.AgenciaException;
import com.startup.cobranza.agencia.mapper.AgenciaMapper;
import com.startup.cobranza.agencia.repository.AgenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgenciaService {

    private final AgenciaRepository agenciaRepository;
    private final AgenciaMapper agenciaMapper;

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
}
