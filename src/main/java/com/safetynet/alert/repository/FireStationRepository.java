package com.safetynet.alert.repository;

import com.safetynet.alert.model.FireStation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * repository For Entity FireStation.
 *
 * @author delaval
 *
 */
@Repository
public interface FireStationRepository
    extends JpaRepository<FireStation, Long> {

  @Query("SELECT f FROM FireStation AS f WHERE f.numberStation= ?1")
  Optional<FireStation> getOneByNumberStation(int numberStation);

  @Query("SELECT f FROM FireStation AS f JOIN f.addresses AS a WHERE a = ?1")
  Optional<FireStation> getOneByAddress(String address);

  @Query("SELECT f FROM FireStation as f JOIN FETCH f.addresses where f.idFireStation = ?1")
  Optional<FireStation> getOneJoinAllById(long l);



}
