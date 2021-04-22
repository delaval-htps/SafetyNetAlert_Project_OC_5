package com.safetynet.alert.database;

import static org.assertj.db.api.Assertions.assertThat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alert.service.AllergyService;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.MedicalRecordService;
import com.safetynet.alert.service.MedicationService;
import com.safetynet.alert.service.PersonService;
import java.io.IOException;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class LoadDatabaseServiceIT {

  // instance to check database
  private static Source source;
  private static Table personTable;
  private static Table fireStationTable;
  private static Table fireStationPersonJointTable;
  private static Table medicalRecordTable;
  private static Table medicationTable;
  private static Table allergyTable;
  private static Table attributionAllergyJointTable;
  private static Table attributionMedicationJointTable;

  // instance that CUT uses
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

  private ObjectMapper objectMapper;
  private Resource resource;

  // class under test
  private LoadDatabaseService classUnderTestLoadDatabaseService;

  @BeforeAll
  static void initSetUp() {
    source = new Source("jdbc:mysql://localhost:3306/SafetyNetAlert", "root", "Jsadmin4all");
    personTable = new Table(source, "person");
    fireStationTable = new Table(source, "fire_station");
    fireStationPersonJointTable = new Table(source, "fire_station_person");
    medicalRecordTable = new Table(source, "medical_record");
    medicationTable = new Table(source, "medication");
    allergyTable = new Table(source, "allergy");
    attributionMedicationJointTable = new Table(source, "attribution_medication");
    attributionAllergyJointTable = new Table(source, "attribution_allergy");
  }

  @Test
  void loadDatabaseService_shouldPersistDataJson_whenBootingApplication() throws IOException {
    // ARRANGE... booting application
    resource = new ClassPathResource("json/data.json");
    objectMapper = new ObjectMapper();
    classUnderTestLoadDatabaseService = new LoadDatabaseFromJsonImpl(objectMapper, resource,
        personService, fireStationService, medicalRecordService, medicationService, allergyService);
    // ACT nothing to do because application start alone with commandLineRunner
    // boolean result = classUnderTestLoadDatabaseService.loadDatabaseFromSource();

    // ASSERT
    assertThat(personTable).exists().hasNumberOfRows(23);
    assertThat(fireStationTable).exists().hasNumberOfRows(4);
    assertThat(medicalRecordTable).exists().hasNumberOfRows(23);
    assertThat(medicationTable).exists().hasNumberOfRows(18);
    assertThat(allergyTable).exists().hasNumberOfRows(6);
    assertThat(fireStationPersonJointTable).exists();
    assertThat(attributionAllergyJointTable).exists().hasNumberOfRows(11);
    assertThat(attributionMedicationJointTable).exists().hasNumberOfRows(19);

    // verify data from first person
    assertThat(personTable).row(0).hasValues(1L,
                                             "1509 Culver St",
                                             "03/06/1984",
                                             "Culver",
                                             "jaboyd@email.com",
                                             "John",
                                             "Boyd",
                                             "841-874-6512",
                                             97451,
                                             1L);

    // verify relationship between first person and medical record
    assertThat(personTable).column(9).hasColumnName("id_medical_record");
    assertThat(personTable).column("id_medical_record").row(0).value(9).isEqualTo(1L);
    assertThat(fireStationPersonJointTable).row(0).hasValues(1L, 1L);
    assertThat(attributionAllergyJointTable).row(0).hasValues(1L, 1L);
    assertThat(attributionMedicationJointTable).row(0).hasValues(1L, 1L);
    assertThat(attributionMedicationJointTable).row(1).hasValues(2L, 1L);
  }
}
