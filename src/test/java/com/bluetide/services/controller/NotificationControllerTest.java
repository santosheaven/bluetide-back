package com.bluetide.services.controller;

import com.bluetide.services.models.Notification;
import com.bluetide.services.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {
    //@Autowired
    private MockMvc mvc;
    //@MockBean
    private NotificationRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAll() throws Exception {
        Notification n = new Notification(); n.setId("n1"); n.setMessage("m");
        when(repo.findAll()).thenReturn(List.of(n));
        mvc.perform(get("/api/notifications")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(n))));
    }

    @Test
    void getByIdFound() throws Exception {
        Notification n = new Notification(); n.setId("n1"); n.setMessage("m");
        when(repo.findById("n1")).thenReturn(Optional.of(n));
        mvc.perform(get("/api/notifications/n1")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(n)));
    }

    @Test
    void create() throws Exception {
        Notification n = new Notification(); n.setMessage("m"); n.setCreatedAt(new Date());
        Notification saved = new Notification(); saved.setId("n2"); saved.setMessage("m");
        when(repo.save(any(Notification.class))).thenReturn(saved);
        mvc.perform(post("/api/notifications").contentType("application/json").content(mapper.writeValueAsString(n)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(saved)));
    }

    @Test
    void updateFound() throws Exception {
        Notification existing = new Notification(); existing.setId("n1");
        Notification updated = new Notification(); updated.setId("n1"); updated.setRead(true);
        when(repo.findById("n1")).thenReturn(Optional.of(existing));
        when(repo.save(any(Notification.class))).thenReturn(updated);
        mvc.perform(put("/api/notifications/n1").contentType("application/json").content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(updated)));
    }

    @Test
    void deleteFound() throws Exception {
        when(repo.existsById("n1")).thenReturn(true);
        doNothing().when(repo).deleteById("n1");
        mvc.perform(delete("/api/notifications/n1")).andExpect(status().isNoContent());
    }
}