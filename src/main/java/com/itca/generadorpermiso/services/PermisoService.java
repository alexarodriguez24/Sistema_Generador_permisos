package com.itca.generadorpermiso.services;

import com.itca.generadorpermiso.entities.PermisosDetalle;
import com.itca.generadorpermiso.repositories.PermisosDetalleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PermisoService {

    @Autowired
    private PermisosDetalleRepository permisosDetalleRepository;

    // Encontrar todos los permisos con relaciones
    public List<PermisosDetalle> findAll() {
        return permisosDetalleRepository.findAllWithRelations();
    }

    // Encontrar por ID con relaciones
    public PermisosDetalle findById(Integer id) {
        Optional<PermisosDetalle> permiso = permisosDetalleRepository.findByIdWithRelations(id);
        return permiso.orElse(null);
    }

    // Encontrar por estado con relaciones
    public List<PermisosDetalle> findByEstado(PermisosDetalle.EstadoPermiso estado) {
        return permisosDetalleRepository.findByEstado(estado);
    }

    public PermisosDetalle save(PermisosDetalle permiso) {
        return permisosDetalleRepository.save(permiso);
    }

    public PermisosDetalle aprobarPermiso(Integer id, String comentarios) {
        return aprobarPermiso(id, null, comentarios);
    }

    // Nueva sobrecarga que acepta el docente aprobador
    public PermisosDetalle aprobarPermiso(Integer id, com.itca.generadorpermiso.entities.Docente aprobador, String comentarios) {
        PermisosDetalle permiso = findById(id);
        if (permiso != null) {
            permiso.setEstado(PermisosDetalle.EstadoPermiso.APROBADO);
            permiso.setFechaAprobacion(LocalDateTime.now());
            permiso.setComentariosAprobador(comentarios);
            permiso.setAprobadoPor(aprobador);
            PermisosDetalle permisoGuardado = permisosDetalleRepository.save(permiso);
            permisosDetalleRepository.flush(); // Forzar la escritura inmediata
            return permisoGuardado;
        }
        return null;
    }

    public PermisosDetalle rechazarPermiso(Integer id, String comentarios) {
        return rechazarPermiso(id, null, comentarios);
    }

    // Nueva sobrecarga que acepta el docente que rechaza
    public PermisosDetalle rechazarPermiso(Integer id, com.itca.generadorpermiso.entities.Docente rechazador, String comentarios) {
        PermisosDetalle permiso = findById(id);
        if (permiso != null) {
            permiso.setEstado(PermisosDetalle.EstadoPermiso.RECHAZADO);
            permiso.setFechaAprobacion(LocalDateTime.now());
            permiso.setComentariosAprobador(comentarios);
            permiso.setAprobadoPor(rechazador);
            PermisosDetalle permisoGuardado = permisosDetalleRepository.save(permiso);
            permisosDetalleRepository.flush(); // Forzar la escritura inmediata
            return permisoGuardado;
        }
        return null;
    }

    public PermisosDetalle marcarComoImpreso(Integer id) {
        return marcarComoImpreso(id, null);
    }

    // Nueva sobrecarga que acepta el docente que imprime
    public PermisosDetalle marcarComoImpreso(Integer id, com.itca.generadorpermiso.entities.Docente impresoPor) {
        PermisosDetalle permiso = findById(id);
        if (permiso != null) {
            permiso.setEstado(PermisosDetalle.EstadoPermiso.IMPRESO);
            permiso.setFechaImpresion(LocalDateTime.now());
            permiso.setImpresoPor(impresoPor);
            PermisosDetalle permisoGuardado = permisosDetalleRepository.save(permiso);
            permisosDetalleRepository.flush(); // Forzar la escritura inmediata
            return permisoGuardado;
        }
        return null;
    }

    public List<PermisosDetalle> findByAlumnoId(Integer alumnoId) {
        return permisosDetalleRepository.findByAlumnoId(alumnoId);
    }

    public List<PermisosDetalle> findActivos() {
        return permisosDetalleRepository.findActivosWithRelations();
    }

    public void deleteLogical(Integer id) {
        PermisosDetalle permiso = findById(id);
        if (permiso != null) {
            permiso.setActivo(false);
            permisosDetalleRepository.save(permiso);
        }
    }

    // Nuevo método para contar permisos por alumno
    public long countByAlumnoIdAndEstado(Integer alumnoId, PermisosDetalle.EstadoPermiso estado) {
        List<PermisosDetalle> permisos = permisosDetalleRepository.findByAlumnoId(alumnoId);
        return permisos.stream()
                .filter(p -> p.getEstado() == estado)
                .count();
    }

    // Método para guardar el comprobante
    public void guardarComprobante(Integer permisoId, MultipartFile archivo) throws IOException {
        PermisosDetalle permiso = findById(permisoId);
        if (permiso != null && archivo != null && !archivo.isEmpty()) {
            permiso.setComprobanteImagen(archivo.getBytes());
            permiso.setComprobanteTipo(archivo.getContentType());
            permiso.setNombreArchivoComprobante(archivo.getOriginalFilename());
            permiso.setComprobanteAdjunto(true);
            permisosDetalleRepository.save(permiso);
        }
    }
}