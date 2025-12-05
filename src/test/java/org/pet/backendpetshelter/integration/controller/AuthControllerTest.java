package org.pet.backendpetshelter.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pet.backendpetshelter.Roles;
import org.pet.backendpetshelter.Configuration.RefreshToken;
import org.pet.backendpetshelter.DTO.AuthResponse;
import org.pet.backendpetshelter.DTO.LoginRequest;
import org.pet.backendpetshelter.DTO.RegisterUserRequest;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Repository.RefreshTokenRepository;
import org.pet.backendpetshelter.Repository.UserRepository;
import org.pet.backendpetshelter.Configuration.JwtService;
import org.pet.backendpetshelter.Service.TokenDenylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.UUID;

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

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TokenDenylistService tokenDenylistService;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    // Create User entity 
    private User createUserEntity(String email, String password, boolean isActive) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhone("12345678");
        user.setPassword(passwordEncoder.encode(password));
        user.setIsActive(isActive);
        user.setRole(Roles.USER);
        return user;
    }

    // Create default User entity (not saved)
    private User createDefaultUserEntity() {
        return createUserEntity("test@example.com", "Test123!", true);
    }

    // Create RegisterUserRequest DTO
    private RegisterUserRequest createRegisterRequest(String email, String password) {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail(email);
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPhone("12345678");
        request.setPassword(password);
        return request;
    }

    // Create LoginRequest DTO
    private LoginRequest createLoginRequest(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    // Helper for creating tokens
    private String createAccessToken(User user) {
        return jwtService.generateAccessToken(
                user.getEmail(),
                Map.of("role", user.getRole().name(), "uid", user.getId()));
    }

    // Helper for creating refresh tokens 
    private RefreshToken createRefreshTokenEntity(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(java.time.Instant.now().plusSeconds(3600));
        refreshToken.setRevoked(false);
        return refreshToken;
    }

    // ========== POST /api/auth/register ==========

    @Test
    @DisplayName("POST /api/auth/register - Should return 201 with UserResponse")
    void register_ShouldReturn201WithUserResponse() throws Exception {
        RegisterUserRequest request = createRegisterRequest("newuser@example.com", "SecurePass123!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.phone").value("12345678"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 for duplicate email")
    void register_ShouldReturn400ForDuplicateEmail() throws Exception {
        User existingUser = createUserEntity("existing@example.com", "ExistingPass123!", true);
        userRepository.save(existingUser);

        RegisterUserRequest request = createRegisterRequest("existing@example.com", "NewPass123!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Email already in use"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 for invalid email format")
    void register_ShouldReturn400ForInvalidEmail() throws Exception {
        RegisterUserRequest request = createRegisterRequest("not-an-email", "ValidPass123!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 for weak password")
    void register_ShouldReturn400ForWeakPassword() throws Exception {
        RegisterUserRequest request = createRegisterRequest("weakpass@example.com", "short");

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
        RegisterUserRequest request = createRegisterRequest("dbtest@example.com", "DbPass123!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Optional<User> savedUser = userRepository.findByEmail("dbtest@example.com");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo("dbtest@example.com");
        assertThat(savedUser.get().getFirstName()).isEqualTo("Test");
        assertThat(savedUser.get().getLastName()).isEqualTo("User");
    }

    @Test
    @DisplayName("POST /api/auth/register - Should hash password in database")
    void register_ShouldHashPasswordInDatabase() throws Exception {
        String rawPassword = "MySecurePassword123!";
        RegisterUserRequest request = createRegisterRequest("hashtest@example.com", rawPassword);

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
        RegisterUserRequest request = createRegisterRequest("notoken@example.com", "NoToken123!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(cookie().doesNotExist("refreshToken"));
    }

    // ========== POST /api/auth/login ==========
    @Test
    @DisplayName("POST /api/auth/login - Should return 200 with AuthResponse on successful login")
    void login_ShouldReturn200WithAuthResponse() throws Exception {
        User user = createUserEntity("loginuser@example.com", "Test123!", true);
        userRepository.save(user);

        LoginRequest request = createLoginRequest("loginuser@example.com", "Test123!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresInSeconds").isNumber());
    }

    @Test
    @DisplayName("POST /api/auth/login - Should verify access token in response body")
    void login_ShouldVerifyAccessTokenInResponseBody() throws Exception {
        User user = createUserEntity("tokenuser@example.com", "Token123!", true);
        userRepository.save(user);

        LoginRequest request = createLoginRequest("tokenuser@example.com", "Token123!");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);

        assertThat(authResponse.getAccessToken()).isNotBlank();
        assertThat(authResponse.getTokenType()).isEqualTo("Bearer");
        assertThat(authResponse.getExpiresInSeconds()).isGreaterThan(0);
    }

    @Test
    @DisplayName("POST /api/auth/login - Should verify access token in Authorization header")
    void login_ShouldVerifyAccessTokenInAuthorizationHeader() throws Exception {
        User user = createUserEntity("headeruser@example.com", "Header123!", true);
        userRepository.save(user);

        LoginRequest request = createLoginRequest("headeruser@example.com", "Header123!");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String authHeader = result.getResponse().getHeader(HttpHeaders.AUTHORIZATION);

        assertThat(authHeader).isNotNull();
        assertThat(authHeader).startsWith("Bearer ");
        String tokenFromHeader = authHeader.substring("Bearer ".length());
        assertThat(tokenFromHeader).isNotBlank();
    }

    @Test
    @DisplayName("POST /api/auth/login - Should verify refresh token in HTTP-only cookie")
    void login_ShouldVerifyRefreshTokenInHttpOnlyCookie() throws Exception {
        User user = createUserEntity("cookieuser@example.com", "Cookie123!", true);
        userRepository.save(user);

        LoginRequest request = createLoginRequest("cookieuser@example.com", "Cookie123!");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        Cookie[] cookies = result.getResponse().getCookies();

        assertThat(cookies).isNotNull();
        Cookie refreshCookie = null;
        for (Cookie c : cookies) {
            if ("refresh_token".equals(c.getName())) {
                refreshCookie = c;
                break;
            }
        }
        assertThat(refreshCookie).isNotNull();
        assertThat(refreshCookie.getValue()).isNotBlank();
        assertThat(refreshCookie.isHttpOnly()).isTrue();
        assertThat(refreshCookie.getPath()).isEqualTo("/api/auth");
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 401 for invalid credentials")
    void login_ShouldReturn400ForInvalidCredentials() throws Exception {
        User user = createUserEntity("invalidlogin@example.com", "Correct123!", true);
        userRepository.save(user);

        LoginRequest request = createLoginRequest("invalidlogin@example.com", "WrongPassword!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad request"))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 401 for non-existent user")
    void login_ShouldReturn401ForNonExistentUser() throws Exception {
        // Arrange: no user created
        LoginRequest request = createLoginRequest("nonexistent@example.com", "SomePassword123!");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 403 for inactive user")
    void login_ShouldReturn403ForInactiveUser() throws Exception {
        User user = createUserEntity("inactive@example.com", "Inactive123!", false);
        userRepository.save(user);

        LoginRequest request = createLoginRequest("inactive@example.com", "Inactive123!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("User is deactivated"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Should delete old refresh tokens")
    void login_ShouldDeleteOldRefreshTokens() throws Exception {
        User user = createUserEntity("deleteold@example.com", "DeleteOld123!", true);
        User savedUser = userRepository.save(user);

        // Create old refresh token
        RefreshToken oldToken = createRefreshTokenEntity(savedUser);
        oldToken.setToken("old-refresh-token-123");
        refreshTokenRepository.save(oldToken);

        LoginRequest request = createLoginRequest("deleteold@example.com", "DeleteOld123!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Optional<RefreshToken> deletedToken = refreshTokenRepository.findByToken("old-refresh-token-123");
        assertThat(deletedToken).isEmpty();
    }

    @Test
    @DisplayName("POST /api/auth/login - Should store new refresh token in database")
    void login_ShouldStoreNewRefreshTokenInDatabase() throws Exception {
        User user = createUserEntity("newtoken@example.com", "NewToken123!", true);
        User savedUser = userRepository.save(user);

        LoginRequest request = createLoginRequest("newtoken@example.com", "NewToken123!");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        Cookie[] cookies = result.getResponse().getCookies();
        String refreshTokenValue = null;
        for (Cookie c : cookies) {
            if ("refresh_token".equals(c.getName())) {
                refreshTokenValue = c.getValue();
                break;
            }
        }

        assertThat(refreshTokenValue).isNotNull();
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByToken(refreshTokenValue);
        assertThat(storedToken).isPresent();
        assertThat(storedToken.get().getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(storedToken.get().getRevoked()).isFalse();
        assertThat(storedToken.get().getExpiresAt()).isAfter(java.time.Instant.now());
    }

    // ========== POST /api/auth/logout ==========

    @Test
    @DisplayName("POST /api/auth/logout - Should return 204 No Content on successful logout")
    void logout_ShouldReturn204NoContent() throws Exception {
        User user = createDefaultUserEntity();
        User savedUser = userRepository.save(user);
        String accessToken = createAccessToken(savedUser);
        RefreshToken refreshToken = createRefreshTokenEntity(savedUser);
        refreshTokenRepository.save(refreshToken);

        mockMvc.perform(post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .cookie(new Cookie("refresh_token", refreshToken.getToken())))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/auth/logout - Should revoke refresh token in database")
    void logout_ShouldRevokeRefreshTokenInDatabase() throws Exception {
        User user = createUserEntity("revoketoken@example.com", "Revoke123!", true);
        User savedUser = userRepository.save(user);

        String accessToken = createAccessToken(savedUser);

        RefreshToken refreshToken = createRefreshTokenEntity(savedUser);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

        mockMvc.perform(post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .cookie(new Cookie("refresh_token", savedRefreshToken.getToken())))
                .andExpect(status().isNoContent());

        Optional<RefreshToken> revokedToken = refreshTokenRepository.findByToken(savedRefreshToken.getToken());
        assertThat(revokedToken).isPresent();
        assertThat(revokedToken.get().getRevoked()).isTrue();
    }

    @Test
    @DisplayName("POST /api/auth/logout - Should add access token to denylist")
    void logout_ShouldAddAccessTokenToDenylist() throws Exception {
        User user = createUserEntity("denylist@example.com", "Denylist123!", true);
        User savedUser = userRepository.save(user);

        String accessToken = createAccessToken(savedUser);

        RefreshToken refreshToken = createRefreshTokenEntity(savedUser);
        refreshTokenRepository.save(refreshToken);

        mockMvc.perform(post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .cookie(new Cookie("refresh_token", refreshToken.getToken())))
                .andExpect(status().isNoContent());

        assertThat(tokenDenylistService.isDenied(accessToken)).isTrue();
    }

    @Test
    @DisplayName("POST /api/auth/logout - Should delete refresh token cookie")
    void logout_ShouldDeleteRefreshTokenCookie() throws Exception {
        User user = createUserEntity("deletecookie@example.com", "DeleteCookie123!", true);
        User savedUser = userRepository.save(user);

        String accessToken = createAccessToken(savedUser);

        RefreshToken refreshToken = createRefreshTokenEntity(savedUser);
        refreshTokenRepository.save(refreshToken);

        MvcResult result = mockMvc.perform(post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .cookie(new Cookie("refresh_token", refreshToken.getToken())))
                .andExpect(status().isNoContent())
                .andReturn();

        Cookie[] cookies = result.getResponse().getCookies();
        assertThat(cookies).isNotNull();
        Cookie deletedCookie = null;
        for (Cookie c : cookies) {
            if ("refresh_token".equals(c.getName())) {
                deletedCookie = c;
                break;
            }
        }
        assertThat(deletedCookie).isNotNull();
        assertThat(deletedCookie.getMaxAge()).isEqualTo(0);
        assertThat(deletedCookie.getValue()).isEmpty();
    }

    @Test
    @DisplayName("POST /api/auth/logout - Should return 401 for missing access token")
    void logout_ShouldReturn401ForMissingAccessToken() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/logout - Should return 401 for revoked access token")
    void logout_ShouldReturn401ForRevokedAccessToken() throws Exception {
        User user = createUserEntity("revokedtoken@example.com", "Revoked123!", true);
        User savedUser = userRepository.save(user);

        String accessToken = createAccessToken(savedUser);

        // Add token to denylist (simulate previous logout)
        tokenDenylistService.deny(accessToken, 3600);

        RefreshToken refreshToken = createRefreshTokenEntity(savedUser);
        refreshTokenRepository.save(refreshToken);

        mockMvc.perform(post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .cookie(new Cookie("refresh_token", refreshToken.getToken())))
                .andExpect(status().isUnauthorized());
    }
}