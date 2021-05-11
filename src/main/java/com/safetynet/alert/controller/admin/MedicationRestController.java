package com.safetynet.alert.controller.admin;

import com.safetynet.alert.model.Medication;
import com.safetynet.alert.service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MedicationRestController {
  @Autowired
  MedicationService medicationService;

  @GetMapping("/medications")
  public Iterable<Medication> getMedications() {
    return medicationService.getMedications();
  }

}
