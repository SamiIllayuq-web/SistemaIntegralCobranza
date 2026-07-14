package com.startup.cobranza.usuario.controller;

import com.startup.cobranza.usuario.dto.UsuarioDTO;
import com.startup.cobranza.usuario.dto.UsuarioFormDTO;
import com.startup.cobranza.usuario.exception.UsuarioException;
import com.startup.cobranza.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model) {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "usuario/lista";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String nuevoForm(Model model) {
        model.addAttribute("usuarioForm", new UsuarioFormDTO());
        return "usuario/formulario";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarForm(@PathVariable Long id, Model model) {
        UsuarioDTO dto = usuarioService.obtenerPorId(id);
        UsuarioFormDTO form = new UsuarioFormDTO();
        form.setUsername(dto.getUsername());
        form.setNombreCompleto(dto.getNombreCompleto());
        form.setRol(dto.getRol());
        model.addAttribute("usuarioForm", form);
        model.addAttribute("usuarioId", id);
        return "usuario/formulario";
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardar(@Valid @ModelAttribute("usuarioForm") UsuarioFormDTO form,
                          BindingResult result,
                          @RequestParam(required = false) Long usuarioId,
                          RedirectAttributes redirectAttrs,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("usuarioId", usuarioId);
            return "usuario/formulario";
        }

        try {
            if (usuarioId != null) {
                usuarioService.actualizar(usuarioId, form);
                redirectAttrs.addFlashAttribute("success", "Usuario actualizado correctamente");
            } else {
                usuarioService.crear(form);
                redirectAttrs.addFlashAttribute("success", "Usuario creado correctamente");
            }
            return "redirect:/usuarios";
        } catch (UsuarioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuarioId", usuarioId);
            return "usuario/formulario";
        }
    }

    @PostMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            usuarioService.eliminar(id);
            redirectAttrs.addFlashAttribute("success", "Usuario eliminado correctamente");
        } catch (UsuarioException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}
