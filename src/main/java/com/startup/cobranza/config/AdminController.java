package com.startup.cobranza.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @DeleteMapping("/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resetAllData() {
        AdminService.ResetResult result = adminService.resetAllData();
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "mensaje", "Datos eliminados",
                "operacionesEliminadas", result.operacionesEliminadas(),
                "bienesEliminados", result.bienesEliminados(),
                "clientesEliminados", result.clientesEliminados(),
                "importacionesEliminadas", result.importacionesEliminadas(),
                "agenciasEliminadas", result.agenciasEliminadas()
        ));
    }
}
