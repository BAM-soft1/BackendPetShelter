package org.pet.backendpetshelter.Service;

import org.pet.backendpetshelter.DTO.VaccinationRequest;
import org.pet.backendpetshelter.DTO.VaccinationResponse;
import org.pet.backendpetshelter.Entity.Vaccination;
import org.pet.backendpetshelter.Repository.VaccinationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VaccinationService {

    private final VaccinationRepository vaccinationRepository;


    public VaccinationService(VaccinationRepository vaccinationRepository) {
        this.vaccinationRepository = vaccinationRepository;
    }



    public List<VaccinationResponse> GetAllVaccinations() {
        return vaccinationRepository.findAll().stream()
                .map(VaccinationResponse::new)
                .collect(Collectors.toList());
    }

    /* Get Specific Vaccination */
    public VaccinationResponse GetVaccinationById(Long id) {
        Vaccination vaccination = vaccinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccination not found with id: " + id));
        return new VaccinationResponse(vaccination);
    }


    /* Add Vaccination */
    public VaccinationResponse addVaccination(VaccinationRequest request) {

        Vaccination vaccination = new Vaccination();

        vaccination.setAnimal(request.getAnimal());
        vaccination.setVeterinarian(request.getVeterinarian());
        vaccination.setDateAdministered(request.getDateAdministered());
        vaccination.setVaccinationType(request.getVaccinationType());
        vaccination.setNextDueDate(request.getNextDueDate());

        vaccinationRepository.save(vaccination);
        return new VaccinationResponse(vaccination);
    }


    /* Update Vaccination */
    public VaccinationResponse updateVaccination(Long id, VaccinationRequest request) {
        Vaccination vaccination = vaccinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccination not found with id: " + id));

        vaccination.setAnimal(request.getAnimal());
        vaccination.setVeterinarian(request.getVeterinarian());
        vaccination.setDateAdministered(request.getDateAdministered());
        vaccination.setVaccinationType(request.getVaccinationType());
        vaccination.setNextDueDate(request.getNextDueDate());

        vaccinationRepository.save(vaccination);
        return new VaccinationResponse(vaccination);
    }

    /* Delete Vaccination */
    public void deleteVaccination(Long id) {
        Vaccination vaccination = vaccinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccination not found with id: " + id));
        vaccinationRepository.delete(vaccination);
    }


}
