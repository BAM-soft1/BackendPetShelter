package org.pet.backendpetshelter.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Veterinarian")
public class Veterinian {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "veterinian_id")
    private Long id;

    // Står ikke en relation i diagram
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String licenseNumber;
    private String clinicName;
    private Boolean isActive;
}
