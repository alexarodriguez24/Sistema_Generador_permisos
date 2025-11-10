package com.itca.generadorpermiso.controllers;

import com.itca.generadorpermiso.entities.Usuario;
import com.itca.generadorpermiso.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam("username") String identificador,
                                @RequestParam String password,
                                HttpSession session) {
        // Usar nuevo método que decide si es carnet (estudiante) o código empleado (docente)
        Usuario usuario = authService.autenticarPorIdentificador(identificador, password);
        if (usuario != null) {
            session.setAttribute("usuario", usuario);
            switch (usuario.getRol()) {
                case ADMIN:
                    return "redirect:/admin/dashboard";
                case COLABORADOR:
                    return "redirect:/colaborador/dashboard";
                case COPIAS:
                    return "redirect:/copias/dashboard";
                case ESTUDIANTE:
                    return "redirect:/permisos/dashboard";
                default:
                    return "redirect:/login?error";
            }
        }
        return "redirect:/login?error";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }
}