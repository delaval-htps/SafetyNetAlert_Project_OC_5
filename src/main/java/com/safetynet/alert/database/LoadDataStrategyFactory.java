package com.safetynet.alert.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class allows to chose a type of load data {@link LoadDataStrategy} with {@link StrategyName}.
 * in function of {@link StrategyName}, the load of data will be from different sources.
 *
 * @author delaval
 *
 */
@Component
public class LoadDataStrategyFactory {

  private Map<StrategyName, LoadDataStrategy> strategies;

  /**
   * Constructor with a Set of {@link LoadDataStrategy}.
   * allows to map {@link LoadDataStrategy} with {@link StrategyName}.
   *
   * @param strategySet
   *            a Set of all type of Services to load data.
   */
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

  /**
   * Method to find a strategy(Service) to load data From any kind of Source.
   *
   * @param strategyName
   *            a type of Strategy to chose for loading data.
   *
   * @return    a LoadDataStrategy to load Data from a source according strategyName.
   */
  public LoadDataStrategy findStrategy(StrategyName strategyName) {

    return strategies.get(strategyName);

  }

}
