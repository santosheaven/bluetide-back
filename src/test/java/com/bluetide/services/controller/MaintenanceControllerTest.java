package com.bluetide.services.controller;

import com.bluetide.services.models.Maintenance;
import com.bluetide.services.service.MaintenanceService;
import com.bluetide.services.exception.ResourceNotFoundException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MaintenanceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MaintenanceControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private MaintenanceService maintenanceService;
    @MockBean private JwtUtils jwtUtils;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAll() throws Exception {
        Maintenance m = new Maintenance(); m.setId("m1"); m.setDescription("d");
        when(maintenanceService.findAll()).thenReturn(List.of(m));
        mvc.perform(get("/api/maintenance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("m1"));
    }

    @Test
    void getByIdFound() throws Exception {
        Maintenance m = new Maintenance(); m.setId("m1"); m.setDescription("d");
        when(maintenanceService.getById("m1")).thenReturn(m);
        mvc.perform(get("/api/maintenance/m1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("m1"));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(maintenanceService.getById("m1")).thenThrow(new ResourceNotFoundException("Maintenance", "id", "m1"));
        mvc.perform(get("/api/maintenance/m1")).andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        Maintenance m = new Maintenance(); m.setDescription("d"); m.setInventoryId("inv1");
        Maintenance saved = new Maintenance(); saved.setId("m2"); saved.setDescription("d"); saved.setInventoryId("inv1");
        when(maintenanceService.create(any(Maintenance.class))).thenReturn(saved);
        mvc.perform(post("/api/maintenance").contentType("application/json").content(mapper.writeValueAsString(m)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("m2"));
    }

    @Test
    void updateFound() throws Exception {
        Maintenance updated = new Maintenance(); updated.setId("m1"); updated.setDescription("upd"); updated.setInventoryId("inv1");
        when(maintenanceService.update(eq("m1"), any(Maintenance.class))).thenReturn(updated);
        mvc.perform(put("/api/maintenance/m1").contentType("application/json").content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.description").value("upd"));
    }

    @Test
    void updateNotFound() throws Exception {
        when(maintenanceService.update(eq("m1"), any(Maintenance.class)))
                .thenThrow(new ResourceNotFoundException("Maintenance", "id", "m1"));
        Maintenance body = new Maintenance(); body.setDescription("X"); body.setInventoryId("inv1");
        mvc.perform(put("/api/maintenance/m1").contentType("application/json").content(mapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFound() throws Exception {
        doNothing().when(maintenanceService).delete("m1");
        mvc.perform(delete("/api/maintenance/m1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Maintenance", "id", "m1")).when(maintenanceService).delete("m1");
        mvc.perform(delete("/api/maintenance/m1")).andExpect(status().isNotFound());
    }
}