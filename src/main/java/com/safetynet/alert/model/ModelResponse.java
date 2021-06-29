package com.safetynet.alert.model;

import com.safetynet.alert.controller.emergency.EmergencyRestController;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
/**
 * Custom Global Model response to give custom informations for a person
 * in the ResponseEntity of {@link EmergencyRestController}
 * using a Map String,Object to specify specially age.
 *
 * @author delaval
 */
@Getter
@Setter
public class ModelResponse {

  protected Map<String, Object> responseMap = new LinkedHashMap<>();

  /**
   * constructor with a parameter of type Person.
   *
   * @param person    the given Person to produce a Map
   *
   */
  public ModelResponse(Person person) {

    responseMap.put("Name", person.getLastName() + " " + person.getFirstName());

    if (person.getBirthDate() != null) {

      int agePerson = calculateAge(person.getBirthDate());
      responseMap.put("Age", agePerson);

    } else {

      responseMap.put("Age", "not specified");
    }

    if (person.getAddress() != null) {

      responseMap.put("Address", person.getAddress());
    }

    if (person.getCity() != null) {

      responseMap.put("City", person.getCity());
    }

    if (person.getZip() != null) {

      responseMap.put("Zip", person.getZip());
    }

    if (person.getPhone() != null) {

      responseMap.put("Phone", person.getPhone());
    }

    if (person.getEmail() != null) {

      responseMap.put("Email", person.getEmail());
    }

  }

  /**
   * Method to calculate age of Person with its given Date birhtDate.
   *
   * @param birthDate
   *              the birthDate of a Person
   *
   * @return  age of person in years
   */
  public int calculateAge(Date birthDate) {

    if (birthDate != null) {

      LocalDate birthDateLocalDate = new java.sql.Date(birthDate.getTime()).toLocalDate();
      return Period.between(birthDateLocalDate, LocalDate.now()).getYears();

    } else {

      return -1;
    }

  }
}
