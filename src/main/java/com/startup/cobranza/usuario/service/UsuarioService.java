package com.startup.cobranza.usuario.service;

import com.startup.cobranza.usuario.dto.UsuarioDTO;
import com.startup.cobranza.usuario.dto.UsuarioFormDTO;
import com.startup.cobranza.usuario.entity.Usuario;
import com.startup.cobranza.usuario.exception.UsuarioException;
import com.startup.cobranza.usuario.mapper.UsuarioMapper;
import com.startup.cobranza.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioException("Usuario no encontrado con id: " + id));
        return usuarioMapper.toDTO(usuario);
    }

    @Transactional
    public UsuarioDTO crear(UsuarioFormDTO form) {
        if (usuarioRepository.existsByUsername(form.getUsername())) {
            throw new UsuarioException("El nombre de usuario ya existe: " + form.getUsername());
        }
        Usuario usuario = usuarioMapper.toEntityFromForm(form);
        return usuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioDTO actualizar(Long id, UsuarioFormDTO form) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioException("Usuario no encontrado con id: " + id));

        usuario.setNombreCompleto(form.getNombreCompleto());
        usuario.setRol(form.getRol());

        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(form.getPassword()));
        }

        return usuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
