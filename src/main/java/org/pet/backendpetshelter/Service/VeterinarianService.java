package org.pet.backendpetshelter.Service;


import jakarta.validation.Valid;
import org.pet.backendpetshelter.DTO.VeterinarianDTORequest;
import org.pet.backendpetshelter.DTO.VeterinarianDTOResponse;
import org.pet.backendpetshelter.Entity.Veterinarian;
import org.pet.backendpetshelter.Reposiotry.VeterinarianRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VeterinarianService {


    private final VeterinarianRepository veterinarianRepository;

    public VeterinarianService(VeterinarianRepository veterinarianRepository) {
        this.veterinarianRepository = veterinarianRepository;
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
     * @param veterinian the veterinian to be added
     * @return VeterinianDTOResponse
     * This method adds a new veterinian to the repository and maps it to VeterinianDTOResponse.
     */

    /* Add Veterinian */
    public VeterinarianDTOResponse addVeterinian(@Valid VeterinarianDTORequest veterinian) {


        Veterinarian newVeterinarian = new Veterinarian();
        newVeterinarian.setUser(veterinian.getUser());
        newVeterinarian.setLicenseNumber(veterinian.getLicenseNumber());
        newVeterinarian.setClinicName(veterinian.getClinicName());
        newVeterinarian.setIsActive(true);
        veterinarianRepository.save(newVeterinarian);
        return new VeterinarianDTOResponse(newVeterinarian);
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
        veterinarian.setUser(request.getUser());
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