package com.startup.cobranza.empresa.service;

import com.startup.cobranza.empresa.dto.EmpresaDTO;
import com.startup.cobranza.empresa.dto.EmpresaFormDTO;
import com.startup.cobranza.empresa.entity.Empresa;
import com.startup.cobranza.empresa.exception.EmpresaException;
import com.startup.cobranza.empresa.mapper.EmpresaMapper;
import com.startup.cobranza.empresa.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;

    public List<EmpresaDTO> listarTodos() {
        return empresaRepository.findAll().stream()
                .map(empresaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<EmpresaDTO> listarActivas() {
        return empresaRepository.findByActivoTrue().stream()
                .map(empresaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public EmpresaDTO obtenerPorId(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EmpresaException("Empresa no encontrada con id: " + id));
        return empresaMapper.toDTO(empresa);
    }

    @Transactional
    public EmpresaDTO crear(EmpresaFormDTO form) {
        if (form.getRuc() != null && !form.getRuc().isBlank()
                && empresaRepository.existsByRuc(form.getRuc())) {
            throw new EmpresaException("Ya existe una empresa con el RUC: " + form.getRuc());
        }
        Empresa empresa = empresaMapper.toEntityFromForm(form);
        return empresaMapper.toDTO(empresaRepository.save(empresa));
    }

    @Transactional
    public EmpresaDTO actualizar(Long id, EmpresaFormDTO form) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EmpresaException("Empresa no encontrada con id: " + id));

        empresa.setNombre(form.getNombre());
        empresa.setRuc(form.getRuc());
        empresa.setTelefono(form.getTelefono());
        empresa.setEmail(form.getEmail());
        empresa.setDireccion(form.getDireccion());

        return empresaMapper.toDTO(empresaRepository.save(empresa));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!empresaRepository.existsById(id)) {
            throw new EmpresaException("Empresa no encontrada con id: " + id);
        }
        empresaRepository.deleteById(id);
    }
}
