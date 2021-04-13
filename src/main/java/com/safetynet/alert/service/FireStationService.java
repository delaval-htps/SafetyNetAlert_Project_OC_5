package com.safetynet.alert.service;

import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.repository.FireStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FireStationService {

  @Autowired
  private FireStationRepository fireStationRepository;

  public FireStation getFireStationById(Long id) {
    return fireStationRepository.getOne(id);
  }

  public Iterable<FireStation> getFireStations() {
    return fireStationRepository.findAll();
  }

  public FireStation saveFireStation(FireStation fireStation) {
    return fireStationRepository.save(fireStation);
  }

  public FireStation getFireStationByNumberStation(int numberStation) {
    return fireStationRepository.getOneByNumberStation(numberStation);
  }
}
