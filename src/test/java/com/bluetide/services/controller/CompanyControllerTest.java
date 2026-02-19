package com.bluetide.services.controller;

import com.bluetide.services.models.Company;
import com.bluetide.services.service.CompanyService;
import com.bluetide.services.exception.ResourceNotFoundException;
import com.bluetide.services.security.JwtAuthenticationFilter;
import com.bluetide.services.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CompanyControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private CompanyService companyService;
    @MockBean private JwtUtils jwtUtils;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAll() throws Exception {
        Company c = new Company(); c.setId("1"); c.setName("Acme");
        when(companyService.findAll()).thenReturn(List.of(c));
        mvc.perform(get("/api/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("1"));
    }

    @Test
    void getByIdFound() throws Exception {
        Company c = new Company(); c.setId("1"); c.setName("Acme");
        when(companyService.getById("1")).thenReturn(c);
        mvc.perform(get("/api/companies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("1"));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(companyService.getById("1")).thenThrow(new ResourceNotFoundException("Company", "id", "1"));
        mvc.perform(get("/api/companies/1")).andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        Company c = new Company(); c.setName("NewCo");
        Company saved = new Company(); saved.setId("2"); saved.setName("NewCo");
        when(companyService.create(any(Company.class))).thenReturn(saved);
        mvc.perform(post("/api/companies")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(c)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("2"));
    }

    @Test
    void updateFound() throws Exception {
        Company updated = new Company(); updated.setId("1"); updated.setName("AcmeUpdated");
        when(companyService.update(eq("1"), any(Company.class))).thenReturn(updated);
        mvc.perform(put("/api/companies/1")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("AcmeUpdated"));
    }

    @Test
    void updateNotFound() throws Exception {
        when(companyService.update(eq("1"), any(Company.class)))
                .thenThrow(new ResourceNotFoundException("Company", "id", "1"));
        Company body = new Company(); body.setName("X");
        mvc.perform(put("/api/companies/1")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFound() throws Exception {
        doNothing().when(companyService).delete("1");
        mvc.perform(delete("/api/companies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Company", "id", "1")).when(companyService).delete("1");
        mvc.perform(delete("/api/companies/1")).andExpect(status().isNotFound());
    }
}