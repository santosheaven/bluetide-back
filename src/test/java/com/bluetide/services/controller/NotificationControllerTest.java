package com.bluetide.services.controller;

import com.bluetide.services.models.Notification;
import com.bluetide.services.service.NotificationService;
import com.bluetide.services.exception.ResourceNotFoundException;
import com.bluetide.services.security.JwtAuthenticationFilter;
import com.bluetide.services.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class NotificationControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private NotificationService notificationService;
    @MockBean private JwtUtils jwtUtils;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAll() throws Exception {
        Notification n = new Notification(); n.setId("n1"); n.setMessage("m");
        when(notificationService.findAll()).thenReturn(List.of(n));
        mvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("n1"));
    }

    @Test
    void getByIdFound() throws Exception {
        Notification n = new Notification(); n.setId("n1"); n.setMessage("m");
        when(notificationService.getById("n1")).thenReturn(n);
        mvc.perform(get("/api/notifications/n1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("n1"));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(notificationService.getById("n1")).thenThrow(new ResourceNotFoundException("Notification", "id", "n1"));
        mvc.perform(get("/api/notifications/n1")).andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        Notification n = new Notification(); n.setMessage("m"); n.setUserId("u1"); n.setType("INFO");
        Notification saved = new Notification(); saved.setId("n2"); saved.setMessage("m"); saved.setUserId("u1"); saved.setType("INFO");
        when(notificationService.create(any(Notification.class))).thenReturn(saved);
        mvc.perform(post("/api/notifications").contentType("application/json").content(mapper.writeValueAsString(n)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("n2"));
    }

    @Test
    void updateFound() throws Exception {
        Notification existing = new Notification(); existing.setId("n1"); existing.setMessage("old"); existing.setUserId("u1"); existing.setType("INFO");
        Notification updated = new Notification(); updated.setId("n1"); updated.setMessage("new"); updated.setRead(true); updated.setUserId("u1"); updated.setType("INFO");
        when(notificationService.getById("n1")).thenReturn(existing);
        when(notificationService.create(any(Notification.class))).thenReturn(updated);
        mvc.perform(put("/api/notifications/n1").contentType("application/json").content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("n1"));
    }

    @Test
    void deleteFound() throws Exception {
        doNothing().when(notificationService).delete("n1");
        mvc.perform(delete("/api/notifications/n1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Notification", "id", "n1")).when(notificationService).delete("n1");
        mvc.perform(delete("/api/notifications/n1")).andExpect(status().isNotFound());
    }
}