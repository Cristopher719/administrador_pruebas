package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "estudiantes")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoDocumento;
    private String numeroDocumento;

    @NotBlank
    private String primerApellido;
    private String segundoApellido;
    private String primerNombre;
    private String segundoNombre;

    private String correo;
    private String telefono;
    private String facultad;

    @Column(unique = true)
    private String numeroRegistro;

    // Tipo de prueba: SABER_PRO o SABER_TYT
    @Enumerated(EnumType.STRING)
    private TipoPrueba tipoPrueba = TipoPrueba.SABER_PRO;

    public enum TipoPrueba {
        SABER_PRO, SABER_TYT
    }

    // Puntaje global
    private Integer puntaje;
    private String puntajeNivel;

    // Competencias genéricas
    private Integer comunicacionEscrita;
    private String comunicacionEscritaNivel;
    private Integer razonamientoCuantitativo;
    private String razonamientoCuantitativoNivel;
    private Integer lecturaCritica;
    private String lecturaCriticaNivel;
    private Integer competenciasCiudadanas;
    private String competenciasCiudadanasNivel;
    private Integer ingles;
    private String inglesNivel;

    // Competencias específicas (Saber Pro - Tecnología e Ingeniería)
    private Integer formulacionProyectos;
    private String formulacionProyectosNivel;
    private Integer pensamientoCientifico;
    private String pensamientoCientificoNivel;
    private Integer disenoSoftware;
    private String disenoSoftwareNivel;

    // Nivel inglés MCER
    private String nivelIngles;

    // Estado
    private String estado;

    // Apto para graduación según puntaje mínimo UTS
    // Saber Pro: mínimo 120/300 | Saber TyT: mínimo 80/200
    private Boolean aptoParaGraduacion = false;

    // Pago
    private boolean pagoCargado = false;
    private String comprobantePago;
    // Nombre original del archivo subido por el estudiante
    private String documentoComprobante;
    // Contenido del archivo en base64 para visualización
    @Column(length = 10485760) // 10MB max en BD
    private String documentoContenidoBase64;
    private String documentoTipo; // application/pdf, image/jpeg, etc.

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Estudiante() {}

    // ── Puntaje mínimo según tipo de prueba ──────────────────────────
    public int getPuntajeMinimo() {
        if (tipoPrueba == TipoPrueba.SABER_TYT) return 80;
        return 120; // SABER_PRO
    }

    public int getPuntajeMaximo() {
        if (tipoPrueba == TipoPrueba.SABER_TYT) return 200;
        return 300; // SABER_PRO
    }

    public boolean cumplePuntajeMinimo() {
        if (puntaje == null) return false;
        return puntaje >= getPuntajeMinimo();
    }

    // ── Niveles genéricos (aplica a ambas pruebas) ───────────────────
    public static String calcularNivel(Integer puntaje) {
        if (puntaje == null) return "Sin datos";
        if (puntaje >= 191) return "Nivel 4";
        if (puntaje >= 156) return "Nivel 3";
        if (puntaje >= 126) return "Nivel 2";
        return "Nivel 1";
    }

    // ── Beneficios reales UTS — Acuerdo 01-009 ──────────────────────
    public String calcularBeneficio() {
        if (puntaje == null) return "Sin información";

        if (tipoPrueba == TipoPrueba.SABER_PRO) {
            // Rangos Saber Pro
            if (puntaje >= 241)
                return "Exime informe final de grado (concepto aprobado) o Seminario Grado IV nota 5.0 + Beca 100% derecho pecuniario de grado";
            if (puntaje >= 211)
                return "Exime informe final de grado (concepto aprobado) o Seminario Grado IV nota 4.7 + Beca 50% derecho pecuniario de grado";
            if (puntaje >= 180)
                return "Exime informe final de grado (concepto aprobado) o Seminario Grado IV con nota 4.5";
            if (puntaje >= 120)
                return "Cumple puntaje mínimo — Sin estímulo económico";
            return "No cumple puntaje mínimo requerido (120 pts) — No apto para graduación";
        } else {
            // Rangos Saber T&T
            if (puntaje >= 171)
                return "Exime informe final de grado (concepto aprobado) o Seminario Grado II nota 5.0 + Beca 100% derecho pecuniario de grado";
            if (puntaje >= 151)
                return "Exime informe final de grado (concepto aprobado) o Seminario Grado II nota 4.7 + Beca 50% derecho pecuniario de grado";
            if (puntaje >= 120)
                return "Exime informe final de grado (concepto aprobado) o Seminario Grado II con nota 4.5";
            if (puntaje >= 80)
                return "Cumple puntaje mínimo — Sin estímulo económico";
            return "No cumple puntaje mínimo requerido (80 pts) — No apto para graduación";
        }
    }

    public String calcularBeneficioCorto() {
        if (puntaje == null) return "Sin info";
        if (tipoPrueba == TipoPrueba.SABER_PRO) {
            if (puntaje >= 241) return "Beca 100% + Exime informe";
            if (puntaje >= 211) return "Beca 50% + Exime informe";
            if (puntaje >= 180) return "Exime informe / Seminario 4.5";
            if (puntaje >= 120) return "Puntaje mínimo OK";
            return "No apto para graduación";
        } else {
            if (puntaje >= 171) return "Beca 100% + Exime informe";
            if (puntaje >= 151) return "Beca 50% + Exime informe";
            if (puntaje >= 120) return "Exime informe / Seminario 4.5";
            if (puntaje >= 80)  return "Puntaje mínimo OK";
            return "No apto para graduación";
        }
    }

    public String getNombreCompleto() {
        StringBuilder sb = new StringBuilder();
        if (primerNombre != null) sb.append(primerNombre).append(" ");
        if (segundoNombre != null) sb.append(segundoNombre).append(" ");
        if (primerApellido != null) sb.append(primerApellido).append(" ");
        if (segundoApellido != null) sb.append(segundoApellido);
        return sb.toString().trim();
    }

    // ── Getters / Setters ────────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String v) { this.tipoDocumento = v; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String v) { this.numeroDocumento = v; }
    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String v) { this.primerApellido = v; }
    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String v) { this.segundoApellido = v; }
    public String getPrimerNombre() { return primerNombre; }
    public void setPrimerNombre(String v) { this.primerNombre = v; }
    public String getSegundoNombre() { return segundoNombre; }
    public void setSegundoNombre(String v) { this.segundoNombre = v; }
    public String getCorreo() { return correo; }
    public void setCorreo(String v) { this.correo = v; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String v) { this.telefono = v; }
    public String getFacultad() { return facultad; }
    public void setFacultad(String v) { this.facultad = v; }
    public String getNumeroRegistro() { return numeroRegistro; }
    public void setNumeroRegistro(String v) { this.numeroRegistro = v; }
    public TipoPrueba getTipoPrueba() { return tipoPrueba; }
    public void setTipoPrueba(TipoPrueba v) { this.tipoPrueba = v; }
    public Integer getPuntaje() { return puntaje; }
    public void setPuntaje(Integer v) { this.puntaje = v; }
    public String getPuntajeNivel() { return puntajeNivel; }
    public void setPuntajeNivel(String v) { this.puntajeNivel = v; }
    public Integer getComunicacionEscrita() { return comunicacionEscrita; }
    public void setComunicacionEscrita(Integer v) { this.comunicacionEscrita = v; }
    public String getComunicacionEscritaNivel() { return comunicacionEscritaNivel; }
    public void setComunicacionEscritaNivel(String v) { this.comunicacionEscritaNivel = v; }
    public Integer getRazonamientoCuantitativo() { return razonamientoCuantitativo; }
    public void setRazonamientoCuantitativo(Integer v) { this.razonamientoCuantitativo = v; }
    public String getRazonamientoCuantitativoNivel() { return razonamientoCuantitativoNivel; }
    public void setRazonamientoCuantitativoNivel(String v) { this.razonamientoCuantitativoNivel = v; }
    public Integer getLecturaCritica() { return lecturaCritica; }
    public void setLecturaCritica(Integer v) { this.lecturaCritica = v; }
    public String getLecturaCriticaNivel() { return lecturaCriticaNivel; }
    public void setLecturaCriticaNivel(String v) { this.lecturaCriticaNivel = v; }
    public Integer getCompetenciasCiudadanas() { return competenciasCiudadanas; }
    public void setCompetenciasCiudadanas(Integer v) { this.competenciasCiudadanas = v; }
    public String getCompetenciasCiudadanasNivel() { return competenciasCiudadanasNivel; }
    public void setCompetenciasCiudadanasNivel(String v) { this.competenciasCiudadanasNivel = v; }
    public Integer getIngles() { return ingles; }
    public void setIngles(Integer v) { this.ingles = v; }
    public String getInglesNivel() { return inglesNivel; }
    public void setInglesNivel(String v) { this.inglesNivel = v; }
    public Integer getFormulacionProyectos() { return formulacionProyectos; }
    public void setFormulacionProyectos(Integer v) { this.formulacionProyectos = v; }
    public String getFormulacionProyectosNivel() { return formulacionProyectosNivel; }
    public void setFormulacionProyectosNivel(String v) { this.formulacionProyectosNivel = v; }
    public Integer getPensamientoCientifico() { return pensamientoCientifico; }
    public void setPensamientoCientifico(Integer v) { this.pensamientoCientifico = v; }
    public String getPensamientoCientificoNivel() { return pensamientoCientificoNivel; }
    public void setPensamientoCientificoNivel(String v) { this.pensamientoCientificoNivel = v; }
    public Integer getDisenoSoftware() { return disenoSoftware; }
    public void setDisenoSoftware(Integer v) { this.disenoSoftware = v; }
    public String getDisenoSoftwareNivel() { return disenoSoftwareNivel; }
    public void setDisenoSoftwareNivel(String v) { this.disenoSoftwareNivel = v; }
    public String getNivelIngles() { return nivelIngles; }
    public void setNivelIngles(String v) { this.nivelIngles = v; }
    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }
    public Boolean getAptoParaGraduacion() { return aptoParaGraduacion; }
    public void setAptoParaGraduacion(Boolean v) { this.aptoParaGraduacion = v; }
    public boolean isPagoCargado() { return pagoCargado; }
    public void setPagoCargado(boolean v) { this.pagoCargado = v; }
    public String getComprobantePago() { return comprobantePago; }
    public void setComprobantePago(String v) { this.comprobantePago = v; }
    public String getDocumentoComprobante() { return documentoComprobante; }
    public void setDocumentoComprobante(String v) { this.documentoComprobante = v; }
    public String getDocumentoContenidoBase64() { return documentoContenidoBase64; }
    public void setDocumentoContenidoBase64(String v) { this.documentoContenidoBase64 = v; }
    public String getDocumentoTipo() { return documentoTipo; }
    public void setDocumentoTipo(String v) { this.documentoTipo = v; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario v) { this.usuario = v; }
}
