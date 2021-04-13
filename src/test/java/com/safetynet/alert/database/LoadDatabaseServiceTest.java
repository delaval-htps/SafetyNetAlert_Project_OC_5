package com.safetynet.alert.database;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class LoadDatabaseServiceTest {

  @Mock
  private PersonService personService;
  @Mock
  private FireStationService fireStationService;
  @Mock
  private MedicalRecordService medicalRecordService;
  @Mock
  private MedicationService medicationService;
  @Mock
  private AllergyService allergyService;
  @Mock
  private static ObjectMapper objectMapper;

  private static String filePathJSon;
  private static File fileJson;
  private static LoadDatabaseService classUnderTest;

  @BeforeEach
  void setUpInit() {
    filePathJSon = "classpath:json/data.json";
    fileJson = new File(filePathJSon);
  }

  @Test
  void loadDatabaseService_shouldPersistData_whenDataJSonCorrect() {
    // Given
    classUnderTest = new LoadDatabaseFromJsonImpl(fileJson);
    doNothing().when(personService).savePerson(Mockito.any(Person.class));
    doNothing().when(fireStationService).saveFireStation(Mockito.any(FireStation.class));
    doNothing().when(medicalRecordService).saveMedicalRecord(Mockito.any(MedicalRecord.class));
    doNothing().when(medicationService).saveMedication(Mockito.any(Medication.class));
    doNothing().when(allergyService).saveAllergy(Mockito.any(Allergy.class));

    // When
    boolean result = classUnderTest.loadDatabaseFromSource();

    // Then
    assertTrue(result);
  }

  @Test
  void loadDatabaseService_shouldThrowsException_whenDataJSonIsNull() {
    // Given
    fileJson = null;
    classUnderTest = new LoadDatabaseFromJsonImpl(fileJson);

    // Then
    assertThrows(NullPointerException.class, () -> {
      classUnderTest.loadDatabaseFromSource();
    });
  }

  @Test
  void loadDatabaseService_shouldThrowsException_whenDataNotJson() {
    // Given

    classUnderTest = new LoadDatabaseFromJsonImpl(fileJson);

    // When
    try {
      when(objectMapper.readTree(any(File.class))).thenThrow(JsonProcessingException.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Then
    assertThrows(JsonProcessingException.class, () -> {
      classUnderTest.loadDatabaseFromSource();
    });
  }

  @Test
  void loadDatabaseService_shouldThrowsException_whenDataJSonProblemToRead() {
    // Given
    classUnderTest = new LoadDatabaseFromJsonImpl(fileJson);

    // When
    try {
      when(objectMapper.readTree(any(File.class))).thenThrow(IOException.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Then
    assertThrows(IOException.class, () -> {
      classUnderTest.loadDatabaseFromSource();
    });
  }
}
