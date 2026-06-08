package com.example.demo.repository;

import com.example.demo.model.Facultad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FacultadRepository extends JpaRepository<Facultad, Long> {
    Optional<Facultad> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}
