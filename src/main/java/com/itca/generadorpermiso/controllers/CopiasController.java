package com.itca.generadorpermiso.controllers;

import com.itca.generadorpermiso.entities.PermisosDetalle;
import com.itca.generadorpermiso.entities.PermisosDetalle.EstadoPermiso;
import com.itca.generadorpermiso.entities.Usuario;
import com.itca.generadorpermiso.services.PermisoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/copias")
public class CopiasController {

    @Autowired
    private PermisoService permisoService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        // Datos del usuario de copias (tomados desde sesión si existe)
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            model.addAttribute("copiasEmail", usuario.getEmail());
            model.addAttribute("copiasNombre", usuario.getNombre() != null ? usuario.getNombre() : "Departamento de Copias");
        } else {
            model.addAttribute("copiasEmail", "copias@itca.edu.sv");
            model.addAttribute("copiasNombre", "Ana Gómez");
        }

        // Estadísticas para el dashboard de copias
        List<PermisosDetalle> permisosPendientes = permisoService.findByEstado(EstadoPermiso.APROBADO);
        List<PermisosDetalle> permisosImpresos = permisoService.findByEstado(EstadoPermiso.IMPRESO);

        model.addAttribute("pendientesCount", permisosPendientes.size());
        model.addAttribute("impresosCount", permisosImpresos.size());
        model.addAttribute("permisosPendientes", permisosPendientes);

        model.addAttribute("userRole", "COPIAS");
        model.addAttribute("dashboardUrl", "/copias/dashboard");
        model.addAttribute("solicitarUrl", "/copias/dashboard");

        return "copias-dashboard";
    }

    @GetMapping("/pendientes")
    public String copiasPendientes(Model model) {
        List<PermisosDetalle> permisosPendientes = permisoService.findByEstado(EstadoPermiso.APROBADO);
        model.addAttribute("permisos", permisosPendientes);
        model.addAttribute("userRole", "COPIAS");
        model.addAttribute("dashboardUrl", "/copias/dashboard");
        return "copias-dashboard";
    }

    @GetMapping("/imprimir/{id}")
    public String imprimirPermiso(@PathVariable Integer id, Model model, HttpSession session) {
        PermisosDetalle permiso = permisoService.findById(id);
        if (permiso != null) {
            // Solo marcar como impreso si está aprobado
            if (permiso.getEstado() == PermisosDetalle.EstadoPermiso.APROBADO) {
                Usuario usuario = (Usuario) session.getAttribute("usuario");
                com.itca.generadorpermiso.entities.Docente docente = usuario != null ? usuario.getDocente() : null;
                permisoService.marcarComoImpreso(id, docente);
                model.addAttribute("mensajeImpresion", "Permiso marcado como IMPRESO exitosamente");
            }
            model.addAttribute("permiso", permiso);
            model.addAttribute("accion", "imprimir");

            // datos de session
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuario != null) {
                model.addAttribute("copiasNombre", usuario.getNombre());
                model.addAttribute("copiasEmail", usuario.getEmail());
            }

            model.addAttribute("userRole", "COPIAS");
            model.addAttribute("dashboardUrl", "/copias/dashboard");
            return "comprobante";
        }
        return "redirect:/copias/pendientes";
    }

    @GetMapping("/generar/{id}")
    public String generarCopia(@PathVariable Integer id, Model model, HttpSession session) {
        PermisosDetalle permiso = permisoService.findById(id);
        if (permiso != null) {
            model.addAttribute("permiso", permiso);
            model.addAttribute("accion", "vista-previa");
            model.addAttribute("mensajeImpresion", "Vista previa - NO se ha marcado como impreso");

            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuario != null) {
                model.addAttribute("copiasNombre", usuario.getNombre());
                model.addAttribute("copiasEmail", usuario.getEmail());
            }

            model.addAttribute("userRole", "COPIAS");
            model.addAttribute("dashboardUrl", "/copias/dashboard");
            return "comprobante";
        }
        return "redirect:/copias/pendientes";
    }

    @GetMapping("/historial")
    public String historialCopias(Model model) {
        List<PermisosDetalle> permisosImpresos = permisoService.findByEstado(EstadoPermiso.IMPRESO);
        model.addAttribute("permisos", permisosImpresos);

        // Estadísticas para la vista
        long totalPermisos = permisosImpresos.size();
        long permisosAprobados = permisosImpresos.stream().filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.APROBADO).count();
        long permisosPendientes = permisosImpresos.stream().filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.PENDIENTE).count();
        long permisosRechazados = permisosImpresos.stream().filter(p -> p.getEstado() == PermisosDetalle.EstadoPermiso.RECHAZADO).count();

        model.addAttribute("totalPermisos", totalPermisos);
        model.addAttribute("permisosAprobados", permisosAprobados);
        model.addAttribute("permisosPendientes", permisosPendientes);
        model.addAttribute("permisosRechazados", permisosRechazados);

        model.addAttribute("userRole", "COPIAS");
        model.addAttribute("dashboardUrl", "/copias/dashboard");
        model.addAttribute("solicitarUrl", "/copias/dashboard");
        return "historial-permisos";
    }


}