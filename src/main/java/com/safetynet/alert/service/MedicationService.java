package com.safetynet.alert.service;

import com.safetynet.alert.model.Medication;
import com.safetynet.alert.repository.MedicationRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicationService {

  @Autowired
  private MedicationRepository medicationRepository;

  public Optional<Medication> getMedicationById(Long id) {

    return medicationRepository.findById(id);

  }

  public Iterable<Medication> getMedications() {

    return medicationRepository.findAll();

  }

  public Medication saveMedication(Medication medication) {

    return medicationRepository.save(medication);

  }

  public Optional<Medication> getMedicationByDesignationAndPosology(String designation,
      String posology) {

    return medicationRepository.getOneByDesignationAndPosology(designation, posology);

  }

  public List<Medication> saveAll(Set<Medication> missingMedications) {

    return medicationRepository.saveAll(missingMedications);

  }

  public Iterable<Medication> getMedicationNotMappedByMedicalRecord() {

    return medicationRepository.getOneNotMappedByMedicalRecord();

  }
}
