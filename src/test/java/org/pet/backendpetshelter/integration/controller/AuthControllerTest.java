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

    // ========== POST /api/auth/register ==========

    @Test
    @DisplayName("POST /api/auth/register - Should return 201 with UserResponse")
    void register_ShouldReturn201WithUserResponse() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("newuser@example.com");
        request.setFirstName("Mohamed");
        request.setLastName("Salah");
        request.setPhone("12345678");
        request.setPassword("SecurePass123!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.firstName").value("Mohamed"))
                .andExpect(jsonPath("$.lastName").value("Salah"))
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
    // ========== POST /api/auth/login ==========

    @Test
    @DisplayName("POST /api/auth/login - Should return 200 with AuthResponse on successful login")
    void login_ShouldReturn200WithAuthResponse() throws Exception {
        User user = new User();
        user.setEmail("loginuser@example.com");
        user.setFirstName("Login");
        user.setLastName("User");
        user.setPhone("12345678");
        user.setPassword(passwordEncoder.encode("Test123!"));
        user.setIsActive(true);
        user.setRole(Roles.USER);
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("loginuser@example.com");
        request.setPassword("Test123!");

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
        User user = new User();
        user.setEmail("tokenuser@example.com");
        user.setFirstName("Token");
        user.setLastName("User");
        user.setPhone("22223333");
        user.setPassword(passwordEncoder.encode("Token123!"));
        user.setIsActive(true);
        user.setRole(Roles.USER);
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("tokenuser@example.com");
        request.setPassword("Token123!");

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
        User user = new User();
        user.setEmail("headeruser@example.com");
        user.setFirstName("Header");
        user.setLastName("User");
        user.setPhone("33334444");
        user.setPassword(passwordEncoder.encode("Header123!"));
        user.setIsActive(true);
        user.setRole(Roles.USER);
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("headeruser@example.com");
        request.setPassword("Header123!");

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
        User user = new User();
        user.setEmail("cookieuser@example.com");
        user.setFirstName("Cookie");
        user.setLastName("User");
        user.setPhone("44445555");
        user.setPassword(passwordEncoder.encode("Cookie123!"));
        user.setIsActive(true);
        user.setRole(Roles.USER);
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("cookieuser@example.com");
        request.setPassword("Cookie123!");

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
        User user = new User();
        user.setEmail("invalidlogin@example.com");
        user.setFirstName("Invalid");
        user.setLastName("User");
        user.setPhone("55556666");
        user.setPassword(passwordEncoder.encode("Correct123!"));
        user.setIsActive(true);
        user.setRole(Roles.USER);
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("invalidlogin@example.com");
        request.setPassword("WrongPassword!");

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
        LoginRequest request = new LoginRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("SomePassword123!");

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
        User user = new User();
        user.setEmail("inactive@example.com");
        user.setFirstName("Inactive");
        user.setLastName("User");
        user.setPhone("66667777");
        user.setPassword(passwordEncoder.encode("Inactive123!"));
        user.setIsActive(false);
        user.setRole(Roles.USER);
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("inactive@example.com");
        request.setPassword("Inactive123!");

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
        User user = new User();
        user.setEmail("deleteold@example.com");
        user.setFirstName("Delete");
        user.setLastName("Old");
        user.setPhone("77778888");
        user.setPassword(passwordEncoder.encode("DeleteOld123!"));
        user.setIsActive(true);
        user.setRole(Roles.USER);
        User savedUser = userRepository.save(user);

        // Create old refresh token
        RefreshToken oldToken = new RefreshToken();
        oldToken.setUser(savedUser);
        oldToken.setToken("old-refresh-token-123");
        oldToken.setRevoked(false);
        oldToken.setExpiresAt(java.time.Instant.now().plusSeconds(3600));
        refreshTokenRepository.save(oldToken);

        LoginRequest request = new LoginRequest();
        request.setEmail("deleteold@example.com");
        request.setPassword("DeleteOld123!");

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
        User user = new User();
        user.setEmail("newtoken@example.com");
        user.setFirstName("New");
        user.setLastName("Token");
        user.setPhone("88889999");
        user.setPassword(passwordEncoder.encode("NewToken123!"));
        user.setIsActive(true);
        user.setRole(Roles.USER);
        User savedUser = userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("newtoken@example.com");
        request.setPassword("NewToken123!");

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
        User user = new User();
        user.setEmail("logoutuser@example.com");
        user.setFirstName("Logout");
        user.setLastName("User");
        user.setPhone("11112222");
        user.setPassword(passwordEncoder.encode("Logout123!"));
        user.setIsActive(true);
        user.setRole(Roles.USER);
        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(
                savedUser.getEmail(),
                Map.of("role", savedUser.getRole().name(), "uid", savedUser.getId()));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(savedUser);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(java.time.Instant.now().plusSeconds(3600));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);

        mockMvc.perform(post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .cookie(new Cookie("refresh_token", refreshToken.getToken())))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/auth/logout - Should revoke refresh token in database")
    void logout_ShouldRevokeRefreshTokenInDatabase() throws Exception {
        User user = new User();
        user.setEmail("revoketoken@example.com");
        user.setFirstName("Revoke");
        user.setLastName("Token");
        user.setPhone("22223333");
        user.setPassword(passwordEncoder.encode("Revoke123!"));
        user.setIsActive(true);
        user.setRole(Roles.USER);
        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(
                savedUser.getEmail(),
                Map.of("role", savedUser.getRole().name(), "uid", savedUser.getId()));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(savedUser);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(java.time.Instant.now().plusSeconds(3600));
        refreshToken.setRevoked(false);
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
        User user = new User();
        user.setEmail("denylist@example.com");
        user.setFirstName("Deny");
        user.setLastName("List");
        user.setPhone("33334444");
        user.setPassword(passwordEncoder.encode("Denylist123!"));
        user.setIsActive(true);
        user.setRole(Roles.USER);
        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(
                savedUser.getEmail(),
                Map.of("role", savedUser.getRole().name(), "uid", savedUser.getId()));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(savedUser);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(java.time.Instant.now().plusSeconds(3600));
        refreshToken.setRevoked(false);
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
        User user = new User();
        user.setEmail("deletecookie@example.com");
        user.setFirstName("Delete");
        user.setLastName("Cookie");
        user.setPhone("44445555");
        user.setPassword(passwordEncoder.encode("DeleteCookie123!"));
        user.setIsActive(true);
        user.setRole(Roles.USER);
        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(
                savedUser.getEmail(),
                Map.of("role", savedUser.getRole().name(), "uid", savedUser.getId()));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(savedUser);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(java.time.Instant.now().plusSeconds(3600));
        refreshToken.setRevoked(false);
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
        User user = new User();
        user.setEmail("revokedtoken@example.com");
        user.setFirstName("Revoked");
        user.setLastName("Token");
        user.setPhone("55556666");
        user.setPassword(passwordEncoder.encode("Revoked123!"));
        user.setIsActive(true);
        user.setRole(Roles.USER);
        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(
                savedUser.getEmail(),
                Map.of("role", savedUser.getRole().name(), "uid", savedUser.getId()));

        // Add token to denylist (simulate previous logout)
        tokenDenylistService.deny(accessToken, 3600);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(savedUser);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(java.time.Instant.now().plusSeconds(3600));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);

        mockMvc.perform(post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .cookie(new Cookie("refresh_token", refreshToken.getToken())))
                .andExpect(status().isUnauthorized());
    }
}