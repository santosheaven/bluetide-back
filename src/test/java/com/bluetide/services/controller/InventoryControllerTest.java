package com.bluetide.services.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.bluetide.services.models.Inventory;
import com.bluetide.services.repository.InventoryRepository;
import com.bluetide.services.security.JwtAuthenticationFilter;
import com.bluetide.services.security.JwtUtils;
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

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class InventoryControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private InventoryRepository repo;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAll() throws Exception {
        Inventory i = new Inventory(); i.setId("i1"); i.setName("Fridge");
        when(repo.findAll()).thenReturn(List.of(i));
        mvc.perform(get("/api/inventory")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(i))));
    }

    @Test
    void getByIdFound() throws Exception {
        Inventory i = new Inventory(); i.setId("i1"); i.setName("Fridge");
        when(repo.findById("i1")).thenReturn(Optional.of(i));
        mvc.perform(get("/api/inventory/i1")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(i)));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(repo.findById("i1")).thenReturn(Optional.empty());
        mvc.perform(get("/api/inventory/i1")).andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        Inventory i = new Inventory(); i.setName("New");
        Inventory saved = new Inventory(); saved.setId("i2"); saved.setName("New");
        when(repo.save(any(Inventory.class))).thenReturn(saved);
        mvc.perform(post("/api/inventory").contentType("application/json").content(mapper.writeValueAsString(i)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(saved)));
    }

    @Test
    void updateFound() throws Exception {
        Inventory existing = new Inventory(); existing.setId("i1");
        Inventory updated = new Inventory(); updated.setId("i1"); updated.setName("U");
        when(repo.findById("i1")).thenReturn(Optional.of(existing));
        when(repo.save(any(Inventory.class))).thenReturn(updated);
        mvc.perform(put("/api/inventory/i1").contentType("application/json").content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(updated)));
    }

    @Test
    void updateNotFound() throws Exception {
        when(repo.findById("i1")).thenReturn(Optional.empty());
        mvc.perform(put("/api/inventory/i1").contentType("application/json").content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFound() throws Exception {
        when(repo.existsById("i1")).thenReturn(true);
        doNothing().when(repo).deleteById("i1");
        mvc.perform(delete("/api/inventory/i1")).andExpect(status().isNoContent());
    }

    @Test
    void deleteNotFound() throws Exception {
        when(repo.existsById("i1")).thenReturn(false);
        mvc.perform(delete("/api/inventory/i1")).andExpect(status().isNotFound());
    }
}