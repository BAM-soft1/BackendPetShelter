package org.pet.backendpetshelter.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pet.backendpetshelter.DTO.RegisterUserRequest;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("AuthController Integration Tests")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    // ========== POST /api/auth/register ==========

    @Test
    @DisplayName("POST /api/auth/register - Should return 201 with UserResponse")
    void register_ShouldReturn201WithUserResponse() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("newuser@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhone("12345678");
        request.setPassword("SecurePass123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phone").value("12345678"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 for duplicate email")
    void register_ShouldReturn400ForDuplicateEmail() throws Exception {
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");
        existingUser.setPhone("55555555");
        existingUser.setPassword(passwordEncoder.encode("ExistingPass123!"));
        existingUser.setIsActive(true);
        userRepository.save(existingUser);

        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("existing@example.com");
        request.setFirstName("New");
        request.setLastName("User");
        request.setPhone("66666666");
        request.setPassword("NewPass123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Email already in use"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 for invalid email format")
    void register_ShouldReturn400ForInvalidEmail() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("not-an-email");
        request.setFirstName("Invalid");
        request.setLastName("Email");
        request.setPhone("22222222");
        request.setPassword("ValidPass123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 for weak password")
    void register_ShouldReturn400ForWeakPassword() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("weakpass@example.com");
        request.setFirstName("Weak");
        request.setLastName("Password");
        request.setPhone("77777777");
        request.setPassword("short");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 for missing required fields")
    void register_ShouldReturn400ForMissingFields() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("missing@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should save user to database")
    void register_ShouldSaveUserToDatabase() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("dbtest@example.com");
        request.setFirstName("Database");
        request.setLastName("Test");
        request.setPhone("87654321");
        request.setPassword("DbPass123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Optional<User> savedUser = userRepository.findByEmail("dbtest@example.com");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo("dbtest@example.com");
        assertThat(savedUser.get().getFirstName()).isEqualTo("Database");
        assertThat(savedUser.get().getLastName()).isEqualTo("Test");
    }

    @Test
    @DisplayName("POST /api/auth/register - Should hash password in database")
    void register_ShouldHashPasswordInDatabase() throws Exception {
        String rawPassword = "MySecurePassword123!";
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("hashtest@example.com");
        request.setFirstName("Hash");
        request.setLastName("Test");
        request.setPhone("11223344");
        request.setPassword(rawPassword);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Optional<User> savedUser = userRepository.findByEmail("hashtest@example.com");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getPassword()).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, savedUser.get().getPassword())).isTrue();
    }

    @Test
    @DisplayName("POST /api/auth/register - Should not issue tokens at registration")
    void register_ShouldNotIssueTokens() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("notoken@example.com");
        request.setFirstName("No");
        request.setLastName("Token");
        request.setPhone("99887766");
        request.setPassword("NoToken123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(cookie().doesNotExist("refreshToken"));
    }
}