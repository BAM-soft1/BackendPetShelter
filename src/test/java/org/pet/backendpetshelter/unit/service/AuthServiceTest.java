package org.pet.backendpetshelter.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.pet.backendpetshelter.Configuration.JwtProperties;
import org.pet.backendpetshelter.Configuration.JwtService;
import org.pet.backendpetshelter.Configuration.RefreshToken;
import org.pet.backendpetshelter.DTO.LoginRequest;
import org.pet.backendpetshelter.DTO.RegisterUserRequest;
import org.pet.backendpetshelter.Repository.RefreshTokenRepository;
import org.pet.backendpetshelter.Repository.UserRepository;
import org.pet.backendpetshelter.Service.AuthService;
import org.pet.backendpetshelter.Service.TokenDenylistService;
import org.pet.backendpetshelter.DTO.UserResponse;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Roles;

import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.EntityNotFoundException;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenDenylistService denylistService;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        when(jwtProperties.getAccessExpirationSeconds()).thenReturn(3600L);
        when(jwtProperties.getRefreshExpirationSeconds()).thenReturn(86400L);
        authService = new AuthService(
                userRepository,
                refreshTokenRepository,
                denylistService,
                passwordEncoder,
                jwtService,
                jwtProperties);
    }

    // ==================== BLACKBOX TESTS ====================

    // ----------------------------- Password -----------------------------\\
    @Nested
    @DisplayName("Password Validation Tests - isPasswordStrong()")
    class PasswordValidationTests {

        private Method isPasswordStrongMethod;

        @BeforeEach
        void setUp() throws NoSuchMethodException {
            isPasswordStrongMethod = AuthService.class.getDeclaredMethod("isPasswordStrong", String.class);
            isPasswordStrongMethod.setAccessible(true);
        }

        private boolean invokeIsPasswordStrong(String password) throws Exception {
            return (boolean) isPasswordStrongMethod.invoke(authService, password);
        }

        @Test
        @DisplayName("Should return false when password is null")
        void testPasswordNull() throws Exception {
            assertFalse(invokeIsPasswordStrong(null));
        }

        @Test
        @DisplayName("Should return false when password is empty string")
        void testPasswordEmpty() throws Exception {
            assertFalse(invokeIsPasswordStrong(""));
        }

        @Test
        @DisplayName("Should return false when password has less than 7 characters")
        void testPasswordTooShort() throws Exception {
            assertFalse(invokeIsPasswordStrong("Pa1!"));
        }

        @Test
        @DisplayName("Should return false when password is exactly 6 characters")
        void testPasswordExactly6Characters() throws Exception {
            assertFalse(invokeIsPasswordStrong("Pass1!"));
        }

        @Test
        @DisplayName("Should return false when password has 7 characters but no special character")
        void testPassword7CharsNoSpecial() throws Exception {
            assertFalse(invokeIsPasswordStrong("Pass123"));
        }

        @Test
        @DisplayName("Should return true when password has 7 characters with special character")
        void testPassword7CharsWithSpecial() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass12!"));
        }

        @Test
        @DisplayName("Should return true when password has more than 7 characters with special character")
        void testPasswordLongWithSpecial() throws Exception {
            assertTrue(invokeIsPasswordStrong("Password123!"));
        }

        @Test
        @DisplayName("Should return true when special character is at the beginning")
        void testPasswordSpecialAtBeginning() throws Exception {
            assertTrue(invokeIsPasswordStrong("!Pass123"));
        }

        @Test
        @DisplayName("Should return true when special character is in the middle")
        void testPasswordSpecialInMiddle() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass!123"));
        }

        @Test
        @DisplayName("Should return true when password has multiple special characters")
        void testPasswordMultipleSpecials() throws Exception {
            assertTrue(invokeIsPasswordStrong("P@ss!23#"));
        }

        @Test
        @DisplayName("Should return false when password is only special characters but less than 7")
        void testPasswordOnlySpecialsTooShort() throws Exception {
            assertFalse(invokeIsPasswordStrong("!@#$%^"));
        }

        @Test
        @DisplayName("Should return true when password is only special characters and 7 or more")
        void testPasswordOnlySpecialsLongEnough() throws Exception {
            assertTrue(invokeIsPasswordStrong("!@#$%^&"));
        }

        @Test
        @DisplayName("Should return false when password is only letters and numbers without special character")
        void testPasswordAlphanumericOnly() throws Exception {
            assertFalse(invokeIsPasswordStrong("Password123"));
        }

        @Test
        @DisplayName("Should return false when password has whitespace instead of special character")
        void testPasswordWithWhitespace() throws Exception {
            assertFalse(invokeIsPasswordStrong("Pass 123"));
        }

        @Test
        @DisplayName("Should return true when password contains whitespace and special character")
        void testPasswordWithWhitespaceAndSpecial() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass 12!"));
        }
    }

    // ----------------------------- USER REGISTRATION
    // -----------------------------\\
    @Nested
    @DisplayName("User Registration Tests - register()")
    class UserRegistrationTests {

        // ==================== TEST HELPERS ====================

        private RegisterUserRequest createValidRequest() {
            RegisterUserRequest request = new RegisterUserRequest();
            request.setEmail("test@example.com");
            request.setFirstName("Sergio");
            request.setLastName("Ramos");
            request.setPhone("12345678");
            request.setPassword("Pass123!");
            return request;
        }

        private User createSavedUser(RegisterUserRequest request) {
            User user = new User();
            user.setId(1L);
            user.setEmail(request.getEmail().toLowerCase());
            user.setFirstName(request.getFirstName().trim());
            user.setLastName(request.getLastName().trim());
            user.setPhone(request.getPhone());
            user.setPassword("$2a$10$hashedPassword");
            user.setIsActive(true);
            user.setRole(Roles.USER);
            return user;
        }

        private void mockSuccessfulRegistration(String email) {
            when(userRepository.existsByEmail(email)).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(1L);
                return u;
            });
        }

        // ==================== VALID PARTITION ====================

        @Test
        @DisplayName("Should successfully register user with all valid fields")
        void testValidRegistrationWithAllFields() {
            RegisterUserRequest request = createValidRequest();
            mockSuccessfulRegistration("test@example.com");

            UserResponse response = authService.register(request);

            assertNotNull(response);
            assertEquals("test@example.com", response.getEmail());
            assertEquals("Sergio", response.getFirstName());
            assertEquals("Ramos", response.getLastName());
            assertEquals("12345678", response.getPhone());
            assertEquals(true, response.getIsActive());
            assertEquals(Roles.USER, response.getRole());
            verify(passwordEncoder).encode("Pass123!");
        }

        @Test
        @DisplayName("Should successfully register user with optional phone omitted")
        void testValidRegistrationWithoutPhone() {
            RegisterUserRequest request = createValidRequest();
            request.setPhone(null);
            mockSuccessfulRegistration("test@example.com");

            UserResponse response = authService.register(request);

            assertNull(response.getPhone());
        }

        // ==================== INVALID PARTITIONS ====================

        @Test
        @DisplayName("Should throw IllegalArgumentException when email already exists")
        void testDuplicateEmailThrowsException() {
            RegisterUserRequest request = createValidRequest();
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.register(request));

            assertEquals("Email already in use", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when password is weak (no special character)")
        void testWeakPasswordThrowsException() {
            RegisterUserRequest request = createValidRequest();
            request.setPassword("Pass123");
            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.register(request));

            assertEquals("Password must be at least 7 characters and include a special character",
                    exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when password is too short")
        void testShortPasswordThrowsException() {
            RegisterUserRequest request = createValidRequest();
            request.setPassword("Pass1!");
            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

            assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        }

        // ==================== VERIFICATION TESTS ====================

        @Test
        @DisplayName("Should verify password is BCrypt hashed and not plain text")
        void testPasswordIsHashed() {
            RegisterUserRequest request = createValidRequest();
            mockSuccessfulRegistration("test@example.com");

            authService.register(request);

            verify(passwordEncoder).encode("Pass123!");
            verify(userRepository).save(
                    argThat(user -> user.getPassword().startsWith("$2a$") && !user.getPassword().equals("Pass123!")));
        }

        @Test
        @DisplayName("Should verify default role is USER")
        void testDefaultRoleIsUser() {
            RegisterUserRequest request = createValidRequest();
            mockSuccessfulRegistration("test@example.com");

            UserResponse response = authService.register(request);

            assertEquals(Roles.USER, response.getRole());
            verify(userRepository).save(argThat(user -> user.getRole() == Roles.USER));
        }

        @Test
        @DisplayName("Should verify isActive defaults to true")
        void testDefaultIsActiveIsTrue() {
            RegisterUserRequest request = createValidRequest();
            mockSuccessfulRegistration("test@example.com");

            UserResponse response = authService.register(request);

            assertEquals(true, response.getIsActive());
            verify(userRepository).save(argThat(user -> user.getIsActive() == true));
        }
    }

    // ----------------------------- USER LOGIN -----------------------------\\

    @Nested
    @DisplayName("User Login Tests - loginIssueTokens()")
    class UserLoginTests {

        // ==================== TEST HELPERS ====================

        private LoginRequest createValidLoginRequest() {
            LoginRequest request = new LoginRequest();
            request.setEmail("test@example.com");
            request.setPassword("Pass123!");
            return request;
        }

        private User createActiveUser() {
            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setPassword("$2a$10$hashedPassword");
            user.setFirstName("Sergio");
            user.setLastName("Ramos");
            user.setIsActive(true);
            user.setRole(Roles.USER);
            return user;
        }

        private void mockSuccessfulLogin(User user) {
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("Pass123!", user.getPassword())).thenReturn(true);
            when(jwtService.generateAccessToken(eq(user.getEmail()), anyMap())).thenReturn("access-token-123");
        }

        // ==================== EQUIVALENCE PARTITIONING - VALID PARTITION
        // ====================

        @Test
        @DisplayName("Should successfully login with valid credentials and active user")
        void testSuccessfulLoginWithValidCredentials() {
            LoginRequest request = createValidLoginRequest();
            User user = createActiveUser();
            mockSuccessfulLogin(user);

            AuthService.LoginPair result = authService.loginIssueTokens(request);

            assertNotNull(result);
            assertEquals("access-token-123", result.getAccessToken());
            assertNotNull(result.getRefreshToken());
            assertEquals(3600L, result.getAccessExpiresInSeconds());
            verify(userRepository).findByEmail("test@example.com");
            verify(passwordEncoder).matches("Pass123!", "$2a$10$hashedPassword");
            verify(jwtService).generateAccessToken(eq("test@example.com"), anyMap());
            verify(refreshTokenRepository).deleteByUserId(1L);
            verify(refreshTokenRepository).save(any(RefreshToken.class));
        }

        // ==================== EQUIVALENCE PARTITIONING - INVALID PARTITION 1: WRONG
        // PASSWORD ====================

        @Test
        @DisplayName("Should throw IllegalArgumentException when password is wrong")
        void testLoginWithWrongPasswordFails() {
            LoginRequest request = createValidLoginRequest();
            request.setPassword("WrongPass123!");
            User user = createActiveUser();

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("WrongPass123!", "$2a$10$hashedPassword")).thenReturn(false);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.loginIssueTokens(request));

            assertEquals("Invalid credentials", exception.getMessage());
            verify(passwordEncoder).matches("WrongPass123!", "$2a$10$hashedPassword");
            verify(jwtService, never()).generateAccessToken(anyString(), anyMap());
            verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
        }

        // ==================== EQUIVALENCE PARTITIONING - INVALID PARTITION 2:
        // NON-EXISTENT EMAIL ====================

        @Test
        @DisplayName("Should throw EntityNotFoundException when email does not exist")
        void testLoginWithNonExistentEmailFails() {
            LoginRequest request = createValidLoginRequest();
            request.setEmail("nonexistent@example.com");

            when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> authService.loginIssueTokens(request));

            assertEquals("Invalid credentials", exception.getMessage());
            verify(userRepository).findByEmail("nonexistent@example.com");
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtService, never()).generateAccessToken(anyString(), anyMap());
        }

        // ==================== EQUIVALENCE PARTITIONING - INVALID PARTITION 3: INACTIVE
        // USER ====================

        @Test
        @DisplayName("Should throw IllegalStateException when user is inactive")
        void testLoginWithInactiveUserFails() {
            LoginRequest request = createValidLoginRequest();
            User user = createActiveUser();
            user.setIsActive(false);

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> authService.loginIssueTokens(request));

            assertEquals("User is deactivated", exception.getMessage());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtService, never()).generateAccessToken(anyString(), anyMap());
        }

        // ==================== TOKEN VERIFICATION TESTS ====================

        @Test
        @DisplayName("Should verify refresh token is created and stored with correct properties")
        void testRefreshTokenIsCreatedAndStored() {
            LoginRequest request = createValidLoginRequest();
            User user = createActiveUser();
            mockSuccessfulLogin(user);

            Instant beforeLogin = Instant.now();
            AuthService.LoginPair result = authService.loginIssueTokens(request);
            Instant afterLogin = Instant.now();

            assertNotNull(result.getRefreshToken());
            assertFalse(result.getRefreshToken().isEmpty());

            verify(refreshTokenRepository).save(argThat(rt -> rt.getUser().getId().equals(1L) &&
                    rt.getToken() != null &&
                    !rt.getToken().isEmpty() &&
                    rt.getExpiresAt() != null &&
                    rt.getExpiresAt().isAfter(beforeLogin.plusSeconds(86400L - 5)) &&
                    rt.getExpiresAt().isBefore(afterLogin.plusSeconds(86400L + 5)) &&
                    !rt.getRevoked()));
        }

        @Test
        @DisplayName("Should delete old refresh tokens before creating new one (single session)")
        void testOldRefreshTokensAreDeleted() {
            LoginRequest request = createValidLoginRequest();
            User user = createActiveUser();
            mockSuccessfulLogin(user);

            authService.loginIssueTokens(request);

            // Verify order: delete THEN save
            InOrder inOrder = inOrder(refreshTokenRepository);
            inOrder.verify(refreshTokenRepository).deleteByUserId(1L);
            inOrder.verify(refreshTokenRepository).save(any(RefreshToken.class));
        }

        @Test
        @DisplayName("Should verify access token contains correct claims (email, role, uid)")
        void testAccessTokenContainsCorrectClaims() {
            LoginRequest request = createValidLoginRequest();
            User user = createActiveUser();
            mockSuccessfulLogin(user);

            authService.loginIssueTokens(request);

            verify(jwtService).generateAccessToken(
                    eq("test@example.com"),
                    argThat(claims -> claims.containsKey("role") &&
                            claims.get("role").equals("USER") &&
                            claims.containsKey("uid") &&
                            claims.get("uid").equals(1L)));
        }
    }

    // ----------------------------- TOKEN REFRESH -----------------------------\\

    @Nested
    @DisplayName("Token Refresh Tests - rotateRefreshToken()")
    class TokenRefreshTests {

        // ==================== TEST HELPERS ====================

        private RefreshToken createValidRefreshToken(String token) {
            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setFirstName("Sergio");
            user.setLastName("Ramos");
            user.setIsActive(true);
            user.setRole(Roles.USER);

            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setId(1L);
            refreshToken.setToken(token);
            refreshToken.setUser(user);
            refreshToken.setExpiresAt(Instant.now().plusSeconds(86400L));
            refreshToken.setRevoked(false);

            return refreshToken;
        }

        // ==================== EQUIVALENCE PARTITIONING - VALID PARTITION
        // ====================

        @Test
        @DisplayName("Should successfully rotate token with valid refresh token")
        void testSuccessfulTokenRotationWithValidToken() {
            String oldRefreshToken = "valid-refresh-token-123";
            RefreshToken oldToken = createValidRefreshToken(oldRefreshToken);

            when(refreshTokenRepository.findByToken(oldRefreshToken)).thenReturn(Optional.of(oldToken));
            when(jwtService.generateAccessToken(eq("test@example.com"), anyMap())).thenReturn("new-access-token");

            Instant beforeRotation = Instant.now();
            AuthService.RotateResult result = authService.rotateRefreshToken(oldRefreshToken);
            Instant afterRotation = Instant.now();

            // Verify result
            assertNotNull(result);
            assertEquals("new-access-token", result.getAccessToken());
            assertNotNull(result.getRefreshToken());
            assertNotEquals(oldRefreshToken, result.getRefreshToken());

            // Verify old token is revoked
            assertTrue(oldToken.getRevoked());

            // Verify order: revoke old THEN create new
            InOrder inOrder = inOrder(refreshTokenRepository);
            inOrder.verify(refreshTokenRepository).save(argThat(rt -> rt.getRevoked()));
            inOrder.verify(refreshTokenRepository).save(argThat(rt -> !rt.getRevoked() &&
                    rt.getUser().getId().equals(1L) &&
                    rt.getToken() != null &&
                    !rt.getToken().equals(oldRefreshToken) &&
                    rt.getExpiresAt().isAfter(beforeRotation.plusSeconds(86400L - 5)) &&
                    rt.getExpiresAt().isBefore(afterRotation.plusSeconds(86400L + 5))));

            verify(jwtService).generateAccessToken(eq("test@example.com"), anyMap());
        }

        // ==================== EQUIVALENCE PARTITIONING - INVALID PARTITION 1:
        // NON-EXISTENT TOKEN ====================

        @Test
        @DisplayName("Should throw EntityNotFoundException when refresh token does not exist")
        void testRotationFailsWithNonExistentToken() {
            String nonExistentToken = "non-existent-token";

            when(refreshTokenRepository.findByToken(nonExistentToken)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> authService.rotateRefreshToken(nonExistentToken));

            assertEquals("Invalid refresh token", exception.getMessage());
            verify(refreshTokenRepository).findByToken(nonExistentToken);
            verify(jwtService, never()).generateAccessToken(anyString(), anyMap());
            verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
        }

        // ==================== EQUIVALENCE PARTITIONING - INVALID PARTITION 2: REVOKED
        // TOKEN ====================

        @Test
        @DisplayName("Should throw IllegalArgumentException when refresh token is revoked")
        void testRotationFailsWithRevokedToken() {
            String revokedToken = "revoked-token-123";
            RefreshToken token = createValidRefreshToken(revokedToken);
            token.setRevoked(true);

            when(refreshTokenRepository.findByToken(revokedToken)).thenReturn(Optional.of(token));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.rotateRefreshToken(revokedToken));

            assertEquals("Refresh token expired or revoked", exception.getMessage());
            verify(jwtService, never()).generateAccessToken(anyString(), anyMap());
        }

        // ==================== EQUIVALENCE PARTITIONING - INVALID PARTITION 3: EXPIRED
        // TOKEN ====================

        @Test
        @DisplayName("Should throw IllegalArgumentException when refresh token is expired")
        void testRotationFailsWithExpiredToken() {
            String expiredToken = "expired-token-123";
            RefreshToken token = createValidRefreshToken(expiredToken);
            token.setExpiresAt(Instant.now().minusSeconds(3600L));

            when(refreshTokenRepository.findByToken(expiredToken)).thenReturn(Optional.of(token));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.rotateRefreshToken(expiredToken));

            assertEquals("Refresh token expired or revoked", exception.getMessage());
            verify(jwtService, never()).generateAccessToken(anyString(), anyMap());
        }

        // ==================== TOKEN VERIFICATION TESTS ====================

        @Test
        @DisplayName("Should verify new access token is generated with correct claims")
        void testNewAccessTokenIsGenerated() {
            String oldRefreshToken = "valid-refresh-token-123";
            RefreshToken oldToken = createValidRefreshToken(oldRefreshToken);

            when(refreshTokenRepository.findByToken(oldRefreshToken)).thenReturn(Optional.of(oldToken));
            when(jwtService.generateAccessToken(eq("test@example.com"), anyMap())).thenReturn("new-access-token");

            AuthService.RotateResult result = authService.rotateRefreshToken(oldRefreshToken);

            assertNotNull(result.getAccessToken());
            assertEquals("new-access-token", result.getAccessToken());

            verify(jwtService).generateAccessToken(
                    eq("test@example.com"),
                    argThat(claims -> claims.containsKey("role") &&
                            claims.get("role").equals("USER") &&
                            claims.containsKey("uid") &&
                            claims.get("uid").equals(1L)));
        }
    }

    // ----------------------------- LOGOUT -----------------------------\\

    @Nested
    @DisplayName("Logout Tests - logout()")
    class LogoutTests {

        // ==================== TEST HELPER ====================

        private RefreshToken createValidRefreshToken(String token) {
            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setRole(Roles.USER);

            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setId(1L);
            refreshToken.setToken(token);
            refreshToken.setUser(user);
            refreshToken.setExpiresAt(Instant.now().plusSeconds(86400L));
            refreshToken.setRevoked(false);

            return refreshToken;
        }

        // ==================== EQUIVALENCE PARTITIONING - VALID PARTITION
        // ====================

        @Test
        @DisplayName("Should successfully logout with valid access and refresh tokens")
        void testSuccessfulLogoutWithValidTokens() {
            String accessToken = "valid-access-token-123";
            String refreshToken = "valid-refresh-token-123";
            RefreshToken token = createValidRefreshToken(refreshToken);

            when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(token));

            authService.logout(accessToken, refreshToken);

            // Verify refresh token is revoked
            assertTrue(token.getRevoked());
            verify(refreshTokenRepository).findByToken(refreshToken);
            verify(refreshTokenRepository).save(token);

            // Verify access token is denylisted
            verify(denylistService).deny(accessToken, 3600L);
        }

        // ==================== EQUIVALENCE PARTITIONING - INVALID PARTITION
        // ====================

        @Test
        @DisplayName("Should handle logout gracefully when refresh token does not exist")
        void testLogoutWithNonExistentRefreshToken() {
            String accessToken = "valid-access-token-123";
            String nonExistentRefreshToken = "non-existent-refresh-token";

            when(refreshTokenRepository.findByToken(nonExistentRefreshToken)).thenReturn(Optional.empty());

            assertDoesNotThrow(() -> authService.logout(accessToken, nonExistentRefreshToken));

            verify(refreshTokenRepository).findByToken(nonExistentRefreshToken);
            verify(refreshTokenRepository, never()).save(any(RefreshToken.class));

            // Access token should still be denylisted
            verify(denylistService).deny(accessToken, 3600L);
        }

        // ==================== VERIFICATION TESTS ====================

        @Test
        @DisplayName("Should verify refresh token is marked as revoked after logout")
        void testRefreshTokenIsRevoked() {
            String accessToken = "valid-access-token-123";
            String refreshToken = "valid-refresh-token-123";
            RefreshToken token = createValidRefreshToken(refreshToken);

            when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(token));

            authService.logout(accessToken, refreshToken);

            verify(refreshTokenRepository).save(argThat(rt -> rt.getId().equals(1L) &&
                    rt.getRevoked() &&
                    rt.getToken().equals(refreshToken)));
        }

        @Test
        @DisplayName("Should verify access token is added to denylist with correct expiration")
        void testAccessTokenIsAddedToDenylist() {
            String accessToken = "valid-access-token-123";
            String refreshToken = "valid-refresh-token-123";
            RefreshToken token = createValidRefreshToken(refreshToken);

            when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(token));

            authService.logout(accessToken, refreshToken);

            verify(denylistService).deny(accessToken, 3600L);
        }
    }

    // ==================== WHITEBOX STATEMENT COVERAGE TESTS ====================

    @Nested
    @DisplayName(" Whitebox Statement Coverage - register()")
    class RegisterWhiteboxTests {

        // ==================== EDGE CASES NOT COVERED IN BLACKBOX TESTING
        // ====================

        @Test
        @DisplayName("WB: Should handle email with mixed case (toLowerCase coverage)")
        void testEmailLowerCase_MixedCase() {
            RegisterUserRequest request = new RegisterUserRequest();
            request.setEmail("TeSt@ExAmPlE.CoM");
            request.setPassword("Pass123!");
            request.setFirstName("Sergio");
            request.setLastName("Ramos");
            request.setPhone("+1234567890");

            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });

            authService.register(request);

            verify(userRepository).existsByEmail("test@example.com");
            verify(userRepository).save(argThat(user -> user.getEmail().equals("test@example.com")));
        }

        @Test
        @DisplayName("WB: Should trim firstName with leading/trailing spaces")
        void testFirstNameTrim_WithSpaces() {
            RegisterUserRequest request = new RegisterUserRequest();
            request.setEmail("test@example.com");
            request.setPassword("Pass123!");
            request.setFirstName("   Sergio   ");
            request.setLastName("Ramos");
            request.setPhone("+1234567890");

            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });

            UserResponse response = authService.register(request);

            assertEquals("Sergio", response.getFirstName());
            verify(userRepository).save(argThat(user -> user.getFirstName().equals("Sergio")));
        }

        @Test
        @DisplayName("WB: Should trim lastName with leading/trailing spaces")
        void testLastNameTrim_WithSpaces() {
            RegisterUserRequest request = new RegisterUserRequest();
            request.setEmail("test@example.com");
            request.setPassword("Pass123!");
            request.setFirstName("Sergio");
            request.setLastName("   Ramos   ");
            request.setPhone("+1234567890");

            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });

            UserResponse response = authService.register(request);

            assertEquals("Ramos", response.getLastName());
            verify(userRepository).save(argThat(user -> user.getLastName().equals("Ramos")));
        }

        @Test
        @DisplayName("WB: Should handle password with backslash special character")
        void testPasswordStrong_BackslashSpecialChar() {
            RegisterUserRequest request = new RegisterUserRequest();
            request.setEmail("test@example.com");
            request.setPassword("Pass123\\");
            request.setFirstName("Sergio");
            request.setLastName("Ramos");
            request.setPhone("+1234567890");

            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });

            assertDoesNotThrow(() -> authService.register(request));
        }

        @Test
        @DisplayName("WB: Should handle password with bracket special characters")
        void testPasswordStrong_BracketSpecialChars() {
            RegisterUserRequest request = new RegisterUserRequest();
            request.setEmail("test@example.com");
            request.setPassword("Pass123[");
            request.setFirstName("Sergio");
            request.setLastName("Ramos");
            request.setPhone("+1234567890");

            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });

            assertDoesNotThrow(() -> authService.register(request));
        }
    }

    @Nested
    @DisplayName("Whitebox Statement Coverage - loginIssueTokens()")
    class LoginWhiteboxTests {

        // ==================== EDGE CASES NOT COVERED IN BLACKBOX TESTING
        // ====================

        @Test
        @DisplayName("WB: Should handle email with uppercase in login (toLowerCase coverage)")
        void testLoginEmail_UpperCase() {
            LoginRequest request = new LoginRequest();
            request.setEmail("TEST@EXAMPLE.COM");
            request.setPassword("Pass123!");

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setPassword("$2a$10$hashedPassword");
            user.setFirstName("Sergio");
            user.setLastName("Ramos");
            user.setIsActive(true);
            user.setRole(Roles.USER);

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("Pass123!", user.getPassword())).thenReturn(true);
            when(jwtService.generateAccessToken(eq("test@example.com"), anyMap())).thenReturn("access-token-123");

            authService.loginIssueTokens(request);

            verify(userRepository).findByEmail("test@example.com");
        }

        @Test
        @DisplayName("WB: Should handle null isActive (Boolean.FALSE.equals coverage)")
        void testIsActive_NullValue() {
            LoginRequest request = new LoginRequest();
            request.setEmail("test@example.com");
            request.setPassword("Pass123!");

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setPassword("$2a$10$hashedPassword");
            user.setFirstName("Sergio");
            user.setLastName("Ramos");
            user.setIsActive(null); // null should not throw exception
            user.setRole(Roles.USER);

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("Pass123!", user.getPassword())).thenReturn(true);
            when(jwtService.generateAccessToken(eq("test@example.com"), anyMap())).thenReturn("access-token-123");

            assertDoesNotThrow(() -> authService.loginIssueTokens(request));
        }
    }

    @Nested
    @DisplayName("Whitebox Statement Coverage - logout()")
    class LogoutWhiteboxTests {

        // ==================== EDGE CASES NOT COVERED WITH BLACKBOX TESTING
        // ====================

        @Test
        @DisplayName("WB: Should handle blank refreshToken (isBlank coverage)")
        void testRefreshToken_BlankString() {
            String accessToken = "access-token";
            String refreshToken = "   "; // blank but not null

            authService.logout(accessToken, refreshToken);

            verify(refreshTokenRepository, never()).findByToken(anyString());
            verify(denylistService).deny(accessToken, 3600L);
        }

        @Test
        @DisplayName("WB: Should handle blank accessToken (isBlank coverage)")
        void testAccessToken_BlankString() {
            String accessToken = "   "; // blank but not null
            String refreshToken = "refresh-token";

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setRole(Roles.USER);

            RefreshToken rt = new RefreshToken();
            rt.setId(1L);
            rt.setToken(refreshToken);
            rt.setUser(user);
            rt.setExpiresAt(Instant.now().plusSeconds(86400L));
            rt.setRevoked(false);

            when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(rt));

            authService.logout(accessToken, refreshToken);

            verify(refreshTokenRepository).save(argThat(RefreshToken::getRevoked));
            verify(denylistService, never()).deny(anyString(), anyLong());
        }
    }


    // ==================== Whitebox DECISION COVERAGE TESTS ====================

    @Nested
    @DisplayName("Decision Coverage - register()")
    class RegisterDecisionCoverageTests {

        // ==================== DECISION POINT 1: existsByEmail() ====================

        @Test
        @DisplayName("DC: Should execute TRUE branch when email exists")
        void testEmailExists_TrueBranch() {
            RegisterUserRequest request = new RegisterUserRequest();
            request.setEmail("existing@example.com");
            request.setPassword("Pass123!");
            request.setFirstName("Sergio");
            request.setLastName("Ramos");

            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.register(request));

            assertEquals("Email already in use", exception.getMessage());
            verify(userRepository).existsByEmail("existing@example.com");
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("DC: Should execute FALSE branch when email does not exist")
        void testEmailExists_FalseBranch() {
            RegisterUserRequest request = new RegisterUserRequest();
            request.setEmail("new@example.com");
            request.setPassword("Pass123!");
            request.setFirstName("Sergio");
            request.setLastName("Ramos");

            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(1L);
                return u;
            });

            UserResponse response = authService.register(request);

            assertNotNull(response);
            verify(userRepository).existsByEmail("new@example.com");
            verify(userRepository).save(any(User.class));
        }

        // ==================== DECISION POINT 2: isPasswordStrong()
        // ====================

        @Test
        @DisplayName("DC: Should execute TRUE branch when password is strong")
        void testIsPasswordStrong_TrueBranch() {
            RegisterUserRequest request = new RegisterUserRequest();
            request.setEmail("test@example.com");
            request.setPassword("StrongPass123!");
            request.setFirstName("Sergio");
            request.setLastName("Ramos");

            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(1L);
                return u;
            });

            UserResponse response = authService.register(request);

            assertNotNull(response);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("DC: Should execute FALSE branch when password is weak")
        void testIsPasswordStrong_FalseBranch() {
            RegisterUserRequest request = new RegisterUserRequest();
            request.setEmail("test@example.com");
            request.setPassword("weak");
            request.setFirstName("Sergio");
            request.setLastName("Ramos");

            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.register(request));

            assertEquals("Password must be at least 7 characters and include a special character",
                    exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Decision Coverage - loginIssueTokens()")
    class LoginDecisionCoverageTests {

        // ==================== DECISION POINT 1: findByEmail().isEmpty()
        // ====================

        @Test
        @DisplayName("DC: Should execute TRUE branch when user does not exist (Optional.empty)")
        void testUserExists_TrueBranch() {
            LoginRequest request = new LoginRequest();
            request.setEmail("nonexistent@example.com");
            request.setPassword("Pass123!");

            when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> authService.loginIssueTokens(request));

            assertEquals("Invalid credentials", exception.getMessage());
            verify(userRepository).findByEmail("nonexistent@example.com");
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("DC: Should execute FALSE branch when user exists (Optional.present)")
        void testUserExists_FalseBranch() {
            LoginRequest request = new LoginRequest();
            request.setEmail("existing@example.com");
            request.setPassword("Pass123!");

            User user = new User();
            user.setId(1L);
            user.setEmail("existing@example.com");
            user.setPassword("$2a$10$hashedPassword");
            user.setIsActive(true);
            user.setRole(Roles.USER);

            when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("Pass123!", "$2a$10$hashedPassword")).thenReturn(true);
            when(jwtService.generateAccessToken(eq("existing@example.com"), anyMap())).thenReturn("token");

            AuthService.LoginPair result = authService.loginIssueTokens(request);

            assertNotNull(result);
            verify(userRepository).findByEmail("existing@example.com");
        }

        // ==================== DECISION POINT 2: Boolean.FALSE.equals(isActive)
        // ====================

        @Test
        @DisplayName("DC: Should execute TRUE branch when user is inactive")
        void testIsActive_TrueBranch() {
            LoginRequest request = new LoginRequest();
            request.setEmail("test@example.com");
            request.setPassword("Pass123!");

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setPassword("$2a$10$hashedPassword");
            user.setIsActive(false); // Inactive
            user.setRole(Roles.USER);

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> authService.loginIssueTokens(request));

            assertEquals("User is deactivated", exception.getMessage());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("DC: Should execute FALSE branch when user is active")
        void testIsActive_FalseBranch() {
            LoginRequest request = new LoginRequest();
            request.setEmail("test@example.com");
            request.setPassword("Pass123!");

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setPassword("$2a$10$hashedPassword");
            user.setIsActive(true); // Active
            user.setRole(Roles.USER);

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("Pass123!", "$2a$10$hashedPassword")).thenReturn(true);
            when(jwtService.generateAccessToken(eq("test@example.com"), anyMap())).thenReturn("token");

            AuthService.LoginPair result = authService.loginIssueTokens(request);

            assertNotNull(result);
            verify(passwordEncoder).matches("Pass123!", "$2a$10$hashedPassword");
        }

        // ==================== DECISION POINT 3: !passwordEncoder.matches()
        // ====================

        @Test
        @DisplayName("DC: Should execute TRUE branch when password does not match")
        void testPasswordMatches_TrueBranch() {
            LoginRequest request = new LoginRequest();
            request.setEmail("test@example.com");
            request.setPassword("WrongPassword!");

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setPassword("$2a$10$hashedPassword");
            user.setIsActive(true);
            user.setRole(Roles.USER);

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("WrongPassword!", "$2a$10$hashedPassword")).thenReturn(false);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.loginIssueTokens(request));

            assertEquals("Invalid credentials", exception.getMessage());
            verify(passwordEncoder).matches("WrongPassword!", "$2a$10$hashedPassword");
            verify(jwtService, never()).generateAccessToken(anyString(), anyMap());
        }

        @Test
        @DisplayName("DC: Should execute FALSE branch when password matches")
        void testPasswordMatches_FalseBranch() {
            LoginRequest request = new LoginRequest();
            request.setEmail("test@example.com");
            request.setPassword("CorrectPassword!");

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setPassword("$2a$10$hashedPassword");
            user.setIsActive(true);
            user.setRole(Roles.USER);

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("CorrectPassword!", "$2a$10$hashedPassword")).thenReturn(true);
            when(jwtService.generateAccessToken(eq("test@example.com"), anyMap())).thenReturn("token");

            AuthService.LoginPair result = authService.loginIssueTokens(request);

            assertNotNull(result);
            verify(jwtService).generateAccessToken(eq("test@example.com"), anyMap());
        }
    }

    @Nested
    @DisplayName("Decision Coverage - rotateRefreshToken()")
    class RotateRefreshTokenDecisionCoverageTests {

        // ==================== DECISION POINT 1: findByToken().isEmpty()
        // ====================

        @Test
        @DisplayName("DC: Should execute TRUE branch when token does not exist (Optional.empty)")
        void testTokenExists_TrueBranch() {
            String nonExistentToken = "non-existent-token";

            when(refreshTokenRepository.findByToken(nonExistentToken)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> authService.rotateRefreshToken(nonExistentToken));

            assertEquals("Invalid refresh token", exception.getMessage());
            verify(refreshTokenRepository).findByToken(nonExistentToken);
            verify(jwtService, never()).generateAccessToken(anyString(), anyMap());
        }

        @Test
        @DisplayName("DC: Should execute FALSE branch when token exists (Optional.present)")
        void testTokenExists_FalseBranch() {
            String existingToken = "existing-token";

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setRole(Roles.USER);

            RefreshToken token = new RefreshToken();
            token.setId(1L);
            token.setToken(existingToken);
            token.setUser(user);
            token.setExpiresAt(Instant.now().plusSeconds(86400L));
            token.setRevoked(false);

            when(refreshTokenRepository.findByToken(existingToken)).thenReturn(Optional.of(token));
            when(jwtService.generateAccessToken(eq("test@example.com"), anyMap())).thenReturn("new-token");

            AuthService.RotateResult result = authService.rotateRefreshToken(existingToken);

            assertNotNull(result);
            verify(refreshTokenRepository).findByToken(existingToken);
        }

        // ==================== DECISION POINT 2: getRevoked() ====================

        @Test
        @DisplayName("DC: Should execute TRUE branch when token is revoked")
        void testGetRevoked_TrueBranch() {
            String revokedToken = "revoked-token";

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setRole(Roles.USER);

            RefreshToken token = new RefreshToken();
            token.setId(1L);
            token.setToken(revokedToken);
            token.setUser(user);
            token.setExpiresAt(Instant.now().plusSeconds(86400L));
            token.setRevoked(true); // Revoked

            when(refreshTokenRepository.findByToken(revokedToken)).thenReturn(Optional.of(token));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.rotateRefreshToken(revokedToken));

            assertEquals("Refresh token expired or revoked", exception.getMessage());
            verify(jwtService, never()).generateAccessToken(anyString(), anyMap());
        }

        @Test
        @DisplayName("DC: Should execute FALSE branch when token is not revoked")
        void testGetRevoked_FalseBranch() {
            String validToken = "valid-token";

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setRole(Roles.USER);

            RefreshToken token = new RefreshToken();
            token.setId(1L);
            token.setToken(validToken);
            token.setUser(user);
            token.setExpiresAt(Instant.now().plusSeconds(86400L));
            token.setRevoked(false); // Not revoked

            when(refreshTokenRepository.findByToken(validToken)).thenReturn(Optional.of(token));
            when(jwtService.generateAccessToken(eq("test@example.com"), anyMap())).thenReturn("new-token");

            AuthService.RotateResult result = authService.rotateRefreshToken(validToken);

            assertNotNull(result);
            verify(jwtService).generateAccessToken(eq("test@example.com"), anyMap());
        }

        // ==================== DECISION POINT 3: getExpiresAt().isBefore(Instant.now())
        // ====================

        @Test
        @DisplayName("DC: Should execute TRUE branch when token is expired")
        void testIsExpired_TrueBranch() {
            String expiredToken = "expired-token";

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setRole(Roles.USER);

            RefreshToken token = new RefreshToken();
            token.setId(1L);
            token.setToken(expiredToken);
            token.setUser(user);
            token.setExpiresAt(Instant.now().minusSeconds(3600L)); // Expired
            token.setRevoked(false);

            when(refreshTokenRepository.findByToken(expiredToken)).thenReturn(Optional.of(token));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.rotateRefreshToken(expiredToken));

            assertEquals("Refresh token expired or revoked", exception.getMessage());
            verify(jwtService, never()).generateAccessToken(anyString(), anyMap());
        }

        @Test
        @DisplayName("DC: Should execute FALSE branch when token is not expired")
        void testIsExpired_FalseBranch() {
            String validToken = "valid-token";

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setRole(Roles.USER);

            RefreshToken token = new RefreshToken();
            token.setId(1L);
            token.setToken(validToken);
            token.setUser(user);
            token.setExpiresAt(Instant.now().plusSeconds(86400L)); // Not expired
            token.setRevoked(false);

            when(refreshTokenRepository.findByToken(validToken)).thenReturn(Optional.of(token));
            when(jwtService.generateAccessToken(eq("test@example.com"), anyMap())).thenReturn("new-token");

            AuthService.RotateResult result = authService.rotateRefreshToken(validToken);

            assertNotNull(result);
            verify(jwtService).generateAccessToken(eq("test@example.com"), anyMap());
        }
    }

    @Nested
    @DisplayName("Decision Coverage - isPasswordStrong()")
    class IsPasswordStrongDecisionCoverageTests {

        private Method isPasswordStrongMethod;

        @BeforeEach
        void setUp() throws NoSuchMethodException {
            isPasswordStrongMethod = AuthService.class.getDeclaredMethod("isPasswordStrong", String.class);
            isPasswordStrongMethod.setAccessible(true);
        }

        private boolean invokeIsPasswordStrong(String password) throws Exception {
            return (boolean) isPasswordStrongMethod.invoke(authService, password);
        }

        // ==================== DECISION POINT 1: pwd.length() < 7 ====================

        @Test
        @DisplayName("DC: Should execute TRUE branch when password length < 7")
        void testPasswordLength_TrueBranch() throws Exception {
            assertFalse(invokeIsPasswordStrong("Pass1!"));
        }

        @Test
        @DisplayName("DC: Should execute FALSE branch when password length >= 7")
        void testPasswordLength_FalseBranch() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass12!"));
        }

        // ==================== DECISION POINT 2: pwd.matches(regex)
        // ====================

        @Test
        @DisplayName("DC: Should execute TRUE branch when password matches regex (has special char)")
        void testPasswordRegex_TrueBranch() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123!"));
        }

        @Test
        @DisplayName("DC: Should execute FALSE branch when password does not match regex (no special char)")
        void testPasswordRegex_FalseBranch() throws Exception {
            assertFalse(invokeIsPasswordStrong("Pass123"));
        }
    }

    @Nested
    @DisplayName("Decision Coverage - logout()")
    class LogoutDecisionCoverageTests {

        // ==================== DECISION POINT 1: refreshToken != null
        // ====================

        @Test
        @DisplayName("DC: Should execute TRUE branch when refreshToken is not null")
        void testRefreshTokenNotNull_TrueBranch() {
            String accessToken = "access-token";
            String refreshToken = "refresh-token";

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setRole(Roles.USER);

            RefreshToken rt = new RefreshToken();
            rt.setId(1L);
            rt.setToken(refreshToken);
            rt.setUser(user);
            rt.setExpiresAt(Instant.now().plusSeconds(86400L));
            rt.setRevoked(false);

            when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(rt));

            authService.logout(accessToken, refreshToken);

            verify(refreshTokenRepository).findByToken(refreshToken);
        }

        @Test
        @DisplayName("DC: Should execute FALSE branch when refreshToken is null")
        void testRefreshTokenNotNull_FalseBranch() {
            String accessToken = "access-token";

            authService.logout(accessToken, null);

            verify(refreshTokenRepository, never()).findByToken(anyString());
            verify(denylistService).deny(accessToken, 3600L);
        }

        // ==================== DECISION POINT 2: !refreshToken.isBlank()
        // ====================

        @Test
        @DisplayName("DC: Should execute TRUE branch when refreshToken is not blank")
        void testRefreshTokenNotBlank_TrueBranch() {
            String accessToken = "access-token";
            String refreshToken = "refresh-token";

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setRole(Roles.USER);

            RefreshToken rt = new RefreshToken();
            rt.setId(1L);
            rt.setToken(refreshToken);
            rt.setUser(user);
            rt.setExpiresAt(Instant.now().plusSeconds(86400L));
            rt.setRevoked(false);

            when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(rt));

            authService.logout(accessToken, refreshToken);

            verify(refreshTokenRepository).findByToken(refreshToken);
        }

        @Test
        @DisplayName("DC: Should execute FALSE branch when refreshToken is blank")
        void testRefreshTokenNotBlank_FalseBranch() {
            String accessToken = "access-token";
            String refreshToken = "   ";

            authService.logout(accessToken, refreshToken);

            verify(refreshTokenRepository, never()).findByToken(anyString());
            verify(denylistService).deny(accessToken, 3600L);
        }

        // ==================== DECISION POINT 3: accessToken != null
        // ====================

        @Test
        @DisplayName("DC: Should execute TRUE branch when accessToken is not null")
        void testAccessTokenNotNull_TrueBranch() {
            String accessToken = "access-token";
            String refreshToken = "refresh-token";

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setRole(Roles.USER);

            RefreshToken rt = new RefreshToken();
            rt.setId(1L);
            rt.setToken(refreshToken);
            rt.setUser(user);
            rt.setExpiresAt(Instant.now().plusSeconds(86400L));
            rt.setRevoked(false);

            when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(rt));

            authService.logout(accessToken, refreshToken);

            verify(denylistService).deny(accessToken, 3600L);
        }

        @Test
        @DisplayName("DC: Should execute FALSE branch when accessToken is null")
        void testAccessTokenNotNull_FalseBranch() {
            String refreshToken = "refresh-token";

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setRole(Roles.USER);

            RefreshToken rt = new RefreshToken();
            rt.setId(1L);
            rt.setToken(refreshToken);
            rt.setUser(user);
            rt.setExpiresAt(Instant.now().plusSeconds(86400L));
            rt.setRevoked(false);

            when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(rt));

            authService.logout(null, refreshToken);

            verify(denylistService, never()).deny(anyString(), anyLong());
        }

        // ==================== DECISION POINT 4: !accessToken.isBlank()
        // ====================

        @Test
        @DisplayName("DC: Should execute TRUE branch when accessToken is not blank")
        void testAccessTokenNotBlank_TrueBranch() {
            String accessToken = "access-token";
            String refreshToken = "refresh-token";

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setRole(Roles.USER);

            RefreshToken rt = new RefreshToken();
            rt.setId(1L);
            rt.setToken(refreshToken);
            rt.setUser(user);
            rt.setExpiresAt(Instant.now().plusSeconds(86400L));
            rt.setRevoked(false);

            when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(rt));

            authService.logout(accessToken, refreshToken);

            verify(denylistService).deny(accessToken, 3600L);
        }

        @Test
        @DisplayName("DC: Should execute FALSE branch when accessToken is blank")
        void testAccessTokenNotBlank_FalseBranch() {
            String accessToken = "   ";
            String refreshToken = "refresh-token";

            User user = new User();
            user.setId(1L);
            user.setEmail("test@example.com");
            user.setRole(Roles.USER);

            RefreshToken rt = new RefreshToken();
            rt.setId(1L);
            rt.setToken(refreshToken);
            rt.setUser(user);
            rt.setExpiresAt(Instant.now().plusSeconds(86400L));
            rt.setRevoked(false);

            when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(rt));

            authService.logout(accessToken, refreshToken);

            verify(denylistService, never()).deny(anyString(), anyLong());
        }
    }

}
