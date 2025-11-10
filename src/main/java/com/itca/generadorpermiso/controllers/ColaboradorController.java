package com.itca.generadorpermiso.controllers;

import com.itca.generadorpermiso.entities.PermisosDetalle;
import com.itca.generadorpermiso.entities.PermisosDetalle.EstadoPermiso;
import com.itca.generadorpermiso.entities.Usuario;
import com.itca.generadorpermiso.services.PermisoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
@RequestMapping("/colaborador")
public class ColaboradorController {
    @Autowired
    private PermisoService permisoService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        // Estadísticas para el colaborador
        long permisosAprobados = permisoService.findByEstado(EstadoPermiso.APROBADO).size();
        long permisosPendientes = permisoService.findByEstado(EstadoPermiso.PENDIENTE).size();
        long permisosRechazados = permisoService.findByEstado(EstadoPermiso.RECHAZADO).size();
        long totalPermisos = permisoService.findAll().size();

        model.addAttribute("permisosAprobados", permisosAprobados);
        model.addAttribute("permisosPendientes", permisosPendientes);
        model.addAttribute("permisosRechazados", permisosRechazados);
        model.addAttribute("totalPermisos", totalPermisos);

        // Datos de sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            model.addAttribute("colaboradorNombre", usuario.getNombre());
            model.addAttribute("colaboradorEmail", usuario.getEmail());
        }

        model.addAttribute("userRole", "COLABORADOR");
        model.addAttribute("dashboardUrl", "/colaborador/dashboard");
        model.addAttribute("solicitarUrl", "/colaborador/dashboard");

        return "colaborador-dashboard";
    }

    @GetMapping("/permisos")
    public String listarPermisos(Model model) {
        List<PermisosDetalle> permisos = permisoService.findAll();
        model.addAttribute("permisos", permisos);

        // Estadísticas para la vista
        long totalPermisos = permisos.size();
        long permisosAprobados = permisos.stream().filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.APROBADO).count();
        long permisosPendientes = permisos.stream().filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.PENDIENTE).count();
        long permisosRechazados = permisos.stream().filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.RECHAZADO).count();

        model.addAttribute("totalPermisos", totalPermisos);
        model.addAttribute("permisosAprobados", permisosAprobados);
        model.addAttribute("permisosPendientes", permisosPendientes);
        model.addAttribute("permisosRechazados", permisosRechazados);

        model.addAttribute("userRole", "COLABORADOR");
        model.addAttribute("dashboardUrl", "/colaborador/dashboard");
        model.addAttribute("solicitarUrl", "/colaborador/dashboard");
        return "historial-permisos"; // Apunta a historial-permisos.html
    }

    @GetMapping("/permisos-aprobados")
    @ResponseBody
    public List<PermisosDetalle> obtenerPermisosAprobados() {
        return permisoService.findByEstado(EstadoPermiso.APROBADO);
    }

    @GetMapping("/contar-aprobados")
    @ResponseBody
    public long contarPermisosAprobados() {
        return permisoService.findByEstado(EstadoPermiso.APROBADO).size();
    }

    @GetMapping("/permiso/{id}")
    public String verPermiso(@PathVariable Integer id, Model model) {
        PermisosDetalle permiso = permisoService.findById(id);
        if (permiso == null) {
            return "redirect:/colaborador/permisos";
        }
        model.addAttribute("permiso", permiso);
        model.addAttribute("userRole", "COLABORADOR");
        model.addAttribute("dashboardUrl", "/colaborador/dashboard");
        return "detalle-permiso";
    }

    @PostMapping("/permiso/{id}/aprobar")
    public String aprobarPermiso(@PathVariable Integer id,
                                 @RequestParam String comentarios,
                                 RedirectAttributes redirectAttributes,
                                 HttpSession session) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            com.itca.generadorpermiso.entities.Docente docente = usuario != null ? usuario.getDocente() : null;
            PermisosDetalle permisoActualizado = permisoService.aprobarPermiso(id, docente, comentarios);
            if (permisoActualizado != null && permisoActualizado.getEstado() == PermisosDetalle.EstadoPermiso.APROBADO) {
                redirectAttributes.addFlashAttribute("success", "Permiso #" + id + " aprobado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Hubo un problema al aprobar el permiso");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al aprobar el permiso: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/colaborador/dashboard";
    }

    @PostMapping("/permiso/{id}/rechazar")
    public String rechazarPermiso(@PathVariable Integer id,
                                  @RequestParam String comentarios,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            com.itca.generadorpermiso.entities.Docente docente = usuario != null ? usuario.getDocente() : null;
            PermisosDetalle permisoActualizado = permisoService.rechazarPermiso(id, docente, comentarios);
            if (permisoActualizado != null && permisoActualizado.getEstado() == PermisosDetalle.EstadoPermiso.RECHAZADO) {
                redirectAttributes.addFlashAttribute("success", "Permiso #" + id + " rechazado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Hubo un problema al rechazar el permiso");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al rechazar el permiso: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/colaborador/dashboard";
    }

    @GetMapping("/permisos/estado/{estado}")
    public String filtrarPorEstado(@PathVariable String estado, Model model) {
        EstadoPermiso estadoPermiso = EstadoPermiso.valueOf(estado.toUpperCase());
        List<PermisosDetalle> permisos = permisoService.findByEstado(estadoPermiso);
        model.addAttribute("permisos", permisos);
        model.addAttribute("estadoFiltro", estadoPermiso);
        model.addAttribute("userRole", "COLABORADOR");
        model.addAttribute("dashboardUrl", "/colaborador/dashboard");
        model.addAttribute("solicitarUrl", "/colaborador/dashboard");
        return "historial-permisos";
    }
}