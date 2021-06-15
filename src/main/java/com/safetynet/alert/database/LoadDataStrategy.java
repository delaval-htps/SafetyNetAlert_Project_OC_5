package com.safetynet.alert.database;

/**
 * Interface for load data from any kind of source.
 *
 * @author delaval
 *
 */
public interface LoadDataStrategy {

  StrategyName getStrategyName();

  boolean loadDatabaseFromSource();

}
