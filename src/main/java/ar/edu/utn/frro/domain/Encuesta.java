package ar.edu.utn.frro.domain;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = "secciones_encuestas",
        joinColumns = {
            @JoinColumn(name = "encuesta_id", nullable = false, updatable = false)
        },
        inverseJoinColumns = {
            @JoinColumn(name = "seccion_id", nullable = false, updatable = false)
        }
    )
    private Set<Seccion> secciones = new HashSet<>();

    public Encuesta() {
        // this can be modified to use different server date timezone as needed
        this.fechaHoraCreacion = new Date();
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

    public Set<Seccion> getSecciones() {
        return secciones;
    }

    public void setSecciones(Set<Seccion> secciones) {
        this.secciones = secciones;
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
