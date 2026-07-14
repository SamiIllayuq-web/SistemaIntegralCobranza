package com.startup.cobranza.agencia.service;

import com.startup.cobranza.agencia.dto.AgenciaDTO;
import com.startup.cobranza.agencia.dto.AgenciaFormDTO;
import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.agencia.exception.AgenciaException;
import com.startup.cobranza.agencia.mapper.AgenciaMapper;
import com.startup.cobranza.agencia.repository.AgenciaRepository;
import com.startup.cobranza.empresa.entity.Empresa;
import com.startup.cobranza.empresa.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgenciaService {

    private final AgenciaRepository agenciaRepository;
    private final EmpresaRepository empresaRepository;
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

    public List<AgenciaDTO> listarPorEmpresa(Long empresaId) {
        return agenciaRepository.findByEmpresaIdAndActivoTrue(empresaId).stream()
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
        Empresa empresa = empresaRepository.findById(form.getEmpresaId())
                .orElseThrow(() -> new AgenciaException("Empresa no encontrada con id: " + form.getEmpresaId()));

        if (form.getCodigo() != null && !form.getCodigo().isBlank()
                && agenciaRepository.existsByCodigo(form.getCodigo())) {
            throw new AgenciaException("Ya existe una agencia con el código: " + form.getCodigo());
        }

        Agencia agencia = agenciaMapper.toEntityFromForm(form, empresa);
        return agenciaMapper.toDTO(agenciaRepository.save(agencia));
    }

    @Transactional
    public AgenciaDTO actualizar(Long id, AgenciaFormDTO form) {
        Agencia agencia = agenciaRepository.findById(id)
                .orElseThrow(() -> new AgenciaException("Agencia no encontrada con id: " + id));

        Empresa empresa = empresaRepository.findById(form.getEmpresaId())
                .orElseThrow(() -> new AgenciaException("Empresa no encontrada con id: " + form.getEmpresaId()));

        agencia.setNombre(form.getNombre());
        agencia.setCodigo(form.getCodigo());
        agencia.setTelefono(form.getTelefono());
        agencia.setDireccion(form.getDireccion());
        agencia.setEmpresa(empresa);

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
