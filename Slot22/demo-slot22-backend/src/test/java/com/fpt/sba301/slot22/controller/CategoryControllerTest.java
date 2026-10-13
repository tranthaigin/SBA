package com.fpt.sba301.slot22.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fpt.sba301.slot22.dto.CategoryResponse;
import com.fpt.sba301.slot22.exception.BusinessConflictException;
import com.fpt.sba301.slot22.exception.ResourceNotFoundException;
import com.fpt.sba301.slot22.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {
    @Autowired MockMvc mvc;
    @MockBean CategoryService service;

    private static final String VALID_BODY = """
            {"categoryName":"Technology News","categoryDescription":"Technology news","active":true}
            """;

    @Test
    void getMissing_returns404() throws Exception {
        when(service.getById((short) 99)).thenThrow(new ResourceNotFoundException("Category 99 not found"));
        mvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void postInvalid_returns400BeforeService() throws Exception {
        mvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\"\",\"categoryDescription\":\"\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void postValid_returns201AndGeneratedId() throws Exception {
        when(service.create(any())).thenReturn(new CategoryResponse((short) 4,
                "Technology News", "Technology news", null, true));
        mvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value(4));
    }

    @Test
    void deleteReferenced_returns409ErrorContract() throws Exception {
        doThrow(new BusinessConflictException("Category is used by one or more news articles"))
                .when(service).delete((short) 1);
        mvc.perform(delete("/api/categories/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void putMissing_returns404() throws Exception {
        when(service.update(org.mockito.ArgumentMatchers.eq((short) 99), any()))
                .thenThrow(new ResourceNotFoundException("Category 99 not found"));
        mvc.perform(put("/api/categories/99").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isNotFound());
    }
}
