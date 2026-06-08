package com.example.demo.repository;

import com.example.demo.model.DocenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DocenteEntityRepository extends JpaRepository<DocenteEntity, Long> {
    Optional<DocenteEntity> findByNumeroDocumento(String doc);
    List<DocenteEntity> findByActivoTrue();
    List<DocenteEntity> findByFacultad_Id(Long facultadId);
}
