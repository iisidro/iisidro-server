package ar.edu.utn.frro.web.rest;

import com.codahale.metrics.annotation.Timed;
import ar.edu.utn.frro.domain.TipoPregunta;
import ar.edu.utn.frro.repository.TipoPreguntaRepository;
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
 * REST controller for managing TipoPregunta.
 */
@RestController
@RequestMapping("/api")
public class TipoPreguntaResource {

    private final Logger log = LoggerFactory.getLogger(TipoPreguntaResource.class);
        
    @Inject
    private TipoPreguntaRepository tipoPreguntaRepository;
    
    /**
     * POST  /tipo-preguntas : Create a new tipoPregunta.
     *
     * @param tipoPregunta the tipoPregunta to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tipoPregunta, or with status 400 (Bad Request) if the tipoPregunta has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/tipo-preguntas",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoPregunta> createTipoPregunta(@Valid @RequestBody TipoPregunta tipoPregunta) throws URISyntaxException {
        log.debug("REST request to save TipoPregunta : {}", tipoPregunta);
        if (tipoPregunta.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("tipoPregunta", "idexists", "A new tipoPregunta cannot already have an ID")).body(null);
        }
        TipoPregunta result = tipoPreguntaRepository.save(tipoPregunta);
        return ResponseEntity.created(new URI("/api/tipo-preguntas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("tipoPregunta", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tipo-preguntas : Updates an existing tipoPregunta.
     *
     * @param tipoPregunta the tipoPregunta to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tipoPregunta,
     * or with status 400 (Bad Request) if the tipoPregunta is not valid,
     * or with status 500 (Internal Server Error) if the tipoPregunta couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/tipo-preguntas",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoPregunta> updateTipoPregunta(@Valid @RequestBody TipoPregunta tipoPregunta) throws URISyntaxException {
        log.debug("REST request to update TipoPregunta : {}", tipoPregunta);
        if (tipoPregunta.getId() == null) {
            return createTipoPregunta(tipoPregunta);
        }
        TipoPregunta result = tipoPreguntaRepository.save(tipoPregunta);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("tipoPregunta", tipoPregunta.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tipo-preguntas : get all the tipoPreguntas.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of tipoPreguntas in body
     */
    @RequestMapping(value = "/tipo-preguntas",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<TipoPregunta> getAllTipoPreguntas() {
        log.debug("REST request to get all TipoPreguntas");
        List<TipoPregunta> tipoPreguntas = tipoPreguntaRepository.findAll();
        return tipoPreguntas;
    }

    /**
     * GET  /tipo-preguntas/:id : get the "id" tipoPregunta.
     *
     * @param id the id of the tipoPregunta to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tipoPregunta, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/tipo-preguntas/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoPregunta> getTipoPregunta(@PathVariable Long id) {
        log.debug("REST request to get TipoPregunta : {}", id);
        TipoPregunta tipoPregunta = tipoPreguntaRepository.findOne(id);
        return Optional.ofNullable(tipoPregunta)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /tipo-preguntas/:id : delete the "id" tipoPregunta.
     *
     * @param id the id of the tipoPregunta to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/tipo-preguntas/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTipoPregunta(@PathVariable Long id) {
        log.debug("REST request to delete TipoPregunta : {}", id);
        tipoPreguntaRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("tipoPregunta", id.toString())).build();
    }

}
