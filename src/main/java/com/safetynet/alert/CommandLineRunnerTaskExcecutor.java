package com.safetynet.alert;

import com.safetynet.alert.database.LoadDataStrategyFactory;
import com.safetynet.alert.database.StrategyName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Component
@Profile("!test")
public class CommandLineRunnerTaskExcecutor implements CommandLineRunner {

  @Autowired
  private LoadDataStrategyFactory loadDataStrategyFactory;


  @Override
  public void run(String... args) throws Exception {

    loadDataStrategyFactory.findStrategy(StrategyName.StrategyProd)
                           .loadDatabaseFromSource();
  }

}
