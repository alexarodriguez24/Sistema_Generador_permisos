package com.itca.generadorpermiso.services;

import com.itca.generadorpermiso.entities.PermisoTipo;
import com.itca.generadorpermiso.repositories.PermisoTipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermisoTipoService {

    @Autowired
    private PermisoTipoRepository permisoTipoRepository;

    public List<PermisoTipo> findAll() {
        return permisoTipoRepository.findAll();
    }

    public PermisoTipo findById(Integer id) {
        Optional<PermisoTipo> tipo = permisoTipoRepository.findById(id);
        return tipo.orElse(null);
    }
}