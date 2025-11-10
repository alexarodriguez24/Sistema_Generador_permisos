package com.itca.generadorpermiso.controllers;

import com.itca.generadorpermiso.entities.PermisosDetalle;
import com.itca.generadorpermiso.entities.Usuario;
import com.itca.generadorpermiso.services.AuthService;
import com.itca.generadorpermiso.services.PermisoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PermisoService permisoService;

    @Autowired
    private AuthService authService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        try {
            // Obtener usuario desde sesión
            Usuario usuario = (Usuario) session.getAttribute("usuario");

            if (usuario != null) {
                model.addAttribute("adminNombre", usuario.getNombre() != null ? usuario.getNombre() : "Administrador");
                model.addAttribute("adminEmail", usuario.getEmail() != null ? usuario.getEmail() : "admin@itca.edu.sv");
            } else {
                model.addAttribute("adminEmail", "admin@itca.edu.sv");
                model.addAttribute("adminNombre", "Administrador");
            }

            List<PermisosDetalle> todosPermisos = permisoService.findAll();
            model.addAttribute("permisos", todosPermisos);

            long totalPermisos = todosPermisos.size();
            long permisosPendientes = todosPermisos.stream()
                    .filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.PENDIENTE)
                    .count();
            long permisosAprobados = todosPermisos.stream()
                    .filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.APROBADO)
                    .count();
            long permisosRechazados = todosPermisos.stream()
                    .filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.RECHAZADO)
                    .count();
            long permisosImpresos = todosPermisos.stream()
                    .filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.IMPRESO)
                    .count();
            long permisosExpirados = todosPermisos.stream()
                    .filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.EXPIRADO)
                    .count();

            model.addAttribute("totalPermisos", totalPermisos);
            model.addAttribute("permisosAprobados", permisosAprobados);
            model.addAttribute("permisosPendientes", permisosPendientes);
            model.addAttribute("permisosRechazados", permisosRechazados);
            model.addAttribute("permisosImpresos", permisosImpresos);
            model.addAttribute("permisosExpirados", permisosExpirados);

            if (totalPermisos > 0) {
                double eficienciaSistema = ((double) (permisosAprobados + permisosRechazados) / totalPermisos) * 100;
                double tasaAprobacion = ((double) permisosAprobados / totalPermisos) * 100;
                double tasaRechazo = ((double) permisosRechazados / totalPermisos) * 100;

                model.addAttribute("eficienciaSistema", String.format("%.1f", eficienciaSistema));
                model.addAttribute("tasaAprobacion", String.format("%.1f", tasaAprobacion));
                model.addAttribute("tasaRechazo", String.format("%.1f", tasaRechazo));

                model.addAttribute("porcentajeAprobados", String.format("%.1f", ((double) permisosAprobados / totalPermisos) * 100));
                model.addAttribute("porcentajePendientes", String.format("%.1f", ((double) permisosPendientes / totalPermisos) * 100));
                model.addAttribute("porcentajeRechazados", String.format("%.1f", ((double) permisosRechazados / totalPermisos) * 100));
                model.addAttribute("porcentajeImpresos", String.format("%.1f", ((double) permisosImpresos / totalPermisos) * 100));
                model.addAttribute("porcentajeExpirados", String.format("%.1f", ((double) permisosExpirados / totalPermisos) * 100));
            } else {
                model.addAttribute("eficienciaSistema", "0.0");
                model.addAttribute("tasaAprobacion", "0.0");
                model.addAttribute("tasaRechazo", "0.0");
                model.addAttribute("porcentajeAprobados", "0.0");
                model.addAttribute("porcentajePendientes", "0.0");
                model.addAttribute("porcentajeRechazados", "0.0");
                model.addAttribute("porcentajeImpresos", "0.0");
                model.addAttribute("porcentajeExpirados", "0.0");
            }

            return "admin-dashboard";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            model.addAttribute("totalPermisos", 0);
            model.addAttribute("permisosAprobados", 0);
            model.addAttribute("permisosPendientes", 0);
            model.addAttribute("permisosRechazados", 0);
            model.addAttribute("permisosImpresos", 0);
            model.addAttribute("permisosExpirados", 0);
            return "admin-dashboard";
        }
    }

    @GetMapping("/permisos")
    public String listarTodosPermisos(Model model, HttpSession session) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuario != null) {
                model.addAttribute("adminNombre", usuario.getNombre() != null ? usuario.getNombre() : "Administrador");
            }

            List<PermisosDetalle> permisos = permisoService.findAll();
            model.addAttribute("permisos", permisos);
            model.addAttribute("userRole", "ADMIN");
            model.addAttribute("dashboardUrl", "/admin/dashboard");
            model.addAttribute("solicitarUrl", "/admin/dashboard");

            long totalPermisos = permisos.size();
            long permisosPendientes = permisos.stream()
                    .filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.PENDIENTE)
                    .count();
            long permisosAprobados = permisos.stream()
                    .filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.APROBADO)
                    .count();
            long permisosRechazados = permisos.stream()
                    .filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.RECHAZADO)
                    .count();

            model.addAttribute("totalPermisos", totalPermisos);
            model.addAttribute("permisosAprobados", permisosAprobados);
            model.addAttribute("permisosPendientes", permisosPendientes);
            model.addAttribute("permisosRechazados", permisosRechazados);

            return "historial-permisos";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar los permisos: " + e.getMessage());
            model.addAttribute("userRole", "ADMIN");
            model.addAttribute("dashboardUrl", "/admin/dashboard");
            return "historial-permisos";
        }
    }

    @GetMapping("/permiso/{id}")
    public String verDetallePermiso(@PathVariable Integer id, Model model, HttpSession session) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuario != null) {
                model.addAttribute("adminNombre", usuario.getNombre() != null ? usuario.getNombre() : "Administrador");
            }

            PermisosDetalle permiso = permisoService.findById(id);
            if (permiso != null) {
                model.addAttribute("permiso", permiso);
                model.addAttribute("userRole", "ADMIN");
                model.addAttribute("dashboardUrl", "/admin/dashboard");
                return "detalle-permiso";
            } else {
                model.addAttribute("error", "Permiso no encontrado");
                return "redirect:/admin/permisos?error=not_found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el permiso: " + e.getMessage());
            return "redirect:/admin/permisos?error=load_error";
        }
    }

    // ACCIONES DEL ADMIN SOBRE LOS PERMISOS

    @PostMapping("/permiso/{id}/aprobar")
    public String aprobarPermiso(@PathVariable Integer id,
                                 @RequestParam(value = "comentarios", required = false) String comentarios,
                                 RedirectAttributes redirectAttributes,
                                 HttpSession session) {
        try {
            // Validar que el permiso existe y está pendiente
            PermisosDetalle permiso = permisoService.findById(id);
            if (permiso == null) {
                redirectAttributes.addFlashAttribute("error", "Permiso no encontrado");
                return "redirect:/admin/permisos";
            }

            if (permiso.getEstado() != PermisosDetalle.EstadoPermiso.PENDIENTE) {
                redirectAttributes.addFlashAttribute("error", "Solo se pueden aprobar permisos pendientes");
                return "redirect:/admin/permiso/" + id;
            }

            // Aprobar el permiso
            PermisosDetalle permisoAprobado = permisoService.aprobarPermiso(id, comentarios);
            if (permisoAprobado != null) {
                redirectAttributes.addFlashAttribute("success", "Permiso aprobado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error al aprobar el permiso");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al aprobar el permiso: " + e.getMessage());
        }
        return "redirect:/admin/permiso/" + id;
    }

    @PostMapping("/permiso/{id}/rechazar")
    public String rechazarPermiso(@PathVariable Integer id,
                                  @RequestParam("comentarios") String comentarios,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {
        try {
            // Validar que el permiso existe y está pendiente
            PermisosDetalle permiso = permisoService.findById(id);
            if (permiso == null) {
                redirectAttributes.addFlashAttribute("error", "Permiso no encontrado");
                return "redirect:/admin/permisos";
            }

            if (permiso.getEstado() != PermisosDetalle.EstadoPermiso.PENDIENTE) {
                redirectAttributes.addFlashAttribute("error", "Solo se pueden rechazar permisos pendientes");
                return "redirect:/admin/permiso/" + id;
            }

            // Validar comentarios
            if (comentarios == null || comentarios.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Los comentarios son obligatorios para rechazar un permiso");
                return "redirect:/admin/permiso/" + id;
            }

            // Rechazar el permiso
            PermisosDetalle permisoRechazado = permisoService.rechazarPermiso(id, comentarios.trim());
            if (permisoRechazado != null) {
                redirectAttributes.addFlashAttribute("success", "Permiso rechazado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error al rechazar el permiso");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al rechazar el permiso: " + e.getMessage());
        }
        return "redirect:/admin/permiso/" + id;
    }

    @PostMapping("/permiso/{id}/marcar-impreso")
    public String marcarComoImpreso(@PathVariable Integer id,
                                    RedirectAttributes redirectAttributes,
                                    HttpSession session) {
        try {
            // Validar que el permiso existe y está aprobado
            PermisosDetalle permiso = permisoService.findById(id);
            if (permiso == null) {
                redirectAttributes.addFlashAttribute("error", "Permiso no encontrado");
                return "redirect:/admin/permisos";
            }

            if (permiso.getEstado() != PermisosDetalle.EstadoPermiso.APROBADO) {
                redirectAttributes.addFlashAttribute("error", "Solo se pueden marcar como impresos los permisos aprobados");
                return "redirect:/admin/permiso/" + id;
            }

            // Marcar como impreso
            PermisosDetalle permisoImpreso = permisoService.marcarComoImpreso(id);
            if (permisoImpreso != null) {
                redirectAttributes.addFlashAttribute("success", "Permiso marcado como impreso exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error al marcar como impreso");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al marcar como impreso: " + e.getMessage());
        }
        return "redirect:/admin/permiso/" + id;
    }

    @PostMapping("/permiso/{id}/eliminar")
    public String eliminarPermiso(@PathVariable Integer id,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {
        try {
            // Validar que el permiso existe
            PermisosDetalle permiso = permisoService.findById(id);
            if (permiso == null) {
                redirectAttributes.addFlashAttribute("error", "Permiso no encontrado");
                return "redirect:/admin/permisos";
            }

            // Eliminar lógicamente
            permisoService.deleteLogical(id);
            redirectAttributes.addFlashAttribute("success", "Permiso eliminado exitosamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el permiso: " + e.getMessage());
        }
        return "redirect:/admin/permisos";
    }

    @PostMapping("/permiso/{id}/reactivar")
    public String reactivarPermiso(@PathVariable Integer id,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) {
        try {
            PermisosDetalle permiso = permisoService.findById(id);
            if (permiso != null) {
                permiso.setActivo(true);
                permisoService.save(permiso);
                redirectAttributes.addFlashAttribute("success", "Permiso reactivado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Permiso no encontrado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al reactivar el permiso: " + e.getMessage());
        }
        return "redirect:/admin/permiso/" + id;
    }

    @GetMapping("/estadisticas")
    public String mostrarEstadisticas(Model model, HttpSession session) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuario != null) {
                model.addAttribute("adminNombre", usuario.getNombre() != null ? usuario.getNombre() : "Administrador");
            }

            List<PermisosDetalle> todosPermisos = permisoService.findAll();

            long totalPermisos = todosPermisos.size();
            long permisosPendientes = todosPermisos.stream()
                    .filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.PENDIENTE)
                    .count();
            long permisosAprobados = todosPermisos.stream()
                    .filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.APROBADO)
                    .count();
            long permisosRechazados = todosPermisos.stream()
                    .filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.RECHAZADO)
                    .count();
            long permisosImpresos = todosPermisos.stream()
                    .filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.IMPRESO)
                    .count();
            long permisosExpirados = todosPermisos.stream()
                    .filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.EXPIRADO)
                    .count();

            model.addAttribute("totalPermisos", totalPermisos);
            model.addAttribute("permisosAprobados", permisosAprobados);
            model.addAttribute("permisosPendientes", permisosPendientes);
            model.addAttribute("permisosRechazados", permisosRechazados);
            model.addAttribute("permisosImpresos", permisosImpresos);
            model.addAttribute("permisosExpirados", permisosExpirados);
            model.addAttribute("permisos", todosPermisos);

            if (totalPermisos > 0) {
                model.addAttribute("porcentajeAprobados", String.format("%.1f", ((double) permisosAprobados / totalPermisos) * 100));
                model.addAttribute("porcentajePendientes", String.format("%.1f", ((double) permisosPendientes / totalPermisos) * 100));
                model.addAttribute("porcentajeRechazados", String.format("%.1f", ((double) permisosRechazados / totalPermisos) * 100));
                model.addAttribute("porcentajeImpresos", String.format("%.1f", ((double) permisosImpresos / totalPermisos) * 100));
                model.addAttribute("porcentajeExpirados", String.format("%.1f", ((double) permisosExpirados / totalPermisos) * 100));

                double eficienciaSistema = ((double) (permisosAprobados + permisosRechazados) / totalPermisos) * 100;
                double tasaAprobacion = ((double) permisosAprobados / totalPermisos) * 100;
                double tasaRechazo = ((double) permisosRechazados / totalPermisos) * 100;

                model.addAttribute("eficienciaSistema", String.format("%.1f", eficienciaSistema));
                model.addAttribute("tasaAprobacion", String.format("%.1f", tasaAprobacion));
                model.addAttribute("tasaRechazo", String.format("%.1f", tasaRechazo));
            }

            return "admin-dashboard";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar las estadísticas: " + e.getMessage());
            return "admin-dashboard";
        }
    }
}