package com.itca.generadorpermiso.repositories;
 
import com.itca.generadorpermiso.entities.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.Optional;
 
@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {
 
    // Buscar alumno por carnet
    Optional<Alumno> findByCarnet(String carnet);
 
    // Verificar si existe un alumno con ese carnet
    boolean existsByCarnet(String carnet);
 
    // Buscar alumnos activos
    List<Alumno> findByActivoTrue();
}