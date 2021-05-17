package com.safetynet.alert.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoadDataStrategyFactory {

  private Map<StrategyName, LoadDataStrategy> strategies;

  @Autowired
  public LoadDataStrategyFactory(Set<LoadDataStrategy> strategySet) {
    createStrategy(strategySet);
  }

  private void createStrategy(Set<LoadDataStrategy> strategySet) {

    strategies = new HashMap<StrategyName, LoadDataStrategy>();

    for (LoadDataStrategy loadDataStrategy : strategySet) {
      strategies.put(loadDataStrategy.getStrategyName(), loadDataStrategy);
    }

  }

  public LoadDataStrategy findStrategy(StrategyName strategyName) {
    return strategies.get(strategyName);
  }

}
