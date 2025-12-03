package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.Veterinarian;

import java.util.Date;

@Getter
@Setter
public class MedicalRecordDTORequest {
    private Animal animal;
    private Veterinarian veterinarian;
    private Date date;
    private String diagnosis;
    private String treatment;
    private int cost;
}