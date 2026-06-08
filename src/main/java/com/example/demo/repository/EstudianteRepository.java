package com.example.demo.repository;

import com.example.demo.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByNumeroRegistro(String numeroRegistro);
    Optional<Estudiante> findByNumeroDocumento(String numeroDocumento);
    Optional<Estudiante> findByUsuario_Id(Long usuarioId);
    List<Estudiante> findByEstado(String estado);
    List<Estudiante> findByFacultadIgnoreCase(String facultad);
    List<Estudiante> findByTipoPrueba(Estudiante.TipoPrueba tipo);

    List<Estudiante> findByPrimerApellidoContainingIgnoreCaseOrPrimerNombreContainingIgnoreCase(
            String apellido, String nombre);

    @Query("SELECT e FROM Estudiante e WHERE e.puntaje IS NOT NULL ORDER BY e.puntaje DESC")
    List<Estudiante> findAllOrderByPuntaje();

    @Query("SELECT e FROM Estudiante e WHERE e.tipoPrueba = 'SABER_PRO' AND e.puntaje IS NOT NULL ORDER BY e.puntaje DESC")
    List<Estudiante> findSaberProOrderByPuntaje();

    @Query("SELECT e FROM Estudiante e WHERE e.tipoPrueba = 'SABER_TYT' AND e.puntaje IS NOT NULL ORDER BY e.puntaje DESC")
    List<Estudiante> findSaberTytOrderByPuntaje();

    @Query("SELECT e FROM Estudiante e WHERE e.aptoParaGraduacion = true ORDER BY e.puntaje DESC")
    List<Estudiante> findAptos();

    // ── Niveles (escala genérica para gráficas) ────────────────────
    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.puntaje >= 191")
    Long countNivel4();

    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.puntaje >= 156 AND e.puntaje < 191")
    Long countNivel3();

    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.puntaje >= 126 AND e.puntaje < 156")
    Long countNivel2();

    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.puntaje < 126 AND e.puntaje IS NOT NULL")
    Long countNivel1();

    @Query("SELECT e FROM Estudiante e WHERE e.puntaje IS NOT NULL AND e.puntaje >= 191 ORDER BY e.puntaje DESC")
    List<Estudiante> findNivel4();

    // ── Promedios ──────────────────────────────────────────────────
    @Query("SELECT AVG(e.puntaje) FROM Estudiante e WHERE e.puntaje IS NOT NULL")
    Double findPromedioPuntaje();

    @Query("SELECT AVG(e.puntaje) FROM Estudiante e WHERE e.puntaje IS NOT NULL AND e.tipoPrueba = 'SABER_PRO'")
    Double findPromedioPuntajeSaberPro();

    @Query("SELECT AVG(e.puntaje) FROM Estudiante e WHERE e.puntaje IS NOT NULL AND e.tipoPrueba = 'SABER_TYT'")
    Double findPromedioPuntajeSaberTyt();

    // ── Beneficios Saber Pro — Acuerdo 01-009 UTS ─────────────────
    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.tipoPrueba = 'SABER_PRO' AND e.puntaje >= 241")
    Long countSaberProBeca100();

    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.tipoPrueba = 'SABER_PRO' AND e.puntaje >= 211 AND e.puntaje < 241")
    Long countSaberProBeca50();

    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.tipoPrueba = 'SABER_PRO' AND e.puntaje >= 180 AND e.puntaje < 211")
    Long countSaberProExime();

    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.tipoPrueba = 'SABER_PRO' AND e.puntaje >= 120 AND e.puntaje < 180")
    Long countSaberProMinimo();

    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.tipoPrueba = 'SABER_PRO' AND e.puntaje < 120 AND e.puntaje IS NOT NULL")
    Long countSaberProNoApto();

    // ── Beneficios Saber T&T — Acuerdo 01-009 UTS ─────────────────
    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.tipoPrueba = 'SABER_TYT' AND e.puntaje >= 171")
    Long countTytBeca100();

    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.tipoPrueba = 'SABER_TYT' AND e.puntaje >= 151 AND e.puntaje < 171")
    Long countTytBeca50();

    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.tipoPrueba = 'SABER_TYT' AND e.puntaje >= 120 AND e.puntaje < 151")
    Long countTytExime();

    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.tipoPrueba = 'SABER_TYT' AND e.puntaje >= 80 AND e.puntaje < 120")
    Long countTytMinimo();

    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.tipoPrueba = 'SABER_TYT' AND e.puntaje < 80 AND e.puntaje IS NOT NULL")
    Long countTytNoApto();

    @Query("SELECT DISTINCT e.facultad FROM Estudiante e WHERE e.facultad IS NOT NULL")
    List<String> findDistinctFacultades();
}
