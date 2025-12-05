package org.pet.backendpetshelter.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "vaccination")
public class Vaccination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccination_id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne
    @JoinColumn(name = "vet_id")
    private Veterinarian veterinarian;

    private Date dateAdministered;

    @ManyToOne
    @JoinColumn(name = "vaccination_type_id")
    private VaccinationType vaccinationType;

    private Date nextDueDate;
}
