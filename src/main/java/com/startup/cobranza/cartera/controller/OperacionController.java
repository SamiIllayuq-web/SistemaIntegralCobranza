package com.startup.cobranza.cartera.controller;

import com.startup.cobranza.cartera.dto.OperacionDTO;
import com.startup.cobranza.cartera.service.OperacionService;
import com.startup.cobranza.empresa.dto.EmpresaDTO;
import com.startup.cobranza.empresa.service.EmpresaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/operaciones")
@RequiredArgsConstructor
public class OperacionController {

    private final OperacionService operacionService;
    private final EmpresaService empresaService;

    @GetMapping
    public String listar(
            @RequestParam(required = false) String agencia,
            @RequestParam(required = false) String situacion,
            @RequestParam(required = false) String expediente,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String cuenta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Model model) {

        Page<OperacionDTO> pageResult = operacionService.listarPaginado(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));

        List<OperacionDTO> operaciones = operacionService.listarFiltradas(
                agencia, situacion, expediente, dni, cuenta,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));

        List<EmpresaDTO> empresas = empresaService.listarActivas();

        model.addAttribute("operaciones", operaciones);
        model.addAttribute("empresas", empresas);
        model.addAttribute("total", operacionService.count());
        model.addAttribute("filtros", new Filtros(agencia, situacion, expediente, dni, cuenta));

        return "operacion/lista";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        OperacionDTO operacion = operacionService.buscarPorId(id);
        if (operacion == null) {
            return "redirect:/operaciones";
        }
        model.addAttribute("operacion", operacion);
        return "operacion/detalle";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        OperacionDTO operacion = operacionService.buscarPorId(id);
        if (operacion == null) {
            return "redirect:/operaciones";
        }
        model.addAttribute("operacion", operacion);
        return "operacion/editar";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute OperacionDTO dto,
                          RedirectAttributes redirectAttrs) {
        try {
            if (dto.getId() != null) {
                operacionService.actualizar(dto.getId(), dto);
                redirectAttrs.addFlashAttribute("success", "Operación actualizada correctamente");
            }
            return "redirect:/operaciones";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
            return "redirect:/operaciones/editar/" + dto.getId();
        }
    }

    public record Filtros(String agencia, String situacion, String expediente, String dni, String cuenta) {}
}
