package com.kevin.carrent.integrationTest;

import com.kevin.carrent.entity.CarModel;
import com.kevin.carrent.repository.CarModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
class CarModelIntegrationTest {
    @Autowired
    private CarModelRepository carModelRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "kevin", roles = "USER")
    void shouldGetCarModels() throws Exception {

        mockMvc.perform(get("/car-models"))
                .andExpect(status().isOk());
    }

    @BeforeEach
    void setUp() {
        carModelRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "kevin", roles = "USER")
    void shouldReturnSavedCarModel() throws Exception {

        CarModel carModel = new CarModel();
        carModel.setBrand("Toyota");
        carModel.setModel("Corolla");

        carModelRepository.save(carModel);

        mockMvc.perform(get("/car-models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("Corolla"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldCreateCarModel() throws Exception {

        mockMvc.perform(
                        post("/car-models")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "brand": "BMW",
                                                "model": "Serie 3"
                                            }
                                        """)
                )
                .andExpect(status().isCreated());

        List<CarModel> carModels = carModelRepository.findAll();

        assertEquals(1, carModels.size());
        assertEquals("BMW", carModels.get(0).getBrand());
        assertEquals("Serie 3", carModels.get(0).getModel());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturnBadRequestWhenCarModelIsInvalid() throws Exception {

        mockMvc.perform(
                        post("/car-models")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "brand": "",
                                                "model": "Serie 3"
                                            }
                                        """)
                )
                .andExpect(status().isBadRequest());

        assertEquals(0, carModelRepository.count());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturn404WhenDeletingNonExistingCarModel() throws Exception {

        mockMvc.perform(
                        delete("/car-models/999")
                                .with(csrf())
                )
                .andExpect(status().isNotFound());

        assertEquals(0, carModelRepository.count());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldUpdateCarModel() throws Exception {

        CarModel carModel = new CarModel();
        carModel.setBrand("Toyota");
        carModel.setModel("Corolla");

        CarModel savedCarModel = carModelRepository.save(carModel);

        mockMvc.perform(
                        put("/car-models/" + savedCarModel.getId())
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "brand": "BMW",
                                                "model": "Serie 3"
                                            }
                                        """)
                )
                .andExpect(status().isOk());

        CarModel updatedCarModel =
                carModelRepository.findById(savedCarModel.getId())
                        .orElseThrow();

        assertEquals("BMW", updatedCarModel.getBrand());
        assertEquals("Serie 3", updatedCarModel.getModel());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldDeleteCarModel() throws Exception {

        CarModel carModel = new CarModel();
        carModel.setBrand("Toyota");
        carModel.setModel("Corolla");

        CarModel savedCarModel = carModelRepository.save(carModel);

        mockMvc.perform(
                        delete("/car-models/" + savedCarModel.getId())
                                .with(csrf())
                )
                .andExpect(status().isNoContent());

        assertTrue(
                carModelRepository.findById(savedCarModel.getId()).isEmpty()
        );
    }
}