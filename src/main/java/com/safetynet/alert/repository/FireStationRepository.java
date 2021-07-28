package com.safetynet.alert.repository;

import com.safetynet.alert.model.FireStation;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
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

  @Query("SELECT f"
      + " FROM FireStation AS f"
      + " WHERE f.numberStation= ?1")
  Optional<FireStation> getOneByNumberStation(int numberStation);

  @Query("SELECT f"
      + " FROM FireStation as f "
      + " JOIN FETCH f.addresses "
      + " where f.idFireStation = ?1")
  Optional<FireStation> getOneJoinAddressesById(long l);

  @Query("SELECT f"
      + " FROM FireStation AS f"
      + " JOIN FETCH f.addresses a"
      + " JOIN FETCH f.persons p"
      + " WHERE f.numberStation= ?1")
  Optional<FireStation> getOneAllFetchByNumberStation(int numberStation);

  @Query("SELECT distinct f"
      + " FROM FireStation as f "
      + " LEFT JOIN FETCH f.addresses ")
  List<FireStation> findAllFetchAddress();

  @Query("SELECT distinct f"
      + " FROM FireStation AS f"
      + " JOIN fetch f.addresses a"
      + " where :address member of a")
  List<FireStation> findFireStationsByAddress(@Valid String address);

  @Query("SELECT distinct f"
      + " FROM FireStation AS f"
      + " LEFT JOIN fetch f.addresses a"
      + " LEFT JOIN Fetch f.persons"
      + " where :address member of a")
  List<FireStation> findFireStationsFetchPersonByAddress(@Valid String address);

}
