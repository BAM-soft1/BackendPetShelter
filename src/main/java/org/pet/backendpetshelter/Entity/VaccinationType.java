package org.pet.backendpetshelter.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class VaccinationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccinationType_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String vaccineName;

    @Column(length = 1000)
    private String description;

    private int duration_months;
    private int required_for_adoption;

}



