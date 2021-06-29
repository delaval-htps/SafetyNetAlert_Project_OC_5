package com.safetynet.alert.model;

import com.safetynet.alert.controller.emergency.EmergencyRestController;

/**
 * Specific Model Response extended {@link ModelResponse} to give custom informations
 * for a person in the ResponseEntity of {@link EmergencyRestController}
 * using a Map String,Object to specify specially MedicalRecord when need it.
 *
 * @author delaval
 *
 */
public class ModelResponseWithMedicalRecord extends ModelResponse {

  /**
   * Constructor for adding informations of medicalRecord's person
   * using constructor of {@link ModelResponse}.
   *
   * @param person    given person to produce the Map
   *
   */
  public ModelResponseWithMedicalRecord(Person person) {

    super(person);

    if (person.getMedicalRecord() != null) {

      responseMap.put("Medications", person.getMedicalRecord().getMedications());
      responseMap.put("Allergies", person.getMedicalRecord().getAllergies());
    } else {

      responseMap.put("MedicalRecord", "not yet created");
    }

  }

}
