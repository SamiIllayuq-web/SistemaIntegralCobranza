package com.startup.cobranza.cliente.controller;

import com.startup.cobranza.cliente.dto.ClienteBusquedaDTO;
import com.startup.cobranza.cliente.dto.ClienteDTO;
import com.startup.cobranza.cliente.dto.ClienteFormDTO;
import com.startup.cobranza.cliente.exception.ClienteException;
import com.startup.cobranza.cliente.service.ClienteService;
import com.startup.cobranza.empresa.dto.EmpresaDTO;
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
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;
    private final EmpresaService empresaService;

    @GetMapping
    public String listar(@ModelAttribute ClienteBusquedaDTO busqueda, Model model) {
        List<ClienteDTO> clientes;
        if (hasBusqueda(busqueda)) {
            clientes = clienteService.buscar(busqueda);
        } else {
            clientes = clienteService.listarActivos();
        }
        List<EmpresaDTO> empresas = empresaService.listarActivas();
        model.addAttribute("clientes", clientes);
        model.addAttribute("empresas", empresas);
        model.addAttribute("busqueda", busqueda);
        return "cliente/lista";
    }

    private boolean hasBusqueda(ClienteBusquedaDTO busqueda) {
        return (busqueda.getNombre() != null && !busqueda.getNombre().isBlank())
                || (busqueda.getDni() != null && !busqueda.getDni().isBlank())
                || (busqueda.getNumeroCuenta() != null && !busqueda.getNumeroCuenta().isBlank())
                || (busqueda.getNumeroOperacion() != null && !busqueda.getNumeroOperacion().isBlank())
                || busqueda.getEmpresaId() != null
                || busqueda.getAgenciaId() != null;
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        try {
            ClienteDTO cliente = clienteService.obtenerPorId(id);
            model.addAttribute("cliente", cliente);
            return "cliente/detalle";
        } catch (ClienteException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/clientes";
        }
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String nuevoForm(Model model) {
        List<EmpresaDTO> empresas = empresaService.listarActivas();
        model.addAttribute("clienteForm", new ClienteFormDTO());
        model.addAttribute("empresas", empresas);
        return "cliente/formulario";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        try {
            ClienteDTO dto = clienteService.obtenerPorId(id);
            List<EmpresaDTO> empresas = empresaService.listarActivas();
            ClienteFormDTO form = toForm(dto);
            model.addAttribute("clienteForm", form);
            model.addAttribute("empresas", empresas);
            model.addAttribute("clienteId", id);
            return "cliente/formulario";
        } catch (ClienteException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/clientes";
        }
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardar(@Valid @ModelAttribute("clienteForm") ClienteFormDTO form,
                          BindingResult result,
                          @RequestParam(required = false) Long clienteId,
                          RedirectAttributes redirectAttrs,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("empresas", empresaService.listarActivas());
            model.addAttribute("clienteId", clienteId);
            return "cliente/formulario";
        }

        try {
            if (clienteId != null) {
                clienteService.actualizar(clienteId, form);
                redirectAttrs.addFlashAttribute("success", "Cliente actualizado correctamente");
            } else {
                clienteService.crear(form);
                redirectAttrs.addFlashAttribute("success", "Cliente creado correctamente");
            }
            return "redirect:/clientes";
        } catch (ClienteException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("empresas", empresaService.listarActivas());
            model.addAttribute("clienteId", clienteId);
            return "cliente/formulario";
        }
    }

    @PostMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            clienteService.eliminar(id);
            redirectAttrs.addFlashAttribute("success", "Cliente eliminado correctamente");
        } catch (ClienteException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clientes";
    }

    private ClienteFormDTO toForm(ClienteDTO dto) {
        return ClienteFormDTO.builder()
                .nombreCompleto(dto.getNombreCompleto())
                .dni(dto.getDni())
                .numeroCuenta(dto.getNumeroCuenta())
                .numeroOperacion(dto.getNumeroOperacion())
                .deudaCapital(dto.getDeudaCapital())
                .deudaTotal(dto.getDeudaTotal())
                .telefono(dto.getTelefono())
                .telefono2(dto.getTelefono2())
                .telefono3(dto.getTelefono3())
                .direccion(dto.getDireccion())
                .estadoGestion(dto.getEstadoGestion())
                .observaciones(dto.getObservaciones())
                .empresaId(dto.getEmpresaId())
                .agenciaId(dto.getAgenciaId())
                .build();
    }
}
