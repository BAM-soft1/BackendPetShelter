package org.pet.backendpetshelter.DTO;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Entity.Veterinian;

@Getter
@Setter
@NoArgsConstructor
public class VeterinianDTOResponse {
    private Long id;
    private User user;
    private String licenseNumber;
    private String clinicName;
    private Boolean isActive;


    public VeterinianDTOResponse(Veterinian veterinian) {
        this.id = veterinian.getId();
        this.user = veterinian.getUser();
        this.licenseNumber = veterinian.getLicenseNumber();
        this.clinicName = veterinian.getClinicName();
        this.isActive = veterinian.getIsActive();
    }
}
