package com.safetynet.alert.service;

import com.safetynet.alert.model.Allergy;
import com.safetynet.alert.repository.AllergyRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AllergyService {

  @Autowired
  private AllergyRepository allergyRepository;

  public Allergy getAllergyById(Long id) {

    return allergyRepository.getOne(id);

  }

  public Iterable<Allergy> getAllergies() {

    return allergyRepository.findAll();

  }

  public Allergy saveAllergy(Allergy allergy) {

    return allergyRepository.save(allergy);

  }

  public Optional<Allergy> getAllergyByDesignation(String designation) {

    return allergyRepository.getOneByDesignation(designation);

  }

  public List<Allergy> saveAll(Set<Allergy> missingAllergies) {

    return allergyRepository.saveAll(missingAllergies);

  }

  public Iterable<Allergy> getAllergiesNotMappedByMedicalRecord() {

    return allergyRepository.findAllNotMappedByMedicalRecord();

  }
}
