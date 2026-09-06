
package com.startup.cobranza.agencia.mapper;

import com.startup.cobranza.agencia.dto.AgenciaDTO;
import com.startup.cobranza.agencia.dto.AgenciaFormDTO;
import com.startup.cobranza.agencia.entity.Agencia;
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
                .activo(entity.getActivo())
                .build();
    }

    public Agencia toEntity(AgenciaDTO dto) {
        if (dto == null) return null;
        return Agencia.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .codigo(dto.getCodigo())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .activo(dto.getActivo())
                .build();
    }

    public Agencia toEntityFromForm(AgenciaFormDTO form) {
        if (form == null) return null;
        return Agencia.builder()
                .id(form.getId())
                .nombre(form.getNombre())
                .codigo(form.getCodigo())
                .telefono(form.getTelefono())
                .direccion(form.getDireccion())
                .activo(true)
                .build();
    }
}
