package ar.edu.utn.frro.repository;

import ar.edu.utn.frro.domain.TipoPregunta;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the TipoPregunta entity.
 */
@SuppressWarnings("unused")
public interface TipoPreguntaRepository extends JpaRepository<TipoPregunta,Long> {

}
