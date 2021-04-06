package com.safetynet.alert.repository;

import com.safetynet.alert.model.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllergyRepository extends JpaRepository<Allergy, Long> {

}
