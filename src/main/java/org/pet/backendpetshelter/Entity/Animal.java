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

    @Column(nullable = false, length = 80)
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

    @Column(nullable = false, length = 80)
    private String status;
    private int price;
    private Boolean isActive;
    private String imageUrl;

}
