package com.startup.cobranza.expediente.controller;

import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.agencia.repository.AgenciaRepository;
import com.startup.cobranza.empresa.dto.EmpresaDTO;
import com.startup.cobranza.empresa.service.EmpresaService;
import com.startup.cobranza.expediente.dto.ExpedienteFormDTO;
import com.startup.cobranza.expediente.entity.*;
import com.startup.cobranza.expediente.repository.ExpedienteRepository;
import com.startup.cobranza.expediente.service.ExpedienteService;
import com.startup.cobranza.expediente.service.ExpedienteService.ResultadoImportacion;
import com.startup.cobranza.usuario.entity.Usuario;
import com.startup.cobranza.usuario.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/expedientes")
@RequiredArgsConstructor
public class ExpedienteController {

    private final ExpedienteService expedienteService;
    private final ExpedienteRepository expedienteRepository;
    private final EmpresaService empresaService;
    private final AgenciaRepository agenciaRepository;
    private final UsuarioRepository usuarioRepository;

    // ===== LISTA =====
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String lista(
            @RequestParam(value = "empresaId", required = false) Long empresaId,
            @RequestParam(value = "situacion", required = false) String situacion,
            @RequestParam(value = "busqueda", required = false) String busqueda,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        Page<Expediente> expedientes;

        if (empresaId != null) {
            expedientes = expedienteRepository.findByEmpresaId(empresaId, PageRequest.of(page, size, Sort.by("id").descending()));
            model.addAttribute("empresaIdSeleccionada", empresaId);
        } else if (situacion != null && !situacion.isEmpty()) {
            expedientes = expedienteRepository.findBySituacion(situacion, PageRequest.of(page, size, Sort.by("id").descending()));
            model.addAttribute("situacionSeleccionada", situacion);
        } else if (busqueda != null && !busqueda.isEmpty()) {
            expedientes = expedienteRepository.findByNumeroExpedienteContaining(busqueda, PageRequest.of(page, size, Sort.by("id").descending()));
            model.addAttribute("busqueda", busqueda);
        } else {
            expedientes = expedienteRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
        }

        model.addAttribute("expedientes", expedientes);
        model.addAttribute("empresas", empresaService.listarActivas());
        return "expediente/lista";
    }

    // ===== DETALLE =====
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String detalle(@PathVariable Long id, Model model) {
        return expedienteRepository.findById(id)
                .map(exp -> {
                    model.addAttribute("expediente", exp);
                    model.addAttribute("breadcrumbParent", "Expedientes");
                    model.addAttribute("breadcrumbParentUrl", "/expedientes");
                    model.addAttribute("breadcrumbCurrent", exp.getNumeroExpediente());
                    return "expediente/detalle";
                })
                .orElse("redirect:/expedientes");
    }

    // ===== NUEVO =====
    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String nuevoForm(Model model) {
        ExpedienteFormDTO form = new ExpedienteFormDTO();
        model.addAttribute("formDTO", form);
        model.addAttribute("empresas", empresaService.listarActivas());
        model.addAttribute("breadcrumbParent", "Expedientes");
        model.addAttribute("breadcrumbParentUrl", "/expedientes");
        model.addAttribute("breadcrumbCurrent", "Nuevo");
        return "expediente/formulario";
    }

    // ===== EDITAR =====
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarForm(@PathVariable Long id, Model model) {
        Expediente exp = expedienteService.obtenerExpediente(id);
        ExpedienteFormDTO form = new ExpedienteFormDTO();
        form.setNumeroExpediente(exp.getNumeroExpediente());
        form.setSituacion(exp.getSituacion());
        form.setTipoProceso(exp.getTipoProceso());
        form.setTipoJuzgado(exp.getTipoJuzgado());
        form.setDistritoJudicial(exp.getDistritoJudicial());
        form.setNumeroJuzgado(exp.getNumeroJuzgado());
        form.setIncidente(exp.getIncidente());
        form.setMontoDemandado(exp.getMontoDemandado());
        form.setEspecialistaLegal(exp.getEspecialistaLegal());
        form.setEtapaProcesal(exp.getEtapaProcesal());
        form.setObservacion(exp.getObservacion());
        form.setComentarioGeneral(exp.getComentarioGeneral());
        form.setEmpresaId(exp.getEmpresa() != null ? exp.getEmpresa().getId() : null);
        form.setAgenciaId(exp.getAgencia() != null ? exp.getAgencia().getId() : null);
        form.setAbogadoId(exp.getAbogado() != null ? exp.getAbogado().getId() : null);

        model.addAttribute("expediente", exp);
        model.addAttribute("formDTO", form);
        model.addAttribute("expedienteId", id);
        model.addAttribute("empresas", empresaService.listarActivas());
        model.addAttribute("breadcrumbParent", "Expedientes");
        model.addAttribute("breadcrumbParentUrl", "/expedientes");
        model.addAttribute("breadcrumbCurrent", exp.getNumeroExpediente());
        return "expediente/formulario";
    }

    // ===== GUARDAR (create + update) =====
    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardar(
            @ModelAttribute("formDTO") ExpedienteFormDTO form,
            @RequestParam(required = false) Long expedienteId,
            RedirectAttributes redirectAttrs) {
        try {
            Expediente exp;
            if (expedienteId != null) {
                exp = expedienteService.obtenerExpediente(expedienteId);
            } else {
                exp = Expediente.builder().activo(true).build();
            }

            exp.setNumeroExpediente(form.getNumeroExpediente());
            exp.setSituacion(form.getSituacion());
            exp.setTipoProceso(form.getTipoProceso());
            exp.setTipoJuzgado(form.getTipoJuzgado());
            exp.setDistritoJudicial(form.getDistritoJudicial());
            exp.setNumeroJuzgado(form.getNumeroJuzgado());
            exp.setIncidente(form.getIncidente());
            exp.setMontoDemandado(form.getMontoDemandado());
            exp.setEspecialistaLegal(form.getEspecialistaLegal());
            exp.setEtapaProcesal(form.getEtapaProcesal());
            exp.setObservacion(form.getObservacion());
            exp.setComentarioGeneral(form.getComentarioGeneral());

            expedienteService.guardarExpediente(exp, form.getEmpresaId(), form.getAgenciaId(), form.getAbogadoId());

            redirectAttrs.addFlashAttribute("success",
                    expedienteId != null ? "Expediente actualizado" : "Expediente creado");
            return "redirect:/expedientes/" + exp.getId();
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error: " + e.getMessage());
            return expedienteId != null ? "redirect:/expedientes/editar/" + expedienteId : "redirect:/expedientes/nuevo";
        }
    }

    // ===== ELIMINAR =====
    @PostMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            expedienteService.eliminarExpediente(id);
            redirectAttrs.addFlashAttribute("success", "Expediente eliminado");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/expedientes";
    }

    // ===== CLIENTES =====
    @PostMapping("/{expId}/clientes/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardarCliente(
            @PathVariable Long expId,
            @ModelAttribute ExpedienteCliente cliente,
            @RequestParam(required = false) Long clienteId,
            RedirectAttributes redirectAttrs) {
        try {
            if (clienteId != null) {
                expedienteService.actualizarCliente(clienteId, cliente);
            } else {
                expedienteService.agregarCliente(expId, cliente);
            }
            redirectAttrs.addFlashAttribute("success", "Cliente guardado");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/expedientes/" + expId;
    }

    @PostMapping("/{expId}/clientes/eliminar/{clienteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarCliente(
            @PathVariable Long expId,
            @PathVariable Long clienteId,
            RedirectAttributes redirectAttrs) {
        try {
            expedienteService.eliminarCliente(clienteId);
            redirectAttrs.addFlashAttribute("success", "Cliente eliminado");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/expedientes/" + expId;
    }

    // ===== BIENES =====
    @PostMapping("/{expId}/bienes/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardarBien(
            @PathVariable Long expId,
            @ModelAttribute BienEmbargado bien,
            @RequestParam(required = false) Long bienId,
            RedirectAttributes redirectAttrs) {
        try {
            if (bienId != null) {
                expedienteService.actualizarBien(bienId, bien);
            } else {
                expedienteService.agregarBien(expId, bien);
            }
            redirectAttrs.addFlashAttribute("success", "Bien guardado");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/expedientes/" + expId;
    }

    @PostMapping("/{expId}/bienes/eliminar/{bienId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarBien(
            @PathVariable Long expId,
            @PathVariable Long bienId,
            RedirectAttributes redirectAttrs) {
        try {
            expedienteService.eliminarBien(bienId);
            redirectAttrs.addFlashAttribute("success", "Bien eliminado");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/expedientes/" + expId;
    }

    // ===== GESTIONES =====
    @PostMapping("/{expId}/gestiones/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardarGestion(
            @PathVariable Long expId,
            @ModelAttribute GestionProcesal gestion,
            @RequestParam(required = false) Long gestionId,
            RedirectAttributes redirectAttrs) {
        try {
            if (gestionId != null) {
                expedienteService.actualizarGestion(gestionId, gestion);
            } else {
                expedienteService.agregarGestion(expId, gestion);
            }
            redirectAttrs.addFlashAttribute("success", "Gestión guardada");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/expedientes/" + expId;
    }

    @PostMapping("/{expId}/gestiones/eliminar/{gestionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarGestion(
            @PathVariable Long expId,
            @PathVariable Long gestionId,
            RedirectAttributes redirectAttrs) {
        try {
            expedienteService.eliminarGestion(gestionId);
            redirectAttrs.addFlashAttribute("success", "Gestión eliminada");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/expedientes/" + expId;
    }

    // ===== IMPORTAR =====
    @GetMapping("/importar")
    @PreAuthorize("hasRole('ADMIN')")
    public String importarForm(Model model, HttpServletRequest request) {
        model.addAttribute("empresas", empresaService.listarActivas());
        model.addAttribute("usuarioNombre", request.getUserPrincipal().getName());
        model.addAttribute("breadcrumbParent", "Expedientes");
        model.addAttribute("breadcrumbParentUrl", "/expedientes");
        model.addAttribute("breadcrumbCurrent", "Importar");
        return "expediente/importar";
    }

    @PostMapping("/importar")
    @PreAuthorize("hasRole('ADMIN')")
    public String importar(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("empresaId") Long empresaId,
            @RequestParam(required = false) Long agenciaId,
            @RequestParam(defaultValue = "CARTERA SELVA CENTRAL") String nombreHoja,
            RedirectAttributes redirectAttrs,
            HttpServletRequest request) {

        String usuario = request.getUserPrincipal().getName();

        if (archivo.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "El archivo está vacío");
            return "redirect:/expedientes/importar";
        }

        try {
            ResultadoImportacion resultado = expedienteService.importarExcelAvanceProcesal(
                    archivo, empresaId, agenciaId, nombreHoja, usuario);

            redirectAttrs.addFlashAttribute("success",
                    String.format("Importación completada. Total: %d | Creados: %d | Actualizados: %d | Fallidos: %d",
                            resultado.total(), resultado.creados(), resultado.actualizados(), resultado.fallidos()));

            if (!resultado.errores().isEmpty()) {
                redirectAttrs.addFlashAttribute("warning",
                        "Errores: " + String.join(", ", resultado.errores().stream().limit(5).toList()));
            }
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/expedientes/importar";
    }

    // ===== REPORTE MAYO MC =====
    @GetMapping("/reportes/mayo-mc")
    @PreAuthorize("hasRole('ADMIN')")
    public String reporteMayoMcForm(Model model) {
        model.addAttribute("empresas", empresaService.listarActivas());
        model.addAttribute("breadcrumbParent", "Expedientes");
        model.addAttribute("breadcrumbParentUrl", "/expedientes");
        model.addAttribute("breadcrumbCurrent", "Reporte MC");
        return "expediente/reporte-mayo-mc";
    }

    @GetMapping("/reportes/mayo-mc/descargar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> descargarMayoMc(
            @RequestParam Long empresaId,
            @RequestParam String mes,
            @RequestParam Integer anio,
            HttpServletRequest request) {

        String usuario = request.getUserPrincipal().getName();
        byte[] excel = expedienteService.generarReporteMayoMc(empresaId, mes, anio, usuario);

        String filename = "Reporte_Mayo_MC_" + mes + "_" + anio + ".xlsx";
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encoded + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}
