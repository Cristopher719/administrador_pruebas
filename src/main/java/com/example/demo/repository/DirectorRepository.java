package com.example.demo.repository;

import com.example.demo.model.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DirectorRepository extends JpaRepository<Director, Long> {
    Optional<Director> findByNumeroDocumento(String doc);
    List<Director> findByActivoTrue();
}
