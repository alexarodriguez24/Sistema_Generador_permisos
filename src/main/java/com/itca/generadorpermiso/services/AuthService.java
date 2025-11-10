package com.itca.generadorpermiso.services;

import com.itca.generadorpermiso.entities.Usuario;
import com.itca.generadorpermiso.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    public Usuario findByCarnet(String carnet) {
        return usuarioRepository.findByCarnet(carnet);
    }

    public Usuario findByCodigoEmpleado(String codigo) {
        return usuarioRepository.findByCodigoEmpleado(codigo);
    }

    public boolean validarCredenciales(String email, String password) {
        if (!"itca".equals(password)) {
            return false;
        }

        Usuario usuario = findByEmail(email);
        return usuario != null && usuario.isActivo();
    }

    // Nuevo comportamiento: aceptar carnets alfanuméricos; distinguir email por '@'
    public Usuario autenticarPorIdentificador(String identificador, String password) {
        if (identificador == null || password == null) {
            return null;
        }

        if (!"itca".equals(password)) {
            return null;
        }

        identificador = identificador.trim();

        // Si contiene '@' se considera correo -> flow de email (admin/colaborador/copias)
        if (identificador.contains("@")) {
            Usuario usuario = findByEmail(identificador);
            if (usuario != null && usuario.isActivo()) {
                Usuario.Rol rol = usuario.getRol();
                if (rol == Usuario.Rol.ADMIN || rol == Usuario.Rol.COLABORADOR || rol == Usuario.Rol.COPIAS) {
                    return usuario;
                }
            }
            return null;
        }

        // Si no contiene '@', admitir carnets alfanuméricos (letras y/o dígitos)
        if (identificador.matches("(?i)[a-z0-9]+")) {
            Usuario usuario = findByCarnet(identificador);
            if (usuario != null && usuario.isActivo() && usuario.getRol() == Usuario.Rol.ESTUDIANTE) {
                return usuario;
            }
            return null;
        }

        // Otros formatos no permitidos
        return null;
    }
}