package com.example.demo.controller;

import com.example.demo.model.Estudiante;
import com.example.demo.service.AuthService;
import com.example.demo.service.EstudianteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequestMapping("/estudiante")
public class EstudianteController {

    @Autowired private AuthService authService;
    @Autowired private EstudianteService estudianteService;

    private boolean esEstudiante(HttpSession session) {
        return authService.isLoggedIn(session) && "ESTUDIANTE".equals(authService.getRol(session));
    }

    private Optional<Estudiante> getMiEstudiante(HttpSession session) {
        Long id = authService.getEstudianteId(session);
        return id != null ? estudianteService.findById(id) : Optional.empty();
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!esEstudiante(session)) return "redirect:/login";
        getMiEstudiante(session).ifPresent(e -> {
            model.addAttribute("estudiante", e);
            model.addAttribute("beneficio", e.calcularBeneficio());
        });
        return "estudiante/dashboard";
    }

    @GetMapping("/datos-personales")
    public String datosPersonales(HttpSession session, Model model) {
        if (!esEstudiante(session)) return "redirect:/login";
        getMiEstudiante(session).ifPresent(e -> model.addAttribute("estudiante", e));
        return "estudiante/datos_personales";
    }

    @GetMapping("/ultimo-resultado")
    public String ultimoResultado(HttpSession session, Model model) {
        if (!esEstudiante(session)) return "redirect:/login";
        getMiEstudiante(session).ifPresent(e -> {
            model.addAttribute("estudiante", e);
            model.addAttribute("beneficio", e.calcularBeneficio());
        });
        return "estudiante/ultimo_resultado";
    }

    @GetMapping("/todos-resultados")
    public String todosResultados(HttpSession session, Model model) {
        if (!esEstudiante(session)) return "redirect:/login";
        getMiEstudiante(session).ifPresent(e -> model.addAttribute("estudiante", e));
        return "estudiante/todos_resultados";
    }

    @GetMapping("/beneficios")
    public String beneficios(HttpSession session, Model model) {
        if (!esEstudiante(session)) return "redirect:/login";
        getMiEstudiante(session).ifPresent(e -> {
            model.addAttribute("estudiante", e);
            model.addAttribute("beneficio", e.calcularBeneficio());
        });
        return "estudiante/beneficios";
    }

    @GetMapping("/cargar-pago")
    public String cargarPagoForm(HttpSession session, Model model) {
        if (!esEstudiante(session)) return "redirect:/login";
        getMiEstudiante(session).ifPresent(e -> model.addAttribute("estudiante", e));
        return "estudiante/cargar_pago";
    }

    @PostMapping("/cargar-pago")
    public String cargarPago(@RequestParam String comprobante,
                             @RequestParam(name = "documento", required = false)
                                 org.springframework.web.multipart.MultipartFile documento,
                             HttpSession session,
                             RedirectAttributes ra) {
        if (!esEstudiante(session)) return "redirect:/login";

        if (comprobante == null || comprobante.isBlank()) {
            ra.addFlashAttribute("error", "Debes ingresar el número de referencia del comprobante.");
            return "redirect:/estudiante/cargar-pago";
        }

        Long estudianteId = authService.getEstudianteId(session);
        if (estudianteId == null) {
            ra.addFlashAttribute("error", "No se encontró tu cuenta de estudiante.");
            return "redirect:/estudiante/cargar-pago";
        }

        estudianteService.cargarPago(estudianteId, comprobante.trim(), documento);
        ra.addFlashAttribute("mensaje",
            "Comprobante enviado al coordinador para revisión. Referencia: " + comprobante.trim());
        return "redirect:/estudiante/dashboard";
    }
}
