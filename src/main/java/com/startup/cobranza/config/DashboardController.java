package com.startup.cobranza.config;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        if (auth != null) {
            model.addAttribute("nombreUsuario", auth.getName());
            model.addAttribute("roles", auth.getAuthorities());
        }
        return "dashboard/index";
    }
}
