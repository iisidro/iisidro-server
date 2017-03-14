package ar.edu.utn.frro.repository;

import ar.edu.utn.frro.domain.Seccion;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Seccion entity.
 */
@SuppressWarnings("unused")
public interface SeccionRepository extends JpaRepository<Seccion,Long> {

    @Query("SELECT s FROM Seccion s WHERE s.encuesta.id = :encuestaId")
    List<Seccion> findAllByEncuesta(@Param("encuestaId") Long encuestaId);

}
