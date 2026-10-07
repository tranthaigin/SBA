package com.example.orchid;

import com.example.orchid.controllers.OrchidController;
import com.example.orchid.pojos.Orchid;
import com.example.orchid.pojos.OrchidCategory;
import com.example.orchid.services.IOrchidService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrchidController.class)
class OrchidControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IOrchidService orchidService;

    private Orchid sampleOrchid;
    private OrchidCategory sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = new OrchidCategory(1L, "Cattleya");
        sampleOrchid = new Orchid(1L, "Cattleya Queen", true, "Description", sampleCategory, true, "http://url");
    }

    @Test
    void getAll_shouldReturnOrchidList() throws Exception {
        when(orchidService.getAll()).thenReturn(List.of(sampleOrchid));

        mockMvc.perform(get("/api/orchids"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].orchidName", is("Cattleya Queen")));
    }

    @Test
    void searchByName_shouldReturnFilteredList() throws Exception {
        when(orchidService.searchByName("Queen")).thenReturn(List.of(sampleOrchid));

        mockMvc.perform(get("/api/orchids?name=Queen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].orchidName", is("Cattleya Queen")));
    }

    @Test
    void getById_whenFound_shouldReturnOrchid() throws Exception {
        when(orchidService.getById(1L)).thenReturn(Optional.of(sampleOrchid));

        mockMvc.perform(get("/api/orchids/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orchidID", is(1)))
                .andExpect(jsonPath("$.orchidName", is("Cattleya Queen")));
    }

    @Test
    void getById_whenNotFound_shouldReturn404() throws Exception {
        when(orchidService.getById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orchids/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_whenValid_shouldReturn201() throws Exception {
        when(orchidService.create(any(Orchid.class))).thenReturn(sampleOrchid);

        String jsonBody = """
                {
                    "orchidName": "Cattleya Queen",
                    "isNatural": true,
                    "orchidDescription": "Description",
                    "orchidCategory": { "categoryId": 1 },
                    "isAttractive": true,
                    "orchidURL": "http://url"
                }
                """;

        mockMvc.perform(post("/api/orchids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orchidID", is(1)))
                .andExpect(jsonPath("$.orchidName", is("Cattleya Queen")));
    }

    @Test
    void create_whenCategoryInvalid_shouldReturn400() throws Exception {
        when(orchidService.create(any(Orchid.class)))
                .thenThrow(new IllegalArgumentException("Category not found: 999"));

        String jsonBody = """
                {
                    "orchidName": "Invalid Orchid",
                    "orchidCategory": { "categoryId": 999 }
                }
                """;

        mockMvc.perform(post("/api/orchids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Category not found: 999"));
    }

    @Test
    void update_whenFound_shouldReturn200() throws Exception {
        when(orchidService.update(eq(1L), any(Orchid.class))).thenReturn(Optional.of(sampleOrchid));

        String jsonBody = """
                {
                    "orchidName": "Cattleya Queen Updated",
                    "isNatural": true,
                    "orchidCategory": { "categoryId": 1 }
                }
                """;

        mockMvc.perform(put("/api/orchids/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orchidID", is(1)));
    }

    @Test
    void update_whenNotFound_shouldReturn404() throws Exception {
        when(orchidService.update(eq(999L), any(Orchid.class))).thenReturn(Optional.empty());

        String jsonBody = """
                {
                    "orchidName": "Nonexistent Orchid",
                    "orchidCategory": { "categoryId": 1 }
                }
                """;

        mockMvc.perform(put("/api/orchids/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_whenFound_shouldReturn204() throws Exception {
        when(orchidService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/orchids/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_whenNotFound_shouldReturn404() throws Exception {
        when(orchidService.delete(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/orchids/999"))
                .andExpect(status().isNotFound());
    }
}

