package org.pet.backendpetshelter.Service;


import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pet.backendpetshelter.Configuration.JwtProperties;
import org.pet.backendpetshelter.Configuration.JwtService;
import org.pet.backendpetshelter.Configuration.RefreshToken;
import org.pet.backendpetshelter.DTO.AuthResponse;
import org.pet.backendpetshelter.DTO.LoginRequest;
import org.pet.backendpetshelter.DTO.RegisterUserRequest;
import org.pet.backendpetshelter.DTO.UserResponse;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Reposiotry.RefreshTokenRepository;
import org.pet.backendpetshelter.Reposiotry.UserRepository;
import org.pet.backendpetshelter.Roles;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenDenylistService denylistService;

    private final long accessExpSeconds;
    private final long refreshExpSeconds;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       TokenDenylistService denylistService,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       JwtProperties props) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.accessExpSeconds = props.getAccessExpirationSeconds();
        this.refreshExpSeconds = props.getRefreshExpirationSeconds();
        this.denylistService = denylistService;
    }

    public UserResponse register(RegisterUserRequest req) {
        if (userRepository.existsByEmail(req.getEmail().toLowerCase())) {
            throw new IllegalArgumentException("Email already in use");
        }

        if (!isPasswordStrong(req.getPassword())) {
            throw new IllegalArgumentException("Password must be at least 7 characters and include a special character");
        }

        User u = new User();
        u.setEmail(req.getEmail().toLowerCase());
        u.setFirstName(req.getFirstName().trim());
        u.setLastName(req.getLastName().trim());
        u.setPhone(req.getPhone());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setIsActive(true);
        u.setRole(Roles.USER);

        User saved = userRepository.save(u);
        return toUserResponse(saved);
    }

    // Intern DTO til login (kun brugt mellem service og controller)
    @Getter
    @AllArgsConstructor
    public static class LoginPair {
        private final String accessToken;
        private final String refreshToken;
        private final long accessExpiresInSeconds;
    }

    @Transactional
    public LoginPair loginIssueTokens(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail().toLowerCase())
                .orElseThrow(() -> new EntityNotFoundException("Invalid credentials"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new IllegalStateException("User is deactivated");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String accessToken = jwtService.generateAccessToken(
                user.getEmail(),
                Map.of("role", user.getRole().name(), "uid", user.getId())
        );

        // Single active refresh pr. user: ryd tidligere og lav nyt
        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setToken(UUID.randomUUID().toString());
        rt.setExpiresAt(Instant.now().plusSeconds(refreshExpSeconds));
        rt.setRevoked(false);
        refreshTokenRepository.save(rt);

        return new LoginPair(accessToken, rt.getToken(), accessExpSeconds);
    }

    // Intern DTO til refresh-rotation
    @Getter
    @AllArgsConstructor
    public static class RotateResult {
        private final String accessToken;
        private final String refreshToken; // ny token som skal i cookie
    }

    public RotateResult rotateRefreshToken(String refreshToken) {
        RefreshToken rt = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException("Invalid refresh token"));

        if (rt.getRevoked() || rt.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Refresh token expired or revoked");
        }

        var user = rt.getUser();

        // Revoke gammel og udsted en ny
        rt.setRevoked(true);
        refreshTokenRepository.save(rt);

        RefreshToken newRt = new RefreshToken();
        newRt.setUser(user);
        newRt.setToken(UUID.randomUUID().toString());
        newRt.setExpiresAt(Instant.now().plusSeconds(refreshExpSeconds));
        newRt.setRevoked(false);
        refreshTokenRepository.save(newRt);

        String accessToken = jwtService.generateAccessToken(
                user.getEmail(),
                Map.of("role", user.getRole().name(), "uid", user.getId())
        );

        return new RotateResult(accessToken, newRt.getToken());
    }

    public void logout(String accessToken, String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepository.findByToken(refreshToken).ifPresent(rt -> {
                rt.setRevoked(true);
                refreshTokenRepository.save(rt);
            });
        }
        if (accessToken != null && !accessToken.isBlank()) {
            denylistService.deny(accessToken, accessExpSeconds);
        }
    }

    private boolean isPasswordStrong(String pwd) {
        if (pwd == null || pwd.length() < 7) return false;
        return pwd.matches(".*[!@#$%^&*()_+=\\-{}:;\"'<>,.?/|\\[\\]\\\\].*");
    }

    private UserResponse toUserResponse(User u) {
        return new UserResponse(
                u.getId(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                u.getPhone(),
                u.getIsActive(),
                u.getRole()
        );
    }

    public long getAccessExpSeconds() { return accessExpSeconds; }
}