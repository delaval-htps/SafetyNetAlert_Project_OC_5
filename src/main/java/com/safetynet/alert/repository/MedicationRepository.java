package com.safetynet.alert.repository;

import com.safetynet.alert.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MedicationRepository extends JpaRepository<Medication, Long> {

  @Query("select m from Medication as m where m.designation=?1 and posology=?2")
  Medication getOneByDesignationAndPosology(String designation, String posology);

}
