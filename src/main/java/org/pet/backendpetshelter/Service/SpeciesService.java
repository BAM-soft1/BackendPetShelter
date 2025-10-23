package org.pet.backendpetshelter.Service;


import org.pet.backendpetshelter.DTO.AnimalDTOResponse;
import org.pet.backendpetshelter.DTO.SpeciesDTORequest;
import org.pet.backendpetshelter.DTO.SpeciesDTOResponse;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.Species;
import org.pet.backendpetshelter.Reposiotry.SpeciesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpeciesService {

    private final SpeciesRepository speciesRepository;

    public SpeciesService(SpeciesRepository speciesRepository) {
        this.speciesRepository = speciesRepository;
    }



    /* Get All Species */
    public List<SpeciesDTOResponse> getAllSpecies(){
        return speciesRepository.findAll().stream().map(SpeciesDTOResponse::new).collect(Collectors.toList());
    }

    /* Get Specific Species */
    public SpeciesDTOResponse GetSpeciesById(Long id){
        Species species = speciesRepository.findById(id).orElseThrow(() -> new RuntimeException("Animal not found with id: " + id));
        return new SpeciesDTOResponse(species);
    }


    /* Add Species */
    public SpeciesDTOResponse addSpecies(SpeciesDTORequest request){

        Species newSpecies = new Species();
        newSpecies.setName(request.getName());
        speciesRepository.save(newSpecies);
        return new SpeciesDTOResponse(newSpecies);
    }


    /* Update Species */
    public SpeciesDTOResponse updateSpecies(Long id, SpeciesDTORequest request){
        Species species = speciesRepository.findById(id).orElseThrow(() -> new RuntimeException("Species not found with id: " + id));
        species.setName(request.getName());
        speciesRepository.save(species);
        return new SpeciesDTOResponse(species);
    }

    /* Delete Species */
    public void deleteSpecies(Long id){
        Species species = speciesRepository.findById(id).orElseThrow(() -> new RuntimeException("Species not found with id: " + id));
        speciesRepository.delete(species);
    }


}
