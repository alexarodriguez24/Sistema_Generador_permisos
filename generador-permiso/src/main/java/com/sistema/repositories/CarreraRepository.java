package com.itca.generadorpermiso.repositories;
 
 
 
import com.itca.generadorpermiso.entities.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
 
@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Integer> {
 
    // Buscar carrera por código
    Optional<Carrera> findByCodigo(String codigo);
 
    // Verificar si existe una carrera con ese código
    boolean existsByCodigo(String codigo);
}
 