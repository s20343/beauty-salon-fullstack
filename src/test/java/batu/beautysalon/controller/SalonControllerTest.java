package batu.beautysalon.controller;

import batu.beautysalon.dto.SalonDetailDto;
import batu.beautysalon.dto.SalonSummaryDto;
import batu.beautysalon.service.SalonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SalonController.class)
@WithMockUser
class SalonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SalonService salonService;

    // -------------------------
    // GET ALL
    // -------------------------
    @Test
    void shouldReturnAllSalons() throws Exception {

        SalonSummaryDto dto = SalonSummaryDto.builder()
                .id(1L)
                .name("Beauty Room")
                .district("Mokotow")
                .rating(4.8)
                .reviewCount(100)
                .build();

        when(salonService.getAllSalons(any(), any()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/salons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Beauty Room"))
                .andExpect(jsonPath("$[0].district").value("Mokotow"));
    }

    // -------------------------
    // GET BY ID
    // -------------------------
    @Test
    void shouldReturnSalonById() throws Exception {

        SalonDetailDto dto = SalonDetailDto.builder()
                .id(1L)
                .name("Luxury Salon")
                .district("Wola")
                .address("Main Street 1")
                .build();

        when(salonService.getSalonById(1L))
                .thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/salons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Luxury Salon"))
                .andExpect(jsonPath("$.district").value("Wola"));
    }

    // -------------------------
    // 404 NOT FOUND
    // -------------------------
    @Test
    void shouldReturn404WhenSalonNotFound() throws Exception {

        when(salonService.getSalonById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/salons/999"))
                .andExpect(status().isNotFound());
    }

    // -------------------------
    // UPDATE
    // -------------------------
    @Test
    void shouldUpdateSalon() throws Exception {

        String requestJson = """
        {
          "name": "Updated Salon",
          "address": "New Address",
          "district": "Wola"
        }
        """;

        SalonDetailDto response = SalonDetailDto.builder()
                .id(1L)
                .name("Updated Salon")
                .address("New Address")
                .district("Wola")
                .build();

        when(salonService.updateSalon(any(), any()))
                .thenReturn(Optional.of(response));

        mockMvc.perform(
                        put("/api/salons/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Salon"))
                .andExpect(jsonPath("$.district").value("Wola"));
    }

    // -------------------------
    // UPDATE NOT FOUND
    // -------------------------
    @Test
    void shouldReturn404WhenUpdatingMissingSalon() throws Exception {

        String requestJson = """
        {
          "name": "Updated Salon",
          "address": "New Address",
          "district": "Wola"
        }
        """;

        when(salonService.updateSalon(any(), any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        put("/api/salons/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                                .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    // -------------------------
    // VALIDATION ERROR
    // -------------------------
    @Test
    void shouldReturn400WhenInvalidRequest() throws Exception {

        String invalidJson = """
        {
          "name": "",
          "address": "",
          "district": ""
        }
        """;

        mockMvc.perform(
                        put("/api/salons/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest());
    }
}