package org.pet.backendpetshelter.DTO;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.MedicalRecord;
import org.pet.backendpetshelter.Entity.Veterinarian;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class MedicalRecordDTOResponse {
    private Long id;
    private Animal animal;
    private Veterinarian veterinarian;
    private Date date;
    private String diagnosis;
    private String treatment;
    private int cost;


    public MedicalRecordDTOResponse(MedicalRecord medicalRecord) {
        this.id = medicalRecord.getId();
        this.animal = medicalRecord.getAnimal();
        this.veterinarian = medicalRecord.getVeterinarian();
        this.date = medicalRecord.getDate();
        this.diagnosis = medicalRecord.getDiagnosis();
        this.treatment = medicalRecord.getTreatment();
        this.cost = medicalRecord.getCost();
    }
}