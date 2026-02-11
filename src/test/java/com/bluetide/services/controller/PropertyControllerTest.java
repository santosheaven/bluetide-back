package com.bluetide.services.controller;

import com.bluetide.services.models.Property;
import com.bluetide.services.repository.PropertyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PropertyController.class)
public class PropertyControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private PropertyRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAll() throws Exception {
        Property p = new Property();
        p.setId("p1");
        p.setAddress("Addr");
        when(repo.findAll()).thenReturn(List.of(p));
        mvc.perform(get("/api/properties")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(p))));
    }

    @Test
    void getByIdFound() throws Exception {
        Property p = new Property();
        p.setId("p1");
        p.setAddress("Addr");
        when(repo.findById("p1")).thenReturn(Optional.of(p));
        mvc.perform(get("/api/properties/p1")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(p)));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(repo.findById("p1")).thenReturn(Optional.empty());
        mvc.perform(get("/api/properties/p1")).andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        Property p = new Property();
        p.setAddress("New");
        Property saved = new Property();
        saved.setId("p2");
        saved.setAddress("New");
        when(repo.save(any(Property.class))).thenReturn(saved);
        mvc.perform(post("/api/properties").contentType("application/json").content(mapper.writeValueAsString(p)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(saved)));
    }

    @Test
    void updateFound() throws Exception {
        Property existing = new Property();
        existing.setId("p1");
        Property updated = new Property();
        updated.setId("p1");
        updated.setAddress("Upd");
        when(repo.findById("p1")).thenReturn(Optional.of(existing));
        when(repo.save(any(Property.class))).thenReturn(updated);
        mvc.perform(put("/api/properties/p1").contentType("application/json").content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(updated)));
    }

    @Test
    void updateNotFound() throws Exception {
        when(repo.findById("p1")).thenReturn(Optional.empty());
        mvc.perform(put("/api/properties/p1").contentType("application/json").content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFound() throws Exception {
        when(repo.existsById("p1")).thenReturn(true);
        doNothing().when(repo).deleteById("p1");
        mvc.perform(delete("/api/properties/p1")).andExpect(status().isNoContent());
    }

    @Test
    void deleteNotFound() throws Exception {
        when(repo.existsById("p1")).thenReturn(false);
        mvc.perform(delete("/api/properties/p1")).andExpect(status().isNotFound());
    }
}