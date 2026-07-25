package com.startup.cobranza.cliente.mapper;

import com.startup.cobranza.cliente.dto.ClienteDTO;
import com.startup.cobranza.cliente.dto.ClienteFormDTO;
import com.startup.cobranza.cliente.entity.Cliente;
import com.startup.cobranza.empresa.entity.Empresa;
import com.startup.cobranza.agencia.entity.Agencia;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class ClienteMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
                .empresaId(entity.getEmpresa() != null ? entity.getEmpresa().getId() : null)
                .empresaNombre(entity.getEmpresa() != null ? entity.getEmpresa().getNombre() : null)
                .fechaCreacion(entity.getFechaCreacion() != null ? entity.getFechaCreacion().format(FORMATTER) : null)
                .activo(entity.getActivo())
                .build();
    }

    public Cliente toEntity(ClienteDTO dto, Empresa empresa, Agencia agencia) {
        if (dto == null) return null;
        return Cliente.builder()
                .id(dto.getId())
                .nombreCompleto(dto.getNombreCompleto())
                .dni(dto.getDni())
                .telefono(dto.getTelefono())
                .telefono2(dto.getTelefono2())
                .telefono3(dto.getTelefono3())
                .direccion(dto.getDireccion())
                .empresa(empresa)
                .activo(dto.getActivo())
                .build();
    }

    public Cliente toEntityFromForm(ClienteFormDTO form, Empresa empresa, Agencia agencia) {
        if (form == null) return null;
        return Cliente.builder()
                .id(form.getId())
                .nombreCompleto(form.getNombreCompleto())
                .dni(form.getDni())
                .telefono(form.getTelefono())
                .telefono2(form.getTelefono2())
                .telefono3(form.getTelefono3())
                .direccion(form.getDireccion())
                .empresa(empresa)
                .activo(true)
                .build();
    }
}
