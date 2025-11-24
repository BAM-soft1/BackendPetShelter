package org.pet.backendpetshelter.Controller;

import org.pet.backendpetshelter.DTO.UserResponse;
import org.pet.backendpetshelter.Repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal String principalEmail) {
        var user = userRepository.findByEmail(principalEmail).orElseThrow();
        return ResponseEntity.ok(new UserResponse(
                user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getPhone(), user.getIsActive(), user.getRole()
        ));
    }
}