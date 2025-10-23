package org.pet.backendpetshelter.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "species_id", nullable = false)
    private Species species;

    private Date birthDate;
    private String Sex;
    private Date intakeDate;
    //private String Status;
    private int price;
    private Boolean isActive;

}
