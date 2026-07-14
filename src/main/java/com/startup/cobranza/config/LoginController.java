package com.startup.cobranza.config;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Model model, Boolean error, Boolean logout) {
        if (error != null && error) {
            model.addAttribute("error", "Credenciales incorrectas");
        }
        if (logout != null && logout) {
            model.addAttribute("success", "Sesión cerrada correctamente");
        }
        return "login/index";
    }
}
