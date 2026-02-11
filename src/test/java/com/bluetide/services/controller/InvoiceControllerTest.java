package com.bluetide.services.controller;

import com.bluetide.services.models.Invoice;
import com.bluetide.services.repository.InvoiceRepository;
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

@WebMvcTest(InvoiceController.class)
public class InvoiceControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private InvoiceRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAll() throws Exception {
        Invoice inv = new Invoice(); inv.setId("inv1"); inv.setAmount(100.0);
        when(repo.findAll()).thenReturn(List.of(inv));
        mvc.perform(get("/api/invoices")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(inv))));
    }

    @Test
    void getByIdFound() throws Exception {
        Invoice inv = new Invoice(); inv.setId("inv1"); inv.setAmount(100.0);
        when(repo.findById("inv1")).thenReturn(Optional.of(inv));
        mvc.perform(get("/api/invoices/inv1")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(inv)));
    }

    @Test
    void create() throws Exception {
        Invoice inv = new Invoice(); inv.setAmount(50.0); inv.setPaymentDate(new Date());
        Invoice saved = new Invoice(); saved.setId("inv2"); saved.setAmount(50.0);
        when(repo.save(any(Invoice.class))).thenReturn(saved);
        mvc.perform(post("/api/invoices").contentType("application/json").content(mapper.writeValueAsString(inv)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(saved)));
    }

    @Test
    void updateFound() throws Exception {
        Invoice existing = new Invoice(); existing.setId("inv1");
        Invoice updated = new Invoice(); updated.setId("inv1"); updated.setAmount(200.0);
        when(repo.findById("inv1")).thenReturn(Optional.of(existing));
        when(repo.save(any(Invoice.class))).thenReturn(updated);
        mvc.perform(put("/api/invoices/inv1").contentType("application/json").content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(updated)));
    }

    @Test
    void deleteFound() throws Exception {
        when(repo.existsById("inv1")).thenReturn(true);
        doNothing().when(repo).deleteById("inv1");
        mvc.perform(delete("/api/invoices/inv1")).andExpect(status().isNoContent());
    }
}