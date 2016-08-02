package ar.edu.utn.frro.repository;

import ar.edu.utn.frro.domain.Pregunta;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Pregunta entity.
 */
@SuppressWarnings("unused")
public interface PreguntaRepository extends JpaRepository<Pregunta,Long> {

}
