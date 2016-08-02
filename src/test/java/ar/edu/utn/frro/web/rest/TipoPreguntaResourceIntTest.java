package ar.edu.utn.frro.web.rest;

import ar.edu.utn.frro.Application;
import ar.edu.utn.frro.domain.TipoPregunta;
import ar.edu.utn.frro.repository.TipoPreguntaRepository;

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
 * Test class for the TipoPreguntaResource REST controller.
 *
 * @see TipoPreguntaResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TipoPreguntaResourceIntTest {

    private static final String DEFAULT_NOMBRE = "AAAAA";
    private static final String UPDATED_NOMBRE = "BBBBB";

    @Inject
    private TipoPreguntaRepository tipoPreguntaRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTipoPreguntaMockMvc;

    private TipoPregunta tipoPregunta;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TipoPreguntaResource tipoPreguntaResource = new TipoPreguntaResource();
        ReflectionTestUtils.setField(tipoPreguntaResource, "tipoPreguntaRepository", tipoPreguntaRepository);
        this.restTipoPreguntaMockMvc = MockMvcBuilders.standaloneSetup(tipoPreguntaResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        tipoPregunta = new TipoPregunta();
        tipoPregunta.setNombre(DEFAULT_NOMBRE);
    }

    @Test
    @Transactional
    public void createTipoPregunta() throws Exception {
        int databaseSizeBeforeCreate = tipoPreguntaRepository.findAll().size();

        // Create the TipoPregunta

        restTipoPreguntaMockMvc.perform(post("/api/tipo-preguntas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tipoPregunta)))
                .andExpect(status().isCreated());

        // Validate the TipoPregunta in the database
        List<TipoPregunta> tipoPreguntas = tipoPreguntaRepository.findAll();
        assertThat(tipoPreguntas).hasSize(databaseSizeBeforeCreate + 1);
        TipoPregunta testTipoPregunta = tipoPreguntas.get(tipoPreguntas.size() - 1);
        assertThat(testTipoPregunta.getNombre()).isEqualTo(DEFAULT_NOMBRE);
    }

    @Test
    @Transactional
    public void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = tipoPreguntaRepository.findAll().size();
        // set the field null
        tipoPregunta.setNombre(null);

        // Create the TipoPregunta, which fails.

        restTipoPreguntaMockMvc.perform(post("/api/tipo-preguntas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tipoPregunta)))
                .andExpect(status().isBadRequest());

        List<TipoPregunta> tipoPreguntas = tipoPreguntaRepository.findAll();
        assertThat(tipoPreguntas).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTipoPreguntas() throws Exception {
        // Initialize the database
        tipoPreguntaRepository.saveAndFlush(tipoPregunta);

        // Get all the tipoPreguntas
        restTipoPreguntaMockMvc.perform(get("/api/tipo-preguntas?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(tipoPregunta.getId().intValue())))
                .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())));
    }

    @Test
    @Transactional
    public void getTipoPregunta() throws Exception {
        // Initialize the database
        tipoPreguntaRepository.saveAndFlush(tipoPregunta);

        // Get the tipoPregunta
        restTipoPreguntaMockMvc.perform(get("/api/tipo-preguntas/{id}", tipoPregunta.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(tipoPregunta.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTipoPregunta() throws Exception {
        // Get the tipoPregunta
        restTipoPreguntaMockMvc.perform(get("/api/tipo-preguntas/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTipoPregunta() throws Exception {
        // Initialize the database
        tipoPreguntaRepository.saveAndFlush(tipoPregunta);
        int databaseSizeBeforeUpdate = tipoPreguntaRepository.findAll().size();

        // Update the tipoPregunta
        TipoPregunta updatedTipoPregunta = new TipoPregunta();
        updatedTipoPregunta.setId(tipoPregunta.getId());
        updatedTipoPregunta.setNombre(UPDATED_NOMBRE);

        restTipoPreguntaMockMvc.perform(put("/api/tipo-preguntas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTipoPregunta)))
                .andExpect(status().isOk());

        // Validate the TipoPregunta in the database
        List<TipoPregunta> tipoPreguntas = tipoPreguntaRepository.findAll();
        assertThat(tipoPreguntas).hasSize(databaseSizeBeforeUpdate);
        TipoPregunta testTipoPregunta = tipoPreguntas.get(tipoPreguntas.size() - 1);
        assertThat(testTipoPregunta.getNombre()).isEqualTo(UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    public void deleteTipoPregunta() throws Exception {
        // Initialize the database
        tipoPreguntaRepository.saveAndFlush(tipoPregunta);
        int databaseSizeBeforeDelete = tipoPreguntaRepository.findAll().size();

        // Get the tipoPregunta
        restTipoPreguntaMockMvc.perform(delete("/api/tipo-preguntas/{id}", tipoPregunta.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<TipoPregunta> tipoPreguntas = tipoPreguntaRepository.findAll();
        assertThat(tipoPreguntas).hasSize(databaseSizeBeforeDelete - 1);
    }
}
