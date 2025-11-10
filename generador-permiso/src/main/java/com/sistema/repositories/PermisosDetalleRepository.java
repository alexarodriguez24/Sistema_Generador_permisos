package com.itca.generadorpermiso.repositories;
 
import com.itca.generadorpermiso.entities.PermisosDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
 
@Repository
public interface PermisosDetalleRepository extends JpaRepository<PermisosDetalle, Integer> {
 
    // Encontrar por estado con JOIN FETCH
    @Query("SELECT p FROM PermisosDetalle p JOIN FETCH p.alumno JOIN FETCH p.tipoPermiso WHERE p.estado = :estado")
    List<PermisosDetalle> findByEstado(@Param("estado") PermisosDetalle.EstadoPermiso estado);
 
    // Encontrar todos con JOIN FETCH
    @Query("SELECT p FROM PermisosDetalle p JOIN FETCH p.alumno JOIN FETCH p.tipoPermiso")
    List<PermisosDetalle> findAllWithRelations();
 
    // Encontrar por ID con JOIN FETCH
    @Query("SELECT p FROM PermisosDetalle p JOIN FETCH p.alumno JOIN FETCH p.tipoPermiso WHERE p.id = :id")
    Optional<PermisosDetalle> findByIdWithRelations(@Param("id") Integer id);
 
    // Encontrar por alumno
    @Query("SELECT p FROM PermisosDetalle p JOIN FETCH p.alumno JOIN FETCH p.tipoPermiso WHERE p.alumno.id = :alumnoId")
    List<PermisosDetalle> findByAlumnoId(@Param("alumnoId") Integer alumnoId);
 
    // Encontrar permisos activos
    @Query("SELECT p FROM PermisosDetalle p JOIN FETCH p.alumno JOIN FETCH p.tipoPermiso WHERE p.activo = true")
    List<PermisosDetalle> findActivosWithRelations();
 
    // Encontrar permisos por rango de fechas
    List<PermisosDetalle> findByFechaInicioPermisoBetween(LocalDate startDate, LocalDate endDate);
 
    // Encontrar permisos pendientes por alumno
    @Query("SELECT p FROM PermisosDetalle p JOIN FETCH p.alumno JOIN FETCH p.tipoPermiso WHERE p.alumno.id = :alumnoId AND p.estado = :estado")
    List<PermisosDetalle> findByAlumnoIdAndEstado(@Param("alumnoId") Integer alumnoId, @Param("estado") PermisosDetalle.EstadoPermiso estado);
 
    // Consulta personalizada para permisos que están activos y no expirados
    @Query("SELECT p FROM PermisosDetalle p JOIN FETCH p.alumno JOIN FETCH p.tipoPermiso WHERE p.activo = true AND p.fechaFinPermiso >= :currentDate")
    List<PermisosDetalle> findPermisosVigentes(@Param("currentDate") LocalDate currentDate);
 
    // Consulta para contar permisos por estado
    @Query("SELECT p.estado, COUNT(p) FROM PermisosDetalle p WHERE p.activo = true GROUP BY p.estado")
    List<Object[]> countPermisosByEstado();
 
    // Encontrar permisos por tipo de permiso
    @Query("SELECT p FROM PermisosDetalle p JOIN FETCH p.alumno JOIN FETCH p.tipoPermiso WHERE p.tipoPermiso.id = :tipoPermisoId")
    List<PermisosDetalle> findByTipoPermisoId(@Param("tipoPermisoId") Integer tipoPermisoId);
}
 