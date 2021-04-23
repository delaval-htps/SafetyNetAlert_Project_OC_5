package com.safetynet.alert.controller;

import com.safetynet.alert.model.Allergy;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.model.MedicalRecord;
import com.safetynet.alert.model.Medication;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.AllergyService;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.MedicalRecordService;
import com.safetynet.alert.service.MedicationService;
import com.safetynet.alert.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminRestController {

  private PersonService personService;
  private FireStationService fireStationService;
  private MedicalRecordService medicalRecordService;
  private MedicationService medicationService;
  private AllergyService allergyService;

  @Autowired
  public AdminRestController(PersonService ps,
                             FireStationService fs,
                             MedicalRecordService mrs,
                             MedicationService ms,
                             AllergyService as) {
    this.personService = ps;
    this.fireStationService = fs;
    this.medicalRecordService = mrs;
    this.medicationService = ms;
    this.allergyService = as;
  }

  @GetMapping("/persons")
  public Iterable<Person> getPersons() {
    return personService.getPersons();
  }

  @GetMapping("/firestations")
  public Iterable<FireStation> getFireStations() {
    return fireStationService.getFireStations();
  }

  @GetMapping("/medicalRecords")
  public Iterable<MedicalRecord> getMedicalRecords() {
    return medicalRecordService.getMedicalRecords();
  }

  @GetMapping("/medications")
  public Iterable<Medication> getMedications() {
    return medicationService.getMedications();
  }

  @GetMapping("/allergies")
  public Iterable<Allergy> getAlllergies() {
    return allergyService.getAllergies();
  }

}
