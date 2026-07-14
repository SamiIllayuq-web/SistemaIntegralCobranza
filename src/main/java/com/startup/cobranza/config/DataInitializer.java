package com.startup.cobranza.config;

import com.startup.cobranza.usuario.entity.Usuario;
import com.startup.cobranza.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = Usuario.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .nombreCompleto("Administrador del Sistema")
                    .rol("ADMIN")
                    .activo(true)
                    .build();
            usuarioRepository.save(admin);
            System.out.println("Usuario admin creado: admin / admin123");
        }
    }
}
