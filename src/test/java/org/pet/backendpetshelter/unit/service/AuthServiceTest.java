package org.pet.backendpetshelter.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.pet.backendpetshelter.Configuration.JwtProperties;
import org.pet.backendpetshelter.Configuration.JwtService;
import org.pet.backendpetshelter.DTO.RegisterUserRequest;
import org.pet.backendpetshelter.Reposiotry.RefreshTokenRepository;
import org.pet.backendpetshelter.Reposiotry.UserRepository;
import org.pet.backendpetshelter.Service.AuthService;
import org.pet.backendpetshelter.Service.TokenDenylistService;
import org.pet.backendpetshelter.DTO.UserResponse;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Roles;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Method;

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
                jwtProperties
        );
    }

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

        // ------  These tests checks every special character ------ \\
        @Test
        @DisplayName("Should return true with exclamation mark special character")
        void testPasswordWithExclamation() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123!"));
        }

        @Test
        @DisplayName("Should return true with at symbol special character")
        void testPasswordWithAtSymbol() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123@"));
        }

        @Test
        @DisplayName("Should return true with hash special character")
        void testPasswordWithHash() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123#"));
        }

        @Test
        @DisplayName("Should return true with dollar sign special character")
        void testPasswordWithDollar() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123$"));
        }

        @Test
        @DisplayName("Should return true with percent special character")
        void testPasswordWithPercent() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123%"));
        }

        @Test
        @DisplayName("Should return true with caret special character")
        void testPasswordWithCaret() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123^"));
        }

        @Test
        @DisplayName("Should return true with ampersand special character")
        void testPasswordWithAmpersand() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123&"));
        }

        @Test
        @DisplayName("Should return true with asterisk special character")
        void testPasswordWithAsterisk() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123*"));
        }

        @Test
        @DisplayName("Should return true with parentheses special characters")
        void testPasswordWithParentheses() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123("));
            assertTrue(invokeIsPasswordStrong("Pass123)"));
        }

        @Test
        @DisplayName("Should return true with underscore special character")
        void testPasswordWithUnderscore() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123_"));
        }

        @Test
        @DisplayName("Should return true with plus special character")
        void testPasswordWithPlus() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123+"));
        }

        @Test
        @DisplayName("Should return true with equals special character")
        void testPasswordWithEquals() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123="));
        }

        @Test
        @DisplayName("Should return true with hyphen special character")
        void testPasswordWithHyphen() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123-"));
        }

        @Test
        @DisplayName("Should return true with curly braces special characters")
        void testPasswordWithCurlyBraces() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123{"));
            assertTrue(invokeIsPasswordStrong("Pass123}"));
        }

        @Test
        @DisplayName("Should return true with square brackets special characters")
        void testPasswordWithSquareBrackets() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123["));
            assertTrue(invokeIsPasswordStrong("Pass123]"));
        }

        @Test
        @DisplayName("Should return true with colon special character")
        void testPasswordWithColon() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123:"));
        }

        @Test
        @DisplayName("Should return true with semicolon special character")
        void testPasswordWithSemicolon() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123;"));
        }

        @Test
        @DisplayName("Should return true with quote special characters")
        void testPasswordWithQuotes() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123\""));
            assertTrue(invokeIsPasswordStrong("Pass123'"));
        }

        @Test
        @DisplayName("Should return true with angle brackets special characters")
        void testPasswordWithAngleBrackets() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123<"));
            assertTrue(invokeIsPasswordStrong("Pass123>"));
        }

        @Test
        @DisplayName("Should return true with comma special character")
        void testPasswordWithComma() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123,"));
        }

        @Test
        @DisplayName("Should return true with period special character")
        void testPasswordWithPeriod() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123."));
        }

        @Test
        @DisplayName("Should return true with question mark special character")
        void testPasswordWithQuestionMark() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123?"));
        }

        @Test
        @DisplayName("Should return true with forward slash special character")
        void testPasswordWithForwardSlash() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123/"));
        }

        @Test
        @DisplayName("Should return true with pipe special character")
        void testPasswordWithPipe() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123|"));
        }

        @Test
        @DisplayName("Should return true with backslash special character")
        void testPasswordWithBackslash() throws Exception {
            assertTrue(invokeIsPasswordStrong("Pass123\\"));
        }

    }


    
}
