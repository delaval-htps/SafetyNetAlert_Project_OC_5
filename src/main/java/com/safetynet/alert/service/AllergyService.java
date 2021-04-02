package com.safetynet.alert.service;

import com.safetynet.alert.model.Allergy;
import com.safetynet.alert.repository.AllergyRepository;
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
}
