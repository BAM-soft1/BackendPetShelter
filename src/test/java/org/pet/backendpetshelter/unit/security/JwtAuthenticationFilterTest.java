package org.pet.backendpetshelter.unit.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pet.backendpetshelter.Configuration.JwtAuthenticationFilter;
import org.pet.backendpetshelter.Configuration.JwtService;
import org.pet.backendpetshelter.Service.TokenDenylistService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter Tests")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenDenylistService denylistService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService, denylistService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    // ==== TEST HELPER ====
    
    private Jws<Claims> createMockJws(String email, String role) {
        Claims claims = new DefaultClaims();
        claims.setSubject(email);
        claims.put("role", role);
        
        return new Jws<Claims>() {
            @Override
            public DefaultJwsHeader getHeader() {
                return new DefaultJwsHeader();
            }

            @Override
            public Claims getBody() {
                return claims;
            }

            @Override
            public String getSignature() {
                return "signature";
            }
        };
    }

    @Nested
    @DisplayName("JwtAuthenticationFilter Blackbox Tests")
    class BlackboxTests {

        // ==== EQUIVALENCE PARTITIONING - VALID PARTITION ====
        
        @Test
        @DisplayName("Should set authentication in SecurityContext with valid token")
        void testValidTokenSetsAuthentication() throws ServletException, IOException {
            String token = "valid.jwt.token";
            request.addHeader("Authorization", "Bearer " + token);
            
            Jws<Claims> mockJws = createMockJws("user@example.com", "USER");
            when(denylistService.isDenied(token)).thenReturn(false);
            when(jwtService.parseAccessToken(token)).thenReturn(mockJws);

            filter.doFilter(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(auth);
            assertEquals("user@example.com", auth.getPrincipal());
            assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("USER")));
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should extract correct claims and set user details")
        void testExtractedClaimsMatchUserDetails() throws ServletException, IOException {
            String token = "valid.jwt.token";
            request.addHeader("Authorization", "Bearer " + token);
            
            Jws<Claims> mockJws = createMockJws("admin@example.com", "ADMIN");
            when(denylistService.isDenied(token)).thenReturn(false);
            when(jwtService.parseAccessToken(token)).thenReturn(mockJws);

            filter.doFilter(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(auth);
            assertEquals("admin@example.com", auth.getPrincipal());
            assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")));
            assertNull(auth.getCredentials());
            verify(filterChain).doFilter(request, response);
        }

        // ==== EQUIVALENCE PARTITIONING - INVALID PARTITION 1: MISSING HEADER ====
        
        @Test
        @DisplayName("Should skip authentication when Authorization header is missing")
        void testMissingAuthorizationHeader() throws ServletException, IOException {
            // No Authorization header set

            filter.doFilter(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNull(auth);
            verify(filterChain).doFilter(request, response);
            verify(jwtService, never()).parseAccessToken(anyString());
        }

        // ==== EQUIVALENCE PARTITIONING - INVALID PARTITION 2: INVALID TOKEN FORMAT ====
        
        @Test
        @DisplayName("Should skip authentication when token format is invalid (not Bearer)")
        void testInvalidTokenFormat() throws ServletException, IOException {
            request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

            filter.doFilter(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNull(auth);
            verify(filterChain).doFilter(request, response);
            verify(jwtService, never()).parseAccessToken(anyString());
        }

        @Test
        @DisplayName("Should skip authentication when Bearer token is malformed")
        void testMalformedBearerToken() throws ServletException, IOException {
            String token = "malformed.token";
            request.addHeader("Authorization", "Bearer " + token);
            
            when(denylistService.isDenied(token)).thenReturn(false);
            when(jwtService.parseAccessToken(token)).thenThrow(new MalformedJwtException("Invalid token"));

            filter.doFilter(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNull(auth);
            verify(filterChain).doFilter(request, response);
        }

        // ==== EQUIVALENCE PARTITIONING - INVALID PARTITION 3: TOKEN IN DENYLIST ====
        
        @Test
        @DisplayName("Should skip authentication when token is in denylist")
        void testTokenInDenylist() throws ServletException, IOException {
            String token = "denied.jwt.token";
            request.addHeader("Authorization", "Bearer " + token);
            
            when(denylistService.isDenied(token)).thenReturn(true);

            filter.doFilter(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNull(auth);
            verify(filterChain).doFilter(request, response);
            verify(jwtService, never()).parseAccessToken(anyString());
        }

        // ==== EQUIVALENCE PARTITIONING - INVALID PARTITION 4: EXPIRED TOKEN ====
        
        @Test
        @DisplayName("Should skip authentication when token is expired")
        void testExpiredToken() throws ServletException, IOException {
            String token = "expired.jwt.token";
            request.addHeader("Authorization", "Bearer " + token);
            
            when(denylistService.isDenied(token)).thenReturn(false);
            when(jwtService.parseAccessToken(token)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

            filter.doFilter(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNull(auth);
            verify(filterChain).doFilter(request, response);
        }

        // ==== FILTER CHAIN CONTINUATION ====
        
        @Test
        @DisplayName("Should continue filter chain after processing valid token")
        void testFilterChainContinuesAfterValidToken() throws ServletException, IOException {
            String token = "valid.jwt.token";
            request.addHeader("Authorization", "Bearer " + token);
            
            Jws<Claims> mockJws = createMockJws("user@example.com", "USER");
            when(denylistService.isDenied(token)).thenReturn(false);
            when(jwtService.parseAccessToken(token)).thenReturn(mockJws);

            filter.doFilter(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("Should continue filter chain after processing invalid token")
        void testFilterChainContinuesAfterInvalidToken() throws ServletException, IOException {
            String token = "invalid.jwt.token";
            request.addHeader("Authorization", "Bearer " + token);
            
            when(denylistService.isDenied(token)).thenReturn(false);
            when(jwtService.parseAccessToken(token)).thenThrow(new MalformedJwtException("Invalid"));

            filter.doFilter(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("Should continue filter chain when no Authorization header")
        void testFilterChainContinuesWithoutHeader() throws ServletException, IOException {
            filter.doFilter(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("JwtAuthenticationFilter Whitebox Statement & Decision Coverage Tests")
    class WhiteboxCoverageTests {

        // ==== DECISION COVERAGE: authHeader == null (TRUE branch) ====
        
        @Test
        @DisplayName("Should skip token processing when authHeader is null")
        void testAuthHeaderNull() throws ServletException, IOException {
            // Decision: authHeader == null → TRUE
            request.removeHeader("Authorization");

            filter.doFilter(request, response, filterChain);

            verify(jwtService, never()).parseAccessToken(anyString());
            verify(denylistService, never()).isDenied(anyString());
            verify(filterChain).doFilter(request, response);
        }

        // ==== DECISION COVERAGE: !authHeader.startsWith("Bearer ") (TRUE branch) ====
        
        @Test
        @DisplayName("Should skip token processing when authHeader does not start with 'Bearer '")
        void testAuthHeaderNotBearer() throws ServletException, IOException {
            // Decision: !authHeader.startsWith("Bearer ") → TRUE
            request.addHeader("Authorization", "Basic credentials");

            filter.doFilter(request, response, filterChain);

            verify(jwtService, never()).parseAccessToken(anyString());
            verify(denylistService, never()).isDenied(anyString());
            verify(filterChain).doFilter(request, response);
        }

        // ==== DECISION COVERAGE: authHeader != null && authHeader.startsWith("Bearer ") (FALSE branch) ====
        
        @Test
        @DisplayName("Should process token when authHeader is valid Bearer token")
        void testAuthHeaderValidBearer() throws ServletException, IOException {
            // Decision: authHeader != null && authHeader.startsWith("Bearer ") → FALSE (enters processing)
            String token = "valid.token";
            request.addHeader("Authorization", "Bearer " + token);
            
            Jws<Claims> mockJws = createMockJws("user@example.com", "USER");
            when(denylistService.isDenied(token)).thenReturn(false);
            when(jwtService.parseAccessToken(token)).thenReturn(mockJws);

            filter.doFilter(request, response, filterChain);

            verify(jwtService).parseAccessToken(token);
            verify(denylistService).isDenied(token);
            verify(filterChain).doFilter(request, response);
        }

        // ==== DECISION COVERAGE: denylistService.isDenied(token) (TRUE branch) ====
        
        @Test
        @DisplayName("Should return early when token is denied")
        void testTokenDeniedReturnsEarly() throws ServletException, IOException {
            // Decision: denylistService.isDenied(token) → TRUE
            String token = "denied.token";
            request.addHeader("Authorization", "Bearer " + token);
            
            when(denylistService.isDenied(token)).thenReturn(true);

            filter.doFilter(request, response, filterChain);

            verify(denylistService).isDenied(token);
            verify(jwtService, never()).parseAccessToken(anyString());
            verify(filterChain).doFilter(request, response);
        }

        // ==== DECISION COVERAGE: denylistService.isDenied(token) (FALSE branch) ====
        
        @Test
        @DisplayName("Should proceed to parse token when not denied")
        void testTokenNotDeniedProceedsToParsing() throws ServletException, IOException {
            // Decision: denylistService.isDenied(token) → FALSE
            String token = "valid.token";
            request.addHeader("Authorization", "Bearer " + token);
            
            Jws<Claims> mockJws = createMockJws("user@example.com", "USER");
            when(denylistService.isDenied(token)).thenReturn(false);
            when(jwtService.parseAccessToken(token)).thenReturn(mockJws);

            filter.doFilter(request, response, filterChain);

            verify(denylistService).isDenied(token);
            verify(jwtService).parseAccessToken(token);
            verify(filterChain).doFilter(request, response);
        }

        // ==== DECISION COVERAGE: Token parsing success (try block executes fully) ====
        
        @Test
        @DisplayName("Should set authentication when token parsing succeeds")
        void testTokenParsingSuccess() throws ServletException, IOException {
            // Decision: Token parsing → SUCCESS (no exception)
            String token = "valid.token";
            request.addHeader("Authorization", "Bearer " + token);
            
            Jws<Claims> mockJws = createMockJws("user@example.com", "USER");
            when(denylistService.isDenied(token)).thenReturn(false);
            when(jwtService.parseAccessToken(token)).thenReturn(mockJws);

            filter.doFilter(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(auth);
            verify(filterChain).doFilter(request, response);
        }

        // ==== DECISION COVERAGE: Token parsing failure (catch block executes) ====
        
        @Test
        @DisplayName("Should catch exception when token parsing fails")
        void testTokenParsingFailureCatchBlock() throws ServletException, IOException {
            // Decision: Token parsing → FAILURE (exception thrown)
            String token = "invalid.token";
            request.addHeader("Authorization", "Bearer " + token);
            
            when(denylistService.isDenied(token)).thenReturn(false);
            when(jwtService.parseAccessToken(token)).thenThrow(new MalformedJwtException("Invalid"));

            filter.doFilter(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNull(auth);
            verify(filterChain).doFilter(request, response);
        }

        // ==== DECISION COVERAGE: email != null && SecurityContext.getAuthentication() == null (TRUE branch) ====
        
        @Test
        @DisplayName("Should set authentication when email is not null and SecurityContext is empty")
        void testSetAuthenticationWhenContextEmpty() throws ServletException, IOException {
            // Decision: email != null && SecurityContextHolder.getContext().getAuthentication() == null → TRUE
            String token = "valid.token";
            request.addHeader("Authorization", "Bearer " + token);
            
            Jws<Claims> mockJws = createMockJws("user@example.com", "USER");
            when(denylistService.isDenied(token)).thenReturn(false);
            when(jwtService.parseAccessToken(token)).thenReturn(mockJws);

            filter.doFilter(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(auth);
            assertEquals("user@example.com", auth.getPrincipal());
            verify(filterChain).doFilter(request, response);
        }

        // ==== DECISION COVERAGE: email != null && SecurityContext.getAuthentication() == null (FALSE branch) ====
        
        @Test
        @DisplayName("Should not overwrite authentication when SecurityContext already has authentication")
        void testDoesNotOverwriteExistingAuthentication() throws ServletException, IOException {
            // Decision: email != null && SecurityContextHolder.getContext().getAuthentication() == null → FALSE
            String token = "valid.token";
            request.addHeader("Authorization", "Bearer " + token);
            
            // Pre-set authentication in SecurityContext
            Authentication existingAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    "existing@example.com", null, null);
            SecurityContextHolder.getContext().setAuthentication(existingAuth);
            
            Jws<Claims> mockJws = createMockJws("user@example.com", "USER");
            when(denylistService.isDenied(token)).thenReturn(false);
            when(jwtService.parseAccessToken(token)).thenReturn(mockJws);

            filter.doFilter(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(auth);
            assertEquals("existing@example.com", auth.getPrincipal()); // Should remain unchanged
            verify(filterChain).doFilter(request, response);
        }

        // ==== STATEMENT COVERAGE: Token extraction with trim() ====
        
        @Test
        @DisplayName("Should trim token after extracting from Bearer header")
        void testTokenTrimmedAfterExtraction() throws ServletException, IOException {
            // Statement: token = authHeader.substring(7).trim()
            String token = "valid.token";
            request.addHeader("Authorization", "Bearer " + token + "  "); // Extra spaces
            
            Jws<Claims> mockJws = createMockJws("user@example.com", "USER");
            when(denylistService.isDenied(token)).thenReturn(false);
            when(jwtService.parseAccessToken(token)).thenReturn(mockJws);

            filter.doFilter(request, response, filterChain);

            verify(jwtService).parseAccessToken(token); // Should be trimmed
            verify(filterChain).doFilter(request, response);
        }

        // ==== STATEMENT COVERAGE: Authority creation with role ====
        
        @Test
        @DisplayName("Should create SimpleGrantedAuthority with role from claims")
        void testAuthorityCreatedWithRole() throws ServletException, IOException {
            // Statement: Collections.singletonList(new SimpleGrantedAuthority(role))
            String token = "valid.token";
            request.addHeader("Authorization", "Bearer " + token);
            
            Jws<Claims> mockJws = createMockJws("user@example.com", "ADMIN");
            when(denylistService.isDenied(token)).thenReturn(false);
            when(jwtService.parseAccessToken(token)).thenReturn(mockJws);

            filter.doFilter(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(auth);
            assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")));
            verify(filterChain).doFilter(request, response);
        }

        // ==== STATEMENT COVERAGE: WebAuthenticationDetailsSource ====
        
        @Test
        @DisplayName("Should set authentication details from request")
        void testAuthenticationDetailsSetFromRequest() throws ServletException, IOException {
            // Statement: auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))
            String token = "valid.token";
            request.addHeader("Authorization", "Bearer " + token);
            request.setRemoteAddr("192.168.1.1");
            
            Jws<Claims> mockJws = createMockJws("user@example.com", "USER");
            when(denylistService.isDenied(token)).thenReturn(false);
            when(jwtService.parseAccessToken(token)).thenReturn(mockJws);

            filter.doFilter(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(auth);
            assertNotNull(auth.getDetails());
            verify(filterChain).doFilter(request, response);
        }
    }
}