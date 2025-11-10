package com.itca.generadorpermiso.entities;
 
import jakarta.persistence.*;
 
@Entity
@Table(name = "docente")
public class Docente {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
 
    @Column(name = "codigo_empleado", nullable = false, unique = true)
    private String codigoEmpleado;
 
    @Column(name = "nom_usuario", nullable = false)
    private String nombre;
 
    @Column(name = "ape_usuario", nullable = false)
    private String apellido;
 
    @Column(name = "id_depto", length = 5)
    private String departamento;
 
    @Column
    private String email;
 
    @Column
    private String estado = "ACTIVO";
 
    // Constructores
    public Docente() {}
 
    public Docente(String codigoEmpleado, String nombre, String apellido, String email) {
        this.codigoEmpleado = codigoEmpleado;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }
 
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
 
    public String getCodigoEmpleado() { return codigoEmpleado; }
    public void setCodigoEmpleado(String codigoEmpleado) { this.codigoEmpleado = codigoEmpleado; }
 
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
 
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
 
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
 
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
 
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
 
    @Override
    public String toString() {
        return nombre + " " + apellido;
    }
}