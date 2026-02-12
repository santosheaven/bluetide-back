package com.bluetide.services.controller;
import com.bluetide.services.models.User;
import com.bluetide.services.repository.UserRepository;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private UserRepository repo;
    @MockBean private JwtUtils jwtUtils;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAll() throws Exception {
        User u = new User(); u.setId("u1"); u.setEmail("a@a.com");
        when(repo.findAll()).thenReturn(List.of(u));
        mvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(u))));
    }

    @Test
    void getByIdFound() throws Exception {
        User u = new User(); u.setId("u1"); u.setEmail("a@a.com");
        when(repo.findById("u1")).thenReturn(Optional.of(u));
        mvc.perform(get("/api/users/u1")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(u)));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(repo.findById("u1")).thenReturn(Optional.empty());
        mvc.perform(get("/api/users/u1")).andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        User u = new User(); u.setEmail("b@b.com");
        User saved = new User(); saved.setId("u2"); saved.setEmail("b@b.com");
        when(repo.save(any(User.class))).thenReturn(saved);
        mvc.perform(post("/api/users").contentType("application/json").content(mapper.writeValueAsString(u)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(saved)));
    }

    @Test
    void updateFound() throws Exception {
        User existing = new User(); existing.setId("u1");
        User updated = new User(); updated.setEmail("x@x.com");
        updated.setId("u1");
        when(repo.findById("u1")).thenReturn(Optional.of(existing));
        when(repo.save(any(User.class))).thenReturn(updated);
        mvc.perform(put("/api/users/u1").contentType("application/json").content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(updated)));
    }

    @Test
    void updateNotFound() throws Exception {
        when(repo.findById("u1")).thenReturn(Optional.empty());
        mvc.perform(put("/api/users/u1").contentType("application/json").content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFound() throws Exception {
        when(repo.existsById("u1")).thenReturn(true);
        doNothing().when(repo).deleteById("u1");
        mvc.perform(delete("/api/users/u1")).andExpect(status().isNoContent());
    }

    @Test
    void deleteNotFound() throws Exception {
        when(repo.existsById("u1")).thenReturn(false);
        mvc.perform(delete("/api/users/u1")).andExpect(status().isNotFound());
    }
}