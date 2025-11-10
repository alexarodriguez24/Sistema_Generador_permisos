package com.itca.generadorpermiso.controllers;

import com.itca.generadorpermiso.entities.PermisosDetalle;
import com.itca.generadorpermiso.entities.PermisoTipo;
import com.itca.generadorpermiso.entities.Usuario;
import com.itca.generadorpermiso.services.AuthService;
import com.itca.generadorpermiso.services.PermisoService;
import com.itca.generadorpermiso.services.PermisoTipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequestMapping("/permisos")
public class PermisoController {

    @Autowired
    private PermisoService permisoService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PermisoTipoService permisoTipoService;

    // DASHBOARD PARA ALUMNO - MÉTODO GET
    @GetMapping("/dashboard")
    public String dashboardAlumno(Model model, HttpSession session) {
        try {
            // Obtener usuario desde sesión
            Usuario usuario = (Usuario) session.getAttribute("usuario");

            if (usuario != null && usuario.getAlumno() != null) {
                model.addAttribute("alumno", usuario.getAlumno());

                // Obtener permisos específicos del alumno
                var misPermisos = permisoService.findByAlumnoId(usuario.getAlumno().getId());

                // Estadísticas para el alumno
                long totalPermisos = misPermisos.size();
                long permisosAprobados = misPermisos.stream()
                        .filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.APROBADO)
                        .count();
                long permisosPendientes = misPermisos.stream()
                        .filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.PENDIENTE)
                        .count();
                long permisosRechazados = misPermisos.stream()
                        .filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.RECHAZADO)
                        .count();

                model.addAttribute("totalPermisos", totalPermisos);
                model.addAttribute("permisosAprobados", permisosAprobados);
                model.addAttribute("permisosPendientes", permisosPendientes);
                model.addAttribute("permisosRechazados", permisosRechazados);
                model.addAttribute("misPermisos", misPermisos);

            } else {
                // Datos de prueba si no hay alumno
                model.addAttribute("alumnoNombre", "Juan Carlos Pérez");
                model.addAttribute("totalPermisos", 0);
                model.addAttribute("permisosAprobados", 0);
                model.addAttribute("permisosPendientes", 0);
                model.addAttribute("permisosRechazados", 0);
            }

            return "alumno-dashboard";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            return "alumno-dashboard";
        }
    }

    @GetMapping("/solicitar")
    public String mostrarFormularioSolicitud(Model model, HttpSession session) {
        try {
            // Obtener usuario desde sesión
            Usuario usuario = (Usuario) session.getAttribute("usuario");

            if (usuario != null && usuario.getAlumno() != null) {
                model.addAttribute("alumno", usuario.getAlumno());
            }

            // Agregar tipos de permiso disponibles
            List<PermisoTipo> tiposPermiso = permisoTipoService.findAll();
            model.addAttribute("tiposPermiso", tiposPermiso);

            model.addAttribute("permiso", new PermisosDetalle());
            return "solicitar-permiso";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "solicitar-permiso";
        }
    }

    @PostMapping("/guardar")
    public String guardarPermiso(@RequestParam("tipoPermisoId") Integer tipoPermisoId,
                                 @RequestParam("fechaInicioPermiso") String fechaInicioPermiso,
                                 @RequestParam("fechaFinPermiso") String fechaFinPermiso,
                                 @RequestParam("motivo") String motivo,
                                 @RequestParam(value = "comentariosAprobador", required = false) String comentariosAprobador,
                                 @RequestParam(value = "comprobante", required = false) MultipartFile comprobante,
                                 Model model,
                                 HttpSession session) {

        try {
            // Obtener usuario desde sesión
            Usuario usuario = (Usuario) session.getAttribute("usuario");

            if (usuario == null || usuario.getAlumno() == null) {
                model.addAttribute("error", "Alumno no encontrado");
                return "redirect:/permisos/solicitar?error";
            }

            // Buscar el tipo de permiso
            PermisoTipo tipoPermiso = permisoTipoService.findById(tipoPermisoId);
            if (tipoPermiso == null) {
                model.addAttribute("error", "Tipo de permiso no válido");
                return "redirect:/permisos/solicitar?error";
            }

            // Convertir fechas de String a LocalDate
            LocalDate fechaInicio = LocalDate.parse(fechaInicioPermiso);
            LocalDate fechaFin = LocalDate.parse(fechaFinPermiso);

            // Validar fechas
            if (fechaFin.isBefore(fechaInicio)) {
                model.addAttribute("error", "La fecha fin no puede ser anterior a la fecha inicio");
                return "redirect:/permisos/solicitar?error";
            }

            // Crear nuevo permiso
            PermisosDetalle permiso = new PermisosDetalle();
            permiso.setAlumno(usuario.getAlumno());
            permiso.setTipoPermiso(tipoPermiso);
            permiso.setFechaInicioPermiso(fechaInicio);
            permiso.setFechaFinPermiso(fechaFin);
            permiso.setFechaSolicitud(java.time.LocalDateTime.now());
            permiso.setMotivo(motivo);
            permiso.setComentariosAprobador(comentariosAprobador);
            permiso.setEstado(PermisosDetalle.EstadoPermiso.PENDIENTE);
            permiso.setActivo(true);

            // Procesar comprobante si se subió - CORREGIDO
            if (comprobante != null && !comprobante.isEmpty()) {
                try {
                    // PRIMERO: Guardar en sistema de archivos
                    String rutaComprobante = guardarComprobante(comprobante, usuario.getAlumno().getId());
                    permiso.setRutaComprobante(rutaComprobante);
                    permiso.setNombreArchivoComprobante(comprobante.getOriginalFilename());

                    // OPCIONAL: También guardar en base de datos (puedes comentar estas líneas si no funciona)
                    // permiso.setComprobanteImagen(comprobante.getBytes());
                    // permiso.setComprobanteTipo(comprobante.getContentType());

                    permiso.setComprobanteAdjunto(true);

                    System.out.println("✅ Comprobante guardado en: " + rutaComprobante);

                } catch (IOException e) {
                    System.err.println("❌ Error al guardar comprobante: " + e.getMessage());
                    permiso.setComprobanteAdjunto(false);
                }
            } else {
                permiso.setComprobanteAdjunto(false);
            }

            // Guardar el permiso
            PermisosDetalle permisoGuardado = permisoService.save(permiso);

            if (permisoGuardado != null && permisoGuardado.getId() != null) {
                System.out.println("✅ Permiso guardado exitosamente con ID: " + permisoGuardado.getId());
                return "redirect:/permisos/dashboard?success";
            } else {
                System.err.println("❌ Error: el permiso no se guardó correctamente");
                return "redirect:/permisos/solicitar?error=save_failed";
            }

        } catch (DateTimeParseException e) {
            e.printStackTrace();
            System.err.println("❌ Error al parsear fechas: " + e.getMessage());
            return "redirect:/permisos/solicitar?error=invalid_date";
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error al guardar el permiso: " + e.getMessage());
            return "redirect:/permisos/solicitar?error=general";
        }
    }

    private String guardarComprobante(MultipartFile comprobante, Integer alumnoId) throws IOException {
        // Usar directorio dentro del proyecto para mejor compatibilidad
        String uploadDir = "uploads/comprobantes/";
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            boolean created = uploadPath.mkdirs();
            System.out.println("📁 Directorio creado: " + created + " en: " + uploadPath.getAbsolutePath());
        }
        // Generar nombre único para el archivo
        String originalFileName = comprobante.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String fileName = "comprobante_" + alumnoId + "_" + System.currentTimeMillis() + fileExtension;

        // Guardar archivo
        Path filePath = Paths.get(uploadDir + fileName);
        Files.write(filePath, comprobante.getBytes());

        System.out.println("💾 Archivo guardado en: " + filePath.toAbsolutePath());

        return uploadDir + fileName; // Retornar ruta relativa
    }

    @GetMapping("/mis-permisos")
    public String verMisPermisos(Model model, HttpSession session) {
        try {
            // Obtener usuario desde sesión
            Usuario usuario = (Usuario) session.getAttribute("usuario");

            List<PermisosDetalle> misPermisos;
            if (usuario != null && usuario.getAlumno() != null) {
                misPermisos = permisoService.findByAlumnoId(usuario.getAlumno().getId());
                model.addAttribute("alumno", usuario.getAlumno());
            } else {
                // Si no hay alumno, mostrar todos los permisos (para demo)
                misPermisos = permisoService.findActivos();
            }

            // Estadísticas básicas
            long totalPermisos = misPermisos.size();
            long permisosAprobados = misPermisos.stream().filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.APROBADO).count();
            long permisosPendientes = misPermisos.stream().filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.PENDIENTE).count();
            long permisosRechazados = misPermisos.stream().filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.RECHAZADO).count();

            model.addAttribute("permisos", misPermisos);
            model.addAttribute("totalPermisos", totalPermisos);
            model.addAttribute("permisosAprobados", permisosAprobados);
            model.addAttribute("permisosPendientes", permisosPendientes);
            model.addAttribute("permisosRechazados", permisosRechazados);

            model.addAttribute("userRole", "ESTUDIANTE");
            model.addAttribute("dashboardUrl", "/permisos/dashboard");
            model.addAttribute("solicitarUrl", "/permisos/solicitar");
            return "historial-permisos";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar los permisos: " + e.getMessage());
            model.addAttribute("userRole", "ESTUDIANTE");
            model.addAttribute("dashboardUrl", "/permisos/dashboard");
            model.addAttribute("solicitarUrl", "/permisos/solicitar");
            return "historial-permisos";
        }
    }

    @GetMapping("/detalle/{id}")
    public String verDetallePermiso(@PathVariable Integer id, Model model, HttpSession session) {
        try {
            PermisosDetalle permiso = permisoService.findById(id);
            if (permiso != null) {
                model.addAttribute("permiso", permiso);
                model.addAttribute("userRole", "ESTUDIANTE");
                model.addAttribute("dashboardUrl", "/permisos/dashboard");
                return "detalle-permiso";
            }
            return "redirect:/permisos/mis-permisos?error=not_found";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/permisos/mis-permisos?error=load_error";
        }
    }

    @PostMapping("/cancelar/{id}")
    public String cancelarPermiso(@PathVariable Integer id) {
        try {
            permisoService.deleteLogical(id);
            return "redirect:/permisos/mis-permisos?success=canceled";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/permisos/mis-permisos?error=cancel_error";
        }
    }

    // ENDPOINTS PARA MOSTRAR Y DESCARGAR COMPROBANTE - MEJORADO
    @GetMapping("/{id}/comprobante")
    public ResponseEntity<byte[]> mostrarComprobante(@PathVariable Integer id) {
        try {
            System.out.println("=== 🔍 SOLICITANDO COMPROBANTE ===");
            System.out.println("📋 Permiso ID: " + id);

            PermisosDetalle permiso = permisoService.findById(id);

            if (permiso == null) {
                System.out.println("❌ Permiso no encontrado");
                return ResponseEntity.notFound().build();
            }

            System.out.println("✅ Permiso encontrado");
            System.out.println("📎 Comprobante adjunto: " + permiso.getComprobanteAdjunto());
            System.out.println("🗂️ Ruta comprobante: " + permiso.getRutaComprobante());
            System.out.println("📄 Nombre archivo: " + permiso.getNombreArchivoComprobante());

            if (!permiso.getComprobanteAdjunto() || permiso.getRutaComprobante() == null) {
                System.out.println("❌ No hay comprobante adjunto o ruta es null");
                return ResponseEntity.notFound().build();
            }

            // SERVIR DESDE SISTEMA DE ARCHIVOS
            try {
                Path filePath = Paths.get(permiso.getRutaComprobante());
                System.out.println("🔍 Buscando archivo en: " + filePath.toAbsolutePath());

                if (!Files.exists(filePath)) {
                    System.out.println("❌ Archivo no existe en la ruta especificada");

                    // Intentar buscar en el classpath o directorio raíz
                    String alternativePath = System.getProperty("user.dir") + "/" + permiso.getRutaComprobante();
                    filePath = Paths.get(alternativePath);
                    System.out.println("🔍 Intentando ruta alternativa: " + filePath.toAbsolutePath());

                    if (!Files.exists(filePath)) {
                        System.out.println("❌ Archivo tampoco existe en ruta alternativa");
                        return ResponseEntity.notFound().build();
                    }
                }

                byte[] fileBytes = Files.readAllBytes(filePath);
                System.out.println("✅ Archivo leído exitosamente");
                System.out.println("📊 Tamaño: " + fileBytes.length + " bytes");

                // Determinar el tipo de contenido
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    // Si no se puede determinar, usar por extensión
                    String fileName = filePath.getFileName().toString().toLowerCase();
                    if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                        contentType = "image/jpeg";
                    } else if (fileName.endsWith(".png")) {
                        contentType = "image/png";
                    } else if (fileName.endsWith(".pdf")) {
                        contentType = "application/pdf";
                    } else {
                        contentType = "application/octet-stream";
                    }
                    System.out.println("🔍 Tipo de contenido determinado por extensión: " + contentType);
                } else {
                    System.out.println("🔍 Tipo de contenido detectado: " + contentType);
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(contentType));
                headers.setContentDisposition(ContentDisposition.builder("inline")
                        .filename(permiso.getNombreArchivoComprobante() != null ?
                                permiso.getNombreArchivoComprobante() : "comprobante")
                        .build());

                System.out.println("✅ Enviando comprobante al cliente");
                return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);

            } catch (IOException e) {
                System.out.println("❌ Error leyendo archivo: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        } catch (Exception e) {
            System.out.println("❌ Error general: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/descargar-comprobante")
    public ResponseEntity<byte[]> descargarComprobante(@PathVariable Integer id) {
        try {
            PermisosDetalle permiso = permisoService.findById(id);

            if (permiso == null || !permiso.getComprobanteAdjunto() || permiso.getRutaComprobante() == null) {
                return ResponseEntity.notFound().build();
            }

            // SERVIR DESDE SISTEMA DE ARCHIVOS
            Path filePath = Paths.get(permiso.getRutaComprobante());

            if (!Files.exists(filePath)) {
                // Intentar ruta alternativa
                String alternativePath = System.getProperty("user.dir") + "/" + permiso.getRutaComprobante();
                filePath = Paths.get(alternativePath);

                if (!Files.exists(filePath)) {
                    return ResponseEntity.notFound().build();
                }
            }

            byte[] fileBytes = Files.readAllBytes(filePath);

            // Determinar el tipo de contenido
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // Generar nombre de archivo para descarga
            String filename = permiso.getNombreArchivoComprobante();
            if (filename == null || filename.isEmpty()) {
                filename = filePath.getFileName().toString();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(filename)
                    .build());

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint para subir comprobante a un permiso existente
    @PostMapping("/{id}/subir-comprobante")
    public String subirComprobante(@PathVariable Integer id,
                                   @RequestParam("comprobante") MultipartFile archivo,
                                   RedirectAttributes redirectAttributes) {
        try {
            PermisosDetalle permiso = permisoService.findById(id);
            if (permiso != null && archivo != null && !archivo.isEmpty()) {
                // Guardar en sistema de archivos
                String rutaComprobante = guardarComprobante(archivo, permiso.getAlumno().getId());
                permiso.setRutaComprobante(rutaComprobante);
                permiso.setNombreArchivoComprobante(archivo.getOriginalFilename());
                permiso.setComprobanteAdjunto(true);

                permisoService.save(permiso);
                redirectAttributes.addFlashAttribute("success", "Comprobante subido exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error al subir el comprobante");
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar el archivo");
        }
        return "redirect:/permisos/detalle/" + id;
    }
}