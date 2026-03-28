package com.lpu.authservice.controller;

import com.lpu.authservice.entity.User;
import com.lpu.authservice.repository.UserRepository;
import com.lpu.authservice.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    // Needed by SecurityConfig
    @MockBean
    private com.lpu.authservice.security.JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ POST /auth/register → 201
    @Test
    void testRegister_Returns201() throws Exception {
        User user = new User(null, "test@gmail.com", "pass123", null, false);
        when(authService.register(any())).thenReturn("Registered successfully");

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Registered successfully"));
    }

    // ✅ POST /auth/login → 200
    @Test
    void testLogin_Returns200() throws Exception {
        User user = new User(null, "test@gmail.com", "pass123", null, false);
        when(authService.login("test@gmail.com", "pass123")).thenReturn("jwt.token.here");

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("jwt.token.here"));
    }

    // ✅ GET /auth/user/{id} → 200
    @Test
    @WithMockUser(roles = "USER")
    void testGetUser_Returns200() throws Exception {
        User user = new User(1L, "test@gmail.com", "encodedPass", "ROLE_USER", false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/auth/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
    }
}
