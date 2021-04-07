package com.safetynet.alert.bdd;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@CucumberContextConfiguration
public class BootingApplication_DataPersited_Steps {

  @Given("application SafetyNet is started and created databases")
  public void application_started_and_and_created_databases() {
    // Nothing to do:just to start application
    // with application.properties database is automaticaly created
  }

  @When("application read the data.json")
  public void application_read_data() {
    // Nothing to do:just to start application
  }

  @Then("the datas from this file are correctly persited in database")
  public void datas_from_file_persisted_in_database() {

  }
}
