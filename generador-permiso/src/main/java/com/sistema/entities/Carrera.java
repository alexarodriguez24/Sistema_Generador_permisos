package com.itca.generadorpermiso.entities;
 
import jakarta.persistence.*;
 
@Entity
@Table(name = "carrera")
public class Carrera {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
 
    @Column(nullable = false)
    private String nombre;
 
    @Column(nullable = false, unique = true, length = 30)
    private String codigo;
 
    // Constructores
    public Carrera() {}
 
    public Carrera(String nombre, String codigo) {
        this.nombre = nombre;
        this.codigo = codigo;
    }
 
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
 
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
 
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
 
    @Override
    public String toString() {
        return nombre + " (" + codigo + ")";
    }
}
 