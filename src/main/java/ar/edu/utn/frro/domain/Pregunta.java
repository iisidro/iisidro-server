package ar.edu.utn.frro.domain;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Pregunta.
 */
@Entity
@Table(name = "pregunta")
public class Pregunta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "informacion")
    private String informacion;

    @ManyToOne
    private TipoPregunta tipo;

    @Column(name = "fecha_hora_creacion")
    private Date fechaHoraCreacion;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "preguntas")
    private Set<Seccion> secciones = new HashSet<>();

    @NotNull
    @Column(name = "requerida", nullable = false)
    private Boolean requerida = false;

    public Pregunta() {
        // this can be modified by a DatetimeUtils and change by timezone and such
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

    public String getInformacion() {
        return informacion;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    public TipoPregunta getTipo() {
        return tipo;
    }

    public void setTipo(TipoPregunta tipoPregunta) {
        this.tipo = tipoPregunta;
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

    public Boolean getRequerida() {
        return requerida;
    }

    public void setRequerida(Boolean requerida) {
        this.requerida = requerida;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pregunta pregunta = (Pregunta) o;
        if (pregunta.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, pregunta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Pregunta{" +
            "id=" + id +
            ", nombre='" + nombre + "'" +
            ", informacion='" + informacion + "'" +
            '}';
    }
}
