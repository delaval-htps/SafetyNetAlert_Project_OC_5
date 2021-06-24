package com.safetynet.alert.repository;

import com.safetynet.alert.model.MedicalRecord;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *repository for Entity MedicalRecord.
 *
 * @author delaval
 *
 */
@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

  @Query("select mr from MedicalRecord as mr "
      + "join fetch mr.person as p "
      + "join fetch mr.medications m "
      + "join fetch mr.allergies a "
      + "where mr.idMedicalRecord=?1")
  Optional<MedicalRecord> getOneJoinAllOtherById(long l);

  @Query("select mr from MedicalRecord as mr "
      + "join fetch mr.person as p "
      + "where p.lastName=?1 and p.firstName=?2")
  Optional<MedicalRecord> getOneByNames(@Valid String lastName, @Valid String firstName);



}
