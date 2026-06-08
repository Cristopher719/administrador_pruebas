package com.example.demo.controller;

import com.example.demo.service.AuthService;
import com.example.demo.service.EstudianteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/docente")
public class DocenteController {

    @Autowired private AuthService authService;
    @Autowired private EstudianteService estudianteService;

    private boolean esDocente(HttpSession session) {
        String rol = authService.getRol(session);
        return authService.isLoggedIn(session) &&
               ("DOCENTE".equals(rol) || "COORDINADOR".equals(rol) || "ADMINISTRADOR".equals(rol));
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!esDocente(session)) return "redirect:/login";
        model.addAttribute("totalEstudiantes", estudianteService.total());
        model.addAttribute("totalSaberPro", estudianteService.totalSaberPro());
        model.addAttribute("totalSaberTyt", estudianteService.totalSaberTyt());
        model.addAttribute("promedioSaberPro", estudianteService.promedioSaberPro());
        model.addAttribute("promedioSaberTyt", estudianteService.promedioSaberTyt());
        model.addAttribute("facultades", estudianteService.findFacultades());
        // Beneficios Saber Pro
        model.addAttribute("proBeca100", estudianteService.countProBeca100());
        model.addAttribute("proBeca50",  estudianteService.countProBeca50());
        model.addAttribute("proExime",   estudianteService.countProExime());
        model.addAttribute("proNoApto",  estudianteService.countProNoApto());
        return "docente/dashboard";
    }

    // Buscar por Facultad
    @GetMapping("/por-facultad")
    public String porFacultad(HttpSession session, Model model,
                              @RequestParam(required = false) String facultad) {
        if (!esDocente(session)) return "redirect:/login";
        model.addAttribute("facultades", estudianteService.findFacultades());
        if (facultad != null && !facultad.isBlank()) {
            model.addAttribute("estudiantes", estudianteService.findByFacultad(facultad));
            model.addAttribute("facultadSeleccionada", facultad);
        }
        return "docente/por_facultad";
    }

    // Buscar por Cédula
    @GetMapping("/por-cedula")
    public String porCedula(HttpSession session, Model model,
                            @RequestParam(required = false) String cedula) {
        if (!esDocente(session)) return "redirect:/login";
        if (cedula != null && !cedula.isBlank()) {
            // Buscar por cédula o por número de registro
            var porDoc = estudianteService.findByNumeroDocumento(cedula);
            var porReg = estudianteService.findByNumeroRegistro(cedula);
            porDoc.or(() -> porReg).ifPresent(e -> model.addAttribute("estudiante", e));
            model.addAttribute("cedula", cedula);
        }
        return "docente/por_cedula";
    }

    // Informe de Alumnos Total y Único (todos los estudiantes)
    @GetMapping("/informe-alumnos")
    public String informeAlumnos(HttpSession session, Model model,
                                 @RequestParam(defaultValue = "TODOS") String tipo) {
        if (!esDocente(session)) return "redirect:/login";
        model.addAttribute("tipoFiltro", tipo);
        model.addAttribute("totalEstudiantes", estudianteService.total());
        model.addAttribute("promedioSaberPro", estudianteService.promedioSaberPro());
        model.addAttribute("promedioSaberTyt", estudianteService.promedioSaberTyt());

        if ("SABER_PRO".equals(tipo)) {
            model.addAttribute("estudiantes", estudianteService.findSaberPro());
        } else if ("SABER_TYT".equals(tipo)) {
            model.addAttribute("estudiantes", estudianteService.findSaberTyt());
        } else {
            model.addAttribute("estudiantes", estudianteService.findAllOrdenados());
        }
        return "docente/informe_alumnos";
    }

    // Informe de Beneficios
    @GetMapping("/informe-beneficios")
    public String informeBeneficios(HttpSession session, Model model) {
        if (!esDocente(session)) return "redirect:/login";
        model.addAttribute("saberPro", estudianteService.findSaberPro());
        model.addAttribute("saberTyt", estudianteService.findSaberTyt());
        model.addAttribute("proBeca100", estudianteService.countProBeca100());
        model.addAttribute("proBeca50",  estudianteService.countProBeca50());
        model.addAttribute("proExime",   estudianteService.countProExime());
        model.addAttribute("proMinimo",  estudianteService.countProMinimo());
        model.addAttribute("proNoApto",  estudianteService.countProNoApto());
        model.addAttribute("tytBeca100", estudianteService.countTytBeca100());
        model.addAttribute("tytBeca50",  estudianteService.countTytBeca50());
        model.addAttribute("tytExime",   estudianteService.countTytExime());
        model.addAttribute("tytMinimo",  estudianteService.countTytMinimo());
        model.addAttribute("tytNoApto",  estudianteService.countTytNoApto());
        return "docente/informe_beneficios";
    }

    // Resolución Beneficios Tecnología e Ingeniería
    @GetMapping("/resolucion-beneficios")
    public String resolucionBeneficios(HttpSession session, Model model) {
        if (!esDocente(session)) return "redirect:/login";
        model.addAttribute("saberPro", estudianteService.findSaberPro());
        model.addAttribute("aptos", estudianteService.findAptos());
        model.addAttribute("proBeca100", estudianteService.countProBeca100());
        model.addAttribute("proBeca50",  estudianteService.countProBeca50());
        model.addAttribute("proExime",   estudianteService.countProExime());
        model.addAttribute("proNoApto",  estudianteService.countProNoApto());
        return "docente/resolucion_beneficios";
    }
}
