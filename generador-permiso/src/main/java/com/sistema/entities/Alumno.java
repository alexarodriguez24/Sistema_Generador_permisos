package com.itca.generadorpermiso.entities;
 
import jakarta.persistence.*;
 
@Entity
@Table(name = "alumno")
public class Alumno {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
 
    @Column(nullable = false, unique = true)
    private String carnet;
 
    @Column(nullable = false)
    private String nombre;
 
    @Column(nullable = false)
    private String apellido;
 
    @Column
    private String email;
 
    @ManyToOne
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;
 
    @Column
    private Boolean activo = true;
 
    // Constructores
    public Alumno() {}
 
    public Alumno(String carnet, String nombre, String apellido, String email) {
        this.carnet = carnet;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }
 
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
 
    public String getCarnet() { return carnet; }
    public void setCarnet(String carnet) { this.carnet = carnet; }
 
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
 
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
 
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
 
    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }
 
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
 
    @Override
    public String toString() {
        return nombre + " " + apellido + " (" + carnet + ")";
    }
}