package ar.edu.utn.frro.web.rest;

import ar.edu.utn.frro.Application;
import ar.edu.utn.frro.domain.Pregunta;
import ar.edu.utn.frro.repository.PreguntaRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Inject
    private PreguntaRepository preguntaRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPreguntaMockMvc;

    private Pregunta pregunta;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PreguntaResource preguntaResource = new PreguntaResource();
        ReflectionTestUtils.setField(preguntaResource, "preguntaRepository", preguntaRepository);
        this.restPreguntaMockMvc = MockMvcBuilders.standaloneSetup(preguntaResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        pregunta = new Pregunta();
        pregunta.setNombre(DEFAULT_NOMBRE);
        pregunta.setInformacion(DEFAULT_INFORMACION);
    }

    @Test
    @Transactional
    public void createPregunta() throws Exception {
        int databaseSizeBeforeCreate = preguntaRepository.findAll().size();

        // Create the Pregunta

        restPreguntaMockMvc.perform(post("/api/preguntas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pregunta)))
                .andExpect(status().isCreated());

        // Validate the Pregunta in the database
        List<Pregunta> preguntas = preguntaRepository.findAll();
        assertThat(preguntas).hasSize(databaseSizeBeforeCreate + 1);
        Pregunta testPregunta = preguntas.get(preguntas.size() - 1);
        assertThat(testPregunta.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testPregunta.getInformacion()).isEqualTo(DEFAULT_INFORMACION);
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
}
