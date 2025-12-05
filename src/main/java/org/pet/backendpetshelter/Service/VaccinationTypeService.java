package org.pet.backendpetshelter.Service;


import org.pet.backendpetshelter.DTO.VaccinationTypeResponse;
import org.pet.backendpetshelter.Entity.VaccinationType;
import org.pet.backendpetshelter.Repository.VaccinationTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VaccinationTypeService {

    private final VaccinationTypeRepository vaccinationTypeRepository;

    public VaccinationTypeService(VaccinationTypeRepository vaccinationTypeRepository) {
        this.vaccinationTypeRepository = vaccinationTypeRepository;
    }


    /* Get All Vaccination Types */
    public List<VaccinationTypeResponse> GetAllVaccinationTypes() {
        return vaccinationTypeRepository.findAll().stream()
                .map(VaccinationTypeResponse::new)
                .collect(Collectors.toList());
    }


    /* Get Specific Vaccination Type */
    public VaccinationTypeResponse GetVaccinationTypeById(Long id) {
        VaccinationType vaccinationType = vaccinationTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccination Type not found with id: " + id));
        return new VaccinationTypeResponse(vaccinationType);
    }

    /* Add Vaccination Type */
    public VaccinationTypeResponse addVaccinationType(VaccinationTypeResponse request) {
        VaccinationType vaccinationType = new VaccinationType();

        vaccinationType.setVaccineName(request.getVaccineName());
        vaccinationType.setDescription(request.getDescription());
        vaccinationType.setDurationMonths(request.getDurationMonths());
        vaccinationType.setRequiredForAdoption(request.getRequiredForAdoption());

        vaccinationTypeRepository.save(vaccinationType);
        return new VaccinationTypeResponse(vaccinationType);


    }


    /* Update Vaccination Type */
    public VaccinationTypeResponse updateVaccinationType(Long id, VaccinationTypeResponse request) {
        VaccinationType vaccinationType = vaccinationTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccination Type not found with id: " + id));

        vaccinationType.setVaccineName(request.getVaccineName());
        vaccinationType.setDescription(request.getDescription());
        vaccinationType.setDurationMonths(request.getDurationMonths());
        vaccinationType.setRequiredForAdoption(request.getRequiredForAdoption());

        vaccinationTypeRepository.save(vaccinationType);
        return new VaccinationTypeResponse(vaccinationType);
    }

    /* Delete Vaccination Type */
    public void deleteVaccinationType(Long id) {
        VaccinationType vaccinationType = vaccinationTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccination Type not found with id: " + id));
        vaccinationTypeRepository.delete(vaccinationType);
    }



}
