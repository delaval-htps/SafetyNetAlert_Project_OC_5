package com.safetynet.alert.repository;

import com.safetynet.alert.model.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AllergyRepository extends JpaRepository<Allergy, Long> {

  @Query("select a from Allergy as a where a.designation=?1")
  Allergy getOneByDesignation(String designation);

}
