package com.startup.cobranza.empresa.controller;

import com.startup.cobranza.empresa.dto.EmpresaDTO;
import com.startup.cobranza.empresa.dto.EmpresaFormDTO;
import com.startup.cobranza.empresa.exception.EmpresaException;
import com.startup.cobranza.empresa.service.EmpresaService;
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
@RequestMapping("/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listar(Model model) {
        List<EmpresaDTO> empresas = empresaService.listarTodos();
        model.addAttribute("empresas", empresas);
        return "empresa/lista";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String nuevoForm(Model model) {
        model.addAttribute("empresaForm", new EmpresaFormDTO());
        return "empresa/formulario";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarForm(@PathVariable Long id, Model model) {
        EmpresaDTO dto = empresaService.obtenerPorId(id);
        EmpresaFormDTO form = new EmpresaFormDTO();
        form.setNombre(dto.getNombre());
        form.setRuc(dto.getRuc());
        form.setTelefono(dto.getTelefono());
        form.setEmail(dto.getEmail());
        form.setDireccion(dto.getDireccion());
        model.addAttribute("empresaForm", form);
        model.addAttribute("empresaId", id);
        return "empresa/formulario";
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardar(@Valid @ModelAttribute("empresaForm") EmpresaFormDTO form,
                          BindingResult result,
                          @RequestParam(required = false) Long empresaId,
                          RedirectAttributes redirectAttrs,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("empresaId", empresaId);
            return "empresa/formulario";
        }

        try {
            if (empresaId != null) {
                empresaService.actualizar(empresaId, form);
                redirectAttrs.addFlashAttribute("success", "Empresa actualizada correctamente");
            } else {
                empresaService.crear(form);
                redirectAttrs.addFlashAttribute("success", "Empresa creada correctamente");
            }
            return "redirect:/empresas";
        } catch (EmpresaException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("empresaId", empresaId);
            return "empresa/formulario";
        }
    }

    @PostMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            empresaService.eliminar(id);
            redirectAttrs.addFlashAttribute("success", "Empresa eliminada correctamente");
        } catch (EmpresaException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/empresas";
    }
}
