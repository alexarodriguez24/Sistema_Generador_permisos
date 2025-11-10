package com.itca.generadorpermiso.entities;
 
import jakarta.persistence.*;
 
@Entity
@Table(name = "permiso_tipo")
public class PermisoTipo {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
 
    @Column(nullable = false)
    private String nombre;
 
    @Column(columnDefinition = "TEXT")
    private String descripcion;
 
    @Column(name = "requiere_comprobante")
    private Boolean requiereComprobante = true;
 
    @Column
    private Boolean activo = true;
 
    // Constructores
    public PermisoTipo() {}
 
    public PermisoTipo(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
 
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
 
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
 
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
 
    public Boolean getRequiereComprobante() { return requiereComprobante; }
    public void setRequiereComprobante(Boolean requiereComprobante) { this.requiereComprobante = requiereComprobante; }
 
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
 
    @Override
    public String toString() {
        return nombre;
    }
}
 