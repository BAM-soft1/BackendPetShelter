package org.pet.backendpetshelter.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "animal")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_id")

    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "species_id", nullable = false)
    private Species species;


    @ManyToOne
    @JoinColumn(name = "breed_id")
    private Breed breed;

    private Date birthDate;
    private String sex;
    private Date intakeDate;
    private String Status;
    private int price;
    private Boolean isActive;

}
