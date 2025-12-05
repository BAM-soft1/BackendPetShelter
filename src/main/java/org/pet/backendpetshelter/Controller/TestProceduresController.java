package org.pet.backendpetshelter.Controller;

import org.pet.backendpetshelter.Reposiotry.AnimalProceduresRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test-procedures")
@CrossOrigin
public class TestProceduresController {

    private final AnimalProceduresRepository animalProceduresRepository;

    public TestProceduresController(AnimalProceduresRepository animalProceduresRepository) {
        this.animalProceduresRepository = animalProceduresRepository;
    }

    /**
     * Test GetAnimalAge function
     * GET /api/test-procedures/animal-age?animalId=1
     */
    @GetMapping("/animal-age")
    public ResponseEntity<Map<String, Object>> testAnimalAge(@RequestParam Long animalId) {
        // You would typically get the birthdate from the animal entity
        // For testing, using a sample date
        Date birthDate = java.sql.Date.valueOf("2020-05-15");
        Integer age = animalProceduresRepository.getAnimalAge(birthDate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("animalId", animalId);
        response.put("birthDate", birthDate);
        response.put("ageInYears", age);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Test HasRequiredVaccinations function
     * GET /api/test-procedures/has-vaccinations?animalId=1
     */
    @GetMapping("/has-vaccinations")
    public ResponseEntity<Map<String, Object>> testHasVaccinations(@RequestParam Long animalId) {
        Boolean hasVaccinations = animalProceduresRepository.hasRequiredVaccinations(animalId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("animalId", animalId);
        response.put("hasRequiredVaccinations", hasVaccinations);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Test GetTotalAdoptionCost function
     * GET /api/test-procedures/adoption-cost?animalId=1
     */
    @GetMapping("/adoption-cost")
    public ResponseEntity<Map<String, Object>> testAdoptionCost(@RequestParam Long animalId) {
        Double totalCost = animalProceduresRepository.getTotalAdoptionCost(animalId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("animalId", animalId);
        response.put("totalAdoptionCost", totalCost);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Test VaccinateAnimalForAdoption procedure
     * POST /api/test-procedures/vaccinate
     */
    @PostMapping("/vaccinate")
    @Transactional
    public ResponseEntity<Map<String, String>> testVaccinate(
            @RequestParam Integer animalId,
            @RequestParam Integer vetId) {
        
        try {
            animalProceduresRepository.vaccinateAnimalForAdoption(animalId, vetId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Animal vaccinated successfully");
            response.put("animalId", animalId.toString());
            response.put("vetId", vetId.toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test CompleteAdoption procedure
     * POST /api/test-procedures/complete-adoption
     */
    @PostMapping("/complete-adoption")
    @Transactional
    public ResponseEntity<Map<String, String>> testCompleteAdoption(
            @RequestParam Integer applicationId,
            @RequestParam String adoptionDate) {
        
        try {
            // Parse date (format: yyyy-MM-dd)
            Date date = java.sql.Date.valueOf(adoptionDate);
            
            animalProceduresRepository.completeAdoption(applicationId, date);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Adoption completed successfully");
            response.put("applicationId", applicationId.toString());
            response.put("adoptionDate", adoptionDate);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
