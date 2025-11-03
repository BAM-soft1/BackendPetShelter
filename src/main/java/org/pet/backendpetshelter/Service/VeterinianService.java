package org.pet.backendpetshelter.Service;


import jakarta.validation.Valid;
import org.pet.backendpetshelter.DTO.VeterinianDTORequest;
import org.pet.backendpetshelter.DTO.VeterinianDTOResponse;
import org.pet.backendpetshelter.Entity.Veterinian;
import org.pet.backendpetshelter.Reposiotry.VeterinianRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VeterinianService {


    private final VeterinianRepository veterinianRepository;

    public VeterinianService(VeterinianRepository veterinianRepository) {
        this.veterinianRepository = veterinianRepository;
    }


    /** * Get All Veterinians
     *
     * @return
     */


    /* Get All Veterinians */
    public List<VeterinianDTOResponse> GetAllVeterinians() {
        return veterinianRepository.findAll().stream()
                .map(VeterinianDTOResponse::new)
                .collect(Collectors.toList());
    }


    /** Get Specific Veterinian
     * @param id the ID of the veterinian to be retrieved
     * @return VeterinianDTOResponse
     * This method retrieves a specific veterinian by ID and maps it to VeterinianDTOResponse.
     */


    /* Get Specific Veterinian */
    public VeterinianDTOResponse GetVeterinianById(Long id) {
        Veterinian veterinian = veterinianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veterinian not found with id: " + id));
        return new VeterinianDTOResponse(veterinian);
    }


    /**  * Add Veterinian
     * @param veterinian the veterinian to be added
     * @return VeterinianDTOResponse
     * This method adds a new veterinian to the repository and maps it to VeterinianDTOResponse.
     */

    /* Add Veterinian */
    public VeterinianDTOResponse addVeterinian(@Valid VeterinianDTORequest veterinian) {


        Veterinian newVeterinian = new Veterinian();
        newVeterinian.setUser(veterinian.getUser());
        newVeterinian.setLicenseNumber(veterinian.getLicenseNumber());
        newVeterinian.setClinicName(veterinian.getClinicName());
        newVeterinian.setIsActive(true);
        veterinianRepository.save(newVeterinian);
        return new VeterinianDTOResponse(newVeterinian);
    }


  /** * Update Veterinian
     * @param id the ID of the veterinian to be updated
     * @param request the updated veterinian data
     * @return VeterinianDTOResponse
     * This method updates an existing veterinian in the repository and maps it to VeterinianDTOResponse.
     */

    /* Update Veterinian */
    public VeterinianDTOResponse updateVeterinian(Long id, VeterinianDTORequest request) {
        Veterinian veterinian = veterinianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veterinian not found with id: " + id));
        veterinian.setUser(request.getUser());
        veterinian.setLicenseNumber(request.getLicenseNumber());
        veterinian.setClinicName(request.getClinicName());
        veterinian.setIsActive(request.getIsActive());
        veterinianRepository.save(veterinian);
        return new VeterinianDTOResponse(veterinian);

    }


    /** * Delete Veterinian
     * @param id the ID of the veterinian to be deleted
     * This method deletes a veterinian from the repository by ID.
     */


    /* Delete Veterinian */
    public void deleteVeterinian(Long id) {
        if (!veterinianRepository.existsById(id)) {
            throw new RuntimeException("Veterinian not found with id: " + id);
        } else {
            veterinianRepository.deleteById(id);
        }
    }



}
