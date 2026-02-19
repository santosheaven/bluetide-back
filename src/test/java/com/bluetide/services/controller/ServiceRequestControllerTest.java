package com.bluetide.services.controller;

import com.bluetide.services.models.ServiceRequest;
import com.bluetide.services.service.ServiceRequestService;
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

@WebMvcTest(ServiceRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ServiceRequestControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private ServiceRequestService serviceRequestService;
    @MockBean private JwtUtils jwtUtils;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAll() throws Exception {
        ServiceRequest s = new ServiceRequest(); s.setId("s1"); s.setDescription("d");
        when(serviceRequestService.findAll()).thenReturn(List.of(s));
        mvc.perform(get("/api/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("s1"));
    }

    @Test
    void getByIdFound() throws Exception {
        ServiceRequest s = new ServiceRequest(); s.setId("s1"); s.setDescription("d");
        when(serviceRequestService.getById("s1")).thenReturn(s);
        mvc.perform(get("/api/services/s1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("s1"));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(serviceRequestService.getById("s1")).thenThrow(new ResourceNotFoundException("ServiceRequest", "id", "s1"));
        mvc.perform(get("/api/services/s1")).andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        ServiceRequest s = new ServiceRequest(); s.setDescription("d"); s.setPropertyId("p1"); s.setServiceType("REPAIR");
        ServiceRequest saved = new ServiceRequest(); saved.setId("s2"); saved.setDescription("d"); saved.setPropertyId("p1"); saved.setServiceType("REPAIR");
        when(serviceRequestService.create(any(ServiceRequest.class))).thenReturn(saved);
        mvc.perform(post("/api/services").contentType("application/json").content(mapper.writeValueAsString(s)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("s2"));
    }

    @Test
    void updateFound() throws Exception {
        ServiceRequest updated = new ServiceRequest(); updated.setId("s1"); updated.setStatus("done"); updated.setPropertyId("p1"); updated.setServiceType("REPAIR");
        when(serviceRequestService.update(eq("s1"), any(ServiceRequest.class))).thenReturn(updated);
        mvc.perform(put("/api/services/s1").contentType("application/json").content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("done"));
    }

    @Test
    void updateNotFound() throws Exception {
        when(serviceRequestService.update(eq("s1"), any(ServiceRequest.class)))
                .thenThrow(new ResourceNotFoundException("ServiceRequest", "id", "s1"));
        ServiceRequest body = new ServiceRequest(); body.setPropertyId("p1"); body.setServiceType("X");
        mvc.perform(put("/api/services/s1").contentType("application/json").content(mapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFound() throws Exception {
        doNothing().when(serviceRequestService).delete("s1");
        mvc.perform(delete("/api/services/s1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("ServiceRequest", "id", "s1")).when(serviceRequestService).delete("s1");
        mvc.perform(delete("/api/services/s1")).andExpect(status().isNotFound());
    }
}