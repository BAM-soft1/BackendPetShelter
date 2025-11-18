package org.pet.backendpetshelter.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "breed")
public class Breed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "breed_id")


    private Long id;

    @ManyToOne
    @JoinColumn(name = "species_id")
    private Species species;

    @Column(nullable = false, length = 32)
    private String name;

}
