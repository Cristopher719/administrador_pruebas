package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired private AuthService authService;

    @GetMapping("/")
    public String index(HttpSession session) {
        if (authService.isLoggedIn(session)) return redirigir(authService.getRol(session));
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(HttpSession session) {
        if (authService.isLoggedIn(session)) return redirigir(authService.getRol(session));
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        HttpSession session, Model model) {
        Optional<Usuario> usuario = authService.login(username, password);
        if (usuario.isPresent()) {
            authService.guardarSesion(session, usuario.get());
            return redirigir(usuario.get().getRol().name());
        }
        model.addAttribute("error", "Usuario o contraseña incorrectos");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout(session);
        return "redirect:/login";
    }

    private String redirigir(String rol) {
        return switch (rol) {
            case "ADMINISTRADOR" -> "redirect:/admin/dashboard";
            case "COORDINADOR"   -> "redirect:/coordinador/dashboard";
            case "DOCENTE"       -> "redirect:/docente/dashboard";
            case "ESTUDIANTE"    -> "redirect:/estudiante/dashboard";
            default              -> "redirect:/login";
        };
    }
}
