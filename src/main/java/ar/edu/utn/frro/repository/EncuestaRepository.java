package ar.edu.utn.frro.repository;

import ar.edu.utn.frro.domain.Encuesta;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Encuesta entity.
 */
@SuppressWarnings("unused")
public interface EncuestaRepository extends JpaRepository<Encuesta,Long> {

}
