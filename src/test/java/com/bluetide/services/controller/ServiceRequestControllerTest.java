package com.bluetide.services.controller;

import com.bluetide.services.models.ServiceRequest;
import com.bluetide.services.repository.ServiceRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

@WebMvcTest(ServiceRequestController.class)
public class ServiceRequestControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private ServiceRequestRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAll() throws Exception {
        ServiceRequest s = new ServiceRequest(); s.setId("s1"); s.setDescription("d");
        when(repo.findAll()).thenReturn(List.of(s));
        mvc.perform(get("/api/services")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(s))));
    }

    @Test
    void getByIdFound() throws Exception {
        ServiceRequest s = new ServiceRequest(); s.setId("s1"); s.setDescription("d");
        when(repo.findById("s1")).thenReturn(Optional.of(s));
        mvc.perform(get("/api/services/s1")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(s)));
    }

    @Test
    void create() throws Exception {
        ServiceRequest s = new ServiceRequest(); s.setDescription("d"); s.setCreatedAt(new Date());
        ServiceRequest saved = new ServiceRequest(); saved.setId("s2"); saved.setDescription("d");
        when(repo.save(any(ServiceRequest.class))).thenReturn(saved);
        mvc.perform(post("/api/services").contentType("application/json").content(mapper.writeValueAsString(s)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(saved)));
    }

    @Test
    void updateFound() throws Exception {
        ServiceRequest existing = new ServiceRequest(); existing.setId("s1");
        ServiceRequest updated = new ServiceRequest(); updated.setId("s1"); updated.setStatus("done");
        when(repo.findById("s1")).thenReturn(Optional.of(existing));
        when(repo.save(any(ServiceRequest.class))).thenReturn(updated);
        mvc.perform(put("/api/services/s1").contentType("application/json").content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(updated)));
    }

    @Test
    void deleteFound() throws Exception {
        when(repo.existsById("s1")).thenReturn(true);
        doNothing().when(repo).deleteById("s1");
        mvc.perform(delete("/api/services/s1")).andExpect(status().isNoContent());
    }
}