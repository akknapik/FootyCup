package com.tournament.app.footycup.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tournament.app.footycup.backend.enums.UserRole;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.UserRepository;
import com.tournament.app.footycup.backend.requests.LoginRequest;
import com.tournament.app.footycup.backend.requests.RegistrationRequest;
import com.tournament.app.footycup.backend.service.EmailProducer;
import com.tournament.app.footycup.backend.service.TokenService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authManager;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private EmailProducer emailProducer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_shouldSucceed() throws Exception {
        RegistrationRequest request = new RegistrationRequest("John", "Doe", "john@example.com", "pass");

        Mockito.when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(any())).thenReturn("encoded");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered"));
    }

    @Test
    void register_shouldFailIfEmailExists() throws Exception {
        RegistrationRequest request = new RegistrationRequest("John", "Doe", "john@example.com", "pass");

        Mockito.when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already in use"));
    }

    @Test
    void login_shouldSucceed() throws Exception {
        LoginRequest request = new LoginRequest("john@example.com", "pass");

        User user = new User();
        user.setEmail("john@example.com");
        user.setUserRole(UserRole.USER);

        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
        Mockito.when(authManager.authenticate(any())).thenReturn(auth);
        Mockito.when(tokenService.generateAccessToken(any(), any())).thenReturn("accessToken");
        Mockito.when(tokenService.generateRefreshToken(any(), any())).thenReturn("refreshToken");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expiresIn").value(900));
    }

    @Test
    void refresh_shouldSucceedWithValidToken() throws Exception {
        String refreshToken = "validRefreshToken";
        String accessToken = "newAccessToken";

        User user = new User();
        user.setEmail("john@example.com");
        user.setUserRole(UserRole.USER);

        Mockito.when(tokenService.isValid(refreshToken)).thenReturn(true);
        Mockito.when(tokenService.getEmailFromToken(refreshToken)).thenReturn("john@example.com");
        Mockito.when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        Mockito.when(tokenService.generateAccessToken(any(), any())).thenReturn(accessToken);

        mockMvc.perform(post("/refresh")
                        .cookie(new Cookie("refreshToken", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expiresIn").value(900));
    }

    @Test
    void refresh_shouldFailWithoutToken() throws Exception {
        mockMvc.perform(post("/refresh"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_shouldDeleteCookies() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().isOk());
    }
}
