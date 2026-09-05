package com.startup.cobranza.config;

import com.startup.cobranza.cartera.repository.ImportacionRepository;
import com.startup.cobranza.cliente.repository.ClienteRepository;
import com.startup.cobranza.empresa.repository.EmpresaRepository;
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
    private final EmpresaRepository empresaRepo;
    private final ImportacionRepository importacionRepo;

    public DashboardController(OperacionRepository operacionRepo,
                               ClienteRepository clienteRepo,
                               EmpresaRepository empresaRepo,
                               ImportacionRepository importacionRepo) {
        this.operacionRepo = operacionRepo;
        this.clienteRepo = clienteRepo;
        this.empresaRepo = empresaRepo;
        this.importacionRepo = importacionRepo;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        model.addAttribute("totalOperaciones", operacionRepo.count());
        model.addAttribute("totalClientes", clienteRepo.count());
        model.addAttribute("totalEmpresas", empresaRepo.count());
        model.addAttribute("totalImportaciones", importacionRepo.count());
        model.addAttribute("fechaHoy", LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        if (auth != null) {
            model.addAttribute("nombreUsuario", auth.getName());
            model.addAttribute("roles", auth.getAuthorities());
        }
        return "dashboard/index";
    }
}
