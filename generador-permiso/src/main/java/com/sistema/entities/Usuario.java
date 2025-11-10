package com.itca.generadorpermiso.entities;
 
import jakarta.persistence.*;
 
@Entity
@Table(name = "usuarios")
public class Usuario {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
 
    @Column(nullable = false, unique = true)
    private String email;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
 
    @Column(nullable = false)
    private boolean activo = true;
 
    @ManyToOne
    @JoinColumn(name = "alumno_id")
    private Alumno alumno;
 
    @ManyToOne
    @JoinColumn(name = "docente_id")
    private Docente docente;
 
    public enum Rol {
        ADMIN, COPIAS, COLABORADOR, ESTUDIANTE
    }
 
    // Constructores
    public Usuario() {}
 
    public Usuario(String email, Rol rol) {
        this.email = email;
        this.rol = rol;
    }
 
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
 
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
 
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
 
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
 
    public Alumno getAlumno() { return alumno; }
    public void setAlumno(Alumno alumno) { this.alumno = alumno; }
 
    public Docente getDocente() { return docente; }
    public void setDocente(Docente docente) { this.docente = docente; }
 
    // Nombre legible para mostrar en vistas (Alumno > Docente > Email)
    public String getNombre() {
        if (alumno != null) {
            return alumno.getNombre() + " " + alumno.getApellido();
        }
        if (docente != null) {
            return docente.getNombre() + " " + docente.getApellido();
        }
        return this.email != null ? this.email : "Usuario";
    }
}
 