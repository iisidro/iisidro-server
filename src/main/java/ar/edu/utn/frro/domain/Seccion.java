package ar.edu.utn.frro.domain;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Seccion.
 */
@Entity
@Table(name = "seccion")
public class Seccion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "orden")
    private Integer orden;

    @NotNull
    @Size(max = 20)
    @Column(name = "codigo", length = 20, nullable = false)
    private String codigo;

    @NotNull
    @Size(max = 255)
    @Column(name = "nombre", length = 255, nullable = false)
    private String nombre;

    @ManyToOne
    @NotNull
    @JoinColumn(name="seccion_encuesta_id")
    private Encuesta encuesta;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = "preguntas_secciones",
        joinColumns = {
            @JoinColumn(name = "seccion_id", nullable = false, updatable = false)
        },
        inverseJoinColumns = {
            @JoinColumn(name = "pregunta_id", nullable = false, updatable = false)
        }
    )
    private Set<Pregunta> preguntas = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Encuesta getEncuesta() {
        return encuesta;
    }

    public void setEncuesta(Encuesta encuesta) {
        this.encuesta = encuesta;
    }

    public Set<Pregunta> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(Set<Pregunta> preguntas) {
        this.preguntas = preguntas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Seccion seccion = (Seccion) o;
        if(seccion.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, seccion.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Seccion{" +
            "id=" + id +
            ", orden='" + orden + "'" +
            ", codigo='" + codigo + "'" +
            ", nombre='" + nombre + "'" +
            '}';
    }
}
