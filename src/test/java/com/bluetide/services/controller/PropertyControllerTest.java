package com.bluetide.services.controller;

import com.bluetide.services.models.Property;
import com.bluetide.services.service.PropertyService;
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

@WebMvcTest(PropertyController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PropertyControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private PropertyService propertyService;
    @MockBean private JwtUtils jwtUtils;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAll() throws Exception {
        Property p = new Property(); p.setId("p1"); p.setAddress("Addr");
        when(propertyService.findAll()).thenReturn(List.of(p));
        mvc.perform(get("/api/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("p1"));
    }

    @Test
    void getByIdFound() throws Exception {
        Property p = new Property(); p.setId("p1"); p.setAddress("Addr");
        when(propertyService.getById("p1")).thenReturn(p);
        mvc.perform(get("/api/properties/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("p1"));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(propertyService.getById("p1")).thenThrow(new ResourceNotFoundException("Property", "id", "p1"));
        mvc.perform(get("/api/properties/p1")).andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        Property p = new Property(); p.setAddress("New");
        Property saved = new Property(); saved.setId("p2"); saved.setAddress("New");
        when(propertyService.create(any(Property.class))).thenReturn(saved);
        mvc.perform(post("/api/properties").contentType("application/json").content(mapper.writeValueAsString(p)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("p2"));
    }

    @Test
    void updateFound() throws Exception {
        Property updated = new Property(); updated.setId("p1"); updated.setAddress("Upd");
        when(propertyService.update(eq("p1"), any(Property.class))).thenReturn(updated);
        mvc.perform(put("/api/properties/p1").contentType("application/json").content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.address").value("Upd"));
    }

    @Test
    void updateNotFound() throws Exception {
        when(propertyService.update(eq("p1"), any(Property.class)))
                .thenThrow(new ResourceNotFoundException("Property", "id", "p1"));
        Property body = new Property(); body.setAddress("X");
        mvc.perform(put("/api/properties/p1").contentType("application/json").content(mapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFound() throws Exception {
        doNothing().when(propertyService).delete("p1");
        mvc.perform(delete("/api/properties/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Property", "id", "p1")).when(propertyService).delete("p1");
        mvc.perform(delete("/api/properties/p1")).andExpect(status().isNotFound());
    }
}