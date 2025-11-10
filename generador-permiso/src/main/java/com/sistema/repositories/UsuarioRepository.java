package com.itca.generadorpermiso.repositories;
 
import com.itca.generadorpermiso.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
 
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.alumno LEFT JOIN FETCH u.docente WHERE u.email = :email")
    Usuario findByEmail(@Param("email") String email);
   
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.alumno a LEFT JOIN FETCH u.docente WHERE LOWER(a.carnet) = LOWER(:carnet)")
    Usuario findByCarnet(@Param("carnet") String carnet);
 
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.alumno LEFT JOIN FETCH u.docente d WHERE d.codigoEmpleado = :codigo")
    Usuario findByCodigoEmpleado(@Param("codigo") String codigo);
}