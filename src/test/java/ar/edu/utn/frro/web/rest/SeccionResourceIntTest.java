package ar.edu.utn.frro.web.rest;

import ar.edu.utn.frro.Application;
import ar.edu.utn.frro.domain.Encuesta;
import ar.edu.utn.frro.domain.Seccion;
import ar.edu.utn.frro.repository.EncuestaRepository;
import ar.edu.utn.frro.repository.SeccionRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the SeccionResource REST controller.
 *
 * @see SeccionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class SeccionResourceIntTest {


    private static final Integer DEFAULT_ORDEN = 1;
    private static final Integer UPDATED_ORDEN = 2;
    private static final String DEFAULT_CODIGO = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_CODIGO = "BBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_NOMBRE = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    @Inject
    private SeccionRepository seccionRepository;

    @Inject
    private EncuestaRepository encuestaRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSeccionMockMvc;

    private Seccion seccion;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SeccionResource seccionResource = new SeccionResource();
        ReflectionTestUtils.setField(seccionResource, "seccionRepository", seccionRepository);
        ReflectionTestUtils.setField(seccionResource, "encuestaRepository", encuestaRepository);

        this.restSeccionMockMvc = MockMvcBuilders.standaloneSetup(seccionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        seccion = new Seccion();
        seccion.setOrden(DEFAULT_ORDEN);
        seccion.setCodigo(DEFAULT_CODIGO);
        seccion.setNombre(DEFAULT_NOMBRE);
    }

    @Test
    @Transactional
    public void createSeccion() throws Exception {
        int databaseSizeBeforeCreate = seccionRepository.findAll().size();

        // Create the Seccion
        restSeccionMockMvc.perform(
                post("/api/secciones")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(seccion)))
                .andExpect(status().isCreated()
                );

        // Validate the Seccion in the database
        List<Seccion> secciones = seccionRepository.findAll();
        assertThat(secciones).hasSize(databaseSizeBeforeCreate + 1);
        Seccion testSeccion = secciones.get(secciones.size() - 1);
        assertThat(testSeccion.getOrden()).isEqualTo(DEFAULT_ORDEN);
        assertThat(testSeccion.getCodigo()).isEqualTo(DEFAULT_CODIGO);
        assertThat(testSeccion.getNombre()).isEqualTo(DEFAULT_NOMBRE);
    }

    @Test
    @Transactional
    public void checkCodigoIsRequired() throws Exception {
        int databaseSizeBeforeTest = seccionRepository.findAll().size();
        // set the field null
        seccion.setCodigo(null);

        // Create the Seccion, which fails.

        restSeccionMockMvc.perform(post("/api/secciones")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(seccion)))
                .andExpect(status().isBadRequest());

        List<Seccion> secciones = seccionRepository.findAll();
        assertThat(secciones).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = seccionRepository.findAll().size();
        // set the field null
        seccion.setNombre(null);

        // Create the Seccion, which fails.

        restSeccionMockMvc.perform(post("/api/secciones")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(seccion)))
                .andExpect(status().isBadRequest());

        List<Seccion> secciones = seccionRepository.findAll();
        assertThat(secciones).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSecciones() throws Exception {
        // Initialize the database
        seccionRepository.saveAndFlush(seccion);

        // Get all the secciones
        restSeccionMockMvc.perform(get("/api/secciones?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(seccion.getId().intValue())))
                .andExpect(jsonPath("$.[*].orden").value(hasItem(DEFAULT_ORDEN)))
                .andExpect(jsonPath("$.[*].codigo").value(hasItem(DEFAULT_CODIGO.toString())))
                .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())));
    }

    @Test
    @Transactional
    public void getSeccion() throws Exception {
        // Initialize the database
        seccionRepository.saveAndFlush(seccion);

        // Get the seccion
        restSeccionMockMvc.perform(get("/api/secciones/{id}", seccion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(seccion.getId().intValue()))
            .andExpect(jsonPath("$.orden").value(DEFAULT_ORDEN))
            .andExpect(jsonPath("$.codigo").value(DEFAULT_CODIGO.toString()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSeccion() throws Exception {
        // Get the seccion
        restSeccionMockMvc.perform(get("/api/secciones/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSeccion() throws Exception {
        // Initialize the database
        seccionRepository.saveAndFlush(seccion);
        int databaseSizeBeforeUpdate = seccionRepository.findAll().size();

        // Update the seccion
        Seccion updatedSeccion = seccion;
        updatedSeccion.setOrden(UPDATED_ORDEN);
        updatedSeccion.setCodigo(UPDATED_CODIGO);
        updatedSeccion.setNombre(UPDATED_NOMBRE);

        restSeccionMockMvc.perform(put("/api/secciones")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSeccion)))
                .andExpect(status().isOk());

        // Validate the Seccion in the database
        List<Seccion> secciones = seccionRepository.findAll();
        assertThat(secciones).hasSize(databaseSizeBeforeUpdate);
        Seccion testSeccion = secciones.get(secciones.size() - 1);
        assertThat(testSeccion.getOrden()).isEqualTo(UPDATED_ORDEN);
        assertThat(testSeccion.getCodigo()).isEqualTo(UPDATED_CODIGO);
        assertThat(testSeccion.getNombre()).isEqualTo(UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    public void deleteSeccion() throws Exception {
        // Initialize the database
        seccionRepository.saveAndFlush(seccion);
        int databaseSizeBeforeDelete = seccionRepository.findAll().size();

        // Get the seccion
        restSeccionMockMvc.perform(delete("/api/secciones/{id}", seccion.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Seccion> secciones = seccionRepository.findAll();
        assertThat(secciones).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void getNoSeccionByEncuesta() throws Exception {
        // Initialize the database
        Encuesta encuestaWithoutSeccion = new Encuesta();
        encuestaWithoutSeccion.setNombre("Test With seccion");

        encuestaWithoutSeccion = encuestaRepository.save(encuestaWithoutSeccion);

        // Get all the secciones by encuesta
        restSeccionMockMvc.perform(get("/api/secciones/encuesta/"+encuestaWithoutSeccion.getId()+"?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*]").isEmpty());
    }

    @Test
    @Transactional
    public void getOneSeccionByEncuesta() throws Exception {
        // Initialize the database
        seccionRepository.saveAndFlush(seccion);
        Set<Seccion> secciones = new HashSet<>();
        secciones.add(seccion);

        Encuesta encuestaWithSeccion = new Encuesta();
        encuestaWithSeccion.setNombre("Test With seccion");
        encuestaWithSeccion.setSecciones(secciones);

        encuestaWithSeccion = encuestaRepository.save(encuestaWithSeccion);

        // Get all the secciones by encuesta
        restSeccionMockMvc.perform(get("/api/secciones/encuesta/"+encuestaWithSeccion.getId()+"?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(seccion.getId().intValue())))
            .andExpect(jsonPath("$.[*].orden").value(hasItem(DEFAULT_ORDEN)))
            .andExpect(jsonPath("$.[*].codigo").value(hasItem(DEFAULT_CODIGO.toString())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())));
    }

    @Test
    @Transactional
    public void getManySeccionesByEncuesta() throws Exception {
        // Initialize the database
        Seccion secondSeccion = new Seccion();
        secondSeccion.setNombre("Segunda");
        secondSeccion.setOrden(2);
        secondSeccion.setCodigo("12345");

        seccionRepository.saveAndFlush(seccion);
        seccionRepository.saveAndFlush(secondSeccion);
        Set<Seccion> secciones = new HashSet<>();
        secciones.add(seccion);
        secciones.add(secondSeccion);

        Encuesta encuestaWithSeccion = new Encuesta();
        encuestaWithSeccion.setNombre("Test With several seccion");
        encuestaWithSeccion.setSecciones(secciones);

        encuestaWithSeccion = encuestaRepository.save(encuestaWithSeccion);

        // Get all the secciones by encuesta
        restSeccionMockMvc.perform(get("/api/secciones/encuesta/"+encuestaWithSeccion.getId()+"?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*]").isNotEmpty())
            .andExpect(jsonPath("$.[*]").isArray())
            .andExpect(jsonPath("$.[*]").value(hasSize(secciones.size())))
            .andExpect(jsonPath("$.[*].id").value(hasItem(seccion.getId().intValue())))
            .andExpect(jsonPath("$.[*].orden").value(hasItem(DEFAULT_ORDEN)))
            .andExpect(jsonPath("$.[*].codigo").value(hasItem(DEFAULT_CODIGO)))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(secondSeccion.getId().intValue())))
            .andExpect(jsonPath("$.[*].orden").value(hasItem(secondSeccion.getOrden())))
            .andExpect(jsonPath("$.[*].codigo").value(hasItem(secondSeccion.getCodigo())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(secondSeccion.getNombre())));
    }

    @Test
    @Transactional
    public void setSeccionInEncuesta() throws Exception {
        // Initialize the database
        Seccion seccion = new Seccion();
        seccion.setNombre("Single");
        seccion.setOrden(1);
        seccion.setCodigo("54321");

        seccionRepository.saveAndFlush(seccion);
        Set<Seccion> secciones = new HashSet<>();
        secciones.add(seccion);

        Encuesta encuesta = new Encuesta();
        encuesta.setNombre("Setting one section");

        encuesta = encuestaRepository.save(encuesta);

        // call post secciones
        restSeccionMockMvc.perform(
                post("/api/secciones/encuesta/{id}", encuesta.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(secciones))
            ).andExpect(status().isOk());

        // assert
        Encuesta updated = encuestaRepository.findOne(encuesta.getId());
        assertThat(updated.getSecciones()).isNotEmpty();
        assertThat(updated.getSecciones()).hasSize(1);
    }

    @Test
    @Transactional
    public void setSeccionesInEncuesta() throws Exception {
        // Initialize the database
        Seccion seccion = new Seccion();
        seccion.setNombre("Single");
        seccion.setOrden(1);
        seccion.setCodigo("54321");

        seccionRepository.saveAndFlush(seccion);
        Seccion seccion2 = new Seccion();
        seccion2.setNombre("Multiple");
        seccion2.setOrden(2);
        seccion2.setCodigo("521");

        seccionRepository.saveAndFlush(seccion2);
        Set<Seccion> secciones = new HashSet<>();
        secciones.add(seccion);
        secciones.add(seccion2);

        Encuesta encuesta = new Encuesta();
        encuesta.setNombre("Setting two sections");

        encuesta = encuestaRepository.save(encuesta);

        // call post secciones
        restSeccionMockMvc.perform(
            post("/api/secciones/encuesta/{id}", encuesta.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(secciones))
        ).andExpect(status().isOk());

        // assert
        Encuesta updated = encuestaRepository.findOne(encuesta.getId());
        assertThat(updated.getSecciones()).isNotEmpty();
        assertThat(updated.getSecciones()).hasSize(2);
    }

    @Test
    @Transactional
    public void updateToNoSeccionsInEncuesta() throws Exception {
        // Initialize the database
        Seccion seccion = new Seccion();
        seccion.setNombre("Single");
        seccion.setOrden(1);
        seccion.setCodigo("54321");

        seccionRepository.saveAndFlush(seccion);
        Seccion seccion2 = new Seccion();
        seccion2.setNombre("Multiple");
        seccion2.setOrden(2);
        seccion2.setCodigo("521");

        seccionRepository.saveAndFlush(seccion2);
        Set<Seccion> secciones = new HashSet<>();
        secciones.add(seccion);
        secciones.add(seccion2);

        Encuesta encuesta = new Encuesta();
        encuesta.setNombre("Setting two sections");

        encuesta = encuestaRepository.save(encuesta);

        // call post secciones
        restSeccionMockMvc.perform(
            post("/api/secciones/encuesta/{id}", encuesta.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(secciones))
        ).andExpect(status().isOk());

        // assert
        Encuesta updated = encuestaRepository.findOne(encuesta.getId());
        assertThat(updated.getSecciones()).isNotEmpty();
        assertThat(updated.getSecciones()).hasSize(2);

        encuesta = encuestaRepository.save(encuesta);

        // call post secciones
        restSeccionMockMvc.perform(
            post("/api/secciones/encuesta/{id}", encuesta.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(new HashSet<>()))
        ).andExpect(status().isOk());

        // assert
        updated = encuestaRepository.findOne(encuesta.getId());
        assertThat(updated.getSecciones()).isEmpty();
    }
}
