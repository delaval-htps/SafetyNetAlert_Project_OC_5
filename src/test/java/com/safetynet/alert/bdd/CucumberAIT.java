package com.safetynet.alert.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * class to configure and run cucumber with Springboot.
 *
 * @author delaval
 *
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    plugin = {"pretty", "html:target/html-cucumber-report"},
    stepNotifications = true)
public class CucumberAIT {


}
