package com.startup.cobranza.agencia.controller;

import com.startup.cobranza.agencia.dto.AgenciaDTO;
import com.startup.cobranza.agencia.service.AgenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agencias")
@RequiredArgsConstructor
public class AgenciaApiController {

    private final AgenciaService agenciaService;

    @GetMapping("/activas")
    public List<AgenciaDTO> activas() {
        return agenciaService.listarActivas();
    }
}
