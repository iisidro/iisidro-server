package ar.edu.utn.frro.web.rest;

import com.codahale.metrics.annotation.Timed;
import ar.edu.utn.frro.domain.Encuesta;
import ar.edu.utn.frro.repository.EncuestaRepository;
import ar.edu.utn.frro.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Encuesta.
 */
@RestController
@RequestMapping("/api")
public class EncuestaResource {

    private final Logger log = LoggerFactory.getLogger(EncuestaResource.class);
        
    @Inject
    private EncuestaRepository encuestaRepository;
    
    /**
     * POST  /encuestas : Create a new encuesta.
     *
     * @param encuesta the encuesta to create
     * @return the ResponseEntity with status 201 (Created) and with body the new encuesta, or with status 400 (Bad Request) if the encuesta has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/encuestas",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Encuesta> createEncuesta(@Valid @RequestBody Encuesta encuesta) throws URISyntaxException {
        log.debug("REST request to save Encuesta : {}", encuesta);
        if (encuesta.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("encuesta", "idexists", "A new encuesta cannot already have an ID")).body(null);
        }
        Encuesta result = encuestaRepository.save(encuesta);
        return ResponseEntity.created(new URI("/api/encuestas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("encuesta", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /encuestas : Updates an existing encuesta.
     *
     * @param encuesta the encuesta to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated encuesta,
     * or with status 400 (Bad Request) if the encuesta is not valid,
     * or with status 500 (Internal Server Error) if the encuesta couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/encuestas",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Encuesta> updateEncuesta(@Valid @RequestBody Encuesta encuesta) throws URISyntaxException {
        log.debug("REST request to update Encuesta : {}", encuesta);
        if (encuesta.getId() == null) {
            return createEncuesta(encuesta);
        }
        Encuesta result = encuestaRepository.save(encuesta);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("encuesta", encuesta.getId().toString()))
            .body(result);
    }

    /**
     * GET  /encuestas : get all the encuestas.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of encuestas in body
     */
    @RequestMapping(value = "/encuestas",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Encuesta> getAllEncuestas() {
        log.debug("REST request to get all Encuestas");
        List<Encuesta> encuestas = encuestaRepository.findAll();
        return encuestas;
    }

    /**
     * GET  /encuestas/:id : get the "id" encuesta.
     *
     * @param id the id of the encuesta to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the encuesta, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/encuestas/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Encuesta> getEncuesta(@PathVariable Long id) {
        log.debug("REST request to get Encuesta : {}", id);
        Encuesta encuesta = encuestaRepository.findOne(id);
        return Optional.ofNullable(encuesta)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /encuestas/:id : delete the "id" encuesta.
     *
     * @param id the id of the encuesta to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/encuestas/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteEncuesta(@PathVariable Long id) {
        log.debug("REST request to delete Encuesta : {}", id);
        encuestaRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("encuesta", id.toString())).build();
    }

}
