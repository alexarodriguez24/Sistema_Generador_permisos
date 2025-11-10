package com.itca.generadorpermiso.entities;
 
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "permisos_detalle")
public class PermisosDetalle {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_permiso_id", nullable = false)
    private PermisoTipo tipoPermiso;
 
    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud = LocalDateTime.now();
 
    @Column(name = "fecha_inicio_permiso", nullable = false)
    private LocalDate fechaInicioPermiso;
 
    @Column(name = "fecha_fin_permiso", nullable = false)
    private LocalDate fechaFinPermiso;
 
    @Column(name = "motivo", columnDefinition = "TEXT", nullable = false)
    private String motivo;
 
    @Column(name = "comprobante_adjunto")
    private Boolean comprobanteAdjunto = false;
 
    @Column(name = "ruta_comprobante", length = 500)
    private String rutaComprobante;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoPermiso estado = EstadoPermiso.PENDIENTE;
 
    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aprobado_por")
    private Docente aprobadoPor;
 
    @Column(name = "comentarios_aprobador", columnDefinition = "TEXT")
    private String comentariosAprobador;
 
    @Column(name = "fecha_impresion")
    private LocalDateTime fechaImpresion;
 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "impreso_por")
    private Docente impresoPor;
 
    @Column(name = "activo")
    private Boolean activo = true;
 
    // Campos para el comprobante en base de datos
    @Lob
    @Column(name = "comprobante_imagen", columnDefinition = "LONGBLOB")
    private byte[] comprobanteImagen;
 
    @Column(name = "comprobante_tipo")
    private String comprobanteTipo; // "image/jpeg", "image/png", etc.
 
    @Column(name = "nombre_archivo_comprobante")
    private String nombreArchivoComprobante;
 
    // Enum para estados del permiso
    public enum EstadoPermiso {
        PENDIENTE, APROBADO, RECHAZADO, EXPIRADO, IMPRESO
    }
 
    // Constructores
    public PermisosDetalle() {}
 
    public PermisosDetalle(Alumno alumno, PermisoTipo tipoPermiso, LocalDate fechaInicioPermiso,
                           LocalDate fechaFinPermiso, String motivo) {
        this.alumno = alumno;
        this.tipoPermiso = tipoPermiso;
        this.fechaInicioPermiso = fechaInicioPermiso;
        this.fechaFinPermiso = fechaFinPermiso;
        this.motivo = motivo;
    }
 
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
 
    public Alumno getAlumno() { return alumno; }
    public void setAlumno(Alumno alumno) { this.alumno = alumno; }
 
    public PermisoTipo getTipoPermiso() { return tipoPermiso; }
    public void setTipoPermiso(PermisoTipo tipoPermiso) { this.tipoPermiso = tipoPermiso; }
 
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
 
    public LocalDate getFechaInicioPermiso() { return fechaInicioPermiso; }
    public void setFechaInicioPermiso(LocalDate fechaInicioPermiso) { this.fechaInicioPermiso = fechaInicioPermiso; }
 
    public LocalDate getFechaFinPermiso() { return fechaFinPermiso; }
    public void setFechaFinPermiso(LocalDate fechaFinPermiso) { this.fechaFinPermiso = fechaFinPermiso; }
 
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
 
    public Boolean getComprobanteAdjunto() { return comprobanteAdjunto; }
    public void setComprobanteAdjunto(Boolean comprobanteAdjunto) { this.comprobanteAdjunto = comprobanteAdjunto; }
 
    public String getRutaComprobante() { return rutaComprobante; }
    public void setRutaComprobante(String rutaComprobante) { this.rutaComprobante = rutaComprobante; }
 
    public EstadoPermiso getEstado() { return estado; }
    public void setEstado(EstadoPermiso estado) { this.estado = estado; }
 
    public LocalDateTime getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(LocalDateTime fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }
 
    public Docente getAprobadoPor() { return aprobadoPor; }
    public void setAprobadoPor(Docente aprobadoPor) { this.aprobadoPor = aprobadoPor; }
 
    public String getComentariosAprobador() { return comentariosAprobador; }
    public void setComentariosAprobador(String comentariosAprobador) { this.comentariosAprobador = comentariosAprobador; }
 
    public LocalDateTime getFechaImpresion() { return fechaImpresion; }
    public void setFechaImpresion(LocalDateTime fechaImpresion) { this.fechaImpresion = fechaImpresion; }
 
    public Docente getImpresoPor() { return impresoPor; }
    public void setImpresoPor(Docente impresoPor) { this.impresoPor = impresoPor; }
 
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
 
    // Getters y Setters para comprobante
    public byte[] getComprobanteImagen() {
        return comprobanteImagen;
    }
 
    public void setComprobanteImagen(byte[] comprobanteImagen) {
        this.comprobanteImagen = comprobanteImagen;
    }
 
    public String getComprobanteTipo() {
        return comprobanteTipo;
    }
 
    public void setComprobanteTipo(String comprobanteTipo) {
        this.comprobanteTipo = comprobanteTipo;
    }
 
    public String getNombreArchivoComprobante() {
        return nombreArchivoComprobante;
    }
 
    public void setNombreArchivoComprobante(String nombreArchivoComprobante) {
        this.nombreArchivoComprobante = nombreArchivoComprobante;
    }
 
    // Métodos seguros para el template
    public String getNombreAlumnoCompleto() {
        return alumno != null ? alumno.getNombre() + " " + alumno.getApellido() : "Alumno no disponible";
    }
 
    public String getCarnetAlumno() {
        return alumno != null ? alumno.getCarnet() : "N/A";
    }
 
    public String getNombreTipoPermiso() {
        return tipoPermiso != null ? tipoPermiso.getNombre() : "Tipo no disponible";
    }
 
    @Override
    public String toString() {
        return "PermisosDetalle{" +
                "id=" + id +
                ", alumno=" + alumno +
                ", tipoPermiso=" + tipoPermiso +
                ", fechaSolicitud=" + fechaSolicitud +
                ", fechaInicioPermiso=" + fechaInicioPermiso +
                ", fechaFinPermiso=" + fechaFinPermiso +
                ", motivo='" + motivo + '\'' +
                ", estado=" + estado +
                '}';
    }
}