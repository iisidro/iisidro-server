package ar.edu.utn.frro.repository;

import ar.edu.utn.frro.domain.Pregunta;

import ar.edu.utn.frro.domain.Seccion;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Pregunta entity.
 */
@SuppressWarnings("unused")
public interface PreguntaRepository extends JpaRepository<Pregunta,Long> {
    @Query("SELECT p FROM Pregunta p WHERE p.seccion.id = :seccionId")
    List<Pregunta> findAllBySeccion(@Param("seccionId") Long id);
}
