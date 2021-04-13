package com.safetynet.alert.database;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alert.service.AllergyService;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.MedicalRecordService;
import com.safetynet.alert.service.MedicationService;
import com.safetynet.alert.service.PersonService;
import java.io.File;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class LoadDatabaseFromJsonImpl implements LoadDatabaseService {

  @Autowired
  private PersonService personService;
  @Autowired
  private FireStationService fireStationService;
  @Autowired
  private MedicalRecordService medicalRecordService;
  @Autowired
  private MedicationService medicationService;
  @Autowired
  private AllergyService allergyService;

  private File fileJson;
  private ObjectMapper objectMapper;

  public LoadDatabaseFromJsonImpl(File file) {
    this.fileJson = file;
    this.objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public LoadDatabaseFromJsonImpl() {}

  @Override
  public boolean loadDatabaseFromSource() {
    return false;

  }
}
