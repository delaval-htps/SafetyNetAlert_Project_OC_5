package com.safetynet.alert.service;

import com.safetynet.alert.model.Medication;
import com.safetynet.alert.repository.MedicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicationService {

  @Autowired
  private MedicationRepository medicationRepository;

  public Medication getMedicationById(Long id) {
    return medicationRepository.getOne(id);
  }

  public Iterable<Medication> getMedications() {
    return medicationRepository.findAll();
  }

  public Medication saveMedication(Medication medication) {
    return medicationRepository.save(medication);
  }

  public Medication getMedicationByDesignationAndPosology(String designation, String posology) {
    return medicationRepository.getOneByDesignationAndPosology(designation, posology);
  }
}
