package com.safetynet.alert.service;

import com.safetynet.alert.model.Allergy;
import com.safetynet.alert.repository.AllergyRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for entity {@link Allergy}.
 *
 * @author delaval
 *
 */
@Service
public class AllergyService {

  @Autowired
  private AllergyRepository allergyRepository;

  /**
   * Retrieve a Allergy by its given Id.
   *
   * @param id
   *          identification of Allergy.
   *
   * @return   a allergy with the given id.
   */
  public Allergy getAllergyById(Long id) {

    return allergyRepository.getOne(id);

  }

  /**
   * Retrieve all existed allergies.
   *
   * @return  a collection of all existed allergies.
   *            Iterable.empty() if there isn't existed one.
   */
  public List<Allergy> getAllergies() {

    return allergyRepository.findAll();

  }

  /**
   * Retrieve a collection of all allergies not Mapped with a MedicalRecord.
   *
   * @return  a collection of all allergies not mapped
   *            Optional.empty() if there isn't one.
   */
  public Iterable<Allergy> getAllergiesNotMappedByMedicalRecord() {

    return allergyRepository.findAllNotMappedByMedicalRecord();

  }

  /**
   * Retrieve the Allergy with its given designation.
   *
   * @param designation
   *          the designation of allergy to research.
   *
   * @return  the allergy with this designation. Optional.empty() if doesn't exist.
   */
  public Optional<Allergy> getAllergyByDesignation(String designation) {

    return allergyRepository.getOneByDesignation(designation);

  }

  /**
   * Retrieve the Allergy with its given designation with Set MedicalRecords.
   *
   * @param designation
   *          the designation of allergy to research.
   *
   * @return  the allergy with this designation. Optional.empty() if doesn't exist.
   */
  public Optional<Allergy> getAllergyFetchMedicalRecordsByDesignation(String designation) {

    return allergyRepository.getOneFetchMedicalRecordsByDesignation(designation);

  }

  /**
   * Retrieve the Allergies of given MedicalRecord with its ID.
   *
   * @param idMedicalRecord
   *          the id of MedicalRecord .
   *
   * @return  List of allergies for the givenMedicalRecord.
   */
  public Iterable<Allergy> getAllergiesByIdMedicalRecord(Long idMedicalRecord) {

    return allergyRepository.getAllergiesByIdMedicalRecord(idMedicalRecord);

  }

  /**
   * Save a Allergy.
   *
   * @param allergy
   *          the Allergy to save.
   *
   * @return the saved allergy.
   */
  public Allergy saveAllergy(Allergy allergy) {

    return allergyRepository.save(allergy);

  }

  /**
   * Save all allergies given in parameter.
   *
   * @param missingAllergies
   *            Set of allergies to save.
   *
   * @return    a List of saved allergies.
   */
  public List<Allergy> saveAll(List<Allergy> missingAllergies) {

    return allergyRepository.saveAll(missingAllergies);

  }



}
