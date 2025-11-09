package org.pet.backendpetshelter.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, Object> details;
}