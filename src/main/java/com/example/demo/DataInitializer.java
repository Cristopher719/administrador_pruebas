package com.example.demo;

import com.example.demo.model.Estudiante;
import com.example.demo.model.Estudiante.TipoPrueba;
import com.example.demo.model.Usuario;
import com.example.demo.model.Facultad;
import com.example.demo.repository.EstudianteRepository;
import com.example.demo.repository.FacultadRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private EstudianteRepository estudianteRepo;
    @Autowired private FacultadRepository facultadRepo;

    @Override
    public void run(String... args) {

        // ── USUARIOS ADMIN ────────────────────────────────────────────
        usuarioRepo.save(new Usuario("admin",       "admin123",  Usuario.Rol.ADMINISTRADOR));
        usuarioRepo.save(new Usuario("coordinador", "coord123",  Usuario.Rol.COORDINADOR));
        usuarioRepo.save(new Usuario("docente",     "doc123",    Usuario.Rol.DOCENTE));

        // ── FACULTADES ─────────────────────────────────────────────
        Facultad facTI = facultadRepo.save(new Facultad("Tecnología e Ingeniería"));
        facultadRepo.save(new Facultad("Administración y Negocios"));
        facultadRepo.save(new Facultad("Ciencias Básicas"));

        // ── DATOS REALES DEL XLSX — SABER PRO ─────────────────────────
        // Columnas: apellido, registro, puntaje, comEsc, razCuant, lecCrit,
        //           compCiud, ingles, formProy, pensCient, disenSoft, nivelIngles, estado
        Object[][] saberPro = {
            {"BARBOSA",    "EK20183007722", 200, 128,182,202,206,183,185,160,197, "B1", "FINALIZADO", "Tecnología e Ingeniería"},
            {"QUINTERO",   "EK20183140703", 165, 125,151,179,163,205,182,144,136, "B2", "FINALIZADO", "Tecnología e Ingeniería"},
            {"PARRA",      "EK20183040545", 164, 159,172,182,142,165,167,132,148, "A2", "FINALIZADO", "Tecnología e Ingeniería"},
            {"ANAYA",      "EK20183025381", 160, 146,199,157,149,147,174,127,171, "A2", "FINALIZADO", "Tecnología e Ingeniería"},
            {"FLOR",       "EK20183025335", 160, 198,153,147,157,146,168,114,160, "A2", "FINALIZADO", "Tecnología e Ingeniería"},
            {"GARCIA",     "EK20183122648", 157, 179,172,158,140,136,128,121,142, "A1", "FINALIZADO", "Tecnología e Ingeniería"},
            {"MANOSALVA",  "EK20183064605", 153, 115,152,159,172,165,142,118,119, "A2", "FINALIZADO", "Tecnología e Ingeniería"},
            {"MENDOZA",    "EK20183187351", 151, 132,123,125,169,204,173,127,171, "B2", "FINALIZADO", "Tecnología e Ingeniería"},
            {"BELTRAN",    "EK20183233820", 150, 142,163,148,155,143,156,131,149, "A2", "FINALIZADO", "Tecnología e Ingeniería"},
            {"SANTAMARIA", "EK20183030016", 150, 138,147,161,148,152,145,124,155, "A1", "FINALIZADO", "Tecnología e Ingeniería"},
            {"SANCHEZ",    "EK20183047073", 149, 141,158,145,153,148,151,128,147, "A2", "FINALIZADO", "Tecnología e Ingeniería"},
            {"ROMERO",     "EK20183236451", 146, 128,142,154,149,143,148,122,148, "A1", "FINALIZADO", "Tecnología e Ingeniería"},
            {"LUNA",       "EK20183041714", 141, 135,148,139,144,138,142,119,143, "A2", "FINALIZADO", "Tecnología e Ingeniería"},
            {"TRIANA",     "EK20183187801", 141, 127,144,136,148,135,139,117,141, "A1", "FINALIZADO", "Tecnología e Ingeniería"},
            {"SUAREZ",     "EK20183176566", 140, 132,141,138,142,136,143,115,140, "A2", "FINALIZADO", "Tecnología e Ingeniería"},
            {"GARCIA",     "EK20183204427", 139, 126,138,135,141,132,138,118,139, "A1", "Tecnología e Ingeniería", "ACTIVO"},
            {"PINZON",     "EK20183196280", 138, 123,135,136,139,130,136,116,137, "A1", "FINALIZADO", "Tecnología e Ingeniería"},
            {"JAIMES",     "EK20183173799", 137, 118,133,138,136,128,134,114,136, "A1", "FINALIZADO", "Tecnología e Ingeniería"},
            {"NINO",       "EK20183009565", 134, 112,131,135,132,124,132,111,133, "A0", "FINALIZADO", "Tecnología e Ingeniería"},
            {"FABIAN",     "EK20183117756", 133, 115,129,131,134,122,131,109,132, "A0", "FINALIZADO", "Tecnología e Ingeniería"},
            {"HERNANDEZ",  "EK20183044579", 132, 118,128,129,133,124,129,110,130, "A1", "FINALIZADO", "Tecnología e Ingeniería"},
            {"LARIOS",     "EK20183045760", 131, 114,126,128,131,121,128,108,129, "A1", "FINALIZADO", "Tecnología e Ingeniería"},
            {"CALDERON",   "EK20183034044", 130, 112,125,126,129,119,126,107,127, "A1", "FINALIZADO", "Tecnología e Ingeniería"},
            {"VILLARREAL", "EK20183041521", 129, 110,124,125,128,118,125,106,126, "A1", "FINALIZADO", "Tecnología e Ingeniería"},
            {"RESTREPO",   "EK20183027436", 126, 108,122,122,126,115,122,104,124, "A1", "FINALIZADO", "Tecnología e Ingeniería"},
            {"CACERES",    "EK20183031592", 125, 118,124,121,127,116,121,103,123, "A2", "FINALIZADO", "Tecnología e Ingeniería"},
            {"TABARES",    "EK20183004153", 124, 115,122,119,125,114,120,101,122, "A2", "FINALIZADO", "Tecnología e Ingeniería"},
            {"NARANJO",    "EK20183030783", 122, 112,119,118,122,111,118, 99,120, "A0", "FINALIZADO", "Tecnología e Ingeniería"},
            {"PRADA",      "EK20183024754", 122, 110,118,117,121,109,117, 98,119, "A1", "FINALIZADO", "Tecnología e Ingeniería"},
            {"VARGAS",     "EK20183186200", 114, 104,112,111,115,103,111, 93,112, "A0", "FINALIZADO", "Tecnología e Ingeniería"},
            {"TORRES",     "EK20183182410", 113, 102,110,109,113,101,109, 91,110, "A1", "FINALIZADO", "Tecnología e Ingeniería"},
            {"ORTIZ",      "EK20183213735", 107,  96,104,103,107, 95,103, 86,104, "A0", "FINALIZADO", "Tecnología e Ingeniería"},
            {"VILLAMIZAR", "EK20183065220", 106,  94,102,101,105, 93,101, 84,102, "A0", "FINALIZADO", "Tecnología e Ingeniería"},
            {"RESTREPO2",  "EK20183028123",  96,  85, 93, 92, 96, 85, 92, 76, 93, "A1", "FINALIZADO", "Tecnología e Ingeniería"},
            {"HIGUERA",    "EK20183207870", null,null,null,null,null,null,null,null,null, null, "ANULADO", "Tecnología e Ingeniería"},
            {"MATIZ",      "EK20183144329", null,null,null,null,null,null,null,null,null, null, "ANULADO", "Tecnología e Ingeniería"},
        };

        int userCount = 1;
        for (Object[] row : saberPro) {
            Estudiante e = new Estudiante();
            e.setPrimerApellido((String) row[0]);
            e.setNumeroRegistro((String) row[1]);
            e.setTipoPrueba(TipoPrueba.SABER_PRO);
            e.setPuntaje((Integer) row[2]);
            e.setComunicacionEscrita(toInt(row[3]));
            e.setRazonamientoCuantitativo(toInt(row[4]));
            e.setLecturaCritica(toInt(row[5]));
            e.setCompetenciasCiudadanas(toInt(row[6]));
            e.setIngles(toInt(row[7]));
            e.setFormulacionProyectos(toInt(row[8]));
            e.setPensamientoCientifico(toInt(row[9]));
            e.setDisenoSoftware(toInt(row[10]));
            e.setNivelIngles((String) row[11]);
            e.setEstado((String) row[12]);
            e.setFacultad((String) row[13]);
            e.setTipoDocumento("CC");

            // Niveles
            e.setPuntajeNivel(Estudiante.calcularNivel(e.getPuntaje()));
            e.setComunicacionEscritaNivel(Estudiante.calcularNivel(e.getComunicacionEscrita()));
            e.setRazonamientoCuantitativoNivel(Estudiante.calcularNivel(e.getRazonamientoCuantitativo()));
            e.setLecturaCriticaNivel(Estudiante.calcularNivel(e.getLecturaCritica()));
            e.setCompetenciasCiudadanasNivel(Estudiante.calcularNivel(e.getCompetenciasCiudadanas()));
            e.setInglesNivel(Estudiante.calcularNivel(e.getIngles()));
            e.setFormulacionProyectosNivel(Estudiante.calcularNivel(e.getFormulacionProyectos()));
            e.setPensamientoCientificoNivel(Estudiante.calcularNivel(e.getPensamientoCientifico()));
            e.setDisenoSoftwareNivel(Estudiante.calcularNivel(e.getDisenoSoftware()));

            // Apto para graduación: mínimo 120 pts en Saber Pro
            e.setAptoParaGraduacion(e.getPuntaje() != null && e.getPuntaje() >= 120);

            estudianteRepo.save(e);

            // Crear usuario estudiante para los que tienen puntaje (FINALIZADO)
            if ("FINALIZADO".equals(e.getEstado()) && e.getPuntaje() != null) {
                String username = "est" + userCount++;
                Usuario u = usuarioRepo.save(new Usuario(username, "est123", Usuario.Rol.ESTUDIANTE));
                e.setUsuario(u);
                estudianteRepo.save(e);
            }
        }

        System.out.println("=================================================");
        System.out.println("  SABER PRO UTS — Datos cargados");
        System.out.println("  admin / admin123        → Administrador");
        System.out.println("  coordinador / coord123  → Coordinador");
        System.out.println("  docente / doc123        → Docente");
        System.out.println("  est1 / est123           → Estudiante BARBOSA (200 pts)");
        System.out.println("  est2 / est123           → Estudiante QUINTERO (165 pts)");
        System.out.println("  H2 Console: http://localhost:8080/h2-console");
        System.out.println("=================================================");
    }

    private Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Integer) return (Integer) o;
        return Integer.parseInt(o.toString());
    }
}
