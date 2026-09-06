package com.startup.cobranza.operacion.controller;

import com.startup.cobranza.agencia.repository.AgenciaRepository;
import com.startup.cobranza.operacion.dto.OperacionDTO;
import com.startup.cobranza.operacion.dto.OperacionFormDTO;
import com.startup.cobranza.operacion.exception.OperacionException;
import com.startup.cobranza.operacion.mapper.OperacionMapper;
import com.startup.cobranza.operacion.service.OperacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/operaciones")
public class OperacionController {

    private final OperacionService operacionService;
    private final OperacionMapper operacionMapper;
    private final AgenciaRepository agenciaRepository;

    public OperacionController(OperacionService operacionService,
                               OperacionMapper operacionMapper,
                               AgenciaRepository agenciaRepository) {
        this.operacionService = operacionService;
        this.operacionMapper = operacionMapper;
        this.agenciaRepository = agenciaRepository;
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        OperacionDTO op = operacionService.obtenerPorId(id);
        model.addAttribute("operacion", op);
        model.addAttribute("breadcrumbParent", "Operaciones");
        model.addAttribute("breadcrumbParentUrl", "/operaciones");
        model.addAttribute("breadcrumbCurrent", op.getNumeroOperacion());
        return "operacion/detalle";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        try {
            OperacionDTO dto = operacionService.obtenerPorId(id);
            OperacionFormDTO form = operacionMapper.toFormDTO(
                    operacionService.obtenerEntityPorId(id));
            model.addAttribute("operacionForm", form);
            model.addAttribute("operacionId", id);
            model.addAttribute("agencias", agenciaRepository.findByActivoTrue());
            return "operacion/formulario";
        } catch (OperacionException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/operaciones/" + id;
        }
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardar(OperacionFormDTO form,
                         RedirectAttributes redirectAttrs,
                         Model model) {
        try {
            if (form.getId() != null) {
                operacionService.actualizar(form.getId(), form);
                redirectAttrs.addFlashAttribute("success", "Operación actualizada correctamente");
                return "redirect:/operaciones/editar/" + form.getId();
            } else {
                OperacionDTO created = operacionService.crear(form);
                redirectAttrs.addFlashAttribute("success", "Operación creada correctamente");
                return "redirect:/operaciones/" + created.getId();
            }
        } catch (OperacionException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("operacionId", form.getId());
            return "operacion/formulario";
        }
    }
}
