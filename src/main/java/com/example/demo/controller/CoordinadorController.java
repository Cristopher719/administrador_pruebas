package com.example.demo.controller;

import com.example.demo.model.Estudiante;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.EstudianteService;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/coordinador")
public class CoordinadorController {

    @Autowired private AuthService authService;
    @Autowired private EstudianteService estudianteService;
    @Autowired private UsuarioRepository usuarioRepo;

    private boolean esCoordinador(HttpSession session) {
        String rol = authService.getRol(session);
        return authService.isLoggedIn(session) &&
               ("COORDINADOR".equals(rol) || "ADMINISTRADOR".equals(rol));
    }

    // ── Dashboard ──────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!esCoordinador(session)) return "redirect:/login";
        model.addAttribute("totalEstudiantes", estudianteService.total());
        model.addAttribute("nivel4",   estudianteService.countNivel4());
        model.addAttribute("nivel3",   estudianteService.countNivel3());
        model.addAttribute("nivel2",   estudianteService.countNivel2());
        model.addAttribute("nivel1",   estudianteService.countNivel1());
        model.addAttribute("promedio", estudianteService.promedio());
        model.addAttribute("pendientes",     estudianteService.findByEstado("PENDIENTE_PAGO").size());
        model.addAttribute("pagoRevision",   estudianteService.findByEstado("PAGO_EN_REVISION").size());
        model.addAttribute("inscritos",      estudianteService.findByEstado("INSCRITO").size());
        model.addAttribute("resFinalizados", estudianteService.findByEstado("FINALIZADO").size());
        return "coordinador/dashboard";
    }

    // ── CRUD Estudiantes ───────────────────────────────────────────
    @GetMapping("/estudiantes")
    public String listarEstudiantes(HttpSession session, Model model,
                                    @RequestParam(required = false) String buscar,
                                    @RequestParam(required = false) String estado) {
        if (!esCoordinador(session)) return "redirect:/login";
        if (buscar != null && !buscar.isBlank()) {
            model.addAttribute("estudiantes", estudianteService.buscar(buscar));
            model.addAttribute("buscar", buscar);
        } else if (estado != null && !estado.isBlank()) {
            model.addAttribute("estudiantes", estudianteService.findByEstado(estado));
            model.addAttribute("estadoFiltro", estado);
        } else {
            model.addAttribute("estudiantes", estudianteService.findAll());
        }
        return "coordinador/estudiantes";
    }

    @GetMapping("/estudiantes/nuevo")
    public String nuevoForm(HttpSession session, Model model) {
        if (!esCoordinador(session)) return "redirect:/login";
        model.addAttribute("estudiante", new Estudiante());
        return "coordinador/form_estudiante";
    }

    @PostMapping("/estudiantes/nuevo")
    public String crear(@ModelAttribute Estudiante estudiante,
                        @RequestParam String username,
                        @RequestParam String password,
                        HttpSession session, RedirectAttributes ra) {
        if (!esCoordinador(session)) return "redirect:/login";

        // Validar registro duplicado
        if (estudianteService.findByNumeroRegistro(estudiante.getNumeroRegistro()).isPresent()) {
            ra.addFlashAttribute("error", "Ya existe un estudiante con el registro: " + estudiante.getNumeroRegistro());
            return "redirect:/coordinador/estudiantes/nuevo";
        }
        // Validar usuario duplicado
        if (usuarioRepo.existsByUsername(username)) {
            ra.addFlashAttribute("error", "El usuario '" + username + "' ya está en uso.");
            return "redirect:/coordinador/estudiantes/nuevo";
        }

        estudiante.setEstado("PENDIENTE_PAGO");
        estudianteService.save(estudiante);

        Usuario u = usuarioRepo.save(new Usuario(username, password, Usuario.Rol.ESTUDIANTE));
        estudiante.setUsuario(u);
        estudianteService.save(estudiante);

        ra.addFlashAttribute("mensaje", "Estudiante creado. Usuario: " + username + " | Clave: " + password);
        return "redirect:/coordinador/estudiantes";
    }

    @GetMapping("/estudiantes/editar/{id}")
    public String editarForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!esCoordinador(session)) return "redirect:/login";
        return estudianteService.findById(id).map(e -> {
            model.addAttribute("estudiante", e);
            return "coordinador/form_estudiante";
        }).orElse("redirect:/coordinador/estudiantes");
    }

    @PostMapping("/estudiantes/editar/{id}")
    public String editar(@PathVariable Long id, @ModelAttribute Estudiante form,
                         HttpSession session, RedirectAttributes ra) {
        if (!esCoordinador(session)) return "redirect:/login";

        estudianteService.findById(id).ifPresent(existing -> {
            // ── Solo actualizar campos del formulario de edición ──────
            existing.setTipoDocumento(form.getTipoDocumento());
            existing.setNumeroDocumento(form.getNumeroDocumento());
            existing.setPrimerApellido(form.getPrimerApellido());
            existing.setSegundoApellido(form.getSegundoApellido());
            existing.setPrimerNombre(form.getPrimerNombre());
            existing.setSegundoNombre(form.getSegundoNombre());
            existing.setCorreo(form.getCorreo());
            existing.setTelefono(form.getTelefono());
            existing.setFacultad(form.getFacultad());
            existing.setTipoPrueba(form.getTipoPrueba());
            if (form.getEstado() != null && !form.getEstado().isBlank()) {
                existing.setEstado(form.getEstado());
            }
            // ── Las notas, niveles, pago y usuario NO se tocan ────────
            estudianteService.save(existing);
        });

        ra.addFlashAttribute("mensaje", "Estudiante actualizado correctamente.");
        return "redirect:/coordinador/estudiantes";
    }

    @GetMapping("/estudiantes/anular/{id}")
    public String anular(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!esCoordinador(session)) return "redirect:/login";
        estudianteService.anular(id);
        ra.addFlashAttribute("mensaje", "Estudiante anulado.");
        return "redirect:/coordinador/estudiantes";
    }

    // ── Gestión de pagos ───────────────────────────────────────────
    @GetMapping("/pagos")
    public String pagos(HttpSession session, Model model) {
        if (!esCoordinador(session)) return "redirect:/login";
        model.addAttribute("enRevision", estudianteService.findByEstado("PAGO_EN_REVISION"));
        model.addAttribute("pendientes", estudianteService.findByEstado("PENDIENTE_PAGO"));
        model.addAttribute("inscritos",  estudianteService.findByEstado("INSCRITO"));
        return "coordinador/pagos";
    }

    @GetMapping("/pagos/aceptar/{id}")
    public String aceptarPago(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!esCoordinador(session)) return "redirect:/login";
        estudianteService.aceptarPago(id);
        ra.addFlashAttribute("mensaje", "Pago aceptado. Estudiante inscrito.");
        return "redirect:/coordinador/pagos";
    }

    @GetMapping("/pagos/rechazar/{id}")
    public String rechazarPago(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!esCoordinador(session)) return "redirect:/login";
        estudianteService.rechazarPago(id);
        ra.addFlashAttribute("mensajeError", "Pago rechazado.");
        return "redirect:/coordinador/pagos";
    }

    // ── Subir resultados individual ────────────────────────────────
    @GetMapping("/resultados/{id}")
    public String resultadosForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!esCoordinador(session)) return "redirect:/login";
        return estudianteService.findById(id).map(e -> {
            if (!"INSCRITO".equals(e.getEstado())) {
                return "redirect:/coordinador/pagos";
            }
            model.addAttribute("estudiante", e);
            return "coordinador/form_resultados";
        }).orElse("redirect:/coordinador/estudiantes");
    }

    @PostMapping("/resultados/{id}")
    public String subirResultados(@PathVariable Long id,
                                  @RequestParam Integer puntaje,
                                  @RequestParam(required = false) Integer comEsc,
                                  @RequestParam(required = false) Integer razCuant,
                                  @RequestParam(required = false) Integer lecCrit,
                                  @RequestParam(required = false) Integer compCiud,
                                  @RequestParam(required = false) Integer ingles,
                                  @RequestParam(required = false) Integer formProy,
                                  @RequestParam(required = false) Integer pensCient,
                                  @RequestParam(required = false) Integer disSoft,
                                  @RequestParam(required = false) String nivelIngles,
                                  HttpSession session, RedirectAttributes ra) {
        if (!esCoordinador(session)) return "redirect:/login";

        String error = estudianteService.subirResultados(id, puntaje, comEsc, razCuant, lecCrit,
                compCiud, ingles, formProy, pensCient, disSoft, nivelIngles);

        if (error != null) {
            ra.addFlashAttribute("error", "Error de validación: " + error);
            return "redirect:/coordinador/resultados/" + id;
        }

        ra.addFlashAttribute("mensaje", "Resultados guardados correctamente. Estado: FINALIZADO");
        return "redirect:/coordinador/estudiantes";
    }

    // ── Ver documento del comprobante ──────────────────────────────
    @GetMapping("/pagos/ver-documento/{id}")
    public void verDocumento(@PathVariable Long id, HttpSession session,
                             jakarta.servlet.http.HttpServletResponse response) throws Exception {
        if (!esCoordinador(session)) {
            response.sendRedirect("/login");
            return;
        }

        var opt = estudianteService.findById(id);
        if (opt.isEmpty() || opt.get().getDocumentoContenidoBase64() == null) {
            response.sendError(404, "Documento no encontrado");
            return;
        }

        Estudiante e = opt.get();
        byte[] bytes = java.util.Base64.getDecoder().decode(e.getDocumentoContenidoBase64());
        String tipo = e.getDocumentoTipo() != null ? e.getDocumentoTipo() : "application/octet-stream";
        String nombre = e.getDocumentoComprobante() != null ? e.getDocumentoComprobante() : "comprobante";

        response.setContentType(tipo);
        response.setHeader("Content-Disposition", "inline; filename=\"" + nombre + "\"");
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
    }

    // ── Carga masiva de notas por Excel ────────────────────────────
    @GetMapping("/cargar-excel")
    public String cargarExcelForm(HttpSession session, Model model) {
        if (!esCoordinador(session)) return "redirect:/login";
        return "coordinador/cargar_excel";
    }

    @PostMapping("/cargar-excel")
    public String procesarExcel(@RequestParam("archivo") MultipartFile archivo,
                                HttpSession session, RedirectAttributes ra) {
        if (!esCoordinador(session)) return "redirect:/login";

        if (archivo.isEmpty()) {
            ra.addFlashAttribute("error", "Debes seleccionar un archivo Excel.");
            return "redirect:/coordinador/cargar-excel";
        }

        List<String> procesados  = new ArrayList<>();
        List<String> noEncontrados = new ArrayList<>();
        List<String> errores     = new ArrayList<>();

        try (InputStream is = archivo.getInputStream();
             Workbook wb  = new XSSFWorkbook(is)) {

            Sheet sheet = wb.getSheetAt(0);

            // Fila 0 = encabezados, datos desde fila 1
            // Columnas esperadas:
            // 0=registro, 1=puntaje, 2=comEsc, 3=razCuant, 4=lecCrit,
            // 5=compCiud, 6=ingles, 7=formProy, 8=pensCient, 9=disSoft, 10=nivelIngles
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String registro = getCellString(row.getCell(0));
                if (registro == null || registro.isBlank()) continue;

                var opt = estudianteService.findByNumeroRegistro(registro.trim());
                if (opt.isEmpty()) {
                    noEncontrados.add(registro);
                    continue;
                }

                Estudiante e = opt.get();
                if (!"INSCRITO".equals(e.getEstado())) {
                    errores.add(registro + " (estado: " + e.getEstado() + " — debe ser INSCRITO)");
                    continue;
                }

                try {
                    Integer puntaje   = getCellInt(row.getCell(1));
                    Integer comEsc    = getCellInt(row.getCell(2));
                    Integer razCuant  = getCellInt(row.getCell(3));
                    Integer lecCrit   = getCellInt(row.getCell(4));
                    Integer compCiud  = getCellInt(row.getCell(5));
                    Integer ingles    = getCellInt(row.getCell(6));
                    Integer formProy  = getCellInt(row.getCell(7));
                    Integer pensCient = getCellInt(row.getCell(8));
                    Integer disSoft   = getCellInt(row.getCell(9));
                    String nivelIng   = getCellString(row.getCell(10));

                    if (puntaje == null) {
                        errores.add(registro + " (puntaje vacío)");
                        continue;
                    }

                    estudianteService.subirResultados(e.getId(), puntaje, comEsc, razCuant,
                            lecCrit, compCiud, ingles, formProy, pensCient, disSoft, nivelIng);
                    procesados.add(registro + " → " + puntaje + " pts");

                } catch (Exception ex) {
                    errores.add(registro + " (error al leer fila)");
                }
            }

        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Error al leer el archivo: " + ex.getMessage());
            return "redirect:/coordinador/cargar-excel";
        }

        ra.addFlashAttribute("procesados",    procesados);
        ra.addFlashAttribute("noEncontrados", noEncontrados);
        ra.addFlashAttribute("errores",       errores);
        return "redirect:/coordinador/cargar-excel";
    }

    // ── Informes ───────────────────────────────────────────────────
    @GetMapping("/informe-general")
    public String informeGeneral(HttpSession session, Model model) {
        if (!esCoordinador(session)) return "redirect:/login";
        model.addAttribute("estudiantes", estudianteService.findAllOrdenados());
        model.addAttribute("total",    estudianteService.total());
        model.addAttribute("promedio", estudianteService.promedio());
        model.addAttribute("nivel4",   estudianteService.countNivel4());
        model.addAttribute("nivel3",   estudianteService.countNivel3());
        model.addAttribute("nivel2",   estudianteService.countNivel2());
        model.addAttribute("nivel1",   estudianteService.countNivel1());
        return "coordinador/informe_general";
    }

    @GetMapping("/informe-detallado")
    public String informeDetallado(HttpSession session, Model model,
                                   @RequestParam(required = false) String cedula) {
        if (!esCoordinador(session)) return "redirect:/login";
        if (cedula != null && !cedula.isBlank()) {
            var porDoc = estudianteService.findByNumeroDocumento(cedula);
            var porReg = estudianteService.findByNumeroRegistro(cedula);
            porDoc.or(() -> porReg).ifPresent(e -> model.addAttribute("estudiante", e));
            model.addAttribute("cedula", cedula);
        }
        return "coordinador/informe_detallado";
    }

    @GetMapping("/beneficios")
    public String beneficios(HttpSession session, Model model) {
        if (!esCoordinador(session)) return "redirect:/login";
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
        return "coordinador/beneficios";
    }

    // ── Helpers lectura Excel ──────────────────────────────────────
    private String getCellString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default      -> null;
        };
    }

    private Integer getCellInt(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> (int) cell.getNumericCellValue();
            case STRING  -> {
                try { yield Integer.parseInt(cell.getStringCellValue().trim()); }
                catch (NumberFormatException e) { yield null; }
            }
            default -> null;
        };
    }
}
