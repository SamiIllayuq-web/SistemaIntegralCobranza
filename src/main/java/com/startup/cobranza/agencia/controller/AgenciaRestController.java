package com.startup.cobranza.agencia.controller;

import com.startup.cobranza.agencia.entity.Agencia;
import com.startup.cobranza.agencia.repository.AgenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/agencias")
@RequiredArgsConstructor
public class AgenciaRestController {

    private final AgenciaRepository agenciaRepository;

    @GetMapping
    public List<Agencia> activas() {
        return agenciaRepository.findByActivoTrue();
    }
}
