package com.safetynet.alert.controller.admin;

import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.service.FireStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FireStationRestController {

  @Autowired
  FireStationService fireStationService;

  @GetMapping("/firestations")
  public Iterable<FireStation> getFireStations() {
    return fireStationService.getFireStations();
  }
}
