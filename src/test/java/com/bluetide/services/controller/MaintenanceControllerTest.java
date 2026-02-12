package com.bluetide.services.controller;
import com.bluetide.services.models.Maintenance;
import com.bluetide.services.repository.MaintenanceRepository;
import com.bluetide.services.security.JwtAuthenticationFilter;
import com.bluetide.services.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MaintenanceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MaintenanceControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private MaintenanceRepository repo;
    @MockBean private JwtUtils jwtUtils;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAll() throws Exception {
        Maintenance m = new Maintenance(); m.setId("m1"); m.setDescription("d");
        when(repo.findAll()).thenReturn(List.of(m));
        mvc.perform(get("/api/maintenance")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(m))));
    }

    @Test
    void getByIdFound() throws Exception {
        Maintenance m = new Maintenance(); m.setId("m1"); m.setDescription("d");
        when(repo.findById("m1")).thenReturn(Optional.of(m));
        mvc.perform(get("/api/maintenance/m1")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(m)));
    }

    @Test
    void create() throws Exception {
        Maintenance m = new Maintenance(); m.setDescription("d"); m.setDate(new Date());
        Maintenance saved = new Maintenance(); saved.setId("m2"); saved.setDescription("d");
        when(repo.save(any(Maintenance.class))).thenReturn(saved);
        mvc.perform(post("/api/maintenance").contentType("application/json").content(mapper.writeValueAsString(m)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(saved)));
    }

    @Test
    void updateFound() throws Exception {
        Maintenance existing = new Maintenance(); existing.setId("m1");
        Maintenance updated = new Maintenance(); updated.setId("m1"); updated.setDescription("upd");
        when(repo.findById("m1")).thenReturn(Optional.of(existing));
        when(repo.save(any(Maintenance.class))).thenReturn(updated);
        mvc.perform(put("/api/maintenance/m1").contentType("application/json").content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(updated)));
    }

    @Test
    void deleteFound() throws Exception {
        when(repo.existsById("m1")).thenReturn(true);
        doNothing().when(repo).deleteById("m1");
        mvc.perform(delete("/api/maintenance/m1")).andExpect(status().isNoContent());
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(repo.findById("m1")).thenReturn(Optional.empty());
        mvc.perform(get("/api/maintenance/m1")).andExpect(status().isNotFound());
    }
}