-- ============================================================================
-- STORED PROCEDURES
-- ============================================================================

-- Procedure 1: Complete an adoption
DROP PROCEDURE IF EXISTS CompleteAdoption;
DELIMITER //
CREATE PROCEDURE CompleteAdoption(
    IN p_application_id INT,
    IN p_adoption_date DATE
)
BEGIN
    DECLARE v_animal_id INT;
    DECLARE v_adopter_id INT;
    DECLARE v_animal_status VARCHAR(20);
    DECLARE v_has_required_vaccines BOOLEAN;

    -- Get application details and validate
    SELECT aa.animal_id, aa.user_id, a.status 
    INTO v_animal_id, v_adopter_id, v_animal_status
    FROM adoption_application aa
    JOIN animal a ON aa.animal_id = a.animal_id
    WHERE aa.adoption_application_id = p_application_id 
    AND aa.status = 'pending' 
    AND aa.is_active = TRUE;
    
    -- Validate data exists and animal is available
    IF v_animal_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Application already approved or does not exist';
    END IF;
    
    IF v_animal_status != 'available' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'animal is not available for adoption';
    END IF;

    SELECT HasRequiredVaccinations(v_animal_id) INTO v_has_required_vaccines;
    
    IF v_has_required_vaccines = FALSE THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot complete adoption: animal does not have all required vaccinations';
    END IF;
    
    -- Create adoption (triggers handle status updates automatically)
    INSERT INTO adoption (application_id, animal_id, adoption_user_id, adoption_date)
    VALUES (p_application_id, v_animal_id, v_adopter_id, p_adoption_date);
END //
DELIMITER ;

-- Procedure 2: Add a new animal with basic info
DROP PROCEDURE IF EXISTS AddNewAnimal;
DELIMITER //
CREATE PROCEDURE AddNewAnimal(
    IN p_name VARCHAR(80),
    IN p_species_name VARCHAR(50),
    IN p_breed_name VARCHAR(100),
    IN p_birth_date DATE,
    IN p_sex VARCHAR(255),
    IN p_price INT,
    OUT p_animal_id INT
)
BEGIN
    DECLARE v_species_id INT;
    DECLARE v_breed_id INT;
    
    -- Get or create species
    SELECT species_id INTO v_species_id FROM species WHERE name = p_species_name;
    IF v_species_id IS NULL THEN
        INSERT INTO species (name) VALUES (p_species_name);
        SET v_species_id = LAST_INSERT_ID();
    END IF;
    
    -- Get or create breed
    SELECT breed_id INTO v_breed_id FROM breed WHERE name = p_breed_name AND species_id = v_species_id;
    IF v_breed_id IS NULL THEN
        INSERT INTO breed (species_id, name) VALUES (v_species_id, p_breed_name);
        SET v_breed_id = LAST_INSERT_ID();
    END IF;
    
    -- Insert animal
    INSERT INTO animal (name, species_id, breed_id, birth_date, sex, intake_date, price)
    VALUES (p_name, v_species_id, v_breed_id, p_birth_date, p_sex, CURDATE(), p_price);
    
    SET p_animal_id = LAST_INSERT_ID();
END //
DELIMITER ;

-- Procedure 3: Get animal medical history
DROP PROCEDURE IF EXISTS GetAnimalMedicalHistory;
DELIMITER //
CREATE PROCEDURE GetAnimalMedicalHistory(
    IN p_animal_id INT
)
BEGIN
    SELECT 
        mr.record_date,
        mr.diagnosis,
        mr.treatment,
        mr.cost,
        CONCAT(u.first_name, ' ', u.last_name) AS veterinarian
    FROM medical_record mr
    JOIN veterinarian v ON mr.vet_id = v.vet_id
    LEFT JOIN users u ON v.user_id = u.user_id
    WHERE mr.animal_id = p_animal_id
    ORDER BY mr.record_date DESC;
END //
DELIMITER ;

-- Procedure 4: Vaccinate animal with all required vaccines
DROP PROCEDURE IF EXISTS VaccinateAnimalForAdoption;
DELIMITER //
CREATE PROCEDURE VaccinateAnimalForAdoption(
    IN p_animal_id INT,
    IN p_vet_id INT
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_vaccine_type_id INT;
    DECLARE v_duration_months INT;
    DECLARE v_next_due_date DATE;
    DECLARE v_animal_species_id INT;
    DECLARE v_vaccination_count INT DEFAULT 0;
    
    -- Cursor to get all required vaccines for this animal's species
    DECLARE vaccine_cursor CURSOR FOR
        SELECT vt.vaccination_type_id, vt.duration_months
        FROM vaccination_type vt
        JOIN vaccination_type_species vts ON vt.vaccination_type_id = vts.vaccination_type_id
        WHERE vts.species_id = v_animal_species_id
        AND vt.required_for_adoption = TRUE;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- Get animal's species
    SELECT species_id INTO v_animal_species_id 
    FROM animal 
    WHERE animal_id = p_animal_id;
    
    -- Check if animal exists
    IF v_animal_species_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'animal not found';
    END IF;
    
    OPEN vaccine_cursor;
    
    vaccination_loop: LOOP
        FETCH vaccine_cursor INTO v_vaccine_type_id, v_duration_months;
        IF done THEN
            LEAVE vaccination_loop;
        END IF;
        
        -- Calculate next due date
        SET v_next_due_date = DATE_ADD(CURDATE(), INTERVAL v_duration_months MONTH);
        
        -- Insert vaccination record
        INSERT INTO vaccination (animal_id, vet_id, vaccination_type_id, date_administered, next_due_date)
        VALUES (p_animal_id, p_vet_id, v_vaccine_type_id, CURDATE(), v_next_due_date);
        
        SET v_vaccination_count = v_vaccination_count + 1;
    END LOOP;
    
    CLOSE vaccine_cursor;
    
    SELECT CONCAT('Successfully administered ', v_vaccination_count, ' required vaccinations for animal ID: ', p_animal_id) AS result;
END //
DELIMITER ;

-- ============================================================================
-- FUNCTIONS
-- ============================================================================

-- Function 1: Calculate animal age in years
DROP FUNCTION IF EXISTS GetAnimalAge;
DELIMITER //
CREATE FUNCTION GetAnimalAge(p_birth_date DATE)
RETURNS INT
DETERMINISTIC
BEGIN
    RETURN TIMESTAMPDIFF(YEAR, p_birth_date, CURDATE());
END //
DELIMITER ;

-- Function 2: Check if animal has all required vaccinations
DROP FUNCTION IF EXISTS HasRequiredVaccinations;
DELIMITER //
CREATE FUNCTION HasRequiredVaccinations(p_animal_id INT)
RETURNS BOOLEAN
READS SQL DATA
BEGIN
    DECLARE v_required_count INT;
    DECLARE v_actual_count INT;
    DECLARE v_animal_species_id INT;

    -- GET THE ANIMAL'S SPECIES (this line was missing!)
    SELECT species_id INTO v_animal_species_id 
    FROM animal 
    WHERE animal_id = p_animal_id;
    
    -- Count required vaccines for this animal's species
    SELECT COUNT(*) INTO v_required_count
    FROM vaccination_type vt
    JOIN vaccination_type_species vts ON vt.vaccination_type_id = vts.vaccination_type_id
    WHERE vts.species_id = v_animal_species_id 
    AND vt.required_for_adoption = TRUE;
    
    -- Count actual vaccines this animal has received for required vaccines (that are still valid)
    SELECT COUNT(DISTINCT v.vaccination_type_id) INTO v_actual_count
    FROM vaccination v
    JOIN vaccination_type vt ON v.vaccination_type_id = vt.vaccination_type_id
    JOIN vaccination_type_species vts ON v.vaccination_type_id = vts.vaccination_type_id
    WHERE v.animal_id = p_animal_id 
    AND vts.species_id = v_animal_species_id
    AND vt.required_for_adoption = TRUE
    AND v.next_due_date >= CURDATE();
    
    RETURN v_actual_count >= v_required_count;
END //
DELIMITER ;

-- Function 3: Get total adoption fee (animal price + medical costs)
DROP FUNCTION IF EXISTS GetTotalAdoptionCost;
DELIMITER //
CREATE FUNCTION GetTotalAdoptionCost(p_animal_id INT)
RETURNS DECIMAL(10,2)
READS SQL DATA
BEGIN
    DECLARE v_animal_price DECIMAL(10,2);
    DECLARE v_medical_costs DECIMAL(10,2);
    
    SELECT price INTO v_animal_price FROM animal WHERE animal_id = p_animal_id;
    
    SELECT IFNULL(SUM(cost), 0) INTO v_medical_costs
    FROM medical_record
    WHERE animal_id = p_animal_id;
    
    RETURN v_animal_price + v_medical_costs;
END //
DELIMITER ;

-- ============================================================================
-- TRIGGERS
-- ============================================================================

-- Trigger 1: Automatically update animal status when adopted
DROP TRIGGER IF EXISTS after_adoption_insert;
DELIMITER //
CREATE TRIGGER after_adoption_insert
AFTER INSERT ON adoption
FOR EACH ROW
BEGIN
    UPDATE animal 
    SET status = 'adopted' 
    WHERE animal_id = NEW.animal_id;
END //
DELIMITER ;

-- Trigger 2: Automatically set application status to 'approved' when adoption is completed
DROP TRIGGER IF EXISTS after_adoption_application_update;
DELIMITER //
CREATE TRIGGER after_adoption_application_update
AFTER INSERT ON adoption
FOR EACH ROW
BEGIN
    UPDATE adoption_application 
    SET status = 'approved' 
    WHERE adoption_application_id = NEW.application_id;
END //
DELIMITER ;

-- Trigger 3: Update animal status when foster care starts
DROP TRIGGER IF EXISTS after_foster_insert;
DELIMITER //
CREATE TRIGGER after_foster_insert
AFTER INSERT ON foster_care
FOR EACH ROW
BEGIN
    IF NEW.is_active = TRUE THEN
        UPDATE animal 
        SET status = 'fostered' 
        WHERE animal_id = NEW.animal_animal_id;
    END IF;
END //
DELIMITER ;

-- Trigger 4: Update animal status when foster care ends
DROP TRIGGER IF EXISTS after_foster_update;
DELIMITER //
CREATE TRIGGER after_foster_update
AFTER UPDATE ON foster_care
FOR EACH ROW
BEGIN
    IF OLD.is_active = TRUE AND NEW.is_active = FALSE THEN
        UPDATE animal 
        SET status = 'available' 
        WHERE animal_id = NEW.animal_animal_id;
    END IF;
END //
DELIMITER ;

-- ============================================================================
-- VIEWS
-- ============================================================================

-- View 1: Available animals with full details
CREATE OR REPLACE VIEW AvailableAnimalsView AS
SELECT 
    a.animal_id,
    a.name,
    s.name AS species,
    b.name AS breed,
    a.birth_date,
    GetAnimalAge(a.birth_date) AS age_years,
    a.sex,
    a.intake_date,
    a.price,
    HasRequiredVaccinations(a.animal_id) AS has_required_vaccines,
    CASE 
        WHEN HasRequiredVaccinations(a.animal_id) THEN 'Ready for adoption'
        ELSE 'Needs vaccinations'
    END AS adoption_status
FROM animal a
JOIN species s ON a.species_id = s.species_id
LEFT JOIN breed b ON a.breed_id = b.breed_id
WHERE a.status = 'available' AND a.is_active = TRUE;

-- View 2: adoption history with adopter details
CREATE OR REPLACE VIEW AdoptionHistoryView AS
SELECT 
    a.adoption_id,
    an.name AS animal_name,
    s.name AS species,
    b.name AS breed,
    CONCAT(u.first_name, ' ', u.last_name) AS adopter_name,
    u.email AS adopter_email,
    u.phone AS adopter_phone,
    a.adoption_date,
    DATEDIFF(CURDATE(), a.adoption_date) AS days_since_adoption
FROM adoption a
JOIN animal an ON a.animal_id = an.animal_id
JOIN species s ON an.species_id = s.species_id
LEFT JOIN breed b ON an.breed_id = b.breed_id
JOIN users u ON a.adoption_user_id = u.user_id
WHERE a.is_active = TRUE;

-- View 3: vaccination status for all animals
CREATE OR REPLACE VIEW VaccinationStatusView AS
SELECT 
    a.animal_id,
    a.name AS animal_name,
    vt.vaccine_name,
    v.date_administered,
    v.next_due_date,
    CASE 
        WHEN v.next_due_date < CURDATE() THEN 'Overdue'
        WHEN v.next_due_date < DATE_ADD(CURDATE(), INTERVAL 30 DAY) THEN 'Due Soon'
        ELSE 'Up to Date'
    END AS status,
    DATEDIFF(v.next_due_date, CURDATE()) AS days_until_due
FROM animal a
LEFT JOIN vaccination v ON a.animal_id = v.animal_id
LEFT JOIN vaccination_type vt ON v.vaccination_type_id = vt.vaccination_type_id
WHERE a.is_active = TRUE
ORDER BY a.animal_id, v.next_due_date;

-- View 4: Pending adoption applications
CREATE OR REPLACE VIEW PendingApplicationsView AS
SELECT 
    aa.adoption_application_id,
    an.name AS animal_name,
    s.name AS species,
    CONCAT(u.first_name, ' ', u.last_name) AS applicant_name,
    u.email AS applicant_email,
    u.phone AS applicant_phone,
    aa.application_date,
    DATEDIFF(CURDATE(), aa.application_date) AS days_pending
FROM adoption_application aa
JOIN animal an ON aa.animal_id = an.animal_id
JOIN species s ON an.species_id = s.species_id
JOIN users u ON aa.user_id = u.user_id
WHERE aa.status = 'pending' AND aa.is_active = TRUE
ORDER BY aa.application_date;

-- ============================================================================
-- INDEXES
-- ============================================================================

-- Index 1: Speed up user login queries
CREATE INDEX idx_user_email ON users(email);

-- Index 2: Speed up animal searches by name
CREATE INDEX idx_animal_name ON animal(name);

-- Index 3: Speed up animal filtering by status
CREATE INDEX idx_animal_status ON animal(status);

-- Index 4: Speed up queries for animals by species
CREATE INDEX idx_animal_species ON animal(species_id);

-- Index 5: Composite index for finding available animals of a specific species
CREATE INDEX idx_animal_species_status ON animal(species_id, status);

-- Index 6: Speed up vaccination lookups by animal
CREATE INDEX idx_vaccination_animal ON vaccination(animal_id);

-- Index 7: Speed up vaccination due date checks
CREATE INDEX idx_vaccination_due_date ON vaccination(next_due_date);

-- Index 8: Speed up medical record lookups
CREATE INDEX idx_medical_animal ON medical_record(animal_id);

-- Index 9: Speed up application queries by user
CREATE INDEX idx_application_user ON adoption_application(user_id);

-- Index 10: Speed up application queries by status
CREATE INDEX idx_application_status ON adoption_application(status);

-- ============================================================================
-- EVENTS
-- ============================================================================

-- Enable the event scheduler (you may need to run this separately with admin privileges)
-- SET GLOBAL event_scheduler = ON;

-- Event 1: Daily check for overdue vaccinations and log them
DROP EVENT IF EXISTS daily_vaccination_check;
DELIMITER //
CREATE EVENT daily_vaccination_check
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_DATE + INTERVAL 1 DAY
DO
BEGIN
    CREATE TEMPORARY TABLE IF NOT EXISTS temp_overdue_vaccines AS
    SELECT 
        a.animal_id,
        a.name,
        vt.vaccine_name,
        v.next_due_date
    FROM animal a
    JOIN vaccination v ON a.animal_id = v.animal_id
    JOIN vaccination_type vt ON v.vaccination_type_id = vt.vaccination_type_id
    WHERE v.next_due_date < CURDATE()
    AND a.is_active = TRUE;
END //
DELIMITER ;

-- Event 2: Monthly cleanup of old rejected applications (older than 6 months)
DROP EVENT IF EXISTS monthly_cleanup_old_applications;
DELIMITER //
CREATE EVENT monthly_cleanup_old_applications
ON SCHEDULE EVERY 1 MONTH
STARTS CURRENT_DATE + INTERVAL 1 MONTH
DO
BEGIN
    UPDATE adoption_application
    SET is_active = FALSE
    WHERE status = 'rejected'
    AND application_date < DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
    AND is_active = TRUE;
END //
DELIMITER ;

-- Event 3: Weekly reminder for animals in foster care over 90 days
DROP EVENT IF EXISTS weekly_long_term_foster_check;
DELIMITER //
CREATE EVENT weekly_long_term_foster_check
ON SCHEDULE EVERY 1 WEEK
STARTS CURRENT_DATE + INTERVAL 1 WEEK
DO
BEGIN
    CREATE TEMPORARY TABLE IF NOT EXISTS temp_long_term_fosters AS
    SELECT 
        fc.fostercare_id,
        a.name AS animal_name,
        CONCAT(u.first_name, ' ', u.last_name) AS foster_parent,
        fc.start_date,
        DATEDIFF(CURDATE(), fc.start_date) AS days_in_foster
    FROM foster_care fc
    JOIN animal a ON fc.animal_animal_id = a.animal_id
    JOIN users u ON fc.foster_parent_user_id = u.user_id
    WHERE fc.is_active = TRUE
    AND fc.end_date IS NULL
    AND DATEDIFF(CURDATE(), fc.start_date) > 90;
END //
DELIMITER ;