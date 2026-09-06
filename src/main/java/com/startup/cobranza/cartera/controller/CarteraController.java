package com.startup.cobranza.cartera.controller;

import com.startup.cobranza.cartera.dto.ImportacionDTO;
import com.startup.cobranza.cartera.exception.CarteraException;
import com.startup.cobranza.cartera.service.CarteraService;
import com.startup.cobranza.operacion.dto.OperacionDTO;
import com.startup.cobranza.operacion.service.OperacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final OperacionService operacionService;

    @GetMapping("/importar")
    @PreAuthorize("hasRole('ADMIN')")
    public String importarForm(Model model, HttpServletRequest request) {
        model.addAttribute("usuarioNombre", request.getUserPrincipal().getName());
        return "cartera/importar";
    }

    @PostMapping("/importar")
    @PreAuthorize("hasRole('ADMIN')")
    public String importar(@RequestParam("archivo") MultipartFile archivo,
                          @RequestParam(required = false) Long agenciaId,
                          @RequestParam String usuario,
                          RedirectAttributes redirectAttrs) {
        try {
            carteraService.importarExcel(archivo, agenciaId, usuario);
            redirectAttrs.addFlashAttribute("success",
                    "Importacion completada. Revise el historial para ver el detalle.");
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

    @GetMapping("/registros")
    public String registros(
            @RequestParam(value = "agenciaId", required = false) Long agenciaId,
            @RequestParam(value = "estado", required = false) String estado,
            @RequestParam(value = "etapa", required = false) String etapa,
            @RequestParam(value = "busqueda", required = false) String busqueda,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size,
            Model model) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("cliente.nombreCompleto").ascending());
        Page<OperacionDTO> pagina = operacionService.listarCarteraConFiltros(
                agenciaId, estado, etapa, busqueda, pageable);

        model.addAttribute("pagina", pagina);
        model.addAttribute("agenciaId", agenciaId);
        model.addAttribute("estado", estado);
        model.addAttribute("etapa", etapa);
        model.addAttribute("busqueda", busqueda);
        return "cartera/registros";
    }

    /**
     * Vista de Expedientes — misma entidad Operacion, solo filtra
     * por numeroExpediente informado y muestra columnas judiciales.
     */
    @GetMapping("/expedientes")
    public String expedientes(
            @RequestParam(value = "situacion", required = false) String situacion,
            @RequestParam(value = "busqueda", required = false) String busqueda,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size,
            Model model) {

        PageRequest pageable = PageRequest.of(page, size,
                Sort.by("numeroExpediente").ascending()
                    .and(Sort.by("cliente.nombreCompleto").ascending()));
        Page<OperacionDTO> pagina = operacionService.listarExpedientes(
                situacion, busqueda, pageable);

        model.addAttribute("pagina", pagina);
        model.addAttribute("situacion", situacion);
        model.addAttribute("busqueda", busqueda);
        return "cartera/expedientes";
    }
}
