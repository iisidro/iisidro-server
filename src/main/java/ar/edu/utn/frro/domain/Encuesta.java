package ar.edu.utn.frro.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * A Encuesta.
 */
@Entity
@Table(name = "encuesta")
public class Encuesta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "fecha_hora_creacion")
    private Date fechaHoraCreacion;

    @Column(name = "fecha_hora_desde")
    private Date fechaHoraDesde;

    @Column(name = "fecha_hora_hasta")
    private Date fechaHoraHasta;

    private enum surveyStatus {
        REVISION, ACTIVA, CERRADA
    }

    @Column(name = "estado_encuesta")
    surveyStatus estado;

    public Encuesta() {
        // this can be modified to use different server date timezone as needed
        this.fechaHoraCreacion = new Date();
        this.estado = surveyStatus.REVISION;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaHoraCreacion() {
        return fechaHoraCreacion;
    }

    public void setFechaHoraCreacion(Date fechaHoraCreacion) {
        this.fechaHoraCreacion = fechaHoraCreacion;
    }

    public surveyStatus getEstado() { return estado; }

    public void setEstado(surveyStatus estado) { this.estado = estado; }

    public Date getFechaHoraDesde() { return fechaHoraDesde; }

    public void setFechaHoraDesde(Date fechaHoraDesde) { this.fechaHoraDesde = fechaHoraDesde; }

    public Date getFechaHoraHasta() { return fechaHoraHasta; }

    public void setFechaHoraHasta(Date fechaHoraHasta) {
        this.fechaHoraHasta = fechaHoraHasta;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Encuesta encuesta = (Encuesta) o;
        if(encuesta.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, encuesta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Encuesta{" +
            "id=" + id +
            ", nombre='" + nombre + "'" +
            '}';
    }
}
