package com.safetynet.alert.repository;

import com.safetynet.alert.model.Allergy;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, Long> {

  @Query("select a from Allergy as a where a.designation=?1")
  Optional<Allergy> getOneByDesignation(String designation);

  @Query("select a from Allergy as a Left Join a.medicalRecords where a.idAllergy=null")
  Iterable<Allergy> findAllNotMappedByMedicalRecord();

}
