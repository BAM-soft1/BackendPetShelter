package org.pet.backendpetshelter.Service;

import org.pet.backendpetshelter.DTO.FosterCareRespons;
import org.pet.backendpetshelter.Entity.FosterCare;
import org.pet.backendpetshelter.Repository.FosterCareRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FosterCareService {

    private final FosterCareRepository repository;

    public FosterCareService(FosterCareRepository repository) {
        this.repository = repository;
    }


    /* Get All Foster Cares */

    public List<FosterCareRespons> GetAllFosterCares() {
        return repository.findAll().stream()
                .map(FosterCareRespons::new)
                .toList();
    }


    /* Get Specific Foster Care */

    public FosterCareRespons GetFosterCareById(Long id){
        FosterCare fosterCare = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Foster Care not found with id: " + id));
        return new FosterCareRespons(fosterCare);
    }


    /* Add Foster Care */
    public FosterCareRespons addFosterCare(FosterCareRespons request){
        FosterCare fosterCare = new FosterCare();

        fosterCare.setFosterParent(request.getFosterParent());
        fosterCare.setAnimal(request.getAnimal());
        fosterCare.setStartDate(request.getStartDate());
        fosterCare.setEndDate(request.getEndDate());
        fosterCare.setIsActive(request.getIsActive());

        repository.save(fosterCare);
        return new FosterCareRespons(fosterCare);
    }


    /* Update Foster Care */
    public FosterCareRespons updateFosterCare(Long id, FosterCareRespons request) {
        FosterCare fosterCare = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Foster Care not found with id: " + id));

        fosterCare.setFosterParent(request.getFosterParent());
        fosterCare.setAnimal(request.getAnimal());
        fosterCare.setStartDate(request.getStartDate());
        fosterCare.setEndDate(request.getEndDate());
        fosterCare.setIsActive(request.getIsActive());

        repository.save(fosterCare);
        return new FosterCareRespons(fosterCare);
    }

    /* Delete Foster Care */
    public void deleteFosterCare(Long id) {
        if (repository.findById(id).isPresent()) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Foster Care not found with id: " + id);
        }
    }


}
