package com.itca.generadorpermiso.repositories;
 
 
import com.itca.generadorpermiso.entities.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
 
@Repository
public interface DocenteRepository extends JpaRepository<Docente, Integer> {
 
    // Buscar docente por código de empleado
    Optional<Docente> findByCodigoEmpleado(String codigoEmpleado);
 
    // Buscar docentes activos
    List<Docente> findByEstado(String estado);
}s