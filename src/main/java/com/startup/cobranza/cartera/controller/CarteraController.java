package com.startup.cobranza.cartera.controller;

import com.startup.cobranza.cartera.dto.ImportacionDTO;
import com.startup.cobranza.cartera.exception.CarteraException;
import com.startup.cobranza.cartera.service.CarteraService;
import com.startup.cobranza.empresa.dto.EmpresaDTO;
import com.startup.cobranza.empresa.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/cartera")
@RequiredArgsConstructor
public class CarteraController {

    private final CarteraService carteraService;
    private final EmpresaService empresaService;

    @GetMapping("/importar")
    @PreAuthorize("hasRole('ADMIN')")
    public String importarForm(Model model, HttpServletRequest request) {
        List<EmpresaDTO> empresas = empresaService.listarActivas();
        model.addAttribute("empresas", empresas);
        model.addAttribute("usuarioNombre", request.getUserPrincipal().getName());
        return "cartera/importar";
    }

    @PostMapping("/importar")
    @PreAuthorize("hasRole('ADMIN')")
    public String importar(@RequestParam("archivo") MultipartFile archivo,
                          @RequestParam("empresaId") Long empresaId,
                          @RequestParam(required = false) Long agenciaId,
                          @RequestParam String usuario,
                          RedirectAttributes redirectAttrs) {
        try {
            carteraService.importarExcel(archivo, empresaId, agenciaId, usuario);
            redirectAttrs.addFlashAttribute("success",
                    "Importación completada. Revise el historial para ver el detalle.");
        } catch (CarteraException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cartera/importar";
    }

    @GetMapping("/historial")
    @PreAuthorize("hasRole('ADMIN')")
    public String historial(Model model) {
        List<ImportacionDTO> importaciones = carteraService.listarImportaciones();
        model.addAttribute("importaciones", importaciones);
        return "cartera/historial";
    }
}
