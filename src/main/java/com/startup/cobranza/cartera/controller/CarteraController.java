package com.startup.cobranza.cartera.controller;

import com.startup.cobranza.agencia.repository.AgenciaRepository;
import com.startup.cobranza.cartera.dto.ImportacionDTO;
import com.startup.cobranza.cartera.entity.ActividadSistema;
import com.startup.cobranza.cartera.exception.CarteraException;
import com.startup.cobranza.cartera.repository.ActividadSistemaRepository;
import com.startup.cobranza.cartera.service.CarteraService;
import com.startup.cobranza.cliente.dto.ClienteExpedienteDTO;
import com.startup.cobranza.cliente.service.ClienteService;
import com.startup.cobranza.operacion.dto.OperacionDTO;
import com.startup.cobranza.operacion.service.OperacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Controller
@RequestMapping("/cartera")
@RequiredArgsConstructor
public class CarteraController {

    private final CarteraService carteraService;
    private final ClienteService clienteService;
    private final OperacionService operacionService;
    private final ActividadSistemaRepository actividadSistemaRepository;
    private final AgenciaRepository agenciaRepository;

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
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO')")
    public String historial(Model model) {
        List<ImportacionDTO> importaciones = carteraService.listarImportaciones();
        model.addAttribute("importaciones", importaciones);
        return "cartera/historial";
    }

    @GetMapping("/actividad")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO')")
    public String actividad(
            @RequestParam(required = false) String tipo,
            Model model) {
        List<ActividadSistema> actividades;
        if (tipo != null && !tipo.isBlank()) {
            actividades = actividadSistemaRepository.findByTipoOrderByFechaDesc(tipo);
        } else {
            actividades = actividadSistemaRepository.findAllByOrderByFechaDesc();
        }
        model.addAttribute("actividades", actividades);
        model.addAttribute("tipoFiltro", tipo);
        return "cartera/actividad";
    }

    @GetMapping("/expedientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO')")
    public String expedientes(
            @RequestParam(value = "agenciaId", required = false) Long agenciaId,
            @RequestParam(value = "busqueda", required = false) String busqueda,
            @RequestParam(value = "situacion", required = false) String situacion,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size,
            Model model) {

        // Sanitizar: string vacío del query params → null
        if (agenciaId != null && agenciaId == 0L) {
            agenciaId = null;
        }

        log.debug("expedientes - agenciaId={}, busqueda={}, situacion={}", agenciaId, busqueda, situacion);

        PageRequest pageable = PageRequest.of(page, size,
                Sort.by("nombreCompleto").ascending());
        Page<ClienteExpedienteDTO> pagina = clienteService.listarClientesConExpedientes(
                agenciaId, situacion, busqueda, pageable);

        model.addAttribute("pagina", pagina);
        model.addAttribute("agenciaId", agenciaId != null ? agenciaId : "");
        model.addAttribute("busqueda", busqueda != null ? busqueda : "");
        model.addAttribute("situacion", situacion != null ? situacion : "");
        model.addAttribute("agencias", agenciaRepository.findByActivoTrue());
        return "cartera/expedientes";
    }

    @GetMapping("/estado-cartera")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO')")
    public String estadoCartera(
            @RequestParam(value = "estadoCartera", required = false) String estadoCartera,
            @RequestParam(value = "etapaProcesal", required = false) String etapaProcesal,
            @RequestParam(value = "agenciaId", required = false) Long agenciaId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size,
            Model model) {

        size = Math.min(size, 200);
        PageRequest pageable = PageRequest.of(page, size,
                Sort.by("cliente.nombreCompleto").ascending()
                    .and(Sort.by("id").ascending()));

        Page<OperacionDTO> pagina = operacionService.listarPorEstadoCartera(
                estadoCartera, etapaProcesal, agenciaId, pageable);

        model.addAttribute("pagina", pagina);
        model.addAttribute("estadoCartera", estadoCartera);
        model.addAttribute("etapaProcesal", etapaProcesal);
        model.addAttribute("agenciaId", agenciaId);
        model.addAttribute("agencias", agenciaRepository.findByActivoTrue());
        return "cartera/estado-cartera";
    }
}