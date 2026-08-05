package com.startup.cobranza.cliente.mapper;

import com.startup.cobranza.cliente.dto.ClienteDTO;
import com.startup.cobranza.cliente.dto.ClienteFormDTO;
import com.startup.cobranza.cliente.entity.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public ClienteDTO toDTO(Cliente entity) {
        if (entity == null) return null;
        return ClienteDTO.builder()
                .id(entity.getId())
                .nombreCompleto(entity.getNombreCompleto())
                .dni(entity.getDni())
                .telefono(entity.getTelefono())
                .telefono2(entity.getTelefono2())
                .telefono3(entity.getTelefono3())
                .direccion(entity.getDireccion())
                .email(entity.getEmail())
                .activo(entity.getActivo())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    public Cliente toEntity(ClienteDTO dto) {
        if (dto == null) return null;
        return Cliente.builder()
                .id(dto.getId())
                .nombreCompleto(dto.getNombreCompleto())
                .dni(dto.getDni())
                .telefono(dto.getTelefono())
                .telefono2(dto.getTelefono2())
                .telefono3(dto.getTelefono3())
                .direccion(dto.getDireccion())
                .email(dto.getEmail())
                .activo(dto.getActivo())
                .build();
    }

    public Cliente toEntityFromForm(ClienteFormDTO form) {
        if (form == null) return null;
        return Cliente.builder()
                .id(form.getId())
                .nombreCompleto(form.getNombreCompleto())
                .dni(form.getDni())
                .telefono(form.getTelefono())
                .telefono2(form.getTelefono2())
                .telefono3(form.getTelefono3())
                .direccion(form.getDireccion())
                .email(form.getEmail())
                .activo(true)
                .build();
    }
}
