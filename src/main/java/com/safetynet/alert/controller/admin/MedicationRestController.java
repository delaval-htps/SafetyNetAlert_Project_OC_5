package com.safetynet.alert.controller.admin;

import com.safetynet.alert.model.Medication;
import com.safetynet.alert.service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Rest Controller for entity {@link Medication}.
 *
 * @author delaval
 *
 */
@RestController
@ApiIgnore
public class MedicationRestController {
  @Autowired
  MedicationService medicationService;

  /**
   * Return all existed Medications.
   *
   * @return    a collection (Iterable) of all existed Medications.
   */
  @GetMapping("/medications")
  public Iterable<Medication> getMedications() {

    return medicationService.getMedications();

  }

}
