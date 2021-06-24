package com.safetynet.alert.service;

import com.safetynet.alert.model.MedicalRecord;
import com.safetynet.alert.repository.MedicalRecordRepository;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * service for Entity MedicalRecord.
 *
 * @author delaval
 *
 */
@Service
public class MedicalRecordService {

  @Autowired
  private MedicalRecordRepository medicalRecordRepository;

  /**
   * Retrieve a medicalRecord with its given Id.
   *
   * @param id
   *            the given identification for MedicalRecord.
   *
   * @return    the existed medicalRecord with given id.OIptional.empty() if it doesn't exist.
   */
  public Optional<MedicalRecord> getMedicalRecordById(Long id) {

    return medicalRecordRepository.findById(id);

  }

  /**
   * Retrieve all existed medicalRecords.
   *
   * @return    collection of all existed medicalRecords.Iterable.empty() if there is no one.
   */
  public Iterable<MedicalRecord> getMedicalRecords() {

    return medicalRecordRepository.findAll();

  }

  /**
   * retrieve a MedicalRecord with all its fields ( Person,Medications,Allergies).
   * Use to avoid the lazy Fetch of getMedicalRecordById().
   *
   * @param l
   *           the identification of MedicalRecord.
   *
   * @return the MedicalRecord with its all fields.Optioonal.empty() if it doesn't exist.
   */
  public Optional<MedicalRecord> getMedicalRecordJoinAllById(long l) {

    return medicalRecordRepository.getOneJoinAllOtherById(l);

  }

  /**
   * Retrieve a MedicalRecord with its given LAstName and FirstName 's mapped Person.
   *    using a inner join to avoid LazyInitialization.
   *
   * @param lastName
   *            lastname of the Person mapped with MedicalRecord.
   *
   * @param firstName
   *            firstName of the Person mapped with MedicalRecord.
   *
   * @return the medicalRecord mapped with Person with the given lastname and firstname.
   */
  public Optional<MedicalRecord> getMedicalRecordByNames(@Valid String lastName,
      @Valid String firstName) {

    return medicalRecordRepository.getOneByNames(lastName, firstName);

  }

  /**
   * Save a given MedicalRecord.
   *
   * @param medicalRecord
   *
   * @return    the saved medicalRecord.
   */
  public MedicalRecord saveMedicalRecord(MedicalRecord medicalRecord) {

    return medicalRecordRepository.save(medicalRecord);

  }

  /**
   * Delete a MedicalRecord.
   *
   * @param medicalRecord
   *
   */
  public void deleteMedicalRecord(MedicalRecord medicalRecord) {

    medicalRecordRepository.delete(medicalRecord);

  }

}
