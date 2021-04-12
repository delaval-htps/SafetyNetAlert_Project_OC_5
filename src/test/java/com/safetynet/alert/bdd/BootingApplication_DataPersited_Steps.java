package com.safetynet.alert.bdd;


import static org.assertj.db.api.Assertions.assertThat;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import java.util.Map;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@CucumberContextConfiguration
public class BootingApplication_DataPersited_Steps {

  private static Source source;
  private static Table personTable;
  private static Table fireStationTable;
  private static Table fireStationPersonJointTable;
  private static Table medicalRecordTable;
  private static Table medicationTable;
  private static Table allergyTable;
  private static Table attributionAllergyJointTable;
  private static Table attributionMedicationJointTable;
  
  private static Map<String, String> personMap;

  @Before
  public void doSomething() {
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

  @Given("application starts and creates databases")
  public void application_starts_and_creates_databases() {
    // Nothing to do:just to start application
    // with application.properties database is automatically created
  }

  @And("data file contains this first person:")
  public void file_data_json_contains_this_first_person(DataTable table) {
    personMap = table.asMaps().get(0);

  }


  @When("application reads the data.json")
  public void application_read_data() {
    // Nothing to do:just to start application
    // with method run of main class, file data.json is read automatically
  }

  @Then("the datas from this file are correctly persited in database")
  public void datas_from_file_persisted_in_database() {
	// check that one entity instance is correctly persisted in database with correct relationship
	    // with the other entity instances. If for one instance, it is the case then it will be the same
	    // for the others.For this check , we use the instance of Person with the id :
		  
		  // verify creation Of Tables
		  
		  assertThat(personTable).exists().hasNumberOfRows(23);
		  assertThat(fireStationTable).exists().hasNumberOfRows(4);
		  assertThat(medicalRecordTable).exists().hasNumberOfRows(11);
		  assertThat(medicationTable).exists().hasNumberOfRows(18);
		  assertThat(allergyTable).exists().hasNumberOfRows(6);
		  assertThat(fireStationPersonJointTable).exists();
		  assertThat(attributionAllergyJointTable).exists().hasNumberOfRows(11);
		  assertThat(attributionMedicationJointTable).exists().hasNumberOfRows(19);
	  
		// verify data from first person
		  assertThat(personTable).row(1).hasValues(
	                                             1,
	                                             personMap.get("firstName"),
	                                             personMap.get("lastName"),
	                                             personMap.get("address"),
	                                             "03/06/1984",
	                                             personMap.get("city"),
	                                             personMap.get("zip"),
	                                             personMap.get("phone"),
	                                             personMap.get("email"));
		  
		  //verify relationship between first person and medical record
		  assertThat(personTable).column().hasColumnName("id_medical_record");
		  assertThat(personTable).column("id_medical_record").row(1).value(1).isEqualTo(1);
		  assertThat(fireStationPersonJointTable).row().hasValues(1,1);
		  assertThat(attributionAllergyJointTable).row().hasValues(1,1);
		  assertThat(attributionMedicationJointTable).row().hasValues(1,1);
		  assertThat(attributionMedicationJointTable).row().hasValues(1,2);
  }
}
