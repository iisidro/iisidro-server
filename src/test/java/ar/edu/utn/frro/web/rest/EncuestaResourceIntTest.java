package ar.edu.utn.frro.web.rest;

import ar.edu.utn.frro.Application;
import ar.edu.utn.frro.domain.Encuesta;
import ar.edu.utn.frro.repository.EncuestaRepository;

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
 * Test class for the EncuestaResource REST controller.
 *
 * @see EncuestaResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class EncuestaResourceIntTest {

    private static final String DEFAULT_NOMBRE = "AAAAA";
    private static final String UPDATED_NOMBRE = "BBBBB";

    @Inject
    private EncuestaRepository encuestaRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restEncuestaMockMvc;

    private Encuesta encuesta;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EncuestaResource encuestaResource = new EncuestaResource();
        ReflectionTestUtils.setField(encuestaResource, "encuestaRepository", encuestaRepository);
        this.restEncuestaMockMvc = MockMvcBuilders.standaloneSetup(encuestaResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        encuesta = new Encuesta();
        encuesta.setNombre(DEFAULT_NOMBRE);
    }

    @Test
    @Transactional
    public void createEncuesta() throws Exception {
        int databaseSizeBeforeCreate = encuestaRepository.findAll().size();

        // Create the Encuesta

        restEncuestaMockMvc.perform(post("/api/encuestas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(encuesta)))
                .andExpect(status().isCreated());

        // Validate the Encuesta in the database
        List<Encuesta> encuestas = encuestaRepository.findAll();
        assertThat(encuestas).hasSize(databaseSizeBeforeCreate + 1);
        Encuesta testEncuesta = encuestas.get(encuestas.size() - 1);
        assertThat(testEncuesta.getNombre()).isEqualTo(DEFAULT_NOMBRE);
    }

    @Test
    @Transactional
    public void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = encuestaRepository.findAll().size();
        // set the field null
        encuesta.setNombre(null);

        // Create the Encuesta, which fails.

        restEncuestaMockMvc.perform(post("/api/encuestas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(encuesta)))
                .andExpect(status().isBadRequest());

        List<Encuesta> encuestas = encuestaRepository.findAll();
        assertThat(encuestas).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEncuestas() throws Exception {
        // Initialize the database
        encuestaRepository.saveAndFlush(encuesta);

        // Get all the encuestas
        restEncuestaMockMvc.perform(get("/api/encuestas?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(encuesta.getId().intValue())))
                .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())));
    }

    @Test
    @Transactional
    public void getEncuesta() throws Exception {
        // Initialize the database
        encuestaRepository.saveAndFlush(encuesta);

        // Get the encuesta
        restEncuestaMockMvc.perform(get("/api/encuestas/{id}", encuesta.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(encuesta.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEncuesta() throws Exception {
        // Get the encuesta
        restEncuestaMockMvc.perform(get("/api/encuestas/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEncuesta() throws Exception {
        // Initialize the database
        encuestaRepository.saveAndFlush(encuesta);
        int databaseSizeBeforeUpdate = encuestaRepository.findAll().size();

        // Update the encuesta
        Encuesta updatedEncuesta = new Encuesta();
        updatedEncuesta.setId(encuesta.getId());
        updatedEncuesta.setNombre(UPDATED_NOMBRE);

        restEncuestaMockMvc.perform(put("/api/encuestas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedEncuesta)))
                .andExpect(status().isOk());

        // Validate the Encuesta in the database
        List<Encuesta> encuestas = encuestaRepository.findAll();
        assertThat(encuestas).hasSize(databaseSizeBeforeUpdate);
        Encuesta testEncuesta = encuestas.get(encuestas.size() - 1);
        assertThat(testEncuesta.getNombre()).isEqualTo(UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    public void deleteEncuesta() throws Exception {
        // Initialize the database
        encuestaRepository.saveAndFlush(encuesta);
        int databaseSizeBeforeDelete = encuestaRepository.findAll().size();

        // Get the encuesta
        restEncuestaMockMvc.perform(delete("/api/encuestas/{id}", encuesta.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Encuesta> encuestas = encuestaRepository.findAll();
        assertThat(encuestas).hasSize(databaseSizeBeforeDelete - 1);
    }
}
