package com.bluetide.services.controller;

import com.bluetide.services.models.Invoice;
import com.bluetide.services.service.InvoiceService;
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

@WebMvcTest(InvoiceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class InvoiceControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private InvoiceService invoiceService;
    @MockBean private JwtUtils jwtUtils;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAll() throws Exception {
        Invoice inv = new Invoice(); inv.setId("inv1"); inv.setAmount(100.0);
        when(invoiceService.findAll()).thenReturn(List.of(inv));
        mvc.perform(get("/api/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("inv1"));
    }

    @Test
    void getByIdFound() throws Exception {
        Invoice inv = new Invoice(); inv.setId("inv1"); inv.setAmount(100.0);
        when(invoiceService.getById("inv1")).thenReturn(inv);
        mvc.perform(get("/api/invoices/inv1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("inv1"));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(invoiceService.getById("inv1")).thenThrow(new ResourceNotFoundException("Invoice", "id", "inv1"));
        mvc.perform(get("/api/invoices/inv1")).andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        Invoice inv = new Invoice(); inv.setAmount(50.0); inv.setPropertyId("p1"); inv.setServiceType("PLUMBING");
        Invoice saved = new Invoice(); saved.setId("inv2"); saved.setAmount(50.0); saved.setPropertyId("p1"); saved.setServiceType("PLUMBING");
        when(invoiceService.create(any(Invoice.class))).thenReturn(saved);
        mvc.perform(post("/api/invoices").contentType("application/json").content(mapper.writeValueAsString(inv)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("inv2"));
    }

    @Test
    void updateFound() throws Exception {
        Invoice updated = new Invoice(); updated.setId("inv1"); updated.setAmount(200.0); updated.setPropertyId("p1"); updated.setServiceType("PLUMBING");
        when(invoiceService.update(eq("inv1"), any(Invoice.class))).thenReturn(updated);
        mvc.perform(put("/api/invoices/inv1").contentType("application/json").content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.amount").value(200.0));
    }

    @Test
    void updateNotFound() throws Exception {
        when(invoiceService.update(eq("inv1"), any(Invoice.class)))
                .thenThrow(new ResourceNotFoundException("Invoice", "id", "inv1"));
        Invoice body = new Invoice(); body.setAmount(1.0); body.setPropertyId("p1"); body.setServiceType("X");
        mvc.perform(put("/api/invoices/inv1").contentType("application/json").content(mapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFound() throws Exception {
        doNothing().when(invoiceService).delete("inv1");
        mvc.perform(delete("/api/invoices/inv1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Invoice", "id", "inv1")).when(invoiceService).delete("inv1");
        mvc.perform(delete("/api/invoices/inv1")).andExpect(status().isNotFound());
    }
}