package com.startup.cobranza.empresa.mapper;

import com.startup.cobranza.empresa.dto.EmpresaDTO;
import com.startup.cobranza.empresa.dto.EmpresaFormDTO;
import com.startup.cobranza.empresa.entity.Empresa;
import org.springframework.stereotype.Component;

@Component
public class EmpresaMapper {

    public EmpresaDTO toDTO(Empresa entity) {
        if (entity == null) return null;
        return EmpresaDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .ruc(entity.getRuc())
                .telefono(entity.getTelefono())
                .email(entity.getEmail())
                .direccion(entity.getDireccion())
                .activo(entity.getActivo())
                .build();
    }

    public Empresa toEntity(EmpresaDTO dto) {
        if (dto == null) return null;
        return Empresa.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .ruc(dto.getRuc())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .direccion(dto.getDireccion())
                .activo(dto.getActivo())
                .build();
    }

    public Empresa toEntityFromForm(EmpresaFormDTO form) {
        if (form == null) return null;
        return Empresa.builder()
                .id(form.getId())
                .nombre(form.getNombre())
                .ruc(form.getRuc())
                .telefono(form.getTelefono())
                .email(form.getEmail())
                .direccion(form.getDireccion())
                .activo(true)
                .build();
    }
}
