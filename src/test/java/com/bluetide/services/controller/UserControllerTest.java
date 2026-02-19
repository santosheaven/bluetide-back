package com.bluetide.services.controller;

import com.bluetide.services.models.User;
import com.bluetide.services.service.UserService;
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

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private UserService userService;
    @MockBean private JwtUtils jwtUtils;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAll() throws Exception {
        User u = new User(); u.setId("u1"); u.setEmail("a@a.com");
        when(userService.findAll()).thenReturn(List.of(u));
        mvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("u1"));
    }

    @Test
    void getByIdFound() throws Exception {
        User u = new User(); u.setId("u1"); u.setEmail("a@a.com");
        when(userService.getById("u1")).thenReturn(u);
        mvc.perform(get("/api/users/u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("u1"));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(userService.getById("u1")).thenThrow(new ResourceNotFoundException("User", "id", "u1"));
        mvc.perform(get("/api/users/u1")).andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        User u = new User(); u.setEmail("b@b.com"); u.setDisplayName("Bob");
        User saved = new User(); saved.setId("u2"); saved.setEmail("b@b.com"); saved.setDisplayName("Bob");
        when(userService.create(any(User.class))).thenReturn(saved);
        mvc.perform(post("/api/users").contentType("application/json").content(mapper.writeValueAsString(u)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("u2"));
    }

    @Test
    void updateFound() throws Exception {
        User updated = new User(); updated.setId("u1"); updated.setEmail("x@x.com"); updated.setDisplayName("Upd");
        when(userService.update(eq("u1"), any(User.class))).thenReturn(updated);
        mvc.perform(put("/api/users/u1").contentType("application/json").content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("x@x.com"));
    }

    @Test
    void updateNotFound() throws Exception {
        when(userService.update(eq("u1"), any(User.class)))
                .thenThrow(new ResourceNotFoundException("User", "id", "u1"));
        User body = new User(); body.setEmail("x@x.com"); body.setDisplayName("X");
        mvc.perform(put("/api/users/u1").contentType("application/json").content(mapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFound() throws Exception {
        doNothing().when(userService).delete("u1");
        mvc.perform(delete("/api/users/u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User", "id", "u1")).when(userService).delete("u1");
        mvc.perform(delete("/api/users/u1")).andExpect(status().isNotFound());
    }
}