package com.safetynet.alert.repository;

import com.safetynet.alert.model.MedicalRecord;
import java.util.List;
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
      + " left join fetch mr.person as p "
      + " left join fetch p.fireStations"
      + " left join fetch mr.medications m "
      + " left join fetch mr.allergies a "
      + " where mr.idMedicalRecord=?1"
      + " order by m.idMedication,a.idAllergy asc")
  Optional<MedicalRecord> getOneJoinAllOtherById(long l);

  @Query("select mr from MedicalRecord as mr "
      + " join fetch mr.person as p "
      + " left join fetch mr.medications m"
      + " left join fetch mr.allergies a"
      + " where p.lastName=?1 and p.firstName=?2")
  Optional<MedicalRecord> getOneByNames(@Valid String lastName, @Valid String firstName);

  @Query("select distinct mr"
      + " from MedicalRecord as mr "
      + " left join mr.person as p "
      + " left join fetch mr.medications m"
      + " left join fetch mr.allergies a"
      + " order by mr.idMedicalRecord,m.idMedication, a.idAllergy asc")
  List<MedicalRecord> findAllFetchAll();


}
