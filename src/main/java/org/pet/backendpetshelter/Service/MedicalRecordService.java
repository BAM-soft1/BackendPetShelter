package org.pet.backendpetshelter.Service;

import jakarta.persistence.EntityNotFoundException;
import org.pet.backendpetshelter.DTO.MedicalRecordDTORequest;
import org.pet.backendpetshelter.DTO.MedicalRecordDTOResponse;
import org.pet.backendpetshelter.Entity.MedicalRecord;
import org.pet.backendpetshelter.Repository.MedicalRecordReposiotry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class  MedicalRecordService {


    private final MedicalRecordReposiotry medicalRecordReposiotry;

    public MedicalRecordService(MedicalRecordReposiotry medicalRecordReposiotry) {
        this.medicalRecordReposiotry = medicalRecordReposiotry;
    }

    /* Get all medical records */

    public List<MedicalRecordDTOResponse> getAllMedicalRecords() {
        return medicalRecordReposiotry.findAll().stream()
                .map(MedicalRecordDTOResponse::new)
                .toList();
    }


    /* Get specific medical record */
    public MedicalRecordDTOResponse getMedicalRecordById(Long id) {

        MedicalRecord medicalRecord = medicalRecordReposiotry.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical Record not found with id: " + id));
        return new MedicalRecordDTOResponse(medicalRecord);

    }


    /* Add medical record */
    public MedicalRecordDTOResponse addMedicalRecord(MedicalRecordDTORequest reuqest) {

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setAnimal(reuqest.getAnimal());
        medicalRecord.setVeterinarian(reuqest.getVeterinarian());
        medicalRecord.setDate(reuqest.getDate());
        medicalRecord.setDiagnosis(reuqest.getDiagnosis());
        medicalRecord.setTreatment(reuqest.getTreatment());
        medicalRecord.setCost(reuqest.getCost());
        medicalRecordReposiotry.save(medicalRecord);
        return new MedicalRecordDTOResponse(medicalRecord);
    }

    /* Update medical record */
    public MedicalRecordDTOResponse updateMedicalRecord(Long id, MedicalRecordDTORequest request) {
        MedicalRecord medicalRecord = medicalRecordReposiotry.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical Record not found with id: " + id));

        medicalRecord.setAnimal(request.getAnimal());
        medicalRecord.setVeterinarian(request.getVeterinarian());
        medicalRecord.setDate(request.getDate());
        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setTreatment(request.getTreatment());
        medicalRecord.setCost(request.getCost());

        medicalRecordReposiotry.save(medicalRecord);
        return new MedicalRecordDTOResponse(medicalRecord);
    }


    /* Delete medical record */
    public void deleteMedicalRecord(Long id) {
        if (medicalRecordReposiotry.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. User not found with id: " + id);
        }
        medicalRecordReposiotry.deleteById(id);

    }
}