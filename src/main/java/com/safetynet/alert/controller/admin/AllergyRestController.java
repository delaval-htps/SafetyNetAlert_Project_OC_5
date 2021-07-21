package com.safetynet.alert.controller.admin;

import com.safetynet.alert.model.Allergy;
import com.safetynet.alert.service.AllergyService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
/**
 * Rest Controller dedicated to entity {@link Allergy}.
 *
 * @author delaval
 *
 */

@RestController
@ApiIgnore
public class AllergyRestController {
  @Autowired
  AllergyService allergyService;

  /**
   * Return all existed allergies.
   *
   * @return Iterable  a collection of all existed allergies
   */
  @GetMapping("/allergies")
  public List<Allergy> getAlllergies() {

    return (List<Allergy>) allergyService.getAllergies();

  }
}
