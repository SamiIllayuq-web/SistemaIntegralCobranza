package com.startup.cobranza.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class GlobalControllerAdvice {

    private static final DateTimeFormatter FECHA_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @ModelAttribute("fechaActual")
    public String fechaActual() {
        return LocalDate.now().format(FECHA_FORMAT);
    }

    @ModelAttribute("nombreUsuario")
    public String nombreUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
            return auth.getName();
        }
        return "Usuario";
    }
}
