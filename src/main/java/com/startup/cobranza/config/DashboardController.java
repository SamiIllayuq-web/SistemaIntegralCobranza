package com.startup.cobranza.config;

import com.startup.cobranza.agencia.repository.AgenciaRepository;
import com.startup.cobranza.cliente.repository.ClienteRepository;
import com.startup.cobranza.operacion.repository.OperacionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class DashboardController {

    private final OperacionRepository operacionRepo;
    private final ClienteRepository clienteRepo;
    private final AgenciaRepository agenciaRepo;

    public DashboardController(OperacionRepository operacionRepo,
                              ClienteRepository clienteRepo,
                              AgenciaRepository agenciaRepo) {
        this.operacionRepo = operacionRepo;
        this.clienteRepo = clienteRepo;
        this.agenciaRepo = agenciaRepo;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        model.addAttribute("totalOperaciones", operacionRepo.count());
        model.addAttribute("totalClientes", clienteRepo.count());
        model.addAttribute("totalAgencias", agenciaRepo.count());
        model.addAttribute("fechaHoy", LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        if (auth != null) {
            model.addAttribute("nombreUsuario", auth.getName());
            model.addAttribute("roles", auth.getAuthorities());
        }
        return "dashboard/index";
    }
}
