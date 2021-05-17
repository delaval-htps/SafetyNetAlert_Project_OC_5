package com.safetynet.alert.database;

public interface LoadDataStrategy {

  StrategyName getStrategyName();

  boolean loadDatabaseFromSource();

}
