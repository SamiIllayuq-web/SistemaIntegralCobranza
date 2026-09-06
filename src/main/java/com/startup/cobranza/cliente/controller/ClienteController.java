package com.startup.cobranza.cliente.controller;

import com.startup.cobranza.cliente.dto.ClienteBandejaDTO;
import com.startup.cobranza.cliente.dto.ClienteBusquedaDTO;
import com.startup.cobranza.cliente.dto.ClienteDTO;
import com.startup.cobranza.cliente.dto.ClienteFormDTO;
import com.startup.cobranza.cliente.exception.ClienteException;
import com.startup.cobranza.cliente.service.ClienteService;
import com.startup.cobranza.gestion.dto.GestionDTO;
import com.startup.cobranza.gestion.service.GestionService;
import com.startup.cobranza.operacion.dto.OperacionDTO;
import com.startup.cobranza.operacion.service.OperacionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private static final int PAGE_SIZE = 50;

    private final ClienteService clienteService;
    private final GestionService gestionService;
    private final OperacionService operacionService;

    public ClienteController(ClienteService clienteService,
                              GestionService gestionService,
                              OperacionService operacionService) {
        this.clienteService = clienteService;
        this.gestionService = gestionService;
        this.operacionService = operacionService;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "50") int size,
                          @RequestParam(required = false) String nombre,
                          @RequestParam(required = false) String dni,
                          @RequestParam(required = false) String estado,
                          @RequestParam(required = false) String estadoCartera,
                          @RequestParam(required = false) String etapa,
                          @RequestParam(required = false) Integer minMora,
                          @RequestParam(required = false) Integer maxMora,
                          @RequestParam(required = false) BigDecimal minMonto,
                          @RequestParam(required = false) BigDecimal maxMonto,
                          Model model) {

        size = Math.min(size, 200); // cap para evitar queries enormes

        ClienteBusquedaDTO filtros = ClienteBusquedaDTO.builder()
                .nombre(nombre)
                .dni(dni)
                .estado(estado)
                .estadoCartera(estadoCartera)
                .etapa(etapa)
                .minMora(minMora)
                .maxMora(maxMora)
                .minMonto(minMonto)
                .maxMonto(maxMonto)
                .build();

        Pageable pageable = PageRequest.of(page, size, Sort.by("cliente.nombreCompleto").ascending());
        Page<ClienteBandejaDTO> pagina = clienteService.listarBandeja(filtros, pageable);

        model.addAttribute("pagina", pagina);
        model.addAttribute("filtros", filtros);
        return "cliente/lista";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        try {
            ClienteDTO cliente = clienteService.obtenerPorId(id);
            List<OperacionDTO> operaciones = operacionService.listarPorCliente(id);
            List<GestionDTO> gestiones = gestionService.listarPorCliente(id);
            model.addAttribute("cliente", cliente);
            model.addAttribute("operaciones", operaciones);
            model.addAttribute("gestiones", gestiones);
            return "cliente/detalle";
        } catch (ClienteException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/clientes";
        }
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String nuevoForm(Model model) {
        model.addAttribute("clienteForm", new ClienteFormDTO());
        return "cliente/formulario";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        try {
            ClienteDTO dto = clienteService.obtenerPorId(id);
            ClienteFormDTO form = toForm(dto);
            model.addAttribute("clienteForm", form);
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
            model.addAttribute("clienteId", clienteId);
            return "cliente/formulario";
        }

        try {
            String usuario = getCurrentUsername();
            if (clienteId != null) {
                clienteService.actualizar(clienteId, form, usuario);
                redirectAttrs.addFlashAttribute("success", "Cliente actualizado correctamente");
            } else {
                clienteService.crear(form);
                redirectAttrs.addFlashAttribute("success", "Cliente creado correctamente");
            }
            return "redirect:/clientes";
        } catch (ClienteException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("clienteId", clienteId);
            return "cliente/formulario";
        }
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "sistema";
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
                .telefono(dto.getTelefono())
                .telefono2(dto.getTelefono2())
                .telefono3(dto.getTelefono3())
                .direccion(dto.getDireccion())
                .email(dto.getEmail())
                .build();
    }
}
