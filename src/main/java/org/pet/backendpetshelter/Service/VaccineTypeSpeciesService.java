package org.pet.backendpetshelter.Service;

import org.pet.backendpetshelter.DTO.VaccinationRequest;
import org.pet.backendpetshelter.DTO.VaccineTypeSpeciesResponse;
import org.pet.backendpetshelter.Entity.VaccineTypeSpecies;
import org.pet.backendpetshelter.Repository.VaccineTypeSpeciesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VaccineTypeSpeciesService {

    private final VaccineTypeSpeciesRepository vaccineTypeSpeciesRepository;

    public VaccineTypeSpeciesService(VaccineTypeSpeciesRepository vaccineTypeSpeciesRepository) {
        this.vaccineTypeSpeciesRepository = vaccineTypeSpeciesRepository;
    }

    /* Get All VaccineTypeSpecies */

    public List<VaccineTypeSpeciesResponse> GetAllVaccineTypeSpecies(){
        return vaccineTypeSpeciesRepository.findAll().stream()
                .map(VaccineTypeSpeciesResponse::new)
                .collect(Collectors.toList());

    }

    /* Get Specific VaccineTypeSpecies */
    public VaccineTypeSpeciesResponse GetVaccineTypeSpeciesById(Long id){
        VaccineTypeSpecies vaccineTypeSpecies = vaccineTypeSpeciesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VaccineTypeSpecies not found with id: " + id));
        return new VaccineTypeSpeciesResponse(vaccineTypeSpecies);
    }

    /* Add VaccineTypeSpecies */
    public VaccineTypeSpeciesResponse addVaccineTypeSpecies(VaccinationRequest request){
        VaccineTypeSpecies vaccineTypeSpecies = new VaccineTypeSpecies();


        vaccineTypeSpecies.setSpecies(request.getAnimal().getSpecies());
        vaccineTypeSpecies.setVaccinationType(request.getVaccinationType());

        vaccineTypeSpeciesRepository.save(vaccineTypeSpecies);
        return new VaccineTypeSpeciesResponse(vaccineTypeSpecies);
    }


    /* Update VaccineTypeSpecies */
    public VaccineTypeSpeciesResponse updateVaccineTypeSpecies(Long id, VaccinationRequest request
    ){
        VaccineTypeSpecies vaccineTypeSpecies = vaccineTypeSpeciesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VaccineTypeSpecies not found with id: " + id));

        vaccineTypeSpecies.setSpecies(request.getAnimal().getSpecies());
        vaccineTypeSpecies.setVaccinationType(request.getVaccinationType());

        vaccineTypeSpeciesRepository.save(vaccineTypeSpecies);
        return new VaccineTypeSpeciesResponse(vaccineTypeSpecies);
    }

    /* Delete VaccineTypeSpecies */
    public void deleteVaccineTypeSpecies(Long id){
        VaccineTypeSpecies vaccineTypeSpecies = vaccineTypeSpeciesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VaccineTypeSpecies not found with id: " + id));
        vaccineTypeSpeciesRepository.delete(vaccineTypeSpecies);
    }
}
