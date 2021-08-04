package com.safetynet.alert.service;

import com.safetynet.alert.model.Medication;
import com.safetynet.alert.repository.MedicationRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for Entity {@link Medication}.
 *
 * @author delaval
 *
 */
@Service
public class MedicationService {

  @Autowired
  private MedicationRepository medicationRepository;

  /**
   * Retrieve a Medication with its given Id.
   *
   * @param id
   *        the identification of Medication to retrieve.
   *
   * @return    the Medication with given id. Optional.empty() if it doesn't exist.
   */
  public Optional<Medication> getMedicationById(Long id) {

    return medicationRepository.findById(id);

  }

  /**
   * Retrieve all existed Medications.
   *
   * @return    the collection of all Medications.Iterable.empty() if there is no one.
   */
  public List<Medication> getMedications() {

    return medicationRepository.findAll();

  }

  /**
   * Retrieve a Medication with its given designation and posology.
   *
   * @param designation
   *          the designation of Medication.
   *
   * @param posology
   *          the posology of Medication.
   *
   * @return  the existed Medication with its given designation and posology.
   *            Optional.empty() if it doesn't exist.
   */
  public Optional<Medication> getMedicationByDesignationAndPosology(String designation,
      String posology) {

    return medicationRepository.getOneByDesignationAndPosology(designation, posology);

  }

  /**
   * Retrieve a Medication with its given designation and posology Fetching Set MedicalRecords.
   *
   * @param designation
   *          the designation of Medication.
   *
   * @param posology
   *          the posology of Medication.
   *
   * @return  the existed Medication with its given designation and posology.
   *            Optional.empty() if it doesn't exist.
   */
  public Optional<Medication> getMedicationFetchMedicalRecordsByDesignationAndPosology(
      String designation,
      String posology) {

    return medicationRepository.getOneFetchMedicalRecordsByDesignationAndPosology(designation,
        posology);

  }


  /**
   * retrieve all Medications not mapped with a MedicalRecord.
   *
   * @return a collection of existed Medications not mapped with any MedicalRecord.
   *            Iterable.empty() if there is no one.
   */
  public Iterable<Medication> getMedicationNotMappedByMedicalRecord() {

    return medicationRepository.getOneNotMappedByMedicalRecord();

  }

  /**
   * retrieve all Medications of a given MedicalRecord by its Id.
   *
   *@param  idMedicalRecord
   *            the id of MedicalRecord
   *
   * @return a collection of existed Medications of this MedicalRecord with its Id.
   */
  public Iterable<Medication> getMedicationsByIdMedicalRecord(Long idMedicalRecord) {

    return medicationRepository.getMedicationsByIdMedicalRecord(idMedicalRecord);

  }

  /**
   * Save the given Medication.
   *
   * @param medication
   *          the medication to save in database.
   *
   * @return  the saved Medication.
   */
  public Medication saveMedication(Medication medication) {

    return medicationRepository.save(medication);

  }

  /**
   * Save all Medications.
   *
   * @param missingMedications
   *            a Set of all Medications to save.
   *
   * @return    a List of saved Medications.
   */
  public List<Medication> saveAll(List<Medication> missingMedications) {

    return medicationRepository.saveAll(missingMedications);

  }



}
