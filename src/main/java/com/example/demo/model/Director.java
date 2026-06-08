package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "directores")
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String primerNombre;
    private String segundoNombre;
    @NotBlank
    private String primerApellido;
    private String segundoApellido;

    @NotBlank
    private String tipoDocumento;
    @NotBlank
    @Column(unique = true)
    private String numeroDocumento;

    private String correo;
    private String telefono;

    @ManyToOne
    @JoinColumn(name = "facultad_id")
    private Facultad facultad;

    private boolean activo = true;

    public Director() {}

    public String getNombreCompleto() {
        return (primerNombre != null ? primerNombre + " " : "")
             + (segundoNombre != null ? segundoNombre + " " : "")
             + (primerApellido != null ? primerApellido + " " : "")
             + (segundoApellido != null ? segundoApellido : "");
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPrimerNombre() { return primerNombre; }
    public void setPrimerNombre(String v) { this.primerNombre = v; }
    public String getSegundoNombre() { return segundoNombre; }
    public void setSegundoNombre(String v) { this.segundoNombre = v; }
    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String v) { this.primerApellido = v; }
    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String v) { this.segundoApellido = v; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String v) { this.tipoDocumento = v; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String v) { this.numeroDocumento = v; }
    public String getCorreo() { return correo; }
    public void setCorreo(String v) { this.correo = v; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String v) { this.telefono = v; }
    public Facultad getFacultad() { return facultad; }
    public void setFacultad(Facultad v) { this.facultad = v; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean v) { this.activo = v; }
}
