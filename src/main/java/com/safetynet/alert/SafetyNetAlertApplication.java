package com.safetynet.alert;

import com.safetynet.alert.database.LoadDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * class main of application safetynet Alert .
 *
 * @author delaval
 *
 */
@SpringBootApplication

public class SafetyNetAlertApplication implements CommandLineRunner {

  @Autowired
  LoadDatabaseService loadDatabaseService;

  public static void main(String[] args) {
    SpringApplication.run(SafetyNetAlertApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {

    loadDatabaseService.loadDatabaseFromSource();

  }

}
