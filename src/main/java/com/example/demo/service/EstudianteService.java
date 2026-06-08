package com.example.demo.service;

import com.example.demo.model.Estudiante;
import com.example.demo.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class EstudianteService {

    @Autowired
    private EstudianteRepository repo;

    // ── CRUD ───────────────────────────────────────────────────────
    public List<Estudiante> findAll() { return repo.findAll(); }
    public List<Estudiante> findAllOrdenados() { return repo.findAllOrderByPuntaje(); }
    public Optional<Estudiante> findById(Long id) { return repo.findById(id); }
    public Optional<Estudiante> findByNumeroRegistro(String r) { return repo.findByNumeroRegistro(r); }
    public Optional<Estudiante> findByNumeroDocumento(String d) { return repo.findByNumeroDocumento(d); }
    public List<Estudiante> findByFacultad(String f) { return repo.findByFacultadIgnoreCase(f); }
    public List<Estudiante> findByEstado(String e) { return repo.findByEstado(e); }
    public List<Estudiante> findSaberPro() { return repo.findSaberProOrderByPuntaje(); }
    public List<Estudiante> findSaberTyt() { return repo.findSaberTytOrderByPuntaje(); }
    public List<Estudiante> findAptos() { return repo.findAptos(); }
    public List<String> findFacultades() { return repo.findDistinctFacultades(); }
    public List<Estudiante> findNivel4() { return repo.findNivel4(); }

    public List<Estudiante> buscar(String q) {
        return repo.findByPrimerApellidoContainingIgnoreCaseOrPrimerNombreContainingIgnoreCase(q, q);
    }

    public Estudiante save(Estudiante e) {
        recalcularNiveles(e);
        if (e.getPuntaje() != null) {
            e.setAptoParaGraduacion(e.cumplePuntajeMinimo());
        } else {
            e.setAptoParaGraduacion(false);
        }
        return repo.save(e);
    }

    public void anular(Long id) {
        repo.findById(id).ifPresent(e -> {
            e.setEstado("ANULADO");
            repo.save(e);
        });
    }

    public void aprobar(Long id) {
        repo.findById(id).ifPresent(e -> {
            e.setEstado("ACTIVO");
            repo.save(e);
        });
    }

    // ── Carga de pago con archivo ──────────────────────────────────
    public void cargarPago(Long id, String comprobante, MultipartFile documento) {
        repo.findById(id).ifPresent(e -> {
            e.setPagoCargado(true);
            e.setComprobantePago(comprobante);
            e.setEstado("PAGO_EN_REVISION");

            if (documento != null && !documento.isEmpty()) {
                try {
                    byte[] bytes = documento.getBytes();
                    String base64 = Base64.getEncoder().encodeToString(bytes);
                    e.setDocumentoComprobante(documento.getOriginalFilename());
                    e.setDocumentoContenidoBase64(base64);
                    e.setDocumentoTipo(documento.getContentType());
                } catch (Exception ex) {
                    // Si falla la lectura del archivo igual guarda el comprobante
                    System.err.println("Error leyendo archivo: " + ex.getMessage());
                }
            }
            repo.save(e);
        });
    }

    public void aceptarPago(Long id) {
        repo.findById(id).ifPresent(e -> {
            e.setEstado("INSCRITO");
            repo.save(e);
        });
    }

    public void rechazarPago(Long id) {
        repo.findById(id).ifPresent(e -> {
            e.setPagoCargado(false);
            e.setComprobantePago(null);
            e.setDocumentoComprobante(null);
            e.setDocumentoContenidoBase64(null);
            e.setDocumentoTipo(null);
            e.setEstado("PENDIENTE_PAGO");
            repo.save(e);
        });
    }

    // ── Subir resultados con cálculo automático del promedio ──────
    public String subirResultados(Long id, Integer puntaje,
                                Integer comEsc, Integer razCuant, Integer lecCrit,
                                Integer compCiud, Integer ingles,
                                Integer formProy, Integer pensCient, Integer disSoft,
                                String nivelIngles) {
        Optional<Estudiante> opt = repo.findById(id);
        if (opt.isEmpty()) return "Estudiante no encontrado";

        Estudiante e = opt.get();

        // Calcular promedio en el servidor como respaldo al cálculo del front
        int[] vals = java.util.stream.IntStream.of(
            comEsc     != null ? comEsc     : -1,
            razCuant   != null ? razCuant   : -1,
            lecCrit    != null ? lecCrit    : -1,
            compCiud   != null ? compCiud   : -1,
            ingles     != null ? ingles     : -1,
            formProy   != null ? formProy   : -1,
            pensCient  != null ? pensCient  : -1,
            disSoft    != null ? disSoft    : -1
        ).filter(v -> v >= 0).toArray();

        if (vals.length == 0) return "Debes ingresar al menos una competencia.";

        int promedio = (int) Math.round(
            java.util.Arrays.stream(vals).average().orElse(0)
        );

        // Si el puntaje llegó del front, usarlo; si no, usar el calculado
        int puntajeFinal = (puntaje != null && puntaje > 0) ? puntaje : promedio;

        // Limitar al máximo según tipo de prueba
        int maxPuntaje = e.getPuntajeMaximo();
        puntajeFinal = Math.min(puntajeFinal, maxPuntaje);

        if (puntajeFinal < 0 || puntajeFinal > maxPuntaje) {
            return "El puntaje " + puntajeFinal + " está fuera del rango (0–" + maxPuntaje + ") para " + e.getTipoPrueba().name();
        }

        // Validar cada competencia
        String errorComp = validarCompetencia("Comunicación Escrita", comEsc) +
                           validarCompetencia("Razonamiento Cuantitativo", razCuant) +
                           validarCompetencia("Lectura Crítica", lecCrit) +
                           validarCompetencia("Competencias Ciudadanas", compCiud) +
                           validarCompetencia("Inglés", ingles) +
                           validarCompetencia("Formulación Proyectos", formProy) +
                           validarCompetencia("Pensamiento Científico", pensCient) +
                           validarCompetencia("Diseño Software", disSoft);
        if (!errorComp.isEmpty()) return errorComp;

        e.setPuntaje(puntajeFinal);
        e.setComunicacionEscrita(comEsc);
        e.setRazonamientoCuantitativo(razCuant);
        e.setLecturaCritica(lecCrit);
        e.setCompetenciasCiudadanas(compCiud);
        e.setIngles(ingles);
        e.setFormulacionProyectos(formProy);
        e.setPensamientoCientifico(pensCient);
        e.setDisenoSoftware(disSoft);
        e.setNivelIngles(nivelIngles);
        e.setEstado("FINALIZADO");
        save(e);
        return null; // null = sin errores
    }

    private String validarCompetencia(String nombre, Integer valor) {
        if (valor == null) return "";
        if (valor < 0 || valor > 300) {
            return nombre + ": valor " + valor + " fuera de rango (0-300). ";
        }
        return "";
    }

    // ── Estadísticas ───────────────────────────────────────────────
    public long total() { return repo.count(); }
    public long totalSaberPro() { return repo.findByTipoPrueba(Estudiante.TipoPrueba.SABER_PRO).size(); }
    public long totalSaberTyt() { return repo.findByTipoPrueba(Estudiante.TipoPrueba.SABER_TYT).size(); }
    public Double promedio() { return repo.findPromedioPuntaje(); }
    public Double promedioSaberPro() { return repo.findPromedioPuntajeSaberPro(); }
    public Double promedioSaberTyt() { return repo.findPromedioPuntajeSaberTyt(); }

    public Long countNivel4() { return repo.countNivel4(); }
    public Long countNivel3() { return repo.countNivel3(); }
    public Long countNivel2() { return repo.countNivel2(); }
    public Long countNivel1() { return repo.countNivel1(); }

    public Long countProBeca100() { return repo.countSaberProBeca100(); }
    public Long countProBeca50()  { return repo.countSaberProBeca50(); }
    public Long countProExime()   { return repo.countSaberProExime(); }
    public Long countProMinimo()  { return repo.countSaberProMinimo(); }
    public Long countProNoApto()  { return repo.countSaberProNoApto(); }

    public Long countTytBeca100() { return repo.countTytBeca100(); }
    public Long countTytBeca50()  { return repo.countTytBeca50(); }
    public Long countTytExime()   { return repo.countTytExime(); }
    public Long countTytMinimo()  { return repo.countTytMinimo(); }
    public Long countTytNoApto()  { return repo.countTytNoApto(); }

    private void recalcularNiveles(Estudiante e) {
        e.setPuntajeNivel(Estudiante.calcularNivel(e.getPuntaje()));
        e.setComunicacionEscritaNivel(Estudiante.calcularNivel(e.getComunicacionEscrita()));
        e.setRazonamientoCuantitativoNivel(Estudiante.calcularNivel(e.getRazonamientoCuantitativo()));
        e.setLecturaCriticaNivel(Estudiante.calcularNivel(e.getLecturaCritica()));
        e.setCompetenciasCiudadanasNivel(Estudiante.calcularNivel(e.getCompetenciasCiudadanas()));
        e.setInglesNivel(Estudiante.calcularNivel(e.getIngles()));
        e.setFormulacionProyectosNivel(Estudiante.calcularNivel(e.getFormulacionProyectos()));
        e.setPensamientoCientificoNivel(Estudiante.calcularNivel(e.getPensamientoCientifico()));
        e.setDisenoSoftwareNivel(Estudiante.calcularNivel(e.getDisenoSoftware()));
    }
}
