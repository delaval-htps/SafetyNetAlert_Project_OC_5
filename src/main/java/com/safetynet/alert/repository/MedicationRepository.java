package com.safetynet.alert.repository;

import com.safetynet.alert.model.Medication;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository for Entity Medication.
 *
 * @author delaval
 *
 */
@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {

  @Query("select m from Medication as m where m.designation=?1 and posology=?2")
  Optional<Medication> getOneByDesignationAndPosology(String designation, String posology);

  @Query("select m from Medication as m Left Join m.medicalRecords as mr where m.idMedication=null")
  Iterable<Medication> getOneNotMappedByMedicalRecord();

  @Query("select m from Medication as m  Left Join m.medicalRecords as mrs"
      + " where mrs.idMedicalRecord=?1")
  List<Medication> getMedicationsByIdMedicalRecord(Long idMedicalRecord);

}
