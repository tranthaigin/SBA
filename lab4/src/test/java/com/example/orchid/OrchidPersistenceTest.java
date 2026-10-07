package com.example.orchid;

import com.example.orchid.pojos.Orchid;
import com.example.orchid.pojos.OrchidCategory;
import com.example.orchid.repositories.IOrchidCategoryRepository;
import com.example.orchid.repositories.IOrchidRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrchidPersistenceTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired IOrchidRepository orchids;
    @Autowired IOrchidCategoryRepository categories;

    private Orchid input(Long categoryId, String name) {
        return new Orchid(null, name, true, "Hoa lan kiểm thử", new OrchidCategory(categoryId, null),
                true, "https://example.com/orchid.jpg");
    }

    @Test
    void crudPersistsFieldsAndReplacesForeignKeyWithoutChangingCategory() throws Exception {
        var first = categories.save(new OrchidCategory("Persistence A"));
        var second = categories.save(new OrchidCategory("Persistence B"));
        Long id = null;
        try {
            Orchid request = input(first.getCategoryId(), "Hoa lan kiểm thử");
            request.setOrchidID(999999L);
            request.getOrchidCategory().setCategoryName("Client must not overwrite category");
            String json = mvc.perform(post("/api/orchids").contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
            id = mapper.readTree(json).get("orchidID").asLong();
            assertThat(id).isNotEqualTo(999999L);
            assertThat(orchids.findById(id).orElseThrow().getOrchidCategory().getCategoryId())
                    .isEqualTo(first.getCategoryId());
            assertThat(categories.findById(first.getCategoryId()).orElseThrow().getCategoryName())
                    .isEqualTo("Persistence A");
            mvc.perform(get("/api/orchids/{id}", id)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.orchidName").value("Hoa lan kiểm thử"))
                    .andExpect(jsonPath("$.isNatural").value(true))
                    .andExpect(jsonPath("$.orchidCategory.orchids").doesNotExist());
            mvc.perform(get("/api/orchids").param("name", "HOA LAN"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$[0].orchidID").value(id));
            Orchid replacement = input(second.getCategoryId(), "Updated persistent orchid");
            replacement.setIsNatural(false);
            replacement.setIsAttractive(false);
            mvc.perform(put("/api/orchids/{id}", id).contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(replacement)))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.isNatural").value(false));
            Orchid stored = orchids.findById(id).orElseThrow();
            assertThat(stored.getOrchidCategory().getCategoryId()).isEqualTo(second.getCategoryId());
            assertThat(stored.getOrchidName()).isEqualTo(replacement.getOrchidName());
            mvc.perform(delete("/api/orchids/{id}", id)).andExpect(status().isNoContent())
                    .andExpect(content().string(""));
            mvc.perform(get("/api/orchids/{id}", id)).andExpect(status().isNotFound());
            assertThat(orchids.existsById(id)).isFalse();
            assertThat(categories.existsById(first.getCategoryId())).isTrue();
            assertThat(categories.existsById(second.getCategoryId())).isTrue();
        } finally {
            if (id != null && orchids.existsById(id)) orchids.deleteById(id);
            categories.deleteById(first.getCategoryId());
            categories.deleteById(second.getCategoryId());
        }
    }

    @Test
    void rejectedUpdateRollsBackAllFields() throws Exception {
        var category = categories.findAll().getFirst();
        var stored = orchids.save(input(category.getCategoryId(), "Rollback original"));
        try {
            mvc.perform(put("/api/orchids/{id}", stored.getOrchidID()).contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(input(999999L, "Must not persist"))))
                    .andExpect(status().isBadRequest());
            assertThat(orchids.findById(stored.getOrchidID()).orElseThrow().getOrchidName())
                    .isEqualTo("Rollback original");
        } finally { orchids.deleteById(stored.getOrchidID()); }
    }

    @Test
    void invalidPayloadsAndMissingResourcesKeepDatabaseUnchanged() throws Exception {
        long before = orchids.count();
        mvc.perform(post("/api/orchids").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orchidName\":\"Missing category\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/orchids").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input(999999L, "Missing category id"))))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/orchids").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input(1L, " "))))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/orchids").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/orchids").contentType(MediaType.TEXT_PLAIN).content("{}"))
                .andExpect(status().isUnsupportedMediaType());
        mvc.perform(get("/api/orchids/999999")).andExpect(status().isNotFound());
        mvc.perform(put("/api/orchids/999999").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input(1L, "Missing orchid"))))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/orchids/999999")).andExpect(status().isNotFound());
        assertThat(orchids.count()).isEqualTo(before);
    }

    @Test
    void originalLabPathWithTrailingSlashWorks() throws Exception {
        mvc.perform(get("/orchids/")).andExpect(status().isOk());
    }

    @Test
    void categoryCreateIgnoresClientIdentityAndRejectsDuplicateAndBlankNames() throws Exception {
        String json = mvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryId\":999999,\"categoryName\":\"Category HTTP test\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        Long id = mapper.readTree(json).get("categoryId").asLong();
        try {
            assertThat(id).isNotEqualTo(999999L);
            mvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"categoryName\":\"category http test\"}"))
                    .andExpect(status().isBadRequest());
            mvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"categoryName\":\" \"}"))
                    .andExpect(status().isBadRequest());
        } finally { categories.deleteById(id); }
    }
}
