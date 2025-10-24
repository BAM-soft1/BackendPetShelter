package org.pet.backendpetshelter.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Breed")
public class Breed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "breed_id")


    private Long id;

    @ManyToOne
    @JoinColumn(name = "species_id")
    private Species species;
    private String name;

}
