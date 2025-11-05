package org.pet.backendpetshelter.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.pet.backendpetshelter.Configuration.JwtProperties;
import org.pet.backendpetshelter.DTO.AuthResponse;
import org.pet.backendpetshelter.DTO.LoginRequest;
import org.pet.backendpetshelter.DTO.RegisterUserRequest;
import org.pet.backendpetshelter.DTO.UserResponse;
import org.pet.backendpetshelter.Service.AuthService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String REFRESH_COOKIE = "refresh_token";

    private final AuthService authService;
    private final long refreshMaxAge;

    public AuthController(AuthService authService, JwtProperties props) {
        this.authService = authService;
        this.refreshMaxAge = props.getRefreshExpirationSeconds();
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        var user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse res) {
        var pair = authService.loginIssueTokens(request);

        ResponseCookie refresh = ResponseCookie.from(REFRESH_COOKIE, pair.getRefreshToken())
                .httpOnly(true)
                .secure(true)            // true i prod (HTTPS)
                .path("/api/auth")
                .maxAge(refreshMaxAge)
                .sameSite("Strict")      // Her kan du sætte SameSite
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + pair.getAccessToken())
                .body(new AuthResponse(pair.getAccessToken(), "Bearer", pair.getAccessExpiresInSeconds()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest req, HttpServletResponse res) {
        String refreshToken = extractRefreshCookie(req);
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var result = authService.rotateRefreshToken(refreshToken);

        ResponseCookie refresh = ResponseCookie.from(REFRESH_COOKIE, result.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/auth")
                .maxAge(refreshMaxAge)
                .sameSite("Strict")
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());

        return ResponseEntity.ok(new AuthResponse(result.getAccessToken(), "Bearer", authService.getAccessExpSeconds()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req, HttpServletResponse res,
                                       @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7).trim();
        }
        String refreshToken = extractRefreshCookie(req);

        authService.logout(accessToken, refreshToken);

        // Slet refresh cookie
        ResponseCookie delete = ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, delete.toString());

        return ResponseEntity.noContent().build();
    }

    private String extractRefreshCookie(HttpServletRequest req) {
        if (req.getCookies() == null) return null;
        for (Cookie c : req.getCookies()) {
            if (REFRESH_COOKIE.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}