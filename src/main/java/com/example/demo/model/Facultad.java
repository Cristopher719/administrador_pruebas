package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "facultades")
public class Facultad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String nombre;

    private String descripcion;
    private boolean activa = true;

    @OneToMany(mappedBy = "facultad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DocenteEntity> docentes;

    public Facultad() {}
    public Facultad(String nombre) { this.nombre = nombre; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String v) { this.nombre = v; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String v) { this.descripcion = v; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean v) { this.activa = v; }
    public List<DocenteEntity> getDocentes() { return docentes; }
    public void setDocentes(List<DocenteEntity> v) { this.docentes = v; }
}
