package org.pet.backendpetshelter.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "adoption")
public class Adoption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adoption_id")

    private Long id;

    @ManyToOne
    @JoinColumn(name = "adoption_user_id", nullable = false)
    private User adoptionUser;


    @ManyToOne
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;


    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private AdoptionApplication application;

    private Date adoptionDate;
    private Boolean isActive;





}
