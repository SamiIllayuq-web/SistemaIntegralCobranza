package com.startup.cobranza.gestion.controller;

import com.startup.cobranza.gestion.dto.GestionDTO;
import com.startup.cobranza.gestion.dto.GestionFormDTO;
import com.startup.cobranza.gestion.exception.GestionException;
import com.startup.cobranza.gestion.service.GestionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes/{clienteId}/gestiones")
@RequiredArgsConstructor
public class GestionController {

    private final GestionService gestionService;

    @PostMapping("/guardar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO')")
    public ResponseEntity<?> guardar(@PathVariable Long clienteId,
                                     @Valid @RequestBody GestionFormDTO form,
                                     HttpServletRequest request) {
        try {
            String usuario = request.getUserPrincipal().getName();
            GestionDTO gestion = gestionService.registrar(clienteId, form, usuario);
            return ResponseEntity.ok(gestion);
        } catch (GestionException e) {
            return ResponseEntity.badRequest().body(new ErrorResp(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<GestionDTO>> listar(@PathVariable Long clienteId) {
        List<GestionDTO> gestiones = gestionService.listarPorCliente(clienteId);
        return ResponseEntity.ok(gestiones);
    }

    public record ErrorResp(String error) {}
}
