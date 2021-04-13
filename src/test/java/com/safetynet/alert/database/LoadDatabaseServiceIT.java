package com.safetynet.alert.database;

import static org.junit.Assert.assertTrue;
import com.safetynet.alert.service.AllergyService;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.MedicalRecordService;
import com.safetynet.alert.service.MedicationService;
import com.safetynet.alert.service.PersonService;
import java.io.File;
import org.junit.jupiter.api.Test;


class LoadDatabaseServiceIT {

  private static File fileJson = new File("classpath:/json/data.json");

  private static PersonService personService = new PersonService();
  private static FireStationService fireStationService = new FireStationService();
  private static MedicalRecordService medicalRecordService = new MedicalRecordService();
  private static MedicationService medicationService = new MedicationService();
  private static AllergyService allergyService = new AllergyService();

  private static LoadDatabaseService classUnderTest = new LoadDatabaseFromJsonImpl(fileJson);


  @Test
  void loadDatabaseService_shouldPersistDataJson_whenBootingApplication() {
    // ARRANGE... booting application

    // ACT
    boolean result = classUnderTest.loadDatabaseFromSource();

    // ASSERT
    assertTrue(result);
  }
}
