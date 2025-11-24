package org.pet.backendpetshelter.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "foster_care")
public class FosterCare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fostercare_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "animal_animal_id")
    private Animal animal;

    @ManyToOne
    @JoinColumn(name = "foster_parent_user_id")
    private User fosterParent;

    private Date startDate;
    private Date endDate;

    @Column(nullable = false)
    private Boolean isActive  = true;;

}
