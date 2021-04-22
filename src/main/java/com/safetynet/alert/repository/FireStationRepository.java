package com.safetynet.alert.repository;

import com.safetynet.alert.model.FireStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FireStationRepository extends JpaRepository<FireStation, Long> {

  @Query("SELECT f FROM FireStation AS f WHERE f.numberStation= ?1")
  FireStation getOneByNumberStation(int numberStation);

}
