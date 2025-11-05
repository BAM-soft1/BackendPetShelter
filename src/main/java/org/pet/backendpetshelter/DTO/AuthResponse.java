package org.pet.backendpetshelter.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String tokenType; // "Bearer"
    private long expiresInSeconds;
}