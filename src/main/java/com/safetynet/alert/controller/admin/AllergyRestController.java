package com.safetynet.alert.controller.admin;

import com.safetynet.alert.model.Allergy;
import com.safetynet.alert.service.AllergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * Rest Controller dedicated to entity {@link Allergy}.
 *
 * @author delaval
 *
 */

@RestController
public class AllergyRestController {
  @Autowired
  AllergyService allergyService;

  /**
   * Return all existed allergies.
   *
   * @return Iterable  a collection of all existed allergies
   */
  @GetMapping("/allergies")
  public Iterable<Allergy> getAlllergies() {

    return allergyService.getAllergies();

  }
}
