package com.safetynet.alert.service;

import com.safetynet.alert.model.MedicalRecord;
import com.safetynet.alert.repository.MedicalRecordRepository;
import java.util.List;
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
  public List<MedicalRecord> getMedicalRecords() {

    return medicalRecordRepository.findAllFetchAll();

  }

  /**
   * retrieve a MedicalRecord with all its fields ( Person,Medications,Allergies).
   * Use to avoid the lazy Fetch of getMedicalRecordById().
   *
   * @param id
   *           the identification of MedicalRecord.
   *
   * @return the MedicalRecord with its all fields.Optioonal.empty() if it doesn't exist.
   */
  public Optional<MedicalRecord> getMedicalRecordJoinAllById(long id) {

    return medicalRecordRepository.getOneJoinAllOtherById(id);

  }

  /**
   * Retrieve a MedicalRecord with its given LAstName and FirstName 's mapped Person.
   * Fetching Medications and allergies
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
   * Retrieve a MedicalRecord with its given LAstName and FirstName 's mapped Person.
   *   fetching all collections
   *
   * @param lastName
   *            lastname of the Person mapped with MedicalRecord.
   *
   * @param firstName
   *            firstName of the Person mapped with MedicalRecord.
   *
   * @return the medicalRecord mapped with Person with the given lastname and firstname.
   */
  public MedicalRecord getMedicalRecordFetchAllByNames(String lastName, String firstName) {

    return medicalRecordRepository.getOneFetchAllByNames(lastName, firstName);

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
