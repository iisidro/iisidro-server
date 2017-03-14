package ar.edu.utn.frro.web.rest;

import ar.edu.utn.frro.Application;
import ar.edu.utn.frro.domain.Encuesta;
import ar.edu.utn.frro.domain.Pregunta;
import ar.edu.utn.frro.domain.Seccion;
import ar.edu.utn.frro.domain.TipoPregunta;
import ar.edu.utn.frro.repository.EncuestaRepository;
import ar.edu.utn.frro.repository.PreguntaRepository;
import ar.edu.utn.frro.repository.SeccionRepository;
import ar.edu.utn.frro.repository.TipoPreguntaRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the PreguntaResource REST controller.
 *
 * @see PreguntaResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PreguntaResourceIntTest {

    private static final String DEFAULT_NOMBRE = "AAAAA";
    private static final String UPDATED_NOMBRE = "BBBBB";
    private static final String DEFAULT_INFORMACION = "AAAAA";
    private static final String UPDATED_INFORMACION = "BBBBB";

    private static final String TIPO_PREGUNTA_NOMBRE = "Mocked";

    private static final String SECCION_PREGUNTA_NOMBRE = "Mocked";
    private static final String SECCION_PREGUNTA_CODIGO = "Mocked";

    @Inject
    private PreguntaRepository preguntaRepository;

    @Inject
    private TipoPreguntaRepository tipoPreguntaRepository;

    @Inject
    private SeccionRepository seccionRepository;

    @Inject
    private EncuestaRepository encuestaRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPreguntaMockMvc;

    private Pregunta pregunta;

    private TipoPregunta tipoPregunta;

    private Seccion seccionPregunta;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PreguntaResource preguntaResource = new PreguntaResource();
        ReflectionTestUtils.setField(preguntaResource, "preguntaRepository", preguntaRepository);
        ReflectionTestUtils.setField(preguntaResource, "seccionRepository", seccionRepository);
        this.restPreguntaMockMvc = MockMvcBuilders.standaloneSetup(preguntaResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        tipoPregunta = new TipoPregunta();
        tipoPregunta.setNombre(TIPO_PREGUNTA_NOMBRE);
        tipoPreguntaRepository.save(tipoPregunta);

        Encuesta encuesta = new Encuesta();
        encuesta.setNombre("Un Numbre");
        encuesta = encuestaRepository.save(encuesta);

        seccionPregunta = new Seccion();
        seccionPregunta.setNombre(SECCION_PREGUNTA_NOMBRE);
        seccionPregunta.setCodigo(SECCION_PREGUNTA_CODIGO);
        seccionPregunta.setOrden(1);
        seccionPregunta.setEncuesta(encuesta);
        seccionPregunta = seccionRepository.save(seccionPregunta);
        assertThat(seccionPregunta.getId()).isNotNull();
    }

    @Before
    public void initTest() {
        pregunta = new Pregunta();
        pregunta.setNombre(DEFAULT_NOMBRE);
        pregunta.setInformacion(DEFAULT_INFORMACION);
        pregunta.setTipo(tipoPregunta);
    }

    @Test
    @Transactional
    public void createPregunta() throws Exception {
        Pregunta testPregunta = createPregunta(pregunta);
        assertThat(testPregunta.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testPregunta.getInformacion()).isEqualTo(DEFAULT_INFORMACION);

        assertThat(testPregunta.getTipo()).isNotNull();
        assertThat(testPregunta.getTipo().getNombre()).isEqualTo(TIPO_PREGUNTA_NOMBRE);
    }

    @Test
    @Transactional
    public void createPreguntaWithNoSeccion() throws Exception {
        Pregunta testPregunta = createPregunta(pregunta);
        assertThat(testPregunta.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testPregunta.getInformacion()).isEqualTo(DEFAULT_INFORMACION);

        assertThat(testPregunta.getTipo()).isNotNull();
        assertThat(testPregunta.getTipo().getNombre()).isEqualTo(TIPO_PREGUNTA_NOMBRE);
    }

    private Pregunta createPregunta(Pregunta pregunta) throws Exception {
        int databaseSizeBeforeCreate = preguntaRepository.findAll().size();
        restPreguntaMockMvc.perform(post("/api/preguntas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pregunta)))
            .andExpect(status().isCreated());

        // Validate the Pregunta in the database
        List<Pregunta> preguntas = preguntaRepository.findAll();
        assertThat(preguntas).hasSize(databaseSizeBeforeCreate + 1);
        return preguntas.get(preguntas.size() - 1);
    }

    @Test
    @Transactional
    public void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = preguntaRepository.findAll().size();
        // set the field null
        pregunta.setNombre(null);

        // Create the Pregunta, which fails.

        restPreguntaMockMvc.perform(post("/api/preguntas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pregunta)))
                .andExpect(status().isBadRequest());

        List<Pregunta> preguntas = preguntaRepository.findAll();
        assertThat(preguntas).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPreguntas() throws Exception {
        // Initialize the database
        preguntaRepository.saveAndFlush(pregunta);

        // Get all the preguntas
        restPreguntaMockMvc.perform(get("/api/preguntas?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(pregunta.getId().intValue())))
                .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())))
                .andExpect(jsonPath("$.[*].informacion").value(hasItem(DEFAULT_INFORMACION.toString())));
    }

    @Test
    @Transactional
    public void getPregunta() throws Exception {
        // Initialize the database
        preguntaRepository.saveAndFlush(pregunta);

        // Get the pregunta
        restPreguntaMockMvc.perform(get("/api/preguntas/{id}", pregunta.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(pregunta.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE.toString()))
            .andExpect(jsonPath("$.informacion").value(DEFAULT_INFORMACION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPregunta() throws Exception {
        // Get the pregunta
        restPreguntaMockMvc.perform(get("/api/preguntas/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePregunta() throws Exception {
        // Initialize the database
        preguntaRepository.saveAndFlush(pregunta);
        int databaseSizeBeforeUpdate = preguntaRepository.findAll().size();

        // Update the pregunta
        Pregunta updatedPregunta = new Pregunta();
        updatedPregunta.setId(pregunta.getId());
        updatedPregunta.setNombre(UPDATED_NOMBRE);
        updatedPregunta.setInformacion(UPDATED_INFORMACION);

        restPreguntaMockMvc.perform(put("/api/preguntas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPregunta)))
                .andExpect(status().isOk());

        // Validate the Pregunta in the database
        List<Pregunta> preguntas = preguntaRepository.findAll();
        assertThat(preguntas).hasSize(databaseSizeBeforeUpdate);
        Pregunta testPregunta = preguntas.get(preguntas.size() - 1);
        assertThat(testPregunta.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testPregunta.getInformacion()).isEqualTo(UPDATED_INFORMACION);
    }

    @Test
    @Transactional
    public void deletePregunta() throws Exception {
        // Initialize the database
        preguntaRepository.saveAndFlush(pregunta);
        int databaseSizeBeforeDelete = preguntaRepository.findAll().size();

        // Get the pregunta
        restPreguntaMockMvc.perform(delete("/api/preguntas/{id}", pregunta.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Pregunta> preguntas = preguntaRepository.findAll();
        assertThat(preguntas).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void getPreguntasWithoutSections() throws Exception {
        // Pregunta without section
        Pregunta preguntaWithoutSection = new Pregunta();
        preguntaWithoutSection.setId(pregunta.getId());
        preguntaWithoutSection.setNombre(UPDATED_NOMBRE);
        preguntaWithoutSection.setInformacion(UPDATED_INFORMACION);

        preguntaRepository.flush();
        preguntaRepository.save(pregunta);
        preguntaRepository.save(preguntaWithoutSection);

        // Get all the preguntas
        restPreguntaMockMvc.perform(get("/api/preguntas/seccion/{seccionId}?sort=id,desc", seccionPregunta.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*]").isEmpty());
    }

    @Test
    @Transactional
    public void getPreguntasWithSections() throws Exception {
        // Pregunta without section
        Pregunta pregunta = new Pregunta();
        pregunta.setId(this.pregunta.getId());
        pregunta.setNombre(UPDATED_NOMBRE);
        pregunta.setInformacion(UPDATED_INFORMACION);

        Pregunta secondPregunta = new Pregunta();
        secondPregunta.setNombre("Seccionada");
        secondPregunta.setInformacion("mucha seccion wow");

        preguntaRepository.flush();
        preguntaRepository.save(pregunta);
        preguntaRepository.save(secondPregunta);

        Set<Pregunta> preguntas = new HashSet<>();
        preguntas.add(pregunta);
        preguntas.add(secondPregunta);

        seccionPregunta.setPreguntas(preguntas);
        seccionRepository.flush();
        seccionRepository.save(seccionPregunta);

        // Get all the preguntas
        restPreguntaMockMvc.perform(get("/api/preguntas/seccion/{seccionId}?sort=id,desc", seccionPregunta.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*]").isNotEmpty());
    }

    @Test
    @Transactional
    public void updatePreguntasSeccionWithInvalidSeccion() throws Exception {
        List<Pregunta> preguntas = new ArrayList<>();
        restPreguntaMockMvc.perform(post("/api/preguntas/seccion/{id}", Long.MAX_VALUE)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(preguntas)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void setPreguntasSeccion() throws Exception {
        // save with no seccion
        preguntaRepository.saveAndFlush(pregunta);

        Set<Pregunta> preguntas = new HashSet<>();
        preguntas.add(pregunta);

        restPreguntaMockMvc.perform(post("/api/preguntas/seccion/{id}", seccionPregunta.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(preguntas)))
            .andExpect(status().isOk());

        // Validate the Pregunta in the database
        Pregunta testPregunta = preguntaRepository.findOne(pregunta.getId());
        assertThat(testPregunta.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testPregunta.getInformacion()).isEqualTo(DEFAULT_INFORMACION);

        Seccion seccion = seccionRepository.findOne(seccionPregunta.getId());
        Set<Pregunta> preguntasFound = seccion.getPreguntas();
        assertThat(preguntasFound).isNotEmpty();
        assertThat(preguntasFound).hasSize(1);
    }

    @Test
    @Transactional
    public void updatePreguntasSeccion() throws Exception {
        // set only one pregunta
        setPreguntasSeccion();

        // update with different preguntas
        Pregunta pregunta = new Pregunta();
        pregunta.setId(this.pregunta.getId());
        pregunta.setNombre(UPDATED_NOMBRE);
        pregunta.setInformacion(UPDATED_INFORMACION);

        Pregunta secondPregunta = new Pregunta();
        secondPregunta.setNombre("Seccionada");
        secondPregunta.setInformacion("mucha seccion wow");

        preguntaRepository.flush();
        preguntaRepository.save(pregunta);
        preguntaRepository.save(secondPregunta);

        Set<Pregunta> updatePreguntas = new HashSet<>();
        updatePreguntas.add(pregunta);
        updatePreguntas.add(secondPregunta);

        seccionPregunta.setPreguntas(updatePreguntas);
        seccionRepository.flush();
        seccionRepository.save(seccionPregunta);

        restPreguntaMockMvc.perform(post("/api/preguntas/seccion/{id}", seccionPregunta.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatePreguntas)))
            .andExpect(status().isOk());

        // validate over the database
        Seccion seccionUpdated = seccionRepository.findOne(seccionPregunta.getId());
        Set<Pregunta> preguntasUpdated = seccionUpdated.getPreguntas();
        assertThat(preguntasUpdated).isNotEmpty();
        assertThat(preguntasUpdated).hasSize(2);
    }

    @Test
    @Transactional
    public void updateNoPreguntasSeccion() throws Exception {
        // set only one pregunta
        setPreguntasSeccion();

        // update with no preguntas, such as deleting data
        Set<Pregunta> updatePreguntas = new HashSet<>();

        seccionPregunta.setPreguntas(updatePreguntas);
        seccionRepository.flush();
        seccionRepository.save(seccionPregunta);

        restPreguntaMockMvc.perform(post("/api/preguntas/seccion/{id}", seccionPregunta.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatePreguntas)))
            .andExpect(status().isOk());

        // validate over the database
        Seccion seccionUpdated = seccionRepository.findOne(seccionPregunta.getId());
        Set<Pregunta> preguntasUpdated = seccionUpdated.getPreguntas();
        assertThat(preguntasUpdated).isEmpty();
    }
}
