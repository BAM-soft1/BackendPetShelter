package org.pet.backendpetshelter.Controller;

import jakarta.validation.Valid;
import org.pet.backendpetshelter.DTO.UserDTORequest;
import org.pet.backendpetshelter.DTO.UserDTOResponse;
import org.pet.backendpetshelter.DTO.UserUpdateDTO;
import org.pet.backendpetshelter.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDTOResponse> getAllUsers() {
        return userService.GetAllUsers();
    }

    @GetMapping("/{id}")
    public UserDTOResponse getUserById(@PathVariable Long id) {
        return userService.GetUserById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<UserDTOResponse> addUser(@Valid @RequestBody UserDTORequest userDTORequest) {
        return ResponseEntity.status(201).body(userService.addUser(userDTORequest));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTOResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserDTOResponse updatedUser = userService.updateUser(id, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
