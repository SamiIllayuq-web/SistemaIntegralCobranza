package com.startup.cobranza.usuario.mapper;

import com.startup.cobranza.usuario.dto.UsuarioDTO;
import com.startup.cobranza.usuario.dto.UsuarioFormDTO;
import com.startup.cobranza.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {

    private final PasswordEncoder passwordEncoder;

    public UsuarioDTO toDTO(Usuario entity) {
        if (entity == null) return null;
        return UsuarioDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .nombreCompleto(entity.getNombreCompleto())
                .rol(entity.getRol())
                .activo(entity.getActivo())
                .build();
    }

    public Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) return null;
        return Usuario.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .nombreCompleto(dto.getNombreCompleto())
                .rol(dto.getRol())
                .activo(dto.getActivo())
                .build();
    }

    public Usuario toEntityFromForm(UsuarioFormDTO form) {
        if (form == null) return null;
        return Usuario.builder()
                .id(form.getId())
                .username(form.getUsername())
                .password(passwordEncoder.encode(form.getPassword()))
                .nombreCompleto(form.getNombreCompleto())
                .rol(form.getRol())
                .activo(true)
                .build();
    }
}
