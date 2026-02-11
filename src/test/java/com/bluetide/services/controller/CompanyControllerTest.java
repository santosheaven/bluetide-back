package com.bluetide.services.controller;

import com.bluetide.services.models.Company;
import com.bluetide.services.repository.CompanyRepository;
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

@WebMvcTest(CompanyController.class)
public class CompanyControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CompanyRepository repo;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAll() throws Exception {
        Company c = new Company(); c.setId("1"); c.setName("Acme");
        when(repo.findAll()).thenReturn(List.of(c));
        mvc.perform(get("/api/companies"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(c))));
    }

    @Test
    void getByIdFound() throws Exception {
        Company c = new Company(); c.setId("1"); c.setName("Acme");
        when(repo.findById("1")).thenReturn(Optional.of(c));
        mvc.perform(get("/api/companies/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(c)));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(repo.findById("1")).thenReturn(Optional.empty());
        mvc.perform(get("/api/companies/1")).andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        Company c = new Company(); c.setName("NewCo");
        Company saved = new Company(); saved.setId("2"); saved.setName("NewCo");
        when(repo.save(any(Company.class))).thenReturn(saved);
        mvc.perform(post("/api/companies")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(c)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(saved)));
    }

    @Test
    void updateFound() throws Exception {
        Company existing = new Company(); existing.setId("1"); existing.setName("Acme");
        Company updated = new Company(); updated.setId("1"); updated.setName("AcmeUpdated");
        when(repo.findById("1")).thenReturn(Optional.of(existing));
        when(repo.save(any(Company.class))).thenReturn(updated);
        mvc.perform(put("/api/companies/1")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(updated)));
    }

    @Test
    void updateNotFound() throws Exception {
        when(repo.findById("1")).thenReturn(Optional.empty());
        mvc.perform(put("/api/companies/1")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFound() throws Exception {
        when(repo.existsById("1")).thenReturn(true);
        doNothing().when(repo).deleteById("1");
        mvc.perform(delete("/api/companies/1")).andExpect(status().isNoContent());
    }

    @Test
    void deleteNotFound() throws Exception {
        when(repo.existsById("1")).thenReturn(false);
        mvc.perform(delete("/api/companies/1")).andExpect(status().isNotFound());
    }
}