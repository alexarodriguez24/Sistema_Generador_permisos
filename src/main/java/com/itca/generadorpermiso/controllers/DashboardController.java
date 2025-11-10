package com.itca.generadorpermiso.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        // Este endpoint genérico redirige al login
        // Cada rol debe usar su ruta específica
        return "redirect:/login";
    }
}