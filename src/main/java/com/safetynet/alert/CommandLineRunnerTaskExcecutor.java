package com.safetynet.alert;

import com.safetynet.alert.database.LoadDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class CommandLineRunnerTaskExcecutor implements CommandLineRunner {

  private LoadDatabaseService loadDatabaseService;

  @Autowired
  public CommandLineRunnerTaskExcecutor(LoadDatabaseService lds) {
    this.loadDatabaseService = lds;
  }

  @Override
  public void run(String... args) throws Exception {
    loadDatabaseService.loadDatabaseFromSource();

  }

}
