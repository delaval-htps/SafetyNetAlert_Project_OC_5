package com.safetynet.alert.bdd;

import static org.assertj.db.api.Assertions.assertThat;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import java.text.ParseException;
import java.util.Map;
import org.assertj.db.type.DateValue;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * Test of acceptance with cucumber.
 *
 * @see booting_application_data_persited.feature in test/resources/features
 * @author delaval
 *
 */
@SpringBootTest
@CucumberContextConfiguration
public class BootingApplicationDataPersitedSteps {

  private Source source;
  private Table personTable;
  private Table fireStationTable;
  private Table fireStationPersonJointTable;
  private Table medicalRecordTable;
  private Table medicationTable;
  private Table allergyTable;
  private Table attributionAllergyJointTable;
  private Table attributionMedicationJointTable;

  @Value("${spring.datasource.url}")
  private String databaseSource;
  @Value("${spring.datasource.username}")
  private String datasourceUsername;
  @Value("${spring.datasource.password}")
  private String datasourcePassword;

  private Map<String, String> personMap;

  /**
   * Set up before.
   */
  @Before
  public void doSomething() {

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

  }

  /**
   * Given cucumber: nothing to do.
   */
  @Given("application starts and creates databases")
  public void application_starts_and_creates_databases() {

    // Nothing to do:just to start application
    // with application.properties database is automatically created
  }

  /**
   * Given cucumber with a table in parameter.
   *
   * @see booting_application_data_persited.feature in test/resources/features.
   *
   * @param table
   *
   */
  @And("data file contains this first person:")
  public void file_data_json_contains_this_first_person(DataTable table) {

    personMap = table.asMaps().get(0);

  }

  /**
   * When cucumber: nothing to do.
   */
  @When("application reads the data.json")
  public void application_read_data() {

    // Nothing to do:just to start application
    // with method run of main class, file data.json is read automatically
  }

  /**
   * Then cucumber: check if database was correclty persisted with data from filedata.json .
   *
   * @throws ParseException
   *            if there is a problem with parsing data json in Object.
   */
  @Then("the datas from this file are correctly persited in database")
  public void datas_from_file_persisted_in_database() throws ParseException {
    // check that one entity instance is correctly persisted in database with

    // correct relationship
    // with the other entity instances. If for one instance, it is the case then
    // it will be the same
    // for the others.For this check , we use the instance of Person with the id
    // :

    // verify creation Of Tables

    assertThat(personTable).exists().hasNumberOfRows(23);
    assertThat(fireStationTable).exists().hasNumberOfRows(4);
    assertThat(medicalRecordTable).exists().hasNumberOfRows(23);
    assertThat(medicationTable).exists().hasNumberOfRows(18);
    assertThat(allergyTable).exists().hasNumberOfRows(6);
    assertThat(attributionAllergyJointTable).exists().hasNumberOfRows(11);
    assertThat(attributionMedicationJointTable).exists().hasNumberOfRows(19);

    // verify data from first person
    assertThat(personTable).row(0).hasValues(1L,
        personMap.get("address"),
        DateValue.of(1984, 03, 06),
        personMap.get("city"),
        personMap.get("email"),
        personMap.get("firstName"),
        personMap.get("lastName"),
        personMap.get("phone"),
        personMap.get("zip"),
        1L);

    // verify relationship between first person and medical record

    assertThat(personTable).column(9).hasColumnName("id_medical_record");
    assertThat(attributionAllergyJointTable).row(0).hasValues(1L, 1L);
    assertThat(attributionMedicationJointTable).row(0).hasValues(1L, 1L);
    assertThat(attributionMedicationJointTable).row(1).hasValues(2L, 1L);

  }
}
