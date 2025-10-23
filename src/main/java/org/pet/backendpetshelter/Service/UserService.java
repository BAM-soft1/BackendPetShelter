package org.pet.backendpetshelter.Service;


import jakarta.persistence.EntityNotFoundException;
import org.pet.backendpetshelter.DTO.UserDTORequest;
import org.pet.backendpetshelter.DTO.UserDTOResponse;
import org.pet.backendpetshelter.DTO.UserUpdateDTO;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Reposiotry.UserRepository;
import org.pet.backendpetshelter.Roles;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    /** Get All Users
     * @return List of UserDTOResponse
     * This method retrieves all users from the repository and maps them to UserDTOResponse.
     */

    /* Get All Users  */
    public List<UserDTOResponse> GetAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTOResponse::new)
                .collect(Collectors.toList());
    }

    /** Get Spescific User
     * @param id the ID of the user to be retrieved
     * @return UserDTOResponse
     * This method retrieves a specific user by ID and maps it to UserDTOResponse.
     */

    /* Get Specific User  */
    public UserDTOResponse GetUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return new UserDTOResponse(user);
    }



    /*Add User */
    public UserDTOResponse addUser(UserDTORequest request){

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setPhone(request.getPhone());
        newUser.setIsActive(request.getIsActive());
        newUser.setRole(Optional.ofNullable(request.getRole()).orElse(Roles.ADOPTER));
        userRepository.save(newUser);
        return new UserDTOResponse(newUser);

    }

    /** Update User
     * @param id the ID of the user to be updated
     * @param request the UserUpdateDTO containing updated user information
     * @return UserDTOResponse
     * This method updates a user's information based on the provided UserUpdateDTO.
     */


    /*Update User  */
    public UserDTOResponse updateUser(Long id, UserUpdateDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setIsActive(request.getIsActive());

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        userRepository.save(user);
        return new UserDTOResponse(user);
    }


    /** Delete User
     * @param id the ID of the user to be deleted
     * This method deletes a user by ID from the repository.
     */


    /* Delete User  */
        public void deleteUser (Long id){
            if (!userRepository.existsById(id)) {
                throw new EntityNotFoundException("Cannot delete. User not found with id: " + id);
            }
            userRepository.deleteById(id);
        }
}
