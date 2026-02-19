package com.bluetide.services.controller;

import com.bluetide.services.models.Inventory;
import com.bluetide.services.service.InventoryService;
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

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class InventoryControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private InventoryService inventoryService;
    @MockBean private JwtUtils jwtUtils;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAll() throws Exception {
        Inventory i = new Inventory(); i.setId("i1"); i.setName("Fridge");
        when(inventoryService.findAll()).thenReturn(List.of(i));
        mvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("i1"));
    }

    @Test
    void getByIdFound() throws Exception {
        Inventory i = new Inventory(); i.setId("i1"); i.setName("Fridge");
        when(inventoryService.getById("i1")).thenReturn(i);
        mvc.perform(get("/api/inventory/i1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("i1"));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(inventoryService.getById("i1")).thenThrow(new ResourceNotFoundException("Inventory", "id", "i1"));
        mvc.perform(get("/api/inventory/i1")).andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        Inventory i = new Inventory(); i.setName("New"); i.setPropertyId("p1");
        Inventory saved = new Inventory(); saved.setId("i2"); saved.setName("New"); saved.setPropertyId("p1");
        when(inventoryService.create(any(Inventory.class))).thenReturn(saved);
        mvc.perform(post("/api/inventory").contentType("application/json").content(mapper.writeValueAsString(i)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("i2"));
    }

    @Test
    void updateFound() throws Exception {
        Inventory updated = new Inventory(); updated.setId("i1"); updated.setName("U"); updated.setPropertyId("p1");
        when(inventoryService.update(eq("i1"), any(Inventory.class))).thenReturn(updated);
        mvc.perform(put("/api/inventory/i1").contentType("application/json").content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("U"));
    }

    @Test
    void updateNotFound() throws Exception {
        when(inventoryService.update(eq("i1"), any(Inventory.class)))
                .thenThrow(new ResourceNotFoundException("Inventory", "id", "i1"));
        Inventory body = new Inventory(); body.setName("X"); body.setPropertyId("p1");
        mvc.perform(put("/api/inventory/i1").contentType("application/json").content(mapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFound() throws Exception {
        doNothing().when(inventoryService).delete("i1");
        mvc.perform(delete("/api/inventory/i1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Inventory", "id", "i1")).when(inventoryService).delete("i1");
        mvc.perform(delete("/api/inventory/i1")).andExpect(status().isNotFound());
    }
}