package com.startup.cobranza.agencia.controller;

import com.startup.cobranza.agencia.dto.AgenciaDTO;
import com.startup.cobranza.agencia.dto.AgenciaFormDTO;
import com.startup.cobranza.agencia.exception.AgenciaException;
import com.startup.cobranza.agencia.service.AgenciaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/agencias")
public class AgenciaController {

    private final AgenciaService agenciaService;

    public AgenciaController(AgenciaService agenciaService) {
        this.agenciaService = agenciaService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model) {
        List<AgenciaDTO> agencias = agenciaService.listarTodos();
        model.addAttribute("agencias", agencias);
        return "agencia/lista";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String nuevoForm(Model model) {
        model.addAttribute("agenciaForm", new AgenciaFormDTO());
        return "agencia/formulario";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarForm(@PathVariable Long id, Model model) {
        AgenciaDTO dto = agenciaService.obtenerPorId(id);
        AgenciaFormDTO form = new AgenciaFormDTO();
        form.setNombre(dto.getNombre());
        form.setCodigo(dto.getCodigo());
        form.setTelefono(dto.getTelefono());
        form.setDireccion(dto.getDireccion());
        model.addAttribute("agenciaForm", form);
        model.addAttribute("agenciaId", id);
        return "agencia/formulario";
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardar(@Valid @ModelAttribute("agenciaForm") AgenciaFormDTO form,
                          BindingResult result,
                          @RequestParam(required = false) Long agenciaId,
                          RedirectAttributes redirectAttrs,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("agenciaId", agenciaId);
            return "agencia/formulario";
        }

        try {
            if (agenciaId != null) {
                agenciaService.actualizar(agenciaId, form);
                redirectAttrs.addFlashAttribute("success", "Agencia actualizada correctamente");
            } else {
                agenciaService.crear(form);
                redirectAttrs.addFlashAttribute("success", "Agencia creada correctamente");
            }
            return "redirect:/agencias";
        } catch (AgenciaException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("agenciaId", agenciaId);
            return "agencia/formulario";
        }
    }

    @PostMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            agenciaService.eliminar(id);
            redirectAttrs.addFlashAttribute("success", "Agencia eliminada correctamente");
        } catch (AgenciaException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/agencias";
    }
}
