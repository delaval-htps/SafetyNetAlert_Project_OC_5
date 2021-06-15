package com.safetynet.alert.database;

import static org.assertj.db.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alert.service.AllergyService;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.MedicalRecordService;
import com.safetynet.alert.service.MedicationService;
import com.safetynet.alert.service.PersonService;
import java.io.IOException;
import java.text.ParseException;
import org.assertj.db.type.DateValue;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class LoadDatabaseFromJsonIT {

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

  @Value("${spring.datasource.url}")
  private String databaseSource;
  @Value("${spring.datasource.username}")
  private String datasourceUsername;
  @Value("${spring.datasource.password}")
  private String datasourcePassword;
  @Value("classpath:${filejson.app}")
  private String filePath;

  private ResourceLoader resourceLoader;

  // class under test
  private LoadDatabaseProdFromJson classUnderTest;

  @BeforeEach
  void initSetUp() {

    databaseSource = databaseSource.split(";")[0];
    source = new Source(databaseSource, datasourceUsername, datasourcePassword);
    personTable = new Table(source, "person");
    fireStationTable = new Table(source, "fire_station");
    medicalRecordTable = new Table(source, "medical_record");
    medicationTable = new Table(source, "medication");
    allergyTable = new Table(source, "allergy");
    attributionMedicationJointTable =
        new Table(source, "attribution_medication");
    attributionAllergyJointTable = new Table(source, "attribution_allergy");

    classUnderTest = new LoadDatabaseProdFromJson(new ObjectMapper(),
                                                  resourceLoader,
                                                  filePath);

  }

  @Test
  void loadDatabaseService_shouldPersistDataJson_whenBootingApplication()
      throws IOException, ParseException {
    // ARRANGE... booting application


    // ACT nothing to do because application start alone with commandLineRunner

    // ASSERT
    assertThat(personTable).exists().hasNumberOfRows(23);
    assertThat(fireStationTable).exists().hasNumberOfRows(4);
    assertThat(medicalRecordTable).exists().hasNumberOfRows(23);
    assertThat(medicationTable).exists().hasNumberOfRows(18);
    assertThat(allergyTable).exists().hasNumberOfRows(6);
    assertThat(attributionAllergyJointTable).exists().hasNumberOfRows(11);
    assertThat(attributionMedicationJointTable).exists().hasNumberOfRows(19);

    // verify data from first person
    assertThat(personTable).row(0).hasValues(1L,
        "1509 Culver St",
        DateValue.of(1984, 06, 03),
        "Culver",
        "jaboyd@email.com",
        "John",
        "Boyd",
        "841-874-6512",
        97451,
        1L,
        1L);

    // verify relationship between first person and medical record
    assertThat(personTable).column(9).hasColumnName("id_fire_station");
    assertThat(personTable).column(10).hasColumnName("id_medical_record");
    assertThat(attributionAllergyJointTable).row(0).hasValues(1L, 1L);
    assertThat(attributionMedicationJointTable).row(0).hasValues(1L, 1L);
    assertThat(attributionMedicationJointTable).row(1).hasValues(2L, 1L);

  }
}
