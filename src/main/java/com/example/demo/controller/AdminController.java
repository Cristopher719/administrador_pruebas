package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.AuthService;
import com.example.demo.service.EstudianteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private AuthService authService;
    @Autowired private EstudianteService estudianteService;
    @Autowired private FacultadRepository facultadRepo;
    @Autowired private DirectorRepository directorRepo;
    @Autowired private DocenteEntityRepository docenteRepo;
    @Autowired private UsuarioRepository usuarioRepo;

    private boolean esAdmin(HttpSession session) {
        return authService.isLoggedIn(session) && "ADMINISTRADOR".equals(authService.getRol(session));
    }

    // ── Dashboard ──────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        model.addAttribute("totalEstudiantes", estudianteService.total());
        model.addAttribute("nivel4",  estudianteService.countNivel4());
        model.addAttribute("nivel3",  estudianteService.countNivel3());
        model.addAttribute("nivel2",  estudianteService.countNivel2());
        model.addAttribute("nivel1",  estudianteService.countNivel1());
        model.addAttribute("promedio", estudianteService.promedio());
        model.addAttribute("topEstudiantes", estudianteService.findNivel4());
        model.addAttribute("proBeca100", estudianteService.countProBeca100());
        model.addAttribute("proBeca50",  estudianteService.countProBeca50());
        model.addAttribute("proExime",   estudianteService.countProExime());
        model.addAttribute("proNoApto",  estudianteService.countProNoApto());
        model.addAttribute("totalFacultades", facultadRepo.count());
        model.addAttribute("totalDocentes",   docenteRepo.count());
        model.addAttribute("totalDirectores", directorRepo.count());
        return "admin/dashboard";
    }

    // ── Estudiantes (solo lectura) ─────────────────────────────────
    @GetMapping("/estudiantes")
    public String listarEstudiantes(HttpSession session, Model model,
                                    @RequestParam(required = false) String buscar) {
        if (!esAdmin(session)) return "redirect:/login";
        if (buscar != null && !buscar.isBlank()) {
            model.addAttribute("estudiantes", estudianteService.buscar(buscar));
            model.addAttribute("buscar", buscar);
        } else {
            model.addAttribute("estudiantes", estudianteService.findAll());
        }
        return "admin/estudiantes";
    }

    // ── Informes ───────────────────────────────────────────────────
    @GetMapping("/informes")
    public String informes(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        model.addAttribute("estudiantes", estudianteService.findAllOrdenados());
        model.addAttribute("total",    estudianteService.total());
        model.addAttribute("promedio", estudianteService.promedio());
        model.addAttribute("nivel4",   estudianteService.countNivel4());
        model.addAttribute("nivel3",   estudianteService.countNivel3());
        model.addAttribute("nivel2",   estudianteService.countNivel2());
        model.addAttribute("nivel1",   estudianteService.countNivel1());
        return "admin/informes";
    }

    // ── Beneficios ─────────────────────────────────────────────────
    @GetMapping("/beneficios")
    public String beneficios(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        model.addAttribute("saberPro",   estudianteService.findSaberPro());
        model.addAttribute("saberTyt",   estudianteService.findSaberTyt());
        model.addAttribute("todos",      estudianteService.findAllOrdenados());
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
        return "admin/beneficios";
    }

    // ── CRUD Facultades ────────────────────────────────────────────
    @GetMapping("/facultades")
    public String facultades(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        model.addAttribute("facultades", facultadRepo.findAll());
        model.addAttribute("facultad", new Facultad());
        return "admin/facultades";
    }

    @PostMapping("/facultades/nueva")
    public String nuevaFacultad(@ModelAttribute Facultad facultad, HttpSession session,
                                RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";
        if (facultadRepo.existsByNombreIgnoreCase(facultad.getNombre())) {
            ra.addFlashAttribute("error", "Ya existe una facultad con ese nombre.");
            return "redirect:/admin/facultades";
        }
        facultadRepo.save(facultad);
        ra.addFlashAttribute("mensaje", "Facultad creada correctamente.");
        return "redirect:/admin/facultades";
    }

    @GetMapping("/facultades/editar/{id}")
    public String editarFacultadForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        return facultadRepo.findById(id).map(f -> {
            model.addAttribute("facultad", f);
            model.addAttribute("facultades", facultadRepo.findAll());
            model.addAttribute("editando", true);
            return "admin/facultades";
        }).orElse("redirect:/admin/facultades");
    }

    @PostMapping("/facultades/editar/{id}")
    public String editarFacultad(@PathVariable Long id, @ModelAttribute Facultad facultad,
                                 HttpSession session, RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";
        facultad.setId(id);
        facultadRepo.save(facultad);
        ra.addFlashAttribute("mensaje", "Facultad actualizada.");
        return "redirect:/admin/facultades";
    }

    @GetMapping("/facultades/eliminar/{id}")
    public String eliminarFacultad(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";
        facultadRepo.deleteById(id);
        ra.addFlashAttribute("mensaje", "Facultad eliminada.");
        return "redirect:/admin/facultades";
    }

    // ── CRUD Directores ────────────────────────────────────────────
    @GetMapping("/directores")
    public String directores(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        model.addAttribute("directores", directorRepo.findAll());
        model.addAttribute("director", new Director());
        model.addAttribute("facultades", facultadRepo.findAll());
        return "admin/directores";
    }

    @PostMapping("/directores/nuevo")
    public String nuevoDirector(@ModelAttribute Director director,
                                @RequestParam(required = false) Long facultadId,
                                HttpSession session, RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";
        if (directorRepo.findByNumeroDocumento(director.getNumeroDocumento()).isPresent()) {
            ra.addFlashAttribute("error", "Ya existe un director con ese documento.");
            return "redirect:/admin/directores";
        }
        if (facultadId != null) {
            facultadRepo.findById(facultadId).ifPresent(director::setFacultad);
        }
        directorRepo.save(director);
        ra.addFlashAttribute("mensaje", "Director creado correctamente.");
        return "redirect:/admin/directores";
    }

    @GetMapping("/directores/editar/{id}")
    public String editarDirectorForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        return directorRepo.findById(id).map(d -> {
            model.addAttribute("director", d);
            model.addAttribute("directores", directorRepo.findAll());
            model.addAttribute("facultades", facultadRepo.findAll());
            model.addAttribute("editando", true);
            return "admin/directores";
        }).orElse("redirect:/admin/directores");
    }

    @PostMapping("/directores/editar/{id}")
    public String editarDirector(@PathVariable Long id, @ModelAttribute Director director,
                                 @RequestParam(required = false) Long facultadId,
                                 HttpSession session, RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";
        director.setId(id);
        if (facultadId != null) {
            facultadRepo.findById(facultadId).ifPresent(director::setFacultad);
        }
        directorRepo.save(director);
        ra.addFlashAttribute("mensaje", "Director actualizado.");
        return "redirect:/admin/directores";
    }

    @GetMapping("/directores/eliminar/{id}")
    public String eliminarDirector(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";
        directorRepo.deleteById(id);
        ra.addFlashAttribute("mensaje", "Director eliminado.");
        return "redirect:/admin/directores";
    }

    // ── CRUD Docentes ──────────────────────────────────────────────
    @GetMapping("/docentes")
    public String docentes(HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        model.addAttribute("docentes", docenteRepo.findAll());
        model.addAttribute("docente", new DocenteEntity());
        model.addAttribute("facultades", facultadRepo.findAll());
        return "admin/docentes";
    }

    @PostMapping("/docentes/nuevo")
    public String nuevoDocente(@ModelAttribute DocenteEntity docente,
                               @RequestParam(required = false) Long facultadId,
                               @RequestParam String username,
                               @RequestParam String password,
                               HttpSession session, RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";
        if (docenteRepo.findByNumeroDocumento(docente.getNumeroDocumento()).isPresent()) {
            ra.addFlashAttribute("error", "Ya existe un docente con ese documento.");
            return "redirect:/admin/docentes";
        }
        if (usuarioRepo.existsByUsername(username)) {
            ra.addFlashAttribute("error", "El nombre de usuario '" + username + "' ya está en uso.");
            return "redirect:/admin/docentes";
        }
        if (facultadId != null) {
            facultadRepo.findById(facultadId).ifPresent(docente::setFacultad);
        }
        Usuario u = usuarioRepo.save(new Usuario(username, password, Usuario.Rol.DOCENTE));
        docente.setUsuario(u);
        docenteRepo.save(docente);
        ra.addFlashAttribute("mensaje", "Docente creado. Usuario: " + username);
        return "redirect:/admin/docentes";
    }

    @GetMapping("/docentes/editar/{id}")
    public String editarDocenteForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!esAdmin(session)) return "redirect:/login";
        return docenteRepo.findById(id).map(d -> {
            model.addAttribute("docente", d);
            model.addAttribute("docentes", docenteRepo.findAll());
            model.addAttribute("facultades", facultadRepo.findAll());
            model.addAttribute("editando", true);
            return "admin/docentes";
        }).orElse("redirect:/admin/docentes");
    }

    @PostMapping("/docentes/editar/{id}")
    public String editarDocente(@PathVariable Long id, @ModelAttribute DocenteEntity docente,
                                @RequestParam(required = false) Long facultadId,
                                HttpSession session, RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";
        docente.setId(id);
        if (facultadId != null) {
            facultadRepo.findById(facultadId).ifPresent(docente::setFacultad);
        }
        // Preservar usuario existente
        docenteRepo.findById(id).ifPresent(existing -> docente.setUsuario(existing.getUsuario()));
        docenteRepo.save(docente);
        ra.addFlashAttribute("mensaje", "Docente actualizado.");
        return "redirect:/admin/docentes";
    }

    @GetMapping("/docentes/eliminar/{id}")
    public String eliminarDocente(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!esAdmin(session)) return "redirect:/login";
        docenteRepo.findById(id).ifPresent(d -> {
            docenteRepo.deleteById(id);
            if (d.getUsuario() != null) usuarioRepo.deleteById(d.getUsuario().getId());
        });
        ra.addFlashAttribute("mensaje", "Docente eliminado.");
        return "redirect:/admin/docentes";
    }
}
