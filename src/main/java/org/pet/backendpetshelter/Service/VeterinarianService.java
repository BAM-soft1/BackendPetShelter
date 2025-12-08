package org.pet.backendpetshelter.Service;


import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.pet.backendpetshelter.DTO.VeterinarianDTORequest;
import org.pet.backendpetshelter.DTO.VeterinarianDTOResponse;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Entity.Veterinarian;
import org.pet.backendpetshelter.Repository.UserRepository;
import org.pet.backendpetshelter.Repository.VeterinarianRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VeterinarianService {


    private final VeterinarianRepository veterinarianRepository;
    private final UserRepository userRepository;

    public VeterinarianService(VeterinarianRepository veterinarianRepository, UserRepository userRepository) {
        this.veterinarianRepository = veterinarianRepository;
        this.userRepository = userRepository;
    }


    /** * Get All Veterinians
     *
     * @return
     */


    /* Get All Veterinians */
    public List<VeterinarianDTOResponse> GetAllVeterinians() {
        return veterinarianRepository.findAll().stream()
                .map(VeterinarianDTOResponse::new)
                .collect(Collectors.toList());
    }


    /** Get Specific Veterinian
     * @param id the ID of the veterinian to be retrieved
     * @return VeterinianDTOResponse
     * This method retrieves a specific veterinian by ID and maps it to VeterinianDTOResponse.
     */


    /* Get Specific Veterinian */
    public VeterinarianDTOResponse GetVeterinianById(Long id) {
        Veterinarian veterinarian = veterinarianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veterinian not found with id: " + id));
        return new VeterinarianDTOResponse(veterinarian);
    }


    /**  * Add Veterinian
     * @param veterinarian the veterinarian to be added
     * @return VeterinianDTOResponse
     * This method adds a new veterinarian to the repository and maps it to VeterinianDTOResponse.
     */

    /* Add Veterinarian */
    public VeterinarianDTOResponse addVeterinarian(@Valid VeterinarianDTORequest veterinarian) {



        validateLicenseNumber(veterinarian.getLicenseNumber());
        validateUser(veterinarian.getUserId());
        validateClinicName(veterinarian.getClinicName());
        validateIsActive(veterinarian.getIsActive());


        User user = userRepository.findById(veterinarian.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));


        Veterinarian newVeterinarian = new Veterinarian();
        newVeterinarian.setUser(user);
        newVeterinarian.setLicenseNumber(veterinarian.getLicenseNumber());
        newVeterinarian.setClinicName(veterinarian.getClinicName());
        newVeterinarian.setIsActive(true);
        veterinarianRepository.save(newVeterinarian);
        return new VeterinarianDTOResponse(newVeterinarian);
    }


    // Validation Methods
    private void validateUser(Long usersId) {
        if (usersId == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        if (!userRepository.existsById(usersId)) {
            throw new IllegalArgumentException("User with ID " + usersId + " does not exist.");
        }

    }

    private void validateLicenseNumber(String licenseNumber) {
        if (licenseNumber == null || licenseNumber.isBlank()) {
            throw new IllegalArgumentException("License number cannot be null or empty.");
        }

        if (licenseNumber.length() > 20) {
            throw new IllegalArgumentException("License number cannot exceed 20 characters.");
        }

        if (!licenseNumber.matches("^[a-zA-Z0-9-]+$")) {
            throw new IllegalArgumentException("License number contains invalid characters.");
        }
    }

    private void validateClinicName(String clinicName) {
        if (clinicName == null || clinicName.isBlank()) {
            throw new IllegalArgumentException("Clinic name cannot be null or empty.");
        }

        if (clinicName.length() > 65) {
            throw new IllegalArgumentException("Clinic name cannot exceed 65 characters.");
        }

        if (!clinicName.matches("^[a-zA-Z0-9\\s'-]+$")) {
            throw new IllegalArgumentException("Clinic name contains invalid characters.");
        }
    }

    private void validateIsActive(Boolean isActive) {
        if (isActive == null) {
            throw new IllegalArgumentException("isActive cannot be null.");
        }
    }


    /** * Update Veterinian
     * @param id the ID of the veterinian to be updated
     * @param request the updated veterinian data
     * @return VeterinianDTOResponse
     * This method updates an existing veterinian in the repository and maps it to VeterinianDTOResponse.
     */

    /* Update Veterinian */
    public VeterinarianDTOResponse updateVeterinian(Long id, VeterinarianDTORequest request) {
        Veterinarian veterinarian = veterinarianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veterinian not found with id: " + id));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        veterinarian.setUser(user);
        veterinarian.setLicenseNumber(request.getLicenseNumber());
        veterinarian.setClinicName(request.getClinicName());
        veterinarian.setIsActive(request.getIsActive());
        veterinarianRepository.save(veterinarian);
        return new VeterinarianDTOResponse(veterinarian);

    }


    /** * Delete Veterinian
     * @param id the ID of the veterinian to be deleted
     * This method deletes a veterinian from the repository by ID.
     */


    /* Delete Veterinian */
    public void deleteVeterinian(Long id) {
        if (!veterinarianRepository.existsById(id)) {
            throw new RuntimeException("Veterinian not found with id: " + id);
        } else {
            veterinarianRepository.deleteById(id);
        }
    }



}