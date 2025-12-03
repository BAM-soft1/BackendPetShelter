package org.pet.backendpetshelter.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vaccination_type")
public class VaccinationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccination_type_id")
    private Long id;

    @Column(unique = true, nullable = false, length = 32)
    private String vaccineName;

    @Column(length = 1000)
    private String description;

    private int durationMonths;
    private int requiredForAdoption;

}



