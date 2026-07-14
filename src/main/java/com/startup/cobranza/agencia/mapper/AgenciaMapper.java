package com.startup.cobranza.agencia.mapper;

import com.startup.cobranza.agencia.dto.AgenciaDTO;
import com.startup.cobranza.agencia.dto.AgenciaFormDTO;
import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.empresa.entity.Empresa;
import org.springframework.stereotype.Component;

@Component
public class AgenciaMapper {

    public AgenciaDTO toDTO(Agencia entity) {
        if (entity == null) return null;
        return AgenciaDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .codigo(entity.getCodigo())
                .telefono(entity.getTelefono())
                .direccion(entity.getDireccion())
                .empresaId(entity.getEmpresa().getId())
                .empresaNombre(entity.getEmpresa().getNombre())
                .activo(entity.getActivo())
                .build();
    }

    public Agencia toEntity(AgenciaDTO dto, Empresa empresa) {
        if (dto == null) return null;
        return Agencia.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .codigo(dto.getCodigo())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .empresa(empresa)
                .activo(dto.getActivo())
                .build();
    }

    public Agencia toEntityFromForm(AgenciaFormDTO form, Empresa empresa) {
        if (form == null) return null;
        return Agencia.builder()
                .id(form.getId())
                .nombre(form.getNombre())
                .codigo(form.getCodigo())
                .telefono(form.getTelefono())
                .direccion(form.getDireccion())
                .empresa(empresa)
                .activo(true)
                .build();
    }
}
